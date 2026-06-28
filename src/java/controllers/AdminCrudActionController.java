package controllers;

import enums.Role;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import models.Account;
import services.AdminAccountService;
import services.AdminVehicleModelImageService;
import services.AdminVehicleModelService;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
@WebServlet(name = "AdminCrudActionController", urlPatterns = {
    "/admin/accounts/save",
    "/admin/accounts/status",
    "/admin/vehicle-models/save",
    "/admin/vehicle-models/delete",
    "/admin/vehicle-model-images/save",
    "/admin/vehicle-model-images/delete"
})
public class AdminCrudActionController extends HttpServlet {
    private final AdminAccountService accountService = new AdminAccountService();
    private final AdminVehicleModelService modelService = new AdminVehicleModelService();
    private final AdminVehicleModelImageService imageService = new AdminVehicleModelImageService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        Account admin = requireAdmin(request, response);
        if (admin == null) {
            return;
        }

        String path = request.getServletPath();
        try {
            if ("/admin/accounts/save".equals(path)) {
                saveAccount(request, admin);
                flash(request, "adminSuccess", "Account saved successfully.");
                response.sendRedirect(request.getContextPath() + "?action=admin-accounts");
                return;
            }
            if ("/admin/accounts/status".equals(path)) {
                accountService.updateStatus(request.getParameter("accountId"), admin.getAccountId(), request.getParameter("status"));
                flash(request, "adminSuccess", "Account status updated.");
                response.sendRedirect(request.getContextPath() + "?action=admin-accounts");
                return;
            }
            if ("/admin/vehicle-models/save".equals(path)) {
                saveVehicleModel(request);
                flash(request, "adminSuccess", "Vehicle model saved successfully.");
                response.sendRedirect(request.getContextPath() + "?action=admin-vehicle-models");
                return;
            }
            if ("/admin/vehicle-models/delete".equals(path)) {
                modelService.delete(request.getParameter("modelId"));
                flash(request, "adminSuccess", "Vehicle model deleted successfully.");
                response.sendRedirect(request.getContextPath() + "?action=admin-vehicle-models");
                return;
            }
            if ("/admin/vehicle-model-images/save".equals(path)) {
                saveVehicleModelImage(request);
                flash(request, "adminSuccess", "Vehicle model image saved successfully.");
                response.sendRedirect(request.getContextPath() + "?action=admin-vehicle-model-images");
                return;
            }
            if ("/admin/vehicle-model-images/delete".equals(path)) {
                imageService.delete(request.getParameter("imageId"));
                flash(request, "adminSuccess", "Vehicle model image deleted successfully.");
                response.sendRedirect(request.getContextPath() + "?action=admin-vehicle-model-images");
                return;
            }
            response.sendRedirect(request.getContextPath() + "?action=admin-dashboard");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            flash(request, "adminError", ex.getMessage());
            response.sendRedirect(fallbackUrl(request, path));
        } catch (RuntimeException ex) {
            flash(request, "adminError", "Cannot process request. Please try again.");
            response.sendRedirect(fallbackUrl(request, path));
        }
    }

    private void saveAccount(HttpServletRequest request, Account admin) {
        String accountId = trim(request.getParameter("accountId"));
        if (accountId.isEmpty()) {
            accountService.create(
                    request.getParameter("fullName"),
                    request.getParameter("email"),
                    request.getParameter("phone"),
                    request.getParameter("password"),
                    request.getParameter("confirmPassword"),
                    request.getParameter("role"),
                    request.getParameter("status"));
        } else {
            accountService.update(
                    accountId,
                    admin.getAccountId(),
                    request.getParameter("fullName"),
                    request.getParameter("phone"),
                    request.getParameter("role"),
                    request.getParameter("status"));
        }
    }

    private void saveVehicleModel(HttpServletRequest request) {
        String modelId = trim(request.getParameter("modelId"));
        if (modelId.isEmpty()) {
            modelService.create(
                    request.getParameter("categoryId"),
                    request.getParameter("name"),
                    request.getParameter("brand"),
                    request.getParameter("seatCount"),
                    request.getParameter("pricePerDay"),
                    request.getParameter("description"));
        } else {
            modelService.update(
                    modelId,
                    request.getParameter("categoryId"),
                    request.getParameter("name"),
                    request.getParameter("brand"),
                    request.getParameter("seatCount"),
                    request.getParameter("pricePerDay"),
                    request.getParameter("description"));
        }
    }

    private void saveVehicleModelImage(HttpServletRequest request) throws IOException, ServletException {
        String imageId = trim(request.getParameter("imageId"));
        String uploadedImagePath = saveVehicleImage(request, imageId.isEmpty());
        String imageUrl = uploadedImagePath == null
                ? firstNotBlank(request.getParameter("currentImageUrl"), request.getParameter("imageUrl"))
                : uploadedImagePath;

        if (imageId.isEmpty()) {
            imageService.create(
                    request.getParameter("modelId"),
                    imageUrl,
                    request.getParameter("imageType"));
        } else {
            imageService.update(
                    imageId,
                    request.getParameter("modelId"),
                    imageUrl,
                    request.getParameter("imageType"));
        }
    }

    private String saveVehicleImage(HttpServletRequest request, boolean required)
            throws IOException, ServletException {
        Part imagePart = request.getPart("imageFile");
        if (imagePart == null || imagePart.getSize() == 0) {
            if (required) {
                throw new IllegalArgumentException("Please choose an image file.");
            }
            return null;
        }

        String originalFileName = submittedFileName(imagePart);
        String extension = fileExtension(originalFileName);
        if (!isAllowedImageExtension(extension)) {
            throw new IllegalArgumentException("Image must be JPG, PNG, WEBP, or GIF.");
        }

        String storedFileName = System.currentTimeMillis() + "-"
                + UUID.randomUUID().toString().substring(0, 8) + extension;
        Path uploadDir = resolveDeployedVehicleImageDir();
        Files.createDirectories(uploadDir);
        Path targetFile = uploadDir.resolve(storedFileName);

        try (java.io.InputStream input = imagePart.getInputStream()) {
            Files.copy(input, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }

        Path sourceDir = resolveSourceVehicleImageDir();
        if (sourceDir != null && !sourceDir.equals(uploadDir)) {
            Files.createDirectories(sourceDir);
            Files.copy(targetFile, sourceDir.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);
        }

        return "assets/images/vehicles/" + storedFileName;
    }

    private Path resolveDeployedVehicleImageDir() {
        String realPath = getServletContext().getRealPath("/assets/images/vehicles");
        if (realPath == null) {
            throw new IllegalStateException("Cannot resolve image upload folder.");
        }
        return Paths.get(realPath);
    }

    private Path resolveSourceVehicleImageDir() {
        String webRoot = getServletContext().getRealPath("/");
        if (webRoot == null) {
            return null;
        }
        String normalized = new File(webRoot).getAbsolutePath();
        String marker = File.separator + "build" + File.separator + "web";
        int markerIndex = normalized.indexOf(marker);
        if (markerIndex < 0) {
            return null;
        }
        return Paths.get(normalized.substring(0, markerIndex), "web", "assets", "images", "vehicles");
    }

    private String submittedFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return "";
        }
        for (String token : contentDisposition.split(";")) {
            String value = token.trim();
            if (value.startsWith("filename")) {
                String fileName = value.substring(value.indexOf('=') + 1).trim().replace("\"", "");
                return Paths.get(fileName).getFileName().toString();
            }
        }
        return "";
    }

    private String fileExtension(String fileName) {
        String safeName = fileName == null ? "" : fileName.trim();
        int dotIndex = safeName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == safeName.length() - 1) {
            return "";
        }
        return safeName.substring(dotIndex).toLowerCase(Locale.ROOT);
    }

    private boolean isAllowedImageExtension(String extension) {
        return ".jpg".equals(extension)
                || ".jpeg".equals(extension)
                || ".png".equals(extension)
                || ".webp".equals(extension)
                || ".gif".equals(extension);
    }

    private String fallbackUrl(HttpServletRequest request, String path) throws IOException {
        String context = request.getContextPath();
        if (path.contains("accounts")) {
            String id = trim(request.getParameter("accountId"));
            return context + "?action=admin-account-form" + (id.isEmpty() ? "" : "&id=" + encode(id));
        }
        if (path.contains("vehicle-model-images")) {
            String id = trim(request.getParameter("imageId"));
            return context + "?action=admin-vehicle-model-image-form" + (id.isEmpty() ? "" : "&id=" + encode(id));
        }
        if (path.contains("vehicle-models")) {
            String id = trim(request.getParameter("modelId"));
            return context + "?action=admin-vehicle-model-form" + (id.isEmpty() ? "" : "&id=" + encode(id));
        }
        return context + "?action=admin-dashboard";
    }

    private Account requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("user") instanceof Account)) {
            response.sendRedirect(request.getContextPath() + "?action=login");
            return null;
        }
        Account user = (Account) session.getAttribute("user");
        if (user.getRole() != Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        return user;
    }

    private void flash(HttpServletRequest request, String key, String value) {
        request.getSession().setAttribute(key, value);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String firstNotBlank(String first, String second) {
        String firstValue = trim(first);
        return firstValue.isEmpty() ? trim(second) : firstValue;
    }

    private String encode(String value) throws IOException {
        return java.net.URLEncoder.encode(value == null ? "" : value, "UTF-8");
    }
}
