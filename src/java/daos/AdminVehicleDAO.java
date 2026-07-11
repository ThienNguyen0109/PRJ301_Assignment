package daos;

import dto.AdminVehicleRow;
import enums.RentalStatus;
import enums.VehicleStatus;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import models.Category;
import models.Station;
import models.Vehicle;
import models.VehicleModel;

public class AdminVehicleDAO {

    public List<AdminVehicleRow> search(EntityManager em, String keyword, String stationId,
            String categoryId, VehicleStatus status) {
        StringBuilder jpql = new StringBuilder(
                "SELECT v.vehicleId, v.modelId, m.name, c.name, v.stationId, s.name, "
                + "v.licensePlate, v.color, v.batteryLevel, v.status "
                + "FROM Vehicle v JOIN v.model m JOIN m.category c JOIN v.station s WHERE 1 = 1 ");
        if (!blank(keyword)) {
            jpql.append("AND (LOWER(v.licensePlate) LIKE :keyword OR LOWER(m.name) LIKE :keyword OR LOWER(s.name) LIKE :keyword) ");
        }
        if (!blank(stationId) && !"ALL".equals(stationId)) {
            jpql.append("AND v.stationId = :stationId ");
        }
        if (!blank(categoryId) && !"ALL".equals(categoryId)) {
            jpql.append("AND m.categoryId = :categoryId ");
        }
        if (status != null) {
            jpql.append("AND v.status = :status ");
        }
        jpql.append("ORDER BY s.name ASC, m.name ASC, v.licensePlate ASC");

        Query query = em.createQuery(jpql.toString());
        if (!blank(keyword)) query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        if (!blank(stationId) && !"ALL".equals(stationId)) query.setParameter("stationId", stationId.trim());
        if (!blank(categoryId) && !"ALL".equals(categoryId)) query.setParameter("categoryId", categoryId.trim());
        if (status != null) query.setParameter("status", status);

        List<AdminVehicleRow> rows = new ArrayList<>();
        for (Object result : query.getResultList()) {
            Object[] value = (Object[]) result;
            rows.add(new AdminVehicleRow(text(value[0]), text(value[1]), text(value[2]), text(value[3]),
                    text(value[4]), text(value[5]), text(value[6]), text(value[7]),
                    integer(value[8]), (VehicleStatus) value[9]));
        }
        return rows;
    }

    public Vehicle findById(EntityManager em, String vehicleId) {
        return blank(vehicleId) ? null : em.find(Vehicle.class, vehicleId.trim());
    }

    public List<VehicleModel> findAllModels(EntityManager em) {
        return em.createQuery("SELECT m FROM VehicleModel m ORDER BY m.name ASC", VehicleModel.class).getResultList();
    }

    public List<Station> findAllStations(EntityManager em) {
        return em.createQuery("SELECT s FROM Station s ORDER BY s.name ASC", Station.class).getResultList();
    }

    public List<Category> findAllCategories(EntityManager em) {
        return em.createQuery("SELECT c FROM Category c ORDER BY c.name ASC", Category.class).getResultList();
    }

    public boolean modelExists(EntityManager em, String modelId) {
        return em.find(VehicleModel.class, modelId) != null;
    }

    public boolean stationExists(EntityManager em, String stationId) {
        return em.find(Station.class, stationId) != null;
    }

    public boolean licensePlateExists(EntityManager em, String licensePlate, String excludeVehicleId) {
        String jpql = "SELECT COUNT(v) FROM Vehicle v WHERE UPPER(v.licensePlate) = :plate"
                + (blank(excludeVehicleId) ? "" : " AND v.vehicleId <> :vehicleId");
        Query query = em.createQuery(jpql).setParameter("plate", licensePlate.trim().toUpperCase());
        if (!blank(excludeVehicleId)) query.setParameter("vehicleId", excludeVehicleId.trim());
        return ((Long) query.getSingleResult()) > 0;
    }

    public boolean hasRentalHistory(EntityManager em, String vehicleId) {
        Long count = em.createQuery("SELECT COUNT(r) FROM Rental r WHERE r.vehicleId = :vehicleId", Long.class)
                .setParameter("vehicleId", vehicleId).getSingleResult();
        return count > 0;
    }

    public boolean hasActiveRental(EntityManager em, String vehicleId) {
        Long count = em.createQuery("SELECT COUNT(r) FROM Rental r WHERE r.vehicleId = :vehicleId "
                + "AND r.status IN (:booked, :rented)", Long.class)
                .setParameter("vehicleId", vehicleId)
                .setParameter("booked", RentalStatus.BOOKED)
                .setParameter("rented", RentalStatus.RENTED)
                .getSingleResult();
        return count > 0;
    }

    public void create(EntityManager em, Vehicle vehicle) { em.persist(vehicle); }
    public void delete(EntityManager em, Vehicle vehicle) { em.remove(vehicle); }

    private boolean blank(String value) { return value == null || value.trim().isEmpty(); }
    private String text(Object value) { return value == null ? null : String.valueOf(value); }
    private Integer integer(Object value) { return value == null ? null : ((Number) value).intValue(); }
}
