package services;

import daos.AdminDiscountDAO;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import models.Discount;
import utils.JPAUtil;

/** Applies validation and usage rules for administrator-managed discount codes. */
public class AdminDiscountService {

    private final AdminDiscountDAO discountDAO = new AdminDiscountDAO();

    public List<Discount> search(String keyword, String status) {
        List<Discount> discounts = JPAUtil.execute(em -> discountDAO.search(em, keyword));
        String selectedStatus = trim(status).toUpperCase();
        if (selectedStatus.isEmpty() || "ALL".equals(selectedStatus)) {
            return discounts;
        }
        List<Discount> filtered = new ArrayList<>();
        for (Discount discount : discounts) {
            if (selectedStatus.equals(statusOf(discount))) {
                filtered.add(discount);
            }
        }
        return filtered;
    }

    public Discount findById(String discountId) {
        return JPAUtil.execute(em -> discountDAO.findById(em, discountId));
    }

    public boolean hasUsage(String discountId) {
        return JPAUtil.execute(em -> discountDAO.hasUsage(em, trim(discountId)));
    }

    public void create(String code, String percentValue, String quantityValue, String expiredAtValue) {
        DiscountValues values = validateValues(code, percentValue, quantityValue, expiredAtValue);
        JPAUtil.executeInTransaction(em -> {
            if (discountDAO.codeExists(em, values.code, null)) {
                throw new IllegalArgumentException("Discount code already exists.");
            }
            Discount discount = new Discount();
            discount.setDiscountId(UUID.randomUUID().toString());
            apply(discount, values);
            discountDAO.create(em, discount);
            return null;
        });
    }

    public void update(String discountId, String code, String percentValue, String quantityValue, String expiredAtValue) {
        required(discountId, "Discount ID");
        DiscountValues values = validateValues(code, percentValue, quantityValue, expiredAtValue);
        JPAUtil.executeInTransaction(em -> {
            Discount discount = discountDAO.findById(em, discountId);
            if (discount == null) {
                throw new IllegalArgumentException("Discount not found.");
            }
            boolean used = discountDAO.hasUsage(em, discount.getDiscountId());
            if (used && (!discount.getCode().equalsIgnoreCase(values.code)
                    || !discount.getDiscountPercent().equals(values.percent))) {
                throw new IllegalStateException("A used discount can only update its quantity and expiry date.");
            }
            if (discountDAO.codeExists(em, values.code, discount.getDiscountId())) {
                throw new IllegalArgumentException("Discount code already exists.");
            }
            apply(discount, values);
            return null;
        });
    }

    public void delete(String discountId) {
        required(discountId, "Discount ID");
        JPAUtil.executeInTransaction(em -> {
            Discount discount = discountDAO.findById(em, discountId);
            if (discount == null) {
                throw new IllegalArgumentException("Discount not found.");
            }
            if (discountDAO.hasUsage(em, discount.getDiscountId())) {
                throw new IllegalStateException("Cannot delete a discount that has been applied to rentals.");
            }
            discountDAO.delete(em, discount);
            return null;
        });
    }

    public String statusOf(Discount discount) {
        if (discount.getExpiredAt() == null || !discount.getExpiredAt().after(new Timestamp(System.currentTimeMillis()))) {
            return "EXPIRED";
        }
        if (discount.getQuantity() == null || discount.getQuantity() <= 0) {
            return "OUT_OF_STOCK";
        }
        return "ACTIVE";
    }

    private DiscountValues validateValues(String code, String percentValue, String quantityValue, String expiredAtValue) {
        required(code, "Discount code");
        String normalizedCode = trim(code).toUpperCase();
        if (normalizedCode.length() > 50) {
            throw new IllegalArgumentException("Discount code must be at most 50 characters.");
        }
        int percent = parseInt(percentValue, "Discount percent");
        if (percent < 1 || percent > 100) {
            throw new IllegalArgumentException("Discount percent must be from 1 to 100.");
        }
        int quantity = parseInt(quantityValue, "Quantity");
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must not be negative.");
        }
        Timestamp expiredAt = parseExpiredAt(expiredAtValue);
        if (!expiredAt.after(new Timestamp(System.currentTimeMillis()))) {
            throw new IllegalArgumentException("Expiry date must be in the future.");
        }
        return new DiscountValues(normalizedCode, percent, quantity, expiredAt);
    }

    private Timestamp parseExpiredAt(String value) {
        required(value, "Expiry date");
        try {
            return Timestamp.valueOf(LocalDateTime.parse(trim(value)));
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Expiry date is invalid.");
        }
    }

    private int parseInt(String value, String label) {
        try {
            return Integer.parseInt(trim(value));
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException(label + " must be a whole number.");
        }
    }

    private void apply(Discount discount, DiscountValues values) {
        discount.setCode(values.code);
        discount.setDiscountPercent(values.percent);
        discount.setQuantity(values.quantity);
        discount.setExpiredAt(values.expiredAt);
    }

    private void required(String value, String label) {
        if (trim(value).isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private static class DiscountValues {
        private final String code;
        private final int percent;
        private final int quantity;
        private final Timestamp expiredAt;

        private DiscountValues(String code, int percent, int quantity, Timestamp expiredAt) {
            this.code = code;
            this.percent = percent;
            this.quantity = quantity;
            this.expiredAt = expiredAt;
        }
    }
}
