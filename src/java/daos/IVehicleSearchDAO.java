package daos;

import java.sql.Date;
import java.util.List;
import models.Vehicle;
import dto.VehicleSearchResult;

/**
 * Interface for vehicle search data access
 */
public interface IVehicleSearchDAO {
    List<VehicleSearchResult> getFeaturedAvailableVehicleModels(int limit);

    VehicleSearchResult getAvailableVehicleModelAtStation(String modelId, String stationId);

    List<VehicleSearchResult> searchAvailableVehicleModels(String stationId, String categoryId);

    List<VehicleSearchResult> searchAvailableVehicleModels(String stationId, String categoryId, Date startDate, Date endDate);

    List<Vehicle> getAvailableVehiclesByModel(String stationId, String modelId, Date startDate, Date endDate);
}

