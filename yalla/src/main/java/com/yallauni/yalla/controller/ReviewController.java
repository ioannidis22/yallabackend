package com.yallauni.yalla.controller;


// Review entity
import com.yallauni.yalla.core.model.Review;
// Ride entity (already commented elsewhere)
import com.yallauni.yalla.core.model.Ride;
// User entity (already commented elsewhere)
import com.yallauni.yalla.core.model.User;
// Service for review logic
import com.yallauni.yalla.core.model.service.ReviewService;
// DTO for creating review
import com.yallauni.yalla.dto.review.ReviewCreateDTO;
// DTO for returning review data
import com.yallauni.yalla.dto.review.ReviewResponseDTO;


// Used for HTTP responses (already commented elsewhere)
import org.springframework.http.ResponseEntity;
// Spring REST controller annotations (already commented elsewhere)
import org.springframework.web.bind.annotation.*;

// Annotation for method-level security (already commented elsewhere)
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews") // All endpoints in this controller start with /api/reviews
public class ReviewController {
    private final ReviewService reviewService;

    
    public ReviewController(ReviewService reviewService) {
        // Inject the review service
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDTO> addReview(@RequestBody ReviewCreateDTO reviewDto) {
        // Add a new review for a ride and reviewer, then return the response DTO
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
        // Find review by id and return as DTO, or 404 if not found
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
        // Return all reviews as a list of DTOs
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
        // Delete review by id
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
