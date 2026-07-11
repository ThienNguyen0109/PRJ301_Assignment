package daos;

import dto.AdminExtraChargeRow;
import enums.ExtraChargeStatus;
import enums.ExtraChargeType;
import enums.RentalStatus;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import models.ExtraCharge;
import models.Rental;

public class AdminExtraChargeDAO {
    private static final String SELECT = "SELECT c, r, a, v, m FROM ExtraCharge c "
            + "JOIN c.rental r JOIN r.customer a JOIN r.vehicle v JOIN v.model m ";

    public List<AdminExtraChargeRow> search(EntityManager em, String keyword, ExtraChargeType type,
            ExtraChargeStatus status) {
        String key = trim(keyword).toLowerCase();
        String jpql = SELECT
                + "WHERE (:key = '' OR LOWER(c.chargeId) LIKE :pattern OR LOWER(c.rentalId) LIKE :pattern "
                + "OR LOWER(a.fullName) LIKE :pattern OR LOWER(a.email) LIKE :pattern "
                + "OR LOWER(v.licensePlate) LIKE :pattern OR LOWER(m.name) LIKE :pattern) "
                + (type == null ? "" : "AND c.chargeType = :type ")
                + (status == null ? "" : "AND c.status = :status ")
                + "ORDER BY c.createdAt DESC";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class)
                .setParameter("key", key)
                .setParameter("pattern", "%" + key + "%");
        if (type != null) {
            query.setParameter("type", type);
        }
        if (status != null) {
            query.setParameter("status", status);
        }
        return map(query.getResultList());
    }

    public AdminExtraChargeRow findDetail(EntityManager em, String chargeId) {
        List<Object[]> rows = em.createQuery(SELECT + "WHERE c.chargeId = :chargeId", Object[].class)
                .setParameter("chargeId", trim(chargeId))
                .setMaxResults(1)
                .getResultList();
        return rows.isEmpty() ? null : map(rows).get(0);
    }

    public ExtraCharge findForUpdate(EntityManager em, String chargeId) {
        return em.find(ExtraCharge.class, trim(chargeId), LockModeType.PESSIMISTIC_WRITE);
    }

    public Rental findRental(EntityManager em, String rentalId) {
        String id = trim(rentalId);
        return id.isEmpty() ? null : em.find(Rental.class, id);
    }

    public List<Rental> findEligibleRentals(EntityManager em) {
        return em.createQuery(
                "SELECT r FROM Rental r JOIN FETCH r.customer JOIN FETCH r.vehicle v JOIN FETCH v.model "
                + "WHERE r.status IN (:rented, :completed) ORDER BY r.createdAt DESC", Rental.class)
                .setParameter("rented", RentalStatus.RENTED)
                .setParameter("completed", RentalStatus.COMPLETED)
                .getResultList();
    }

    public void create(EntityManager em, ExtraCharge charge) {
        em.persist(charge);
    }

    private List<AdminExtraChargeRow> map(List<Object[]> rows) {
        List<AdminExtraChargeRow> result = new ArrayList<>();
        for (Object[] row : rows) {
            ExtraCharge c = (ExtraCharge) row[0];
            models.Account a = (models.Account) row[2];
            models.Vehicle v = (models.Vehicle) row[3];
            models.VehicleModel m = (models.VehicleModel) row[4];
            result.add(new AdminExtraChargeRow(c.getChargeId(), c.getRentalId(), c.getIncidentId(),
                    a.getFullName(), a.getEmail(), m.getName(), v.getLicensePlate(), c.getChargeType(),
                    c.getAmount(), c.getDescription(), c.getStatus(), c.getCreatedAt(), c.getPaidAt()));
        }
        return result;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
