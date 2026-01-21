package com.yallauni.yalla.core.model.service;

import java.util.List;
import java.util.Optional;
import com.yallauni.yalla.core.model.Review;
import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;

/**
 * Service interface for review operations.
 * Defines business logic for creating and managing ride reviews.
 * Implemented by ReviewServiceImpl.
 */
public interface ReviewService {
    // Add a new review for a ride and reviewer
    Review addReview(Review review, Ride ride, User reviewer);

    // Find review by id
    Optional<Review> findById(Long id);

    // Find all reviews for a specific ride
    List<Review> findByRide(Ride ride);

    // Find all reviews written by a specific user
    List<Review> findByReviewer(User reviewer);

    // Return all reviews
    List<Review> findAll();

    // Delete review by id
    void deleteReview(Long id);
}
