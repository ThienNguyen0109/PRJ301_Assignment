package services;

import daos.AdminVehicleDAO;
import dto.AdminVehicleRow;
import enums.VehicleStatus;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import models.Category;
import models.Station;
import models.Vehicle;
import models.VehicleModel;
import utils.JPAUtil;

public class AdminVehicleService {
    private final AdminVehicleDAO vehicleDAO = new AdminVehicleDAO();

    public List<AdminVehicleRow> search(String keyword, String stationId, String categoryId, String statusValue) {
        return JPAUtil.execute(em -> vehicleDAO.search(em, keyword, stationId, categoryId, parseStatusFilter(statusValue)));
    }
    public Vehicle findById(String vehicleId) { return JPAUtil.execute(em -> vehicleDAO.findById(em, vehicleId)); }
    public List<VehicleModel> findAllModels() { return JPAUtil.execute(vehicleDAO::findAllModels); }
    public List<Station> findAllStations() { return JPAUtil.execute(vehicleDAO::findAllStations); }
    public List<Category> findAllCategories() { return JPAUtil.execute(vehicleDAO::findAllCategories); }

    public void create(String modelId, String stationId, String licensePlate, String color,
            String batteryLevel, String statusValue) {
        VehicleStatus status = parseStatus(statusValue);
        validateInput(modelId, stationId, licensePlate, batteryLevel);
        if (status == VehicleStatus.RENTED) {
            throw new IllegalArgumentException("A vehicle cannot be created as RENTED. Use the booking flow instead.");
        }
        JPAUtil.executeInTransaction(em -> {
            validateReferences(em, modelId, stationId);
            if (vehicleDAO.licensePlateExists(em, licensePlate, null)) {
                throw new IllegalArgumentException("License plate already exists.");
            }
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleId(UUID.randomUUID().toString());
            vehicle.setModelId(modelId.trim());
            vehicle.setStationId(stationId.trim());
            vehicle.setLicensePlate(normalizePlate(licensePlate));
            vehicle.setColor(blankToNull(color));
            vehicle.setBatteryLevel(parseBattery(batteryLevel));
            vehicle.setStatus(status);
            vehicle.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            vehicleDAO.create(em, vehicle);
            return null;
        });
    }

    public void update(String vehicleId, String modelId, String stationId, String licensePlate,
            String color, String batteryLevel, String statusValue) {
        required(vehicleId, "Vehicle ID");
        VehicleStatus requestedStatus = parseStatus(statusValue);
        validateInput(modelId, stationId, licensePlate, batteryLevel);
        JPAUtil.executeInTransaction(em -> {
            Vehicle vehicle = vehicleDAO.findById(em, vehicleId);
            if (vehicle == null) throw new IllegalArgumentException("Vehicle not found.");
            validateReferences(em, modelId, stationId);
            if (vehicleDAO.licensePlateExists(em, licensePlate, vehicleId)) {
                throw new IllegalArgumentException("License plate already exists.");
            }
            boolean activeRental = vehicleDAO.hasActiveRental(em, vehicleId);
            if (activeRental && requestedStatus != VehicleStatus.RENTED) {
                throw new IllegalStateException("Cannot change a vehicle with an active booking or rental away from RENTED.");
            }
            if (requestedStatus == VehicleStatus.RENTED && !activeRental) {
                throw new IllegalStateException("A vehicle can only be RENTED through an active booking or rental.");
            }
            if (activeRental && (!vehicle.getModelId().equals(modelId.trim()) || !vehicle.getStationId().equals(stationId.trim()))) {
                throw new IllegalStateException("Cannot change model or station while the vehicle has an active booking or rental.");
            }
            vehicle.setModelId(modelId.trim());
            vehicle.setStationId(stationId.trim());
            vehicle.setLicensePlate(normalizePlate(licensePlate));
            vehicle.setColor(blankToNull(color));
            vehicle.setBatteryLevel(parseBattery(batteryLevel));
            vehicle.setStatus(requestedStatus);
            return null;
        });
    }

    public void delete(String vehicleId) {
        required(vehicleId, "Vehicle ID");
        JPAUtil.executeInTransaction(em -> {
            Vehicle vehicle = vehicleDAO.findById(em, vehicleId);
            if (vehicle == null) throw new IllegalArgumentException("Vehicle not found.");
            if (vehicleDAO.hasRentalHistory(em, vehicleId)) {
                throw new IllegalStateException("Cannot delete a vehicle with rental history.");
            }
            vehicleDAO.delete(em, vehicle);
            return null;
        });
    }

    private void validateInput(String modelId, String stationId, String licensePlate, String batteryLevel) {
        required(modelId, "Vehicle model"); required(stationId, "Station"); required(licensePlate, "License plate");
        if (licensePlate.trim().length() > 20) throw new IllegalArgumentException("License plate must not exceed 20 characters.");
        parseBattery(batteryLevel);
    }
    private void validateReferences(javax.persistence.EntityManager em, String modelId, String stationId) {
        if (!vehicleDAO.modelExists(em, modelId.trim())) throw new IllegalArgumentException("Vehicle model not found.");
        if (!vehicleDAO.stationExists(em, stationId.trim())) throw new IllegalArgumentException("Station not found.");
    }
    private VehicleStatus parseStatusFilter(String value) {
        return blank(value) || "ALL".equalsIgnoreCase(value.trim()) ? null : parseStatus(value);
    }
    private VehicleStatus parseStatus(String value) {
        try { return VehicleStatus.valueOf(value == null ? "" : value.trim().toUpperCase()); }
        catch (Exception ex) { throw new IllegalArgumentException("Vehicle status is invalid."); }
    }
    private int parseBattery(String value) {
        try { int result = Integer.parseInt(value); if (result < 0 || result > 100) throw new NumberFormatException(); return result; }
        catch (Exception ex) { throw new IllegalArgumentException("Battery level must be between 0 and 100."); }
    }
    private String normalizePlate(String value) { return value.trim().toUpperCase(); }
    private String blankToNull(String value) { return blank(value) ? null : value.trim(); }
    private void required(String value, String label) { if (blank(value)) throw new IllegalArgumentException(label + " is required."); }
    private boolean blank(String value) { return value == null || value.trim().isEmpty(); }
}
