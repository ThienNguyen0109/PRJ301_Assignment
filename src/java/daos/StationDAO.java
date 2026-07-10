package daos;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Station;
import utils.JPAUtil;

/**
 * Data Access Object for Station using JPA.
 */
public class StationDAO implements IStationDAO {

    private static final Logger LOGGER = Logger.getLogger(StationDAO.class.getName());

    @Override
    public List<Station> getAllStations() {
        try {
            return JPAUtil.execute(em -> em.createQuery(
                    "SELECT s FROM Station s ORDER BY s.name",
                    Station.class)
                    .getResultList());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not load stations with JPA", ex);
            return Collections.emptyList();
        }
    }

    @Override
    public Station getStationById(String id) {
        try {
            return JPAUtil.execute(em -> em.find(Station.class, id));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not find station", ex);
            return null;
        }
    }

    @Override
    public boolean createStation(Station station) {
        try {
            if (getStationById(station.getStationId()) != null) {
                return false;
            }
            JPAUtil.executeInTransaction(em -> {
                em.persist(station);
                return null;
            });
            return true;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not create station", ex);
            return false;
        }
    }

    @Override
    public boolean updateStation(Station station) {
        try {
            JPAUtil.executeInTransaction(em -> {
                em.merge(station);
                return null;
            });
            return true;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not update station", ex);
            return false;
        }
    }

    @Override
    public boolean deleteStation(String id) {
        try {
            JPAUtil.executeInTransaction(em -> {
                Station station = em.find(Station.class, id);

                if (station == null) {
                    return null;
                }
                Long vehicleCount = em.createQuery(
                        "SELECT COUNT(v) FROM Vehicle v WHERE v.stationId = :stationId", Long.class)
                        .setParameter("stationId", id)
                        .getSingleResult();
                Long rentalCount = em.createQuery(
                        "SELECT COUNT(r) FROM Rental r WHERE r.pickupStationId = :stationId", Long.class)
                        .setParameter("stationId", id)
                        .getSingleResult();
                if (vehicleCount > 0 || rentalCount > 0) {
                    throw new IllegalStateException("Cannot delete a station that is used by vehicles or rentals.");
                }
                em.remove(station);

                return null;
            });

            return true;

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not delete station", ex);
            return false;
        }
    }

}
