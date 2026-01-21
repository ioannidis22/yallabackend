package com.yallauni.yalla.dto.booking;

/**
 * DTO for creating a new booking request.
 * Contains the essential information needed to book a seat on a ride.
 * Used by passengers to request a booking from a driver.
 */
public class BookingCreateDTO {
    /** The unique identifier of the ride to book */
    private Long rideId;
    /** Optional message from passenger to driver for the booking request */
    private String message;

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
