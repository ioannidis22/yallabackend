package com.yallauni.yalla.core.model.repository;

// Spring Data JPA base repository (already commented elsewhere)
import org.springframework.data.jpa.repository.JpaRepository;

// Review entity
import com.yallauni.yalla.core.model.Review;
// Ride entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Ride;
// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Find all reviews for a specific ride
    List<Review> findByRide(Ride ride);

    // Find all reviews written by a specific user
    List<Review> findByReviewer(User reviewer);
}
