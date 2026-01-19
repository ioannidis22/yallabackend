package com.yallauni.yalla.core.model.repository;

import com.yallauni.yalla.core.model.Booking;
import com.yallauni.yalla.core.model.Payment;
import com.yallauni.yalla.core.model.Payment.PaymentStatus;
import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find by Stripe PaymentIntent ID
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    // Find by idempotency key
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    // Find payments by user
    List<Payment> findByUserOrderByCreatedAtDesc(User user);

    // Find payments by user and status
    List<Payment> findByUserAndStatus(User user, PaymentStatus status);

    // Find payments by ride
    List<Payment> findByRide(Ride ride);

    // Find payments by booking
    Optional<Payment> findByBooking(Booking booking);

    // Find payments by status
    List<Payment> findByStatus(PaymentStatus status);

    // Find payments created after a certain date
    List<Payment> findByCreatedAtAfter(LocalDateTime dateTime);

    // Find successful payments for a ride
    List<Payment> findByRideAndStatus(Ride ride, PaymentStatus status);

    // Count payments by status
    long countByStatus(PaymentStatus status);

    // Sum of successful payments for a user
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.user = :user AND p.status = 'SUCCEEDED'")
    Long sumSuccessfulPaymentsByUser(@Param("user") User user);

    // Sum of successful payments for a ride
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.ride = :ride AND p.status = 'SUCCEEDED'")
    Long sumSuccessfulPaymentsByRide(@Param("ride") Ride ride);

    // Find pending payments older than X minutes (for cleanup)
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :cutoff")
    List<Payment> findStalePendingPayments(@Param("cutoff") LocalDateTime cutoff);

    // Statistics queries
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.createdAt >= :since")
    long countPaymentsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'SUCCEEDED' AND p.completedAt >= :since")
    long countSuccessfulPaymentsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCEEDED' AND p.completedAt >= :since")
    Long sumSuccessfulPaymentsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(p.refundAmount), 0) FROM Payment p WHERE p.refundedAt >= :since")
    Long sumRefundsSince(@Param("since") LocalDateTime since);
}
