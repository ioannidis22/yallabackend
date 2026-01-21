package com.yallauni.yalla.dto.admin;

/**
 * Data Transfer Object representing dashboard statistics for the admin panel.
 * Contains metrics about users, rides, and reviews in the system.
 * Used by AdminController to display admin dashboard data.
 */
public class DashboardDTO {
    /** Total number of registered users in the system */
    private long totalUsers;
    /** Total number of users with driver role */
    private long totalDrivers;
    /** Total number of users with passenger role */
    private long totalPassengers;
    /** Total number of users who have been banned */
    private long bannedUsers;
    /** Total number of rides created in the system */
    private long totalRides;
    /** Number of rides currently in progress */
    private long activeRides;
    /** Number of rides that have been completed */
    private long completedRides;
    /** Total number of reviews submitted */
    private long totalReviews;
    /** Average rating across all reviews (1.0 - 5.0) */
    private double averageRating;

    
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalDrivers() {
        return totalDrivers;
    }

    public void setTotalDrivers(long totalDrivers) {
        this.totalDrivers = totalDrivers;
    }

    public long getTotalPassengers() {
        return totalPassengers;
    }

    public void setTotalPassengers(long totalPassengers) {
        this.totalPassengers = totalPassengers;
    }

    public long getBannedUsers() {
        return bannedUsers;
    }

    public void setBannedUsers(long bannedUsers) {
        this.bannedUsers = bannedUsers;
    }

    public long getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(long totalRides) {
        this.totalRides = totalRides;
    }

    public long getActiveRides() {
        return activeRides;
    }

    public void setActiveRides(long activeRides) {
        this.activeRides = activeRides;
    }

    public long getCompletedRides() {
        return completedRides;
    }

    public void setCompletedRides(long completedRides) {
        this.completedRides = completedRides;
    }

    public long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    // Ticket stats
    private long pendingTickets;
    private long totalTickets;

    public long getPendingTickets() {
        return pendingTickets;
    }

    public void setPendingTickets(long pendingTickets) {
        this.pendingTickets = pendingTickets;
    }

    public long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }
}
