package services;

import daos.AccountDAO;
import daos.IAccountDAO;
import daos.IWalletDAO;
import daos.WalletDAO;
import dto.GoogleUserProfile;
import enums.Role;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import models.Account;
import models.Wallet;
import utils.EnvUtil;
import utils.PasswordUtil;

public class GoogleAuthService {
    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";
    private static final String SCOPE = "openid email profile";

    private final IAccountDAO accountDAO = new AccountDAO();
    private final IWalletDAO walletDAO = new WalletDAO();

    public String buildAuthorizationUrl(ServletContext context, String redirectUri, String state) {
        String clientId = requireConfig(context, "GOOGLE_CLIENT_ID");
        return AUTH_URL
                + "?client_id=" + encode(clientId)
                + "&redirect_uri=" + encode(redirectUri)
                + "&response_type=code"
                + "&scope=" + encode(SCOPE)
                + "&state=" + encode(state)
                + "&prompt=select_account";
    }

    public Account handleCallback(ServletContext context, String code, String redirectUri) {
        String idToken = exchangeCodeForIdToken(context, code, redirectUri);
        verifyIdToken(context, idToken);
        GoogleUserProfile profile = parseProfileFromIdToken(idToken);
        return loginOrCreateAccount(profile);
    }

    private String exchangeCodeForIdToken(ServletContext context, String code, String redirectUri) {
        String clientId = requireConfig(context, "GOOGLE_CLIENT_ID");
        String clientSecret = requireConfig(context, "GOOGLE_CLIENT_SECRET");
        String body = "code=" + encode(code)
                + "&client_id=" + encode(clientId)
                + "&client_secret=" + encode(clientSecret)
                + "&redirect_uri=" + encode(redirectUri)
                + "&grant_type=authorization_code";

        String response = postForm(TOKEN_URL, body);
        String idToken = jsonString(response, "id_token");
        if (isBlank(idToken)) {
            throw new IllegalStateException("Không thể xác thực Google. Vui lòng thử lại.");
        }
        return idToken;
    }

    private void verifyIdToken(ServletContext context, String idToken) {
        String response = get(TOKEN_INFO_URL + encode(idToken));
        String audience = jsonString(response, "aud");
        String issuer = jsonString(response, "iss");
        String emailVerified = jsonValue(response, "email_verified");
        String clientId = requireConfig(context, "GOOGLE_CLIENT_ID");

        if (!clientId.equals(audience)) {
            throw new IllegalStateException("Google token không hợp lệ.");
        }
        if (!"accounts.google.com".equals(issuer) && !"https://accounts.google.com".equals(issuer)) {
            throw new IllegalStateException("Google token issuer không hợp lệ.");
        }
        if (!"true".equalsIgnoreCase(emailVerified.replace("\"", ""))) {
            throw new IllegalStateException("Email Google chưa được xác minh.");
        }
    }

    private GoogleUserProfile parseProfileFromIdToken(String idToken) {
        String[] parts = idToken.split("\\.");
        if (parts.length < 2) {
            throw new IllegalStateException("Google token không hợp lệ.");
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        GoogleUserProfile profile = new GoogleUserProfile();
        profile.setSub(jsonString(payloadJson, "sub"));
        profile.setEmail(jsonString(payloadJson, "email"));
        profile.setEmailVerified("true".equalsIgnoreCase(jsonValue(payloadJson, "email_verified").replace("\"", "")));
        profile.setName(jsonString(payloadJson, "name"));
        profile.setPicture(jsonString(payloadJson, "picture"));

        if (isBlank(profile.getEmail())) {
            throw new IllegalStateException("Không thể lấy email từ Google.");
        }
        return profile;
    }

    private Account loginOrCreateAccount(GoogleUserProfile profile) {
        if (!profile.isEmailVerified()) {
            throw new IllegalStateException("Email Google chưa được xác minh.");
        }

        Account existing = accountDAO.getAccountByEmail(profile.getEmail());
        if (existing != null) {
            ensureActive(existing);
            return existing;
        }

        Account account = new Account();
        account.setAccountId(UUID.randomUUID().toString());
        account.setEmail(profile.getEmail());
        account.setPassword(PasswordUtil.hashPassword(UUID.randomUUID().toString()));
        account.setFullName(defaultName(profile));
        account.setPhone("");
        account.setIsVerified(true);
        account.setRole(Role.CUSTOMER);
        account.setStatus("ACTIVE");
        account.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        if (!accountDAO.createAccount(account)) {
            throw new IllegalStateException("Không thể tạo tài khoản Google. Vui lòng thử lại.");
        }

        Account created = accountDAO.getAccountByEmail(profile.getEmail());
        if (created == null) {
            throw new IllegalStateException("Không thể lấy thông tin tài khoản Google.");
        }

        if (walletDAO.getWalletByAccountId(created.getAccountId()) == null) {
            walletDAO.createWallet(new Wallet(created.getAccountId()));
        }
        return created;
    }

    private void ensureActive(Account account) {
        if (!"ACTIVE".equals(account.getStatus())) {
            throw new IllegalStateException("Tài khoản của bạn đã bị vô hiệu hóa.");
        }
    }

    private String defaultName(GoogleUserProfile profile) {
        if (!isBlank(profile.getName())) {
            return profile.getName().trim();
        }
        String email = profile.getEmail();
        int atIndex = email == null ? -1 : email.indexOf('@');
        return atIndex > 0 ? email.substring(0, atIndex) : "Google User";
    }

    private String requireConfig(ServletContext context, String key) {
        String value = EnvUtil.get(key, context);
        if (isBlank(value)) {
            throw new IllegalStateException("Thiếu cấu hình " + key + " trong file .env.");
        }
        return value;
    }

    private String postForm(String url, String body) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(bytes);
            }
            return readResponse(connection);
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể kết nối Google OAuth. Vui lòng thử lại.", ex);
        }
    }

    private String get(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            return readResponse(connection);
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể xác minh Google token. Vui lòng thử lại.", ex);
        }
    }

    private String readResponse(HttpURLConnection connection) throws Exception {
        int status = connection.getResponseCode();
        InputStream stream = status >= 200 && status < 300
                ? connection.getInputStream()
                : connection.getErrorStream();
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        if (status < 200 || status >= 300) {
            throw new IllegalStateException("Google OAuth trả về lỗi: " + status);
        }
        return response.toString();
    }

    private String jsonString(String json, String key) {
        return unescape(jsonValue(json, key).replaceAll("^\"|\"$", ""));
    }

    private String jsonValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(\"(?:\\\\.|[^\"])*\"|true|false|null|[0-9]+)");
        Matcher matcher = pattern.matcher(json == null ? "" : json);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String unescape(String value) {
        return value == null
                ? ""
                : value.replace("\\\"", "\"")
                        .replace("\\/", "/")
                        .replace("\\\\", "\\")
                        .replace("\\n", "\n")
                        .replace("\\r", "\r")
                        .replace("\\t", "\t");
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value == null ? "" : value, "UTF-8");
        } catch (Exception ex) {
            return "";
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
