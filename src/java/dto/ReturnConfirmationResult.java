package dto;

import enums.PaymentMethod;
import java.math.BigDecimal;

public class ReturnConfirmationResult {
    private final boolean damaged;
    private final BigDecimal lateFee;
    private final BigDecimal damageFee;
    private final PaymentMethod extraChargePaymentMethod;
    private final String extraChargeOrderId;
    private final BigDecimal extraChargePaymentAmount;

    public ReturnConfirmationResult(boolean damaged, BigDecimal lateFee,
            PaymentMethod lateFeePaymentMethod, String lateFeeOrderId) {
        this(damaged, lateFee, BigDecimal.ZERO, lateFeePaymentMethod, lateFeeOrderId, lateFee);
    }

    public ReturnConfirmationResult(boolean damaged, BigDecimal lateFee, BigDecimal damageFee,
            PaymentMethod extraChargePaymentMethod, String extraChargeOrderId, BigDecimal extraChargePaymentAmount) {
        this.damaged = damaged;
        this.lateFee = lateFee;
        this.damageFee = damageFee;
        this.extraChargePaymentMethod = extraChargePaymentMethod;
        this.extraChargeOrderId = extraChargeOrderId;
        this.extraChargePaymentAmount = extraChargePaymentAmount;
    }

    public boolean isDamaged() {
        return damaged;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public BigDecimal getDamageFee() {
        return damageFee;
    }

    public PaymentMethod getLateFeePaymentMethod() {
        return extraChargePaymentMethod;
    }

    public String getLateFeeOrderId() {
        return extraChargeOrderId;
    }

    public String getExtraChargeOrderId() {
        return extraChargeOrderId;
    }

    public BigDecimal getExtraChargePaymentAmount() {
        return extraChargePaymentAmount;
    }

    public boolean isLateFeeVNPayPending() {
        return isExtraChargeVNPayPending();
    }

    public boolean isExtraChargeVNPayPending() {
        return extraChargePaymentAmount != null
                && extraChargePaymentAmount.signum() > 0
                && extraChargePaymentMethod == PaymentMethod.VNPAY
                && extraChargeOrderId != null
                && !extraChargeOrderId.trim().isEmpty();
    }
}
