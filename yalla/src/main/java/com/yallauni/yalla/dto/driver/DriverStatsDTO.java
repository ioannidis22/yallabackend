package com.yallauni.yalla.dto.driver;

/**
 * DTO for driver statistics.
 * Contains metrics about a driver's activity.
 * Used by driver dashboard and admin reports.
 */
public class DriverStatsDTO {
    /** Unique identifier of the driver */
    private Long driverId;
    /** Full name of the driver */
    private String driverName;
    /** Total number of completed trips */
    private long totalTripsCompleted;
    /** Total number of cancelled trips */
    private long totalTripsCancelled;
    /** Number of rides currently scheduled */
    private long scheduledRides;
    /** Number of rides currently in progress */
    private long inProgressRides;
    /** Number of pending booking requests */
    private long pendingBookings;
    /** Number of accepted bookings */
    private long acceptedBookings;
    /** Number of rejected bookings */
    private long rejectedBookings;
    /** Average rating received from passengers (1.0 - 5.0) */
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
