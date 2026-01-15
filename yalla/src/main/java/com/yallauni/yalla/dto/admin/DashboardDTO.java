package com.yallauni.yalla.dto.admin;

public class DashboardDTO {
    private long totalUsers;
    private long totalDrivers;
    private long totalPassengers;
    private long totalRides;
    private long activeRides;
    private long completedRides;
    private long totalReviews;
    private double averageRating;

    // Getters and Setters
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
}
