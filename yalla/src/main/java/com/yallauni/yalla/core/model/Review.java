// Review entity for ride ratings (student-style comment)
package com.yallauni.yalla.core.model;

import jakarta.persistence.*; // JPA annotations
import jakarta.validation.constraints.*; // validation constraints

@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @DecimalMin("1.0")
    @DecimalMax("5.0")
    @Column(nullable = false)
    private double rating;

    @Size(max = 500)
    private String comment;

    // Typical getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}