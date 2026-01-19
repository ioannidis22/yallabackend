package com.yallauni.yalla.dto.payment;

import jakarta.validation.constraints.*;

/**
 * DTO for refund requests.
 */
public class RefundRequestDTO {

    @NotNull(message = "Payment ID is required")
    private Long paymentId;

    // If null, full refund is processed
    @Min(value = 1, message = "Refund amount must be at least 1 cent")
    private Long amount;

    @NotBlank(message = "Refund reason is required")
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
