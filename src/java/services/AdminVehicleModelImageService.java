package services;

import daos.AdminVehicleModelImageDAO;
import dto.AdminVehicleModelImageRow;
import enums.VehicleModelImageType;
import java.util.List;
import java.util.UUID;
import models.VehicleModel;
import models.VehicleModelImage;
import utils.JPAUtil;

public class AdminVehicleModelImageService {
    private final AdminVehicleModelImageDAO imageDAO = new AdminVehicleModelImageDAO();

    public List<AdminVehicleModelImageRow> search(String keyword, String modelId, String typeValue) {
        VehicleModelImageType type = isBlank(typeValue) || "ALL".equals(typeValue)
                ? null
                : VehicleModelImageType.fromValue(typeValue);
        return JPAUtil.execute(em -> imageDAO.search(em, keyword, modelId, type));
    }

    public VehicleModelImage findById(String imageId) {
        return JPAUtil.execute(em -> imageDAO.findById(em, imageId));
    }

    public List<VehicleModel> findAllModels() {
        return JPAUtil.execute(imageDAO::findAllModels);
    }

    public void create(String modelId, String imageUrl, String imageType) {
        validateRequired(modelId, "Vehicle model");
        validateRequired(imageUrl, "Image URL");
        VehicleModelImageType type = VehicleModelImageType.fromValue(imageType);

        JPAUtil.executeInTransaction(em -> {
            if (!imageDAO.modelExists(em, modelId)) {
                throw new IllegalArgumentException("Vehicle model not found.");
            }
            VehicleModelImage image = new VehicleModelImage();
            image.setImageId(UUID.randomUUID().toString());
            image.setModelId(modelId.trim());
            image.setImageUrl(imageUrl.trim());
            image.setImageType(type);
            imageDAO.create(em, image);
            return null;
        });
    }

    public void update(String imageId, String modelId, String imageUrl, String imageType) {
        validateRequired(imageId, "Image ID");
        validateRequired(modelId, "Vehicle model");
        validateRequired(imageUrl, "Image URL");
        VehicleModelImageType type = VehicleModelImageType.fromValue(imageType);

        JPAUtil.executeInTransaction(em -> {
            VehicleModelImage image = imageDAO.findById(em, imageId);
            if (image == null) {
                throw new IllegalArgumentException("Vehicle model image not found.");
            }
            if (!imageDAO.modelExists(em, modelId)) {
                throw new IllegalArgumentException("Vehicle model not found.");
            }
            image.setModelId(modelId.trim());
            image.setImageUrl(imageUrl.trim());
            image.setImageType(type);
            imageDAO.update(em, image);
            return null;
        });
    }

    public void delete(String imageId) {
        validateRequired(imageId, "Image ID");
        JPAUtil.executeInTransaction(em -> {
            VehicleModelImage image = imageDAO.findById(em, imageId);
            if (image == null) {
                throw new IllegalArgumentException("Vehicle model image not found.");
            }
            imageDAO.delete(em, image);
            return null;
        });
    }

    private void validateRequired(String value, String label) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
