package com.yallauni.yalla.dto.ride;

import java.util.List;

/**
 * Data Transfer Object for returning ride information in API responses.
 * Contains comprehensive ride details including driver and passenger info.
 */
public class RideResponseDTO {
    /** Unique identifier of the ride */
    private Long id;
    /** Starting location of the ride */
    private String startingPoint;
    /** Destination of the ride */
    private String destination;
    /** Scheduled departure time */
    private String departureTime;
    /** Current status (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED) */
    private String status;
    /** Price per seat */
    private Double price;
    /** Total number of available seats */
    private int availableSeats;
    /** Number of remaining seats after bookings */
    private int remainingSeats;
    /** Current number of passengers */
    private int passengerCount;
    /** Driver's notes about the ride */
    private String driverNotes;
    /** ID of the driver */
    private Long driverId;
    /** Full name of the driver */
    private String driverName;
    /** List of passengers on this ride */
    private List<PassengerInfo> passengers;

    // class for passenger info
    public static class PassengerInfo {
        private Long id;
        private String name;
        private String email;

        public PassengerInfo() {
        }

        public PassengerInfo(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getRemainingSeats() {
        return remainingSeats;
    }

    public void setRemainingSeats(int remainingSeats) {
        this.remainingSeats = remainingSeats;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public String getDriverNotes() {
        return driverNotes;
    }

    public void setDriverNotes(String driverNotes) {
        this.driverNotes = driverNotes;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public List<PassengerInfo> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerInfo> passengers) {
        this.passengers = passengers;
    }
}