package services;

import daos.AdminMaintenanceDAO;
import dto.AdminMaintenanceRow;
import enums.MaintenanceStatus;
import enums.VehicleStatus;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import models.Vehicle;
import models.VehicleMaintenance;
import realtime.RealtimeEventPublisher;
import utils.JPAUtil;

public class AdminMaintenanceService {
    private final AdminMaintenanceDAO maintenanceDAO = new AdminMaintenanceDAO();

    public List<AdminMaintenanceRow> search(String keyword, String status) {
        return JPAUtil.execute(em -> maintenanceDAO.search(em, keyword, parseStatusFilter(status)));
    }

    public AdminMaintenanceRow findDetail(String maintenanceId) {
        return JPAUtil.execute(em -> maintenanceDAO.findDetail(em, maintenanceId));
    }

    public List<Vehicle> findAllVehicles() {
        return JPAUtil.execute(maintenanceDAO::findAvailableVehicles);
    }

    public void create(String vehicleId, String description) {
        required(vehicleId, "Vehicle");
        required(description, "Description");
        JPAUtil.executeInTransaction(em -> {
            Vehicle vehicle = maintenanceDAO.findVehicleForUpdate(em, vehicleId);
            if (vehicle == null) {
                throw new IllegalArgumentException("Vehicle not found.");
            }
            if (maintenanceDAO.hasActiveRental(em, vehicleId)) {
                throw new IllegalStateException("Cannot move a vehicle with active booking/rental to maintenance.");
            }
            if (maintenanceDAO.hasPendingMaintenance(em, vehicleId, null)) {
                throw new IllegalStateException("Vehicle already has a pending maintenance record.");
            }
            VehicleMaintenance maintenance = new VehicleMaintenance();
            maintenance.setMaintenanceId(UUID.randomUUID().toString());
            maintenance.setVehicleId(vehicleId.trim());
            maintenance.setDescription(description.trim());
            maintenance.setMaintenanceDate(new Timestamp(System.currentTimeMillis()));
            maintenance.setStatus(MaintenanceStatus.PENDING);
            vehicle.setStatus(VehicleStatus.MAINTENANCE);
            maintenanceDAO.create(em, maintenance);
            return null;
        });
        RealtimeEventPublisher.admin("MAINTENANCE_CREATED", "Maintenance created", "A vehicle was moved to maintenance.");
    }

    public void markCompleted(String maintenanceId) {
        required(maintenanceId, "Maintenance ID");
        JPAUtil.executeInTransaction(em -> {
            VehicleMaintenance maintenance = maintenanceDAO.findForUpdate(em, maintenanceId);
            if (maintenance == null) {
                throw new IllegalArgumentException("Maintenance record not found.");
            }
            if (maintenance.getStatus() != MaintenanceStatus.PENDING) {
                throw new IllegalStateException("Only pending maintenance can be completed.");
            }
            Vehicle vehicle = maintenanceDAO.findVehicleForUpdate(em, maintenance.getVehicleId());
            if (vehicle == null) {
                throw new IllegalStateException("Related vehicle not found.");
            }
            maintenance.setStatus(MaintenanceStatus.COMPLETED);
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            return null;
        });
        RealtimeEventPublisher.admin("MAINTENANCE_COMPLETED", "Maintenance completed", "A vehicle returned to AVAILABLE.");
    }

    private MaintenanceStatus parseStatusFilter(String value) {
        if (value == null || value.trim().isEmpty() || "ALL".equalsIgnoreCase(value.trim())) {
            return null;
        }
        try {
            return MaintenanceStatus.valueOf(value.trim().toUpperCase());
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Maintenance status is invalid.");
        }
    }

    private void required(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }
}
