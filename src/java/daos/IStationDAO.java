package daos;

import java.util.List;
import models.Station;

/**
 * Interface for Station data access
 */
public interface IStationDAO {
    List<Station> getAllStations();
}

