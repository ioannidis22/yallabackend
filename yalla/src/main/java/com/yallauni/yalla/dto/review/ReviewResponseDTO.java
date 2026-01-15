package com.yallauni.yalla.dto.review;

public class ReviewResponseDTO {
    private Long id; // Unique ID of the review
    private Long rideId; // ID of the ride being reviewed
    private Long reviewerId; // ID of the user who wrote the review
    private double rating; // Rating value (1.0 - 5.0)
    private String comment; // Optional comment

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
