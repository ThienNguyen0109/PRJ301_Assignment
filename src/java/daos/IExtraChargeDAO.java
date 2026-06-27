package daos;

import java.util.List;
import javax.persistence.EntityManager;
import models.ExtraCharge;

public interface IExtraChargeDAO {
    ExtraCharge create(EntityManager em, ExtraCharge charge);
    ExtraCharge findForUpdate(EntityManager em, String chargeId);
    List<ExtraCharge> findByRentalId(String rentalId);
    List<ExtraCharge> findByCustomerId(String customerId);
}
