package daos;

import dto.AdminRentalStatusHistoryRow;
import enums.RentalStatus;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import models.RentalStatusHistory;

public class AdminRentalStatusHistoryDAO {
    private static final String SELECT = "SELECT h, r, a, v, m FROM RentalStatusHistory h "
            + "JOIN h.rental r JOIN r.customer a JOIN r.vehicle v JOIN v.model m ";

    public List<AdminRentalStatusHistoryRow> search(EntityManager em, String keyword, RentalStatus status,
            Date startDate, Date endDate) {
        String key = trim(keyword).toLowerCase();
        String jpql = SELECT
                + "WHERE (:key = '' OR LOWER(h.historyId) LIKE :pattern OR LOWER(h.rentalId) LIKE :pattern "
                + "OR LOWER(a.fullName) LIKE :pattern OR LOWER(a.email) LIKE :pattern OR LOWER(v.licensePlate) LIKE :pattern) "
                + (status == null ? "" : "AND h.status = :status ")
                + (startDate == null ? "" : "AND h.changedAt >= :startDate ")
                + (endDate == null ? "" : "AND h.changedAt < :endDate ")
                + "ORDER BY h.changedAt DESC";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class)
                .setParameter("key", key)
                .setParameter("pattern", "%" + key + "%");
        if (status != null) {
            query.setParameter("status", status);
        }
        if (startDate != null) {
            query.setParameter("startDate", new java.sql.Timestamp(startDate.getTime()));
        }
        if (endDate != null) {
            query.setParameter("endDate", new java.sql.Timestamp(endDate.toLocalDate().plusDays(1).atStartOfDay()
                    .atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()));
        }
        return map(query.getResultList());
    }

    public AdminRentalStatusHistoryRow findDetail(EntityManager em, String id) {
        List<Object[]> rows = em.createQuery(SELECT + "WHERE h.historyId = :id", Object[].class)
                .setParameter("id", trim(id))
                .setMaxResults(1)
                .getResultList();
        return rows.isEmpty() ? null : map(rows).get(0);
    }

    private List<AdminRentalStatusHistoryRow> map(List<Object[]> rows) {
        List<AdminRentalStatusHistoryRow> result = new ArrayList<>();
        for (Object[] row : rows) {
            RentalStatusHistory h = (RentalStatusHistory) row[0];
            models.Account a = (models.Account) row[2];
            models.Vehicle v = (models.Vehicle) row[3];
            models.VehicleModel m = (models.VehicleModel) row[4];
            result.add(new AdminRentalStatusHistoryRow(h.getHistoryId(), h.getRentalId(), a.getFullName(),
                    a.getEmail(), m.getName(), v.getLicensePlate(), h.getStatus(), h.getChangedAt()));
        }
        return result;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
