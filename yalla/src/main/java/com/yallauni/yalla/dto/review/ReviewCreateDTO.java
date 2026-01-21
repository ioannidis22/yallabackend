package com.yallauni.yalla.dto.review;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for creating a new review.
 * Contains the rating and optional comment for a ride review.
 */
public class ReviewCreateDTO {
    @NotNull // The ID of the ride being reviewed
    private Long rideId;

    @NotNull // The ID of the user writing the review
    private Long reviewerId;
    /** Rating value between 1.0 and 5.0 */
    @DecimalMin("1.0") // Minimum allowed rating
    @DecimalMax("5.0") // Maximum allowed rating
    private double rating;
    
    @Size(max = 500) // Optional comment, up to 500 characters
    private String comment;

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
