package com.yallauni.yalla.service;

import com.yallauni.yalla.model.Review;
import com.yallauni.yalla.model.Ride;
import com.yallauni.yalla.model.User;
import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review addReview(Review review, Ride ride, User reviewer);

    Optional<Review> findById(Long id);

    List<Review> findByRide(Ride ride);

    List<Review> findByReviewer(User reviewer);

    List<Review> findAll();

    void deleteReview(Long id);
}
