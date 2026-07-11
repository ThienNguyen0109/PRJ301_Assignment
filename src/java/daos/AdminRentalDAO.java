package daos;

import dto.AdminRentalRow;
import enums.RentalStatus;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import enums.PaymentStatus;
import models.Payment;
import models.Rental;
import models.RentalStatusHistory;
import models.Station;
import models.Vehicle;

public class AdminRentalDAO {
    private static final String SELECT = "SELECT r, c, v, m, s FROM Rental r "
            + "JOIN r.customer c JOIN r.vehicle v JOIN v.model m JOIN r.pickupStation s ";

    public List<AdminRentalRow> search(EntityManager em, String keyword, String stationId,
            RentalStatus status, Date startDate, Date endDate) {
        String key = trim(keyword).toLowerCase();
        StringBuilder jpql = new StringBuilder(SELECT + "WHERE 1 = 1 ");
        if (!key.isEmpty()) {
            jpql.append("AND (LOWER(r.rentalId) LIKE :keyword OR LOWER(c.fullName) LIKE :keyword "
                    + "OR LOWER(c.email) LIKE :keyword OR c.phone LIKE :keyword "
                    + "OR LOWER(v.licensePlate) LIKE :keyword) ");
        }
        if (!trim(stationId).isEmpty() && !"ALL".equalsIgnoreCase(stationId.trim())) jpql.append("AND r.pickupStationId = :stationId ");
        if (status != null) jpql.append("AND r.status = :status ");
        if (startDate != null) jpql.append("AND r.startDate >= :startDate ");
        if (endDate != null) jpql.append("AND r.startDate <= :endDate ");
        jpql.append("ORDER BY r.createdAt DESC, r.startDate DESC");
        TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class);
        if (!key.isEmpty()) query.setParameter("keyword", "%" + key + "%");
        if (!trim(stationId).isEmpty() && !"ALL".equalsIgnoreCase(stationId.trim())) query.setParameter("stationId", stationId.trim());
        if (status != null) query.setParameter("status", status);
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        return map(query.getResultList());
    }

    public AdminRentalRow findDetail(EntityManager em, String rentalId) {
        String id = trim(rentalId);
        if (id.isEmpty()) return null;
        List<Object[]> rows = em.createQuery(SELECT + "WHERE r.rentalId = :rentalId", Object[].class)
                .setParameter("rentalId", id).setMaxResults(1).getResultList();
        return rows.isEmpty() ? null : map(rows).get(0);
    }

    public List<Station> findAllStations(EntityManager em) {
        return em.createQuery("SELECT s FROM Station s ORDER BY s.name", Station.class).getResultList();
    }

    public List<Payment> findPayments(EntityManager em, String rentalId) {
        return em.createQuery("SELECT p FROM Payment p WHERE p.rentalId = :rentalId ORDER BY p.paymentDate DESC", Payment.class)
                .setParameter("rentalId", rentalId).getResultList();
    }

    public List<RentalStatusHistory> findHistory(EntityManager em, String rentalId) {
        return em.createQuery("SELECT h FROM RentalStatusHistory h WHERE h.rentalId = :rentalId ORDER BY h.changedAt DESC", RentalStatusHistory.class)
                .setParameter("rentalId", rentalId).getResultList();
    }

    public Rental findForUpdate(EntityManager em, String rentalId) { return em.find(Rental.class, trim(rentalId), LockModeType.PESSIMISTIC_WRITE); }
    public Vehicle findVehicleForUpdate(EntityManager em, String vehicleId) { return em.find(Vehicle.class, vehicleId, LockModeType.PESSIMISTIC_WRITE); }

    public boolean hasSuccessfulPayment(EntityManager em, String rentalId) {
        Long count = em.createQuery(
                "SELECT COUNT(p) FROM Payment p WHERE p.rentalId = :rentalId AND p.status = :status",
                Long.class)
                .setParameter("rentalId", rentalId)
                .setParameter("status", PaymentStatus.SUCCESS)
                .getSingleResult();
        return count != null && count > 0;
    }

    private List<AdminRentalRow> map(List<Object[]> rows) {
        List<AdminRentalRow> result = new ArrayList<>();
        for (Object[] row : rows) {
            Rental r = (Rental) row[0]; models.Account c = (models.Account) row[1];
            Vehicle v = (Vehicle) row[2]; models.VehicleModel m = (models.VehicleModel) row[3]; Station s = (Station) row[4];
            result.add(new AdminRentalRow(r.getRentalId(), r.getCustomerId(), c.getFullName(), c.getEmail(), c.getPhone(),
                    r.getVehicleId(), v.getLicensePlate(), m.getName(), s.getName(), r.getStartDate(), r.getEndDate(),
                    r.getActualReturnDate(), r.getTotalDays(), r.getTotalAmount(), r.getLateFee(), r.getStatus(),
                    v.getStatus(), r.getCreatedAt()));
        }
        return result;
    }
    private String trim(String value) { return value == null ? "" : value.trim(); }
}
