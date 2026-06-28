package services;

import daos.AdminVehicleModelDAO;
import dto.AdminVehicleModelRow;
import java.util.List;
import java.util.UUID;
import models.Category;
import models.VehicleModel;
import utils.JPAUtil;

public class AdminVehicleModelService {
    private final AdminVehicleModelDAO modelDAO = new AdminVehicleModelDAO();

    public List<AdminVehicleModelRow> search(String keyword, String categoryId) {
        return JPAUtil.execute(em -> modelDAO.search(em, keyword, categoryId));
    }

    public VehicleModel findById(String modelId) {
        return JPAUtil.execute(em -> modelDAO.findById(em, modelId));
    }

    public List<Category> findAllCategories() {
        return JPAUtil.execute(modelDAO::findAllCategories);
    }

    public void create(String categoryId, String name, String brand, String seatCount,
            String pricePerDay, String description) {
        validateRequired(categoryId, "Category");
        validateRequired(name, "Model name");
        int seats = parsePositiveInt(seatCount, "Seat count");
        double price = parsePositiveDouble(pricePerDay, "Price per day");

        JPAUtil.executeInTransaction(em -> {
            if (modelDAO.nameExists(em, name, categoryId, null)) {
                throw new IllegalArgumentException("Model name already exists in this category.");
            }
            VehicleModel model = new VehicleModel();
            model.setModelId(UUID.randomUUID().toString());
            model.setCategoryId(categoryId.trim());
            model.setName(name.trim());
            model.setBrand(blankToNull(brand));
            model.setSeatCount(seats);
            model.setPricePerDay(price);
            model.setDescription(blankToNull(description));
            modelDAO.create(em, model);
            return null;
        });
    }

    public void update(String modelId, String categoryId, String name, String brand, String seatCount,
            String pricePerDay, String description) {
        validateRequired(modelId, "Model ID");
        validateRequired(categoryId, "Category");
        validateRequired(name, "Model name");
        int seats = parsePositiveInt(seatCount, "Seat count");
        double price = parsePositiveDouble(pricePerDay, "Price per day");

        JPAUtil.executeInTransaction(em -> {
            VehicleModel model = modelDAO.findById(em, modelId);
            if (model == null) {
                throw new IllegalArgumentException("Vehicle model not found.");
            }
            if (modelDAO.nameExists(em, name, categoryId, modelId)) {
                throw new IllegalArgumentException("Model name already exists in this category.");
            }
            model.setCategoryId(categoryId.trim());
            model.setName(name.trim());
            model.setBrand(blankToNull(brand));
            model.setSeatCount(seats);
            model.setPricePerDay(price);
            model.setDescription(blankToNull(description));
            modelDAO.update(em, model);
            return null;
        });
    }

    public void delete(String modelId) {
        validateRequired(modelId, "Model ID");
        JPAUtil.executeInTransaction(em -> {
            VehicleModel model = modelDAO.findById(em, modelId);
            if (model == null) {
                throw new IllegalArgumentException("Vehicle model not found.");
            }
            if (modelDAO.countVehicles(em, modelId) > 0) {
                throw new IllegalArgumentException("Cannot delete model because vehicles are linked.");
            }
            if (modelDAO.countImages(em, modelId) > 0) {
                throw new IllegalArgumentException("Cannot delete model because images are linked.");
            }
            modelDAO.delete(em, model);
            return null;
        });
    }

    private int parsePositiveInt(String value, String label) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) throw new NumberFormatException();
            return parsed;
        } catch (Exception ex) {
            throw new IllegalArgumentException(label + " must be greater than 0.");
        }
    }

    private double parsePositiveDouble(String value, String label) {
        try {
            double parsed = Double.parseDouble(value);
            if (parsed <= 0) throw new NumberFormatException();
            return parsed;
        } catch (Exception ex) {
            throw new IllegalArgumentException(label + " must be greater than 0.");
        }
    }

    private void validateRequired(String value, String label) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
