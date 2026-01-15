package com.yallauni.yalla.core.model.service.impl;

// Review entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Review;
// Ride entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Ride;
// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;
// Repository for review data (already commented elsewhere)
import com.yallauni.yalla.core.model.repository.ReviewRepository;
// Review service interface
import com.yallauni.yalla.core.model.service.ReviewService;

// Marks this class as a Spring service (already commented elsewhere)
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review addReview(Review review, Ride ride, User reviewer) {
        // Add a new review for a ride and reviewer
        if (review == null || ride == null || reviewer == null) {
            throw new IllegalArgumentException("Review, ride, and reviewer must not be null");
        }
        if (review.getRating() < 1.0 || review.getRating() > 5.0) {
            throw new IllegalArgumentException("Rating must be between 1.0 and 5.0");
        }
        review.setRide(ride);
        review.setReviewer(reviewer);
        return reviewRepository.save(review);
    }

    @Override
    public Optional<Review> findById(Long id) {
        // Find review by id
        if (id == null)
            throw new IllegalArgumentException("Review ID must not be null");
        return reviewRepository.findById(id);
    }

    @Override
    public List<Review> findByRide(Ride ride) {
        // Find all reviews for a specific ride
        return reviewRepository.findByRide(ride);
    }

    @Override
    public List<Review> findByReviewer(User reviewer) {
        // Find all reviews written by a specific user
        return reviewRepository.findByReviewer(reviewer);
    }

    @Override
    public List<Review> findAll() {
        // Return all reviews
        return reviewRepository.findAll();
    }

    @Override
    public void deleteReview(Long id) {
        // Delete review by id
        reviewRepository.deleteById(id);
    }
}