package daos;

import dto.AdminVehicleModelImageRow;
import enums.VehicleModelImageType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import models.VehicleModel;
import models.VehicleModelImage;

public class AdminVehicleModelImageDAO {

    public List<AdminVehicleModelImageRow> search(EntityManager em, String keyword, String modelId, VehicleModelImageType type) {
        StringBuilder jpql = new StringBuilder(
                "SELECT new dto.AdminVehicleModelImageRow(i.imageId, i.modelId, m.name, i.imageUrl, i.imageType) "
                + "FROM VehicleModelImage i JOIN i.model m WHERE 1 = 1 ");
        if (!isBlank(keyword)) {
            jpql.append("AND (LOWER(m.name) LIKE :keyword OR LOWER(i.imageUrl) LIKE :keyword) ");
        }
        if (!isBlank(modelId) && !"ALL".equals(modelId)) {
            jpql.append("AND i.modelId = :modelId ");
        }
        if (type != null) {
            jpql.append("AND i.imageType = :type ");
        }
        jpql.append("ORDER BY m.name ASC, i.imageType ASC");

        TypedQuery<AdminVehicleModelImageRow> query = em.createQuery(jpql.toString(), AdminVehicleModelImageRow.class);
        if (!isBlank(keyword)) {
            query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        if (!isBlank(modelId) && !"ALL".equals(modelId)) {
            query.setParameter("modelId", modelId.trim());
        }
        if (type != null) {
            query.setParameter("type", type);
        }
        return query.getResultList();
    }

    public VehicleModelImage findById(EntityManager em, String imageId) {
        return isBlank(imageId) ? null : em.find(VehicleModelImage.class, imageId);
    }

    public List<VehicleModel> findAllModels(EntityManager em) {
        return em.createQuery("SELECT m FROM VehicleModel m ORDER BY m.name ASC", VehicleModel.class).getResultList();
    }

    public boolean modelExists(EntityManager em, String modelId) {
        Long count = em.createQuery("SELECT COUNT(m) FROM VehicleModel m WHERE m.modelId = :modelId", Long.class)
                .setParameter("modelId", modelId)
                .getSingleResult();
        return count > 0;
    }

    public void create(EntityManager em, VehicleModelImage image) {
        em.persist(image);
    }

    public VehicleModelImage update(EntityManager em, VehicleModelImage image) {
        return em.merge(image);
    }

    public void delete(EntityManager em, VehicleModelImage image) {
        em.remove(image);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
