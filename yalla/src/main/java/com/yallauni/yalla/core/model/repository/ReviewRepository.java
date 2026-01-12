package com.yallauni.yalla.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yallauni.yalla.core.model.Review;
import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRide(Ride ride);

    List<Review> findByReviewer(User reviewer);
}
