package daos;

import java.util.List;
import models.Station;

/**
 * Interface for Station data access
 */
public interface IStationDAO {
    List<Station> getAllStations();

    Station getStationById(String id);

    boolean createStation(Station station);

    boolean updateStation(Station station);

    boolean deleteStation(String id);
}

