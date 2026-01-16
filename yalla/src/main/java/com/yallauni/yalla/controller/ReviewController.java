package com.yallauni.yalla.controller;

import com.yallauni.yalla.core.model.Review;
import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.service.ReviewService;
import com.yallauni.yalla.core.model.service.RideService;
import com.yallauni.yalla.core.model.repository.UserRepository;
import com.yallauni.yalla.dto.review.ReviewCreateDTO;
import com.yallauni.yalla.dto.review.ReviewResponseDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final RideService rideService;
    private final UserRepository userRepository;

    public ReviewController(ReviewService reviewService, RideService rideService, UserRepository userRepository) {
        this.reviewService = reviewService;
        this.rideService = rideService;
        this.userRepository = userRepository;
    }

    // Add review - reviewer is the authenticated user, must be involved in the ride
    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addReview(@RequestBody ReviewCreateDTO reviewDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User reviewer = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (reviewer == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        try {
            // Verify the ride exists
            Optional<Ride> rideOpt = rideService.findById(reviewDto.getRideId());
            if (rideOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Ride not found");
            }

            Ride ride = rideOpt.get();

            // Verify user was involved in the ride (driver or passenger)
            boolean isAdmin = reviewer.getUserType() == User.UserType.ADMIN;
            boolean isDriver = ride.getDriver() != null && ride.getDriver().getUserID().equals(reviewer.getUserID());
            boolean isPassenger = ride.getPassengers() != null && ride.getPassengers().stream()
                    .anyMatch(p -> p.getUserID().equals(reviewer.getUserID()));

            if (!isDriver && !isPassenger && !isAdmin) {
                return ResponseEntity.status(403).body("Access denied: You can only review rides you participated in");
            }

            Review review = new Review();
            review.setRating(reviewDto.getRating());
            review.setComment(reviewDto.getComment());

            Review saved = reviewService.addReview(review, ride, reviewer);
            return ResponseEntity.ok(mapToDto(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get my reviews - reviews written by or about the authenticated user
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyReviews(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        List<ReviewResponseDTO> myReviews = reviewService.findAll().stream()
                .filter(r -> r.getReviewer() != null && r.getReviewer().getUserID().equals(currentUser.getUserID()))
                .map(this::mapToDto)
                .toList();

        return ResponseEntity.ok(myReviews);
    }

    // Get review by ID - only reviewer or admin
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getReviewById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Optional<Review> reviewOpt = reviewService.findById(id);
        if (reviewOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Review review = reviewOpt.get();
        boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
        boolean isReviewer = review.getReviewer() != null
                && review.getReviewer().getUserID().equals(currentUser.getUserID());

        // Also allow if user was driver of the reviewed ride
        boolean isRideDriver = review.getRide() != null &&
                review.getRide().getDriver() != null &&
                review.getRide().getDriver().getUserID().equals(currentUser.getUserID());

        if (!isReviewer && !isRideDriver && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: You can only view your own reviews");
        }

        return ResponseEntity.ok(mapToDto(review));
    }

    // Get all reviews - admin only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReviewResponseDTO> getAllReviews() {
        return reviewService.findAll().stream().map(this::mapToDto).toList();
    }

    // Delete review - only reviewer or admin
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReview(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        Optional<Review> reviewOpt = reviewService.findById(id);
        if (reviewOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Review review = reviewOpt.get();
        boolean isAdmin = currentUser.getUserType() == User.UserType.ADMIN;
        boolean isReviewer = review.getReviewer() != null
                && review.getReviewer().getUserID().equals(currentUser.getUserID());

        if (!isReviewer && !isAdmin) {
            return ResponseEntity.status(403).body("Access denied: You can only delete your own reviews");
        }

        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    private ReviewResponseDTO mapToDto(Review r) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(r.getId());
        dto.setRideId(r.getRide() != null ? r.getRide().getRideId() : null);
        dto.setReviewerId(r.getReviewer() != null ? r.getReviewer().getUserID() : null);
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        return dto;
    }
}
