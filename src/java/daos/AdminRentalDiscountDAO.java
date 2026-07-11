package daos;

import dto.AdminRentalDiscountRow;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import models.RentalDiscount;

public class AdminRentalDiscountDAO {
    private static final String SELECT = "SELECT rd, r, d, a FROM RentalDiscount rd "
            + "JOIN rd.rental r JOIN rd.discount d JOIN r.customer a ";

    public List<AdminRentalDiscountRow> search(EntityManager em, String keyword) {
        String key = trim(keyword).toLowerCase();
        TypedQuery<Object[]> query = em.createQuery(
                SELECT + "WHERE (:key = '' OR LOWER(rd.rentalDiscountId) LIKE :pattern "
                + "OR LOWER(r.rentalId) LIKE :pattern OR LOWER(d.code) LIKE :pattern "
                + "OR LOWER(a.fullName) LIKE :pattern OR LOWER(a.email) LIKE :pattern) "
                + "ORDER BY r.createdAt DESC",
                Object[].class)
                .setParameter("key", key)
                .setParameter("pattern", "%" + key + "%");
        return map(query.getResultList());
    }

    public AdminRentalDiscountRow findDetail(EntityManager em, String id) {
        List<Object[]> rows = em.createQuery(SELECT + "WHERE rd.rentalDiscountId = :id", Object[].class)
                .setParameter("id", trim(id))
                .setMaxResults(1)
                .getResultList();
        return rows.isEmpty() ? null : map(rows).get(0);
    }

    private List<AdminRentalDiscountRow> map(List<Object[]> rows) {
        List<AdminRentalDiscountRow> result = new ArrayList<>();
        for (Object[] row : rows) {
            RentalDiscount rd = (RentalDiscount) row[0];
            models.Rental r = (models.Rental) row[1];
            models.Discount d = (models.Discount) row[2];
            models.Account a = (models.Account) row[3];
            result.add(new AdminRentalDiscountRow(rd.getRentalDiscountId(), rd.getRentalId(),
                    rd.getDiscountId(), d.getCode(), d.getDiscountPercent(), a.getFullName(),
                    a.getEmail(), r.getStartDate(), r.getEndDate()));
        }
        return result;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
