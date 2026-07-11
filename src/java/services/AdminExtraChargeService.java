package services;

import daos.AdminExtraChargeDAO;
import dto.AdminExtraChargeRow;
import enums.ExtraChargeStatus;
import enums.ExtraChargeType;
import enums.RentalStatus;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import models.ExtraCharge;
import models.Rental;
import utils.JPAUtil;

public class AdminExtraChargeService {
    private final AdminExtraChargeDAO chargeDAO = new AdminExtraChargeDAO();

    public List<AdminExtraChargeRow> search(String keyword, String type, String status) {
        return JPAUtil.execute(em -> chargeDAO.search(em, keyword, parseTypeFilter(type), parseStatusFilter(status)));
    }

    public AdminExtraChargeRow findDetail(String chargeId) {
        return JPAUtil.execute(em -> chargeDAO.findDetail(em, chargeId));
    }

    public List<Rental> findEligibleRentals() {
        return JPAUtil.execute(chargeDAO::findEligibleRentals);
    }

    public void create(String rentalId, String type, String amount, String description) {
        required(rentalId, "Rental");
        ExtraChargeType chargeType = parseTypeRequired(type);
        BigDecimal chargeAmount = parseAmount(amount);
        required(description, "Description");
        JPAUtil.executeInTransaction(em -> {
            Rental rental = chargeDAO.findRental(em, rentalId);
            if (rental == null) {
                throw new IllegalArgumentException("Rental not found.");
            }
            if (rental.getStatus() != RentalStatus.RENTED && rental.getStatus() != RentalStatus.COMPLETED) {
                throw new IllegalStateException("Extra charges can only be created for RENTED or COMPLETED rentals.");
            }
            ExtraCharge charge = new ExtraCharge();
            charge.setChargeId(UUID.randomUUID().toString());
            charge.setRentalId(rentalId.trim());
            charge.setChargeType(chargeType);
            charge.setAmount(chargeAmount);
            charge.setDescription(description.trim());
            charge.setStatus(ExtraChargeStatus.UNPAID);
            charge.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            chargeDAO.create(em, charge);
            return null;
        });
    }

    public void update(String chargeId, String type, String amount, String description) {
        required(chargeId, "Charge ID");
        ExtraChargeType chargeType = parseTypeRequired(type);
        BigDecimal chargeAmount = parseAmount(amount);
        required(description, "Description");
        JPAUtil.executeInTransaction(em -> {
            ExtraCharge charge = chargeDAO.findForUpdate(em, chargeId);
            if (charge == null) {
                throw new IllegalArgumentException("Extra charge not found.");
            }
            if (charge.getStatus() != ExtraChargeStatus.UNPAID) {
                throw new IllegalStateException("Only UNPAID charges can be updated.");
            }
            charge.setChargeType(chargeType);
            charge.setAmount(chargeAmount);
            charge.setDescription(description.trim());
            return null;
        });
    }

    public void cancel(String chargeId) {
        required(chargeId, "Charge ID");
        JPAUtil.executeInTransaction(em -> {
            ExtraCharge charge = chargeDAO.findForUpdate(em, chargeId);
            if (charge == null) {
                throw new IllegalArgumentException("Extra charge not found.");
            }
            if (charge.getStatus() == ExtraChargeStatus.PAID) {
                throw new IllegalStateException("Cannot cancel a PAID charge.");
            }
            if (charge.getStatus() == ExtraChargeStatus.CANCELLED) {
                throw new IllegalStateException("Charge is already cancelled.");
            }
            charge.setStatus(ExtraChargeStatus.CANCELLED);
            return null;
        });
    }

    private ExtraChargeType parseTypeFilter(String value) {
        if (value == null || value.trim().isEmpty() || "ALL".equalsIgnoreCase(value.trim())) {
            return null;
        }
        return parseTypeRequired(value);
    }

    private ExtraChargeType parseTypeRequired(String value) {
        try {
            return ExtraChargeType.valueOf(value == null ? "" : value.trim().toUpperCase());
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Extra charge type is invalid.");
        }
    }

    private ExtraChargeStatus parseStatusFilter(String value) {
        if (value == null || value.trim().isEmpty() || "ALL".equalsIgnoreCase(value.trim())) {
            return null;
        }
        try {
            return ExtraChargeStatus.valueOf(value.trim().toUpperCase());
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Extra charge status is invalid.");
        }
    }

    private BigDecimal parseAmount(String value) {
        try {
            BigDecimal amount = new BigDecimal(value == null ? "" : value.trim().replace(",", ""));
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException();
            }
            return amount;
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
    }

    private void required(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }
}
