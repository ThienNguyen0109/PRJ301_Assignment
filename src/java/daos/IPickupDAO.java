package daos;

import dto.PickupRentalDTO;
import java.sql.Date;
import java.util.List;
import javax.persistence.EntityManager;
import models.Rental;
import models.Vehicle;

public interface IPickupDAO {
    List<PickupRentalDTO> searchBookedRentals(String keyword, Date pickupDate);
    PickupRentalDTO findRentalDetail(String rentalId);
    Rental findRentalForUpdate(EntityManager em, String rentalId);
    Vehicle findVehicleForUpdate(EntityManager em, String vehicleId);
}
