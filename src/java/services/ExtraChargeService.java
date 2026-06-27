package services;

import daos.ExtraChargeDAO;
import daos.IExtraChargeDAO;
import enums.ExtraChargeStatus;
import enums.ExtraChargeType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import models.ExtraCharge;

public class ExtraChargeService {
    private final IExtraChargeDAO extraChargeDAO;

    public ExtraChargeService() {
        this(new ExtraChargeDAO());
    }

    public ExtraChargeService(IExtraChargeDAO extraChargeDAO) {
        this.extraChargeDAO = extraChargeDAO;
    }

    public ExtraCharge createCharge(EntityManager em, String rentalId, String incidentId,
            ExtraChargeType type, BigDecimal amount, String description, ExtraChargeStatus status) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        ExtraCharge charge = new ExtraCharge();
        charge.setChargeId(UUID.randomUUID().toString());
        charge.setRentalId(rentalId);
        charge.setIncidentId(incidentId);
        charge.setChargeType(type);
        charge.setAmount(amount);
        charge.setDescription(description);
        charge.setStatus(status);
        charge.setCreatedAt(now());
        if (status == ExtraChargeStatus.PAID) {
            charge.setPaidAt(now());
        }
        return extraChargeDAO.create(em, charge);
    }

    public ExtraCharge findForUpdate(EntityManager em, String chargeId) {
        return extraChargeDAO.findForUpdate(em, chargeId);
    }

    public List<ExtraCharge> findByRentalId(String rentalId) {
        return extraChargeDAO.findByRentalId(rentalId);
    }

    public List<ExtraCharge> findByCustomerId(String customerId) {
        return extraChargeDAO.findByCustomerId(customerId);
    }

    public void markPaid(ExtraCharge charge) {
        if (charge != null) {
            charge.setStatus(ExtraChargeStatus.PAID);
            charge.setPaidAt(now());
        }
    }

    public void markUnpaid(ExtraCharge charge) {
        if (charge != null) {
            charge.setStatus(ExtraChargeStatus.UNPAID);
            charge.setPaidAt(null);
        }
    }

    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }
}
