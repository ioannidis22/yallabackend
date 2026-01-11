package com.yallauni.yalla.service.impl;

import com.yallauni.yalla.model.Review;
import com.yallauni.yalla.model.Ride;
import com.yallauni.yalla.model.User;
import com.yallauni.yalla.repository.ReviewRepository;
import com.yallauni.yalla.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (id == null)
            throw new IllegalArgumentException("Review ID must not be null");
        return reviewRepository.findById(id);
    }

    @Override
    public List<Review> findByRide(Ride ride) {
        return reviewRepository.findByRide(ride);
    }

    @Override
    public List<Review> findByReviewer(User reviewer) {
        return reviewRepository.findByReviewer(reviewer);
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}