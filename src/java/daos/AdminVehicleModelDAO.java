package daos;

import dto.AdminVehicleModelRow;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import models.Category;
import models.VehicleModel;

public class AdminVehicleModelDAO {

    public List<AdminVehicleModelRow> search(EntityManager em, String keyword, String categoryId) {
        StringBuilder jpql = new StringBuilder(
                "SELECT m.modelId, m.categoryId, c.name, m.name, m.brand, "
                + "m.seatCount, m.pricePerDay, m.description, "
                + "(SELECT COUNT(v) FROM Vehicle v WHERE v.modelId = m.modelId), "
                + "(SELECT COUNT(i) FROM VehicleModelImage i WHERE i.modelId = m.modelId) "
                + "FROM VehicleModel m JOIN m.category c WHERE 1 = 1 ");
        if (!isBlank(keyword)) {
            jpql.append("AND (LOWER(m.name) LIKE :keyword OR LOWER(m.brand) LIKE :keyword) ");
        }
        if (!isBlank(categoryId) && !"ALL".equals(categoryId)) {
            jpql.append("AND m.categoryId = :categoryId ");
        }
        jpql.append("ORDER BY m.name ASC");

        Query query = em.createQuery(jpql.toString());
        if (!isBlank(keyword)) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        if (!isBlank(categoryId) && !"ALL".equals(categoryId)) {
            query.setParameter("categoryId", categoryId.trim());
        }

        List<AdminVehicleModelRow> rows = new ArrayList<>();
        for (Object result : query.getResultList()) {
            Object[] values = (Object[]) result;
            rows.add(new AdminVehicleModelRow(
                    asString(values[0]),
                    asString(values[1]),
                    asString(values[2]),
                    asString(values[3]),
                    asString(values[4]),
                    asInteger(values[5]),
                    asDouble(values[6]),
                    asString(values[7]),
                    asLong(values[8]),
                    asLong(values[9])));
        }
        return rows;
    }

    public VehicleModel findById(EntityManager em, String modelId) {
        return isBlank(modelId) ? null : em.find(VehicleModel.class, modelId);
    }

    public List<Category> findAllCategories(EntityManager em) {
        return em.createQuery("SELECT c FROM Category c ORDER BY c.name ASC", Category.class).getResultList();
    }

    public boolean nameExists(EntityManager em, String name, String categoryId, String excludeModelId) {
        String jpql = "SELECT COUNT(m) FROM VehicleModel m WHERE LOWER(m.name) = :name AND m.categoryId = :categoryId "
                + (isBlank(excludeModelId) ? "" : "AND m.modelId <> :excludeId");
        TypedQuery<Long> query = em.createQuery(jpql, Long.class)
                .setParameter("name", name == null ? "" : name.trim().toLowerCase())
                .setParameter("categoryId", categoryId);
        if (!isBlank(excludeModelId)) {
            query.setParameter("excludeId", excludeModelId.trim());
        }
        return query.getSingleResult() > 0;
    }

    public void create(EntityManager em, VehicleModel model) {
        em.persist(model);
    }

    public VehicleModel update(EntityManager em, VehicleModel model) {
        return em.merge(model);
    }

    public void delete(EntityManager em, VehicleModel model) {
        em.remove(model);
    }

    public long countVehicles(EntityManager em, String modelId) {
        return em.createQuery("SELECT COUNT(v) FROM Vehicle v WHERE v.modelId = :modelId", Long.class)
                .setParameter("modelId", modelId)
                .getSingleResult();
    }

    public long countImages(EntityManager em, String modelId) {
        return em.createQuery("SELECT COUNT(i) FROM VehicleModelImage i WHERE i.modelId = :modelId", Long.class)
                .setParameter("modelId", modelId)
                .getSingleResult();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Integer asInteger(Object value) {
        return value == null ? null : ((Number) value).intValue();
    }

    private Double asDouble(Object value) {
        return value == null ? null : ((Number) value).doubleValue();
    }

    private Long asLong(Object value) {
        return value == null ? 0L : ((Number) value).longValue();
    }
}
