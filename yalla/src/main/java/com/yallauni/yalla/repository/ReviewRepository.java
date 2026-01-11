package com.yallauni.yalla.repository;

import com.yallauni.yalla.model.Review;
import com.yallauni.yalla.model.Ride;
import com.yallauni.yalla.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRide(Ride ride);

    List<Review> findByReviewer(User reviewer);
}
