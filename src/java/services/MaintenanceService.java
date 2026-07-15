package services;

import daos.IMaintenanceDAO;
import daos.MaintenanceDAO;
import dto.MaintenanceVehicleDTO;
import enums.MaintenanceStatus;
import enums.VehicleStatus;
import java.util.List;
import models.Vehicle;
import models.VehicleMaintenance;
import realtime.RealtimeEventPublisher;
import utils.JPAUtil;

public class MaintenanceService {
    private static final int READY_BATTERY_LEVEL = 80;

    private final IMaintenanceDAO maintenanceDAO;

    public MaintenanceService() { this(new MaintenanceDAO()); }
    public MaintenanceService(IMaintenanceDAO maintenanceDAO) { this.maintenanceDAO = maintenanceDAO; }

    public List<MaintenanceVehicleDTO> findPendingMaintenance(String keyword) {
        return maintenanceDAO.findPendingMaintenance(keyword);
    }

    public void markCompleted(String maintenanceId, int batteryLevel) {
        if (maintenanceId == null || maintenanceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Maintenance ID không được để trống.");
        }
        if (batteryLevel < READY_BATTERY_LEVEL || batteryLevel > 100) {
            throw new IllegalArgumentException("Battery level must be from 80 to 100 before publishing the vehicle.");
        }
        JPAUtil.executeInTransaction(em -> {
            VehicleMaintenance maintenance = maintenanceDAO.findMaintenanceForUpdate(em, maintenanceId.trim());
            if (maintenance == null) throw new IllegalStateException("Không tìm thấy yêu cầu bảo trì.");
            if (maintenance.getStatus() != MaintenanceStatus.PENDING) {
                throw new IllegalStateException("Chỉ yêu cầu PENDING mới được hoàn tất.");
            }
            Vehicle vehicle = maintenanceDAO.findVehicleForUpdate(em, maintenance.getVehicleId());
            if (vehicle == null) throw new IllegalStateException("Không tìm thấy xe cần bảo trì.");
            if (vehicle.getStatus() != VehicleStatus.MAINTENANCE) {
                throw new IllegalStateException("Xe không còn ở trạng thái MAINTENANCE.");
            }
            maintenance.setStatus(MaintenanceStatus.COMPLETED);
            vehicle.setBatteryLevel(batteryLevel);
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            return null;
        });
        RealtimeEventPublisher.staff("MAINTENANCE_COMPLETED", "Maintenance completed",
                "A vehicle is available for rental again.");
        RealtimeEventPublisher.admin("VEHICLE_AVAILABILITY_CHANGED", "Vehicle availability changed",
                "A maintenance vehicle returned to AVAILABLE.");
        RealtimeEventPublisher.all("VEHICLE_AVAILABILITY_CHANGED", "Vehicle available",
                "A vehicle has returned from maintenance.");
    }
}
