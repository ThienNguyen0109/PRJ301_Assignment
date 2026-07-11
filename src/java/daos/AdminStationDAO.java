package daos;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import models.Station;
import utils.JPAUtil;

public class AdminStationDAO {

    public List<Station> search(String keyword) {
        return JPAUtil.execute(em -> {
            String jpql = "SELECT s FROM Station s "
                    + "WHERE (:keyword = '' OR LOWER(s.name) LIKE :pattern "
                    + "OR LOWER(s.address) LIKE :pattern "
                    + "OR LOWER(COALESCE(s.contactNumber, '')) LIKE :pattern) "
                    + "ORDER BY s.name";
            String normalized = keyword == null ? "" : keyword.trim().toLowerCase();
            return em.createQuery(jpql, Station.class)
                    .setParameter("keyword", normalized)
                    .setParameter("pattern", "%" + normalized + "%")
                    .getResultList();
        });
    }

    public Station findById(String stationId) {
        if (stationId == null || stationId.trim().isEmpty()) {
            return null;
        }
        return JPAUtil.execute(em -> em.find(Station.class, stationId.trim()));
    }

    public boolean nameExists(EntityManager em, String name, String excludedStationId) {
        String jpql = "SELECT COUNT(s) FROM Station s WHERE LOWER(s.name) = :name "
                + (isBlank(excludedStationId) ? "" : "AND s.stationId <> :stationId");
        TypedQuery<Long> query = em.createQuery(jpql, Long.class)
                .setParameter("name", name.trim().toLowerCase());
        if (!isBlank(excludedStationId)) {
            query.setParameter("stationId", excludedStationId.trim());
        }
        return query.getSingleResult() > 0;
    }

    public void create(Station station) {
        JPAUtil.executeInTransaction(em -> {
            em.persist(station);
            return null;
        });
    }

    public void update(Station station) {
        JPAUtil.executeInTransaction(em -> {
            em.merge(station);
            return null;
        });
    }

    public void delete(String stationId) {
        JPAUtil.executeInTransaction(em -> {
            Station station = em.find(Station.class, stationId);
            if (station == null) {
                throw new IllegalArgumentException("Station not found.");
            }
            if (hasRelatedData(em, stationId)) {
                throw new IllegalStateException("Cannot delete a station that is used by vehicles or rentals.");
            }
            em.remove(station);
            return null;
        });
    }

    private boolean hasRelatedData(EntityManager em, String stationId) {
        Long vehicleCount = em.createQuery(
                "SELECT COUNT(v) FROM Vehicle v WHERE v.stationId = :stationId", Long.class)
                .setParameter("stationId", stationId)
                .getSingleResult();
        Long rentalCount = em.createQuery(
                "SELECT COUNT(r) FROM Rental r WHERE r.pickupStationId = :stationId", Long.class)
                .setParameter("stationId", stationId)
                .getSingleResult();
        return vehicleCount > 0 || rentalCount > 0;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
