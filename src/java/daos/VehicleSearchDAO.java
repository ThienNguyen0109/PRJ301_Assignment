package daos;

import dto.VehicleSearchResult;
import enums.RentalStatus;
import enums.VehicleModelImageType;
import enums.VehicleStatus;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import models.Vehicle;
import models.VehicleModelImage;
import utils.JPAUtil;

/**
 * Data Access Object for vehicle availability search using JPA.
 */
public class VehicleSearchDAO implements IVehicleSearchDAO {
    private static final Logger LOGGER = Logger.getLogger(VehicleSearchDAO.class.getName());

    @Override
    public List<VehicleSearchResult> getFeaturedAvailableVehicleModels(int limit) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            List<VehicleSearchResult> results = mapSearchResults(em.createQuery(
                    "SELECT v.model.modelId, v.model.name, v.model.brand, v.model.description, " +
                    "       v.model.pricePerDay, v.model.seatCount, " +
                    "       v.station.stationId, v.station.name, v.station.address, COUNT(v) " +
                    "FROM Vehicle v " +
                    "WHERE v.status = :status " +
                    "GROUP BY v.model.modelId, v.model.name, v.model.brand, v.model.description, " +
                    "         v.model.pricePerDay, v.model.seatCount, " +
                    "         v.station.stationId, v.station.name, v.station.address " +
                    "ORDER BY COUNT(v) DESC, v.model.name, v.station.name")
                    .setParameter("status", VehicleStatus.AVAILABLE)
                    .setMaxResults(Math.max(1, limit))
                    .getResultList(), true);
            attachThumbnails(em, results);
            return results;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not load featured vehicle models with JPA", ex);
            return Collections.emptyList();
        } finally {
            close(em);
        }
    }

    @Override
    public VehicleSearchResult getAvailableVehicleModelAtStation(String modelId, String stationId) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            Query query = em.createQuery(
                    "SELECT v.model.modelId, v.model.name, v.model.brand, v.model.description, " +
                    "       v.model.pricePerDay, v.model.seatCount, " +
                    "       v.station.stationId, v.station.name, v.station.address, COUNT(v) " +
                    "FROM Vehicle v " +
                    "WHERE v.status = :status " +
                    "  AND v.model.modelId = :modelId " +
                    "  AND v.station.stationId = :stationId " +
                    "GROUP BY v.model.modelId, v.model.name, v.model.brand, v.model.description, " +
                    "         v.model.pricePerDay, v.model.seatCount, " +
                    "         v.station.stationId, v.station.name, v.station.address");
            query.setParameter("status", VehicleStatus.AVAILABLE);
            query.setParameter("modelId", modelId);
            query.setParameter("stationId", stationId);
            List<VehicleSearchResult> results = mapSearchResults(query.getResultList(), true);
            attachThumbnails(em, results);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not load vehicle model at station with JPA", ex);
            return null;
        } finally {
            close(em);
        }
    }

    @Override
    public List<VehicleSearchResult> searchAvailableVehicleModels(String stationId, String categoryId) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            StringBuilder jpql = new StringBuilder();
            jpql.append("SELECT v.model.modelId, v.model.name, v.model.brand, v.model.description, ");
            jpql.append("       v.model.pricePerDay, v.model.seatCount, ");
            jpql.append("       v.station.stationId, v.station.name, v.station.address, COUNT(v) ");
            jpql.append("FROM Vehicle v ");
            jpql.append("WHERE v.status = :status ");

            if (!isBlank(stationId)) {
                jpql.append("AND v.station.stationId = :stationId ");
            }
            if (!isBlank(categoryId)) {
                jpql.append("AND v.model.category.categoryId = :categoryId ");
            }

            jpql.append("GROUP BY v.model.modelId, v.model.name, v.model.brand, v.model.description, ");
            jpql.append("         v.model.pricePerDay, v.model.seatCount, ");
            jpql.append("         v.station.stationId, v.station.name, v.station.address ");
            jpql.append("ORDER BY v.station.name, v.model.name");

            Query query = em.createQuery(jpql.toString());
            query.setParameter("status", VehicleStatus.AVAILABLE);
            if (!isBlank(stationId)) {
                query.setParameter("stationId", stationId);
            }
            if (!isBlank(categoryId)) {
                query.setParameter("categoryId", categoryId);
            }
            List<VehicleSearchResult> results = mapSearchResults(query.getResultList(), true);
            attachThumbnails(em, results);
            return results;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not search vehicle models with JPA", ex);
            return Collections.emptyList();
        } finally {
            close(em);
        }
    }

    @Override
    public List<VehicleSearchResult> searchAvailableVehicleModels(String stationId, String categoryId, Date startDate, Date endDate) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            Query query = em.createQuery(
                    "SELECT v.model.modelId, v.model.name, v.model.brand, v.model.description, " +
                    "       v.model.pricePerDay, v.model.seatCount, COUNT(v) " +
                    "FROM Vehicle v " +
                    "WHERE v.station.stationId = :stationId " +
                    "  AND (v.status = :availableStatus OR v.status = :rentedStatus) " +
                    "  AND v.model.category.categoryId = :categoryId " +
                    "  AND NOT EXISTS ( " +
                    "      SELECT r FROM Rental r " +
                    "      WHERE r.vehicle = v " +
                    "        AND (r.status = :bookedStatus OR r.status = :rentedRentalStatus) " +
                    "        AND r.startDate < :endDate " +
                    "        AND r.endDate > :startDate " +
                    "  ) " +
                    "GROUP BY v.model.modelId, v.model.name, v.model.brand, v.model.description, " +
                    "         v.model.pricePerDay, v.model.seatCount " +
                    "ORDER BY v.model.name");
            query.setParameter("stationId", stationId);
            query.setParameter("categoryId", categoryId);
            query.setParameter("availableStatus", VehicleStatus.AVAILABLE);
            query.setParameter("rentedStatus", VehicleStatus.RENTED);
            query.setParameter("bookedStatus", RentalStatus.BOOKED);
            query.setParameter("rentedRentalStatus", RentalStatus.RENTED);
            query.setParameter("endDate", endDate);
            query.setParameter("startDate", startDate);
            List<VehicleSearchResult> results = mapSearchResults(query.getResultList(), false);
            attachThumbnails(em, results);
            return results;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not search available vehicle models by date with JPA", ex);
            return Collections.emptyList();
        } finally {
            close(em);
        }
    }

    @Override
    public List<Vehicle> getAvailableVehiclesByModel(String stationId, String modelId, Date startDate, Date endDate) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery(
                    "SELECT v FROM Vehicle v " +
                    "WHERE v.station.stationId = :stationId " +
                    "AND v.model.modelId = :modelId " +
                    "AND (v.status = :availableStatus OR v.status = :rentedStatus) " +
                    "AND NOT EXISTS (" +
                    "    SELECT r FROM Rental r " +
                    "    WHERE r.vehicle = v " +
                    "    AND (r.status = :bookedStatus OR r.status = :rentedRentalStatus) " +
                    "    AND r.startDate < :endDate " +
                    "    AND r.endDate > :startDate" +
                    ") " +
                    "ORDER BY v.licensePlate",
                    Vehicle.class)
                    .setParameter("stationId", stationId)
                    .setParameter("modelId", modelId)
                    .setParameter("availableStatus", VehicleStatus.AVAILABLE)
                    .setParameter("rentedStatus", VehicleStatus.RENTED)
                    .setParameter("bookedStatus", RentalStatus.BOOKED)
                    .setParameter("rentedRentalStatus", RentalStatus.RENTED)
                    .setParameter("endDate", endDate)
                    .setParameter("startDate", startDate)
                    .getResultList();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not load available vehicles by model with JPA", ex);
            return Collections.emptyList();
        } finally {
            close(em);
        }
    }

    private List<VehicleSearchResult> mapSearchResults(List<?> rows, boolean includeStation) {
        List<VehicleSearchResult> results = new ArrayList<>();
        for (Object row : rows) {
            Object[] data = (Object[]) row;
            VehicleSearchResult result = new VehicleSearchResult();
            result.setModelId(asString(data[0]));
            result.setModelName(asString(data[1]));
            result.setBrand(asString(data[2]));
            result.setDescription(asString(data[3]));
            result.setPricePerDay(asDouble(data[4]));
            result.setSeatCount(asInteger(data[5]));

            if (includeStation) {
                result.setStationId(asString(data[6]));
                result.setStationName(asString(data[7]));
                result.setStationAddress(asString(data[8]));
                result.setRemaining(asInteger(data[9]));
            } else {
                result.setRemaining(asInteger(data[6]));
            }
            results.add(result);
        }
        return results;
    }

    private void attachThumbnails(EntityManager em, List<VehicleSearchResult> results) {
        for (VehicleSearchResult result : results) {
            result.setThumbnailImage(findThumbnailImage(em, result.getModelId()));
        }
    }

    private String findThumbnailImage(EntityManager em, String modelId) {
        if (isBlank(modelId)) {
            return null;
        }

        List<VehicleModelImage> frontImages = em.createQuery(
                "SELECT img FROM VehicleModelImage img " +
                "WHERE img.model.modelId = :modelId AND img.imageType = :imageType",
                VehicleModelImage.class)
                .setParameter("modelId", modelId)
                .setParameter("imageType", VehicleModelImageType.FRONT)
                .setMaxResults(1)
                .getResultList();
        if (!frontImages.isEmpty()) {
            return frontImages.get(0).getImageUrl();
        }

        List<VehicleModelImage> images = em.createQuery(
                "SELECT img FROM VehicleModelImage img WHERE img.model.modelId = :modelId",
                VehicleModelImage.class)
                .setParameter("modelId", modelId)
                .setMaxResults(1)
                .getResultList();
        return images.isEmpty() ? null : images.get(0).getImageUrl();
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private Double asDouble(Object value) {
        return value instanceof Number ? ((Number) value).doubleValue() : null;
    }

    private Integer asInteger(Object value) {
        return value instanceof Number ? ((Number) value).intValue() : null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void close(EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}
