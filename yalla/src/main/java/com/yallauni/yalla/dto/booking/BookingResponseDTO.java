package com.yallauni.yalla.dto.booking;

public class BookingResponseDTO {
    private Long id;
    private Long rideId;
    private String rideStartingPoint;
    private String rideDestination;
    private String rideDepartureTime;
    private String rideStatus;
    private Long passengerId;
    private String passengerName;
    private String passengerEmail;
    private String status;
    private String createdAt;
    private String passengerMessage;
    private String driverResponse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getRideStartingPoint() {
        return rideStartingPoint;
    }

    public void setRideStartingPoint(String rideStartingPoint) {
        this.rideStartingPoint = rideStartingPoint;
    }

    public String getRideDestination() {
        return rideDestination;
    }

    public void setRideDestination(String rideDestination) {
        this.rideDestination = rideDestination;
    }

    public String getRideDepartureTime() {
        return rideDepartureTime;
    }

    public void setRideDepartureTime(String rideDepartureTime) {
        this.rideDepartureTime = rideDepartureTime;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPassengerMessage() {
        return passengerMessage;
    }

    public void setPassengerMessage(String passengerMessage) {
        this.passengerMessage = passengerMessage;
    }

    public String getDriverResponse() {
        return driverResponse;
    }

    public void setDriverResponse(String driverResponse) {
        this.driverResponse = driverResponse;
    }
}
