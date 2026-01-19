package com.yallauni.yalla.dto.booking;

public class BookingCreateDTO {
    private Long rideId;
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
