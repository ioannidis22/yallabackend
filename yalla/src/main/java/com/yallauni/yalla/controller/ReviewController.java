package com.yallauni.yalla.controller;


import com.yallauni.yalla.core.model.Review;
import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.service.ReviewService;
import com.yallauni.yalla.dto.review.ReviewCreateDTO;
import com.yallauni.yalla.dto.review.ReviewResponseDTO;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDTO> addReview(@RequestBody ReviewCreateDTO reviewDto) {
        try {
            Review review = new Review();
            review.setRating(reviewDto.getRating());
            review.setComment(reviewDto.getComment());
            Ride ride = new Ride();
            ride.setRideId(reviewDto.getRideId());
            User reviewer = new User();
            reviewer.setUserID(reviewDto.getReviewerId());
            Review saved = reviewService.addReview(review, ride, reviewer);
            ReviewResponseDTO response = new ReviewResponseDTO();
            response.setId(saved.getId());
            response.setRideId(saved.getRide().getRideId());
            response.setReviewerId(saved.getReviewer().getUserID());
            response.setRating(saved.getRating());
            response.setComment(saved.getComment());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long id) {
        Optional<Review> review = reviewService.findById(id);
        if (review.isPresent()) {
            Review r = review.get();
            ReviewResponseDTO dto = new ReviewResponseDTO();
            dto.setId(r.getId());
            dto.setRideId(r.getRide().getRideId());
            dto.setReviewerId(r.getReviewer().getUserID());
            dto.setRating(r.getRating());
            dto.setComment(r.getComment());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ReviewResponseDTO> getAllReviews() {
        List<Review> reviews = reviewService.findAll();
        return reviews.stream().map(r -> {
            ReviewResponseDTO dto = new ReviewResponseDTO();
            dto.setId(r.getId());
            dto.setRideId(r.getRide().getRideId());
            dto.setReviewerId(r.getReviewer().getUserID());
            dto.setRating(r.getRating());
            dto.setComment(r.getComment());
            return dto;
        }).toList();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
