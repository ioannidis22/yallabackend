package com.yallauni.yalla.dto.driver;

public class DriverStatsDTO {
    private Long driverId;
    private String driverName;
    private long totalTripsCompleted;
    private long totalTripsCancelled;
    private long scheduledRides;
    private long inProgressRides;
    private long pendingBookings;
    private long acceptedBookings;
    private long rejectedBookings;
    private double averageRating;

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

    public long getTotalTripsCompleted() {
        return totalTripsCompleted;
    }

    public void setTotalTripsCompleted(long totalTripsCompleted) {
        this.totalTripsCompleted = totalTripsCompleted;
    }

    public long getTotalTripsCancelled() {
        return totalTripsCancelled;
    }

    public void setTotalTripsCancelled(long totalTripsCancelled) {
        this.totalTripsCancelled = totalTripsCancelled;
    }

    public long getScheduledRides() {
        return scheduledRides;
    }

    public void setScheduledRides(long scheduledRides) {
        this.scheduledRides = scheduledRides;
    }

    public long getInProgressRides() {
        return inProgressRides;
    }

    public void setInProgressRides(long inProgressRides) {
        this.inProgressRides = inProgressRides;
    }

    public long getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(long pendingBookings) {
        this.pendingBookings = pendingBookings;
    }

    public long getAcceptedBookings() {
        return acceptedBookings;
    }

    public void setAcceptedBookings(long acceptedBookings) {
        this.acceptedBookings = acceptedBookings;
    }

    public long getRejectedBookings() {
        return rejectedBookings;
    }

    public void setRejectedBookings(long rejectedBookings) {
        this.rejectedBookings = rejectedBookings;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
