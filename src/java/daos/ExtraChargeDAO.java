package daos;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import models.ExtraCharge;
import utils.JPAUtil;

public class ExtraChargeDAO implements IExtraChargeDAO {
    @Override
    public ExtraCharge create(EntityManager em, ExtraCharge charge) {
        em.persist(charge);
        return charge;
    }

    @Override
    public ExtraCharge findForUpdate(EntityManager em, String chargeId) {
        return em.find(ExtraCharge.class, chargeId, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    public List<ExtraCharge> findByRentalId(String rentalId) {
        return JPAUtil.execute(em -> em.createQuery(
                "SELECT c FROM ExtraCharge c WHERE c.rentalId = :rentalId ORDER BY c.createdAt ASC",
                ExtraCharge.class)
                .setParameter("rentalId", rentalId)
                .getResultList());
    }

    @Override
    public List<ExtraCharge> findByCustomerId(String customerId) {
        return JPAUtil.execute(em -> em.createQuery(
                "SELECT c FROM ExtraCharge c JOIN c.rental r "
                + "WHERE r.customerId = :customerId ORDER BY c.createdAt DESC",
                ExtraCharge.class)
                .setParameter("customerId", customerId)
                .getResultList());
    }
}
