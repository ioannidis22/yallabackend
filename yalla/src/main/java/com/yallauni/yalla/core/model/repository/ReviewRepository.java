package com.yallauni.yalla.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.yallauni.yalla.core.model.Review;
import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;

import java.util.List;

/**
 * Repository interface for Review entity database operations.
 * Extends JpaRepository to provide CRUD operations and custom queries.
 * Used by ReviewService for review data access.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Find all reviews for a specific ride
    List<Review> findByRide(Ride ride);

    // Find all reviews written by a specific user
    List<Review> findByReviewer(User reviewer);
}
