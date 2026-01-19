package com.yallauni.yalla.dto.payment;

import com.yallauni.yalla.core.model.Payment;
import java.math.BigDecimal;

/**
 * DTO for payment responses.
 */
public class PaymentResponseDTO {

    private Long id;
    private String stripePaymentIntentId;
    private String clientSecret;
    private Long amount;
    private BigDecimal displayAmount;
    private String currency;
    private String status;
    private Long userId;
    private String userEmail;
    private Long rideId;
    private Long bookingId;
    private String paymentMethodType;
    private String cardBrand;
    private String cardLastFour;
    private String description;
    private String errorMessage;
    private Long refundAmount;
    private String refundReason;
    private String createdAt;
    private String completedAt;
    private String refundedAt;

    // Static factory method
    public static PaymentResponseDTO fromEntity(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setStripePaymentIntentId(payment.getStripePaymentIntentId());
        dto.setAmount(payment.getAmount());
        dto.setDisplayAmount(payment.getDisplayAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setStatus(payment.getStatus().name());

        if (payment.getUser() != null) {
            dto.setUserId(payment.getUser().getUserID());
            dto.setUserEmail(payment.getUser().getEmailAddress());
        }

        if (payment.getRide() != null) {
            dto.setRideId(payment.getRide().getRideId());
        }

        if (payment.getBooking() != null) {
            dto.setBookingId(payment.getBooking().getId());
        }

        dto.setPaymentMethodType(payment.getPaymentMethodType());
        dto.setCardBrand(payment.getCardBrand());
        dto.setCardLastFour(payment.getCardLastFour());
        dto.setDescription(payment.getDescription());
        dto.setErrorMessage(payment.getErrorMessage());
        dto.setRefundAmount(payment.getRefundAmount());
        dto.setRefundReason(payment.getRefundReason());

        if (payment.getCreatedAt() != null) {
            dto.setCreatedAt(payment.getCreatedAt().toString());
        }
        if (payment.getCompletedAt() != null) {
            dto.setCompletedAt(payment.getCompletedAt().toString());
        }
        if (payment.getRefundedAt() != null) {
            dto.setRefundedAt(payment.getRefundedAt().toString());
        }

        return dto;
    }

    // Static factory method with client secret (for new payment intents)
    public static PaymentResponseDTO fromEntityWithSecret(Payment payment, String clientSecret) {
        PaymentResponseDTO dto = fromEntity(payment);
        dto.setClientSecret(clientSecret);
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStripePaymentIntentId() {
        return stripePaymentIntentId;
    }

    public void setStripePaymentIntentId(String stripePaymentIntentId) {
        this.stripePaymentIntentId = stripePaymentIntentId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public BigDecimal getDisplayAmount() {
        return displayAmount;
    }

    public void setDisplayAmount(BigDecimal displayAmount) {
        this.displayAmount = displayAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getCardLastFour() {
        return cardLastFour;
    }

    public void setCardLastFour(String cardLastFour) {
        this.cardLastFour = cardLastFour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Long refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(String refundedAt) {
        this.refundedAt = refundedAt;
    }
}
