package com.yallauni.yalla.dto.booking;

/**
 * Data Transfer Object for returning booking information in API responses.
 * Contains detailed information about a booking including ride and passenger
 * details.
 * Used by controllers to send booking data to clients.
 */
public class BookingResponseDTO {
    /** Unique identifier of the booking */
    private Long id;
    /** ID of the ride associated with this booking */
    private Long rideId;
    /** Starting location of the ride */
    private String rideStartingPoint;
    /** Destination of the ride */
    private String rideDestination;
    /** Scheduled departure time of the ride */
    private String rideDepartureTime;
    /** Current status of the ride (e.g., SCHEDULED, IN_PROGRESS, COMPLETED) */
    private String rideStatus;
    /** ID of the passenger who made the booking */
    private Long passengerId;
    /** Full name of the passenger */
    private String passengerName;
    /** Email address of the passenger */
    private String passengerEmail;
    /** Current status of the booking (PENDING, ACCEPTED, REJECTED, CANCELLED) */
    private String status;
    /** Timestamp when the booking was created */
    private String createdAt;
    /** Message sent by the passenger when creating the booking */
    private String passengerMessage;
    /** Response message from the driver (if any) */
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
