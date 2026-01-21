package com.yallauni.yalla.dto.review;

/**
 * Data Transfer Object for returning review information in API responses.
 * Contains review details for display in the UI.
 */
public class ReviewResponseDTO {
    /** Unique ID of the review */
    private Long id; // Unique ID of the review
    /** ID of the ride being reviewed */
    private Long rideId; // ID of the ride being reviewed
    /** ID of the user who wrote the review */
    private Long reviewerId; // ID of the user who wrote the review
    /** Rating value (1.0 - 5.0) */
    private double rating; // Rating value (1.0 - 5.0)
    /** Optional comment about the ride experience */
    private String comment; // Optional comment

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

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
