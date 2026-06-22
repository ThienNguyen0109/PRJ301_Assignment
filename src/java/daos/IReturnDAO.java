package daos;

import dto.ReturnRentalDTO;
import java.sql.Date;
import java.util.List;
import javax.persistence.EntityManager;
import models.Rental;
import models.Vehicle;

public interface IReturnDAO {
    List<ReturnRentalDTO> searchRentedRentals(String keyword, Date endDate);
    ReturnRentalDTO findRentalDetail(String rentalId);
    Rental findRentalForUpdate(EntityManager em, String rentalId);
    Vehicle findVehicleForUpdate(EntityManager em, String vehicleId);
}
