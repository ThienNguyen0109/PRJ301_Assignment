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
}
