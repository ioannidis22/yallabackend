package com.yallauni.yalla.dto.review;

import jakarta.validation.constraints.*;

public class ReviewCreateDTO {
    @NotNull
    private Long rideId;
    @NotNull
    private Long reviewerId;
    @DecimalMin("1.0")
    @DecimalMax("5.0")
    private double rating;
    @Size(max = 500)
    private String comment;

    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
