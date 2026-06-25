package dto;

import enums.PaymentMethod;
import java.math.BigDecimal;

public class ReturnConfirmationResult {
    private final boolean damaged;
    private final BigDecimal lateFee;
    private final PaymentMethod lateFeePaymentMethod;
    private final String lateFeeOrderId;

    public ReturnConfirmationResult(boolean damaged, BigDecimal lateFee,
            PaymentMethod lateFeePaymentMethod, String lateFeeOrderId) {
        this.damaged = damaged;
        this.lateFee = lateFee;
        this.lateFeePaymentMethod = lateFeePaymentMethod;
        this.lateFeeOrderId = lateFeeOrderId;
    }

    public boolean isDamaged() {
        return damaged;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public PaymentMethod getLateFeePaymentMethod() {
        return lateFeePaymentMethod;
    }

    public String getLateFeeOrderId() {
        return lateFeeOrderId;
    }

    public boolean isLateFeeVNPayPending() {
        return lateFee != null
                && lateFee.signum() > 0
                && lateFeePaymentMethod == PaymentMethod.VNPAY
                && lateFeeOrderId != null
                && !lateFeeOrderId.trim().isEmpty();
    }
}
