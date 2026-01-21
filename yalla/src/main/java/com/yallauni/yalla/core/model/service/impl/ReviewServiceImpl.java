package com.yallauni.yalla.core.model.service.impl;

import com.yallauni.yalla.core.model.Review;
import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.repository.ReviewRepository;
import com.yallauni.yalla.core.model.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ReviewService.
 * Provides business logic for reviews creation and management.
 * Validates rating ranges.
 * Also uses ReviewRepository for database interactions.
 */
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