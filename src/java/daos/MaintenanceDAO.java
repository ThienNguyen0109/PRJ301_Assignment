package daos;

import dto.MaintenanceVehicleDTO;
import enums.MaintenanceStatus;
import enums.VehicleStatus;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import models.Vehicle;
import models.VehicleMaintenance;
import models.VehicleModel;
import utils.JPAUtil;

public class MaintenanceDAO implements IMaintenanceDAO {
    @Override
    public List<MaintenanceVehicleDTO> findPendingMaintenance(String keyword) {
        return JPAUtil.execute(em -> {
            String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
            String jpql = "SELECT vm, v, m FROM VehicleMaintenance vm JOIN vm.vehicle v JOIN v.model m "
                    + "WHERE vm.status = :maintenanceStatus AND v.status = :vehicleStatus "
                    + (normalized.isEmpty() ? "" : "AND (LOWER(v.licensePlate) LIKE :keyword OR LOWER(m.name) LIKE :keyword OR LOWER(vm.description) LIKE :keyword) ")
                    + "ORDER BY vm.maintenanceDate DESC";
            javax.persistence.TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class)
                    .setParameter("maintenanceStatus", MaintenanceStatus.PENDING)
                    .setParameter("vehicleStatus", VehicleStatus.MAINTENANCE);
            if (!normalized.isEmpty()) query.setParameter("keyword", "%" + normalized + "%");
            return query.getResultList().stream().map(this::map).collect(Collectors.toList());
        });
    }

    @Override
    public VehicleMaintenance findMaintenanceForUpdate(EntityManager em, String maintenanceId) {
        return em.find(VehicleMaintenance.class, maintenanceId, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    public Vehicle findVehicleForUpdate(EntityManager em, String vehicleId) {
        return em.find(Vehicle.class, vehicleId, LockModeType.PESSIMISTIC_WRITE);
    }

    private MaintenanceVehicleDTO map(Object[] row) {
        VehicleMaintenance maintenance = (VehicleMaintenance) row[0];
        Vehicle vehicle = (Vehicle) row[1];
        VehicleModel model = (VehicleModel) row[2];
        return new MaintenanceVehicleDTO(maintenance.getMaintenanceId(), vehicle.getVehicleId(),
                model.getName(), vehicle.getLicensePlate(), maintenance.getDescription(),
                maintenance.getMaintenanceDate(), maintenance.getStatus(), vehicle.getStatus());
    }
}
