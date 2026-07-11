package daos;

import java.util.List;
import javax.persistence.EntityManager;
import models.Discount;

/** Database queries used by the Discount administration module. */
public class AdminDiscountDAO {

    public List<Discount> search(EntityManager em, String keyword) {
        String key = trim(keyword).toLowerCase();
        return em.createQuery(
                "SELECT d FROM Discount d "
                + "WHERE :key = '' OR LOWER(d.code) LIKE :pattern "
                + "ORDER BY d.expiredAt ASC, d.code ASC",
                Discount.class)
                .setParameter("key", key)
                .setParameter("pattern", "%" + key + "%")
                .getResultList();
    }

    public Discount findById(EntityManager em, String discountId) {
        String id = trim(discountId);
        return id.isEmpty() ? null : em.find(Discount.class, id);
    }

    public boolean codeExists(EntityManager em, String code, String excludedDiscountId) {
        String excludedId = trim(excludedDiscountId);
        Long count = em.createQuery(
                "SELECT COUNT(d) FROM Discount d "
                + "WHERE UPPER(d.code) = :code "
                + "AND (:excludedId = '' OR d.discountId <> :excludedId)", Long.class)
                .setParameter("code", trim(code).toUpperCase())
                .setParameter("excludedId", excludedId)
                .getSingleResult();
        return count != null && count > 0;
    }

    public boolean hasUsage(EntityManager em, String discountId) {
        Long count = em.createQuery(
                "SELECT COUNT(rd) FROM RentalDiscount rd WHERE rd.discountId = :discountId", Long.class)
                .setParameter("discountId", discountId)
                .getSingleResult();
        return count != null && count > 0;
    }

    public void create(EntityManager em, Discount discount) {
        em.persist(discount);
    }

    public void delete(EntityManager em, Discount discount) {
        em.remove(discount);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
