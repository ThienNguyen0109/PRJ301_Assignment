package daos;

import dto.AdminMaintenanceRow;
import enums.MaintenanceStatus;
import enums.RentalStatus;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import models.Vehicle;
import models.VehicleMaintenance;

public class AdminMaintenanceDAO {
    private static final String SELECT = "SELECT mt, v, m, s FROM VehicleMaintenance mt "
            + "JOIN mt.vehicle v JOIN v.model m JOIN v.station s ";

    public List<AdminMaintenanceRow> search(EntityManager em, String keyword, MaintenanceStatus status) {
        String key = trim(keyword).toLowerCase();
        String jpql = SELECT
                + "WHERE (:key = '' OR LOWER(mt.maintenanceId) LIKE :pattern OR LOWER(v.licensePlate) LIKE :pattern "
                + "OR LOWER(m.name) LIKE :pattern OR LOWER(s.name) LIKE :pattern) "
                + (status == null ? "" : "AND mt.status = :status ")
                + "ORDER BY mt.maintenanceDate DESC";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class)
                .setParameter("key", key)
                .setParameter("pattern", "%" + key + "%");
        if (status != null) {
            query.setParameter("status", status);
        }
        return map(query.getResultList());
    }

    public AdminMaintenanceRow findDetail(EntityManager em, String maintenanceId) {
        List<Object[]> rows = em.createQuery(SELECT + "WHERE mt.maintenanceId = :maintenanceId", Object[].class)
                .setParameter("maintenanceId", trim(maintenanceId))
                .setMaxResults(1)
                .getResultList();
        return rows.isEmpty() ? null : map(rows).get(0);
    }

    public VehicleMaintenance findForUpdate(EntityManager em, String maintenanceId) {
        return em.find(VehicleMaintenance.class, trim(maintenanceId), LockModeType.PESSIMISTIC_WRITE);
    }

    public Vehicle findVehicleForUpdate(EntityManager em, String vehicleId) {
        return em.find(Vehicle.class, trim(vehicleId), LockModeType.PESSIMISTIC_WRITE);
    }

    public boolean hasActiveRental(EntityManager em, String vehicleId) {
        Long count = em.createQuery(
                "SELECT COUNT(r) FROM Rental r WHERE r.vehicleId = :vehicleId AND r.status IN (:booked, :rented)",
                Long.class)
                .setParameter("vehicleId", vehicleId)
                .setParameter("booked", RentalStatus.BOOKED)
                .setParameter("rented", RentalStatus.RENTED)
                .getSingleResult();
        return count != null && count > 0;
    }

    public boolean hasPendingMaintenance(EntityManager em, String vehicleId, String excludeMaintenanceId) {
        Long count = em.createQuery(
                "SELECT COUNT(mt) FROM VehicleMaintenance mt WHERE mt.vehicleId = :vehicleId "
                + "AND mt.status = :status AND (:excludeId = '' OR mt.maintenanceId <> :excludeId)",
                Long.class)
                .setParameter("vehicleId", vehicleId)
                .setParameter("status", MaintenanceStatus.PENDING)
                .setParameter("excludeId", trim(excludeMaintenanceId))
                .getSingleResult();
        return count != null && count > 0;
    }

    public List<Vehicle> findAvailableVehicles(EntityManager em) {
        return em.createQuery("SELECT v FROM Vehicle v JOIN FETCH v.model ORDER BY v.licensePlate", Vehicle.class)
                .getResultList();
    }

    public void create(EntityManager em, VehicleMaintenance maintenance) {
        em.persist(maintenance);
    }

    private List<AdminMaintenanceRow> map(List<Object[]> rows) {
        List<AdminMaintenanceRow> result = new ArrayList<>();
        for (Object[] row : rows) {
            VehicleMaintenance mt = (VehicleMaintenance) row[0];
            Vehicle v = (Vehicle) row[1];
            models.VehicleModel m = (models.VehicleModel) row[2];
            models.Station s = (models.Station) row[3];
            result.add(new AdminMaintenanceRow(mt.getMaintenanceId(), mt.getVehicleId(), m.getName(),
                    v.getLicensePlate(), s.getName(), mt.getDescription(), mt.getMaintenanceDate(),
                    mt.getStatus(), v.getStatus()));
        }
        return result;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
