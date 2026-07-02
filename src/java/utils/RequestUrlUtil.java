package utils;

import javax.servlet.http.HttpServletRequest;

public final class RequestUrlUtil {

    private RequestUrlUtil() {
    }

    public static String getBaseUrl(HttpServletRequest request) {
        String scheme = firstForwardedValue(request.getHeader("X-Forwarded-Proto"));
        String host = firstForwardedValue(request.getHeader("X-Forwarded-Host"));

        if (isBlank(scheme)) {
            scheme = request.getScheme();
        }
        if (isBlank(host)) {
            host = request.getHeader("Host");
        }
        if (isBlank(host)) {
            host = request.getServerName();
            int port = request.getServerPort();
            if (port > 0 && port != 80 && port != 443) {
                host += ":" + port;
            }
        }

        return scheme + "://" + host + request.getContextPath();
    }

    public static String buildUrl(HttpServletRequest request, String path) {
        String safePath = path == null ? "" : path.trim();
        if (!safePath.startsWith("/")) {
            safePath = "/" + safePath;
        }
        return getBaseUrl(request) + safePath;
    }

    private static String firstForwardedValue(String value) {
        if (isBlank(value)) {
            return "";
        }
        int comma = value.indexOf(',');
        return comma >= 0 ? value.substring(0, comma).trim() : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
