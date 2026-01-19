package com.yallauni.yalla.dto.payment;

import jakarta.validation.constraints.*;

/**
 * DTO for creating a new payment intent.
 */
public class PaymentCreateDTO {

    @NotNull(message = "Amount is required")
    @Min(value = 50, message = "Minimum payment amount is 50 cents")
    @Max(value = 99999999, message = "Maximum payment amount exceeded")
    private Long amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
    @Pattern(regexp = "^[A-Za-z]{3}$", message = "Invalid currency format")
    private String currency;

    private Long rideId;

    private Long bookingId;

    private String description;

    // Idempotency key to prevent duplicate payments
    private String idempotencyKey;

    // Getters and Setters
    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
