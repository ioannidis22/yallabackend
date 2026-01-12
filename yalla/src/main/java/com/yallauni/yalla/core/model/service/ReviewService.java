package com.yallauni.yalla.core.model.service;

import java.util.List;
import java.util.Optional;

import com.yallauni.yalla.core.model.Review;
import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;

public interface ReviewService {
    Review addReview(Review review, Ride ride, User reviewer);

    Optional<Review> findById(Long id);

    List<Review> findByRide(Ride ride);

    List<Review> findByReviewer(User reviewer);

    List<Review> findAll();

    void deleteReview(Long id);
}
