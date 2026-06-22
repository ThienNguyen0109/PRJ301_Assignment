package daos;

import dto.MaintenanceVehicleDTO;
import java.util.List;
import javax.persistence.EntityManager;
import models.Vehicle;
import models.VehicleMaintenance;

public interface IMaintenanceDAO {
    List<MaintenanceVehicleDTO> findPendingMaintenance(String keyword);
    VehicleMaintenance findMaintenanceForUpdate(EntityManager em, String maintenanceId);
    Vehicle findVehicleForUpdate(EntityManager em, String vehicleId);
}
