package services;

import daos.IMaintenanceDAO;
import daos.MaintenanceDAO;
import dto.MaintenanceVehicleDTO;
import enums.MaintenanceStatus;
import enums.VehicleStatus;
import java.util.List;
import models.Vehicle;
import models.VehicleMaintenance;
import utils.JPAUtil;

public class MaintenanceService {
    private final IMaintenanceDAO maintenanceDAO;

    public MaintenanceService() { this(new MaintenanceDAO()); }
    public MaintenanceService(IMaintenanceDAO maintenanceDAO) { this.maintenanceDAO = maintenanceDAO; }

    public List<MaintenanceVehicleDTO> findPendingMaintenance(String keyword) {
        return maintenanceDAO.findPendingMaintenance(keyword);
    }

    public void markCompleted(String maintenanceId) {
        if (maintenanceId == null || maintenanceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Maintenance ID không được để trống.");
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
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            return null;
        });
    }
}
