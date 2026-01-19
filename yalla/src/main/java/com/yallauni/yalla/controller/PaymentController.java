package com.yallauni.yalla.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;

import com.yallauni.yalla.core.model.Booking;
import com.yallauni.yalla.core.model.Payment;
import com.yallauni.yalla.core.model.Payment.PaymentStatus;
import com.yallauni.yalla.core.model.Ride;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.repository.BookingRepository;
import com.yallauni.yalla.core.model.repository.PaymentRepository;
import com.yallauni.yalla.core.model.repository.RideRepository;
import com.yallauni.yalla.core.model.repository.UserRepository;
import com.yallauni.yalla.core.model.service.StripeService;
import com.yallauni.yalla.dto.payment.*;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Comprehensive Payment Controller with Stripe integration.
 * Handles payment creation, webhooks, refunds, and payment history.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final StripeService stripeService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;

    public PaymentController(StripeService stripeService,
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            RideRepository rideRepository,
            BookingRepository bookingRepository) {
        this.stripeService = stripeService;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.bookingRepository = bookingRepository;
    }

    // ==================== PUBLIC ENDPOINTS ====================

    /**
     * Get Stripe publishable key for frontend.
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("publishableKey", stripeService.getPublishableKey());
        config.put("supportedCurrencies", stripeService.getSupportedCurrencies());
        return ResponseEntity.ok(config);
    }

    // ==================== AUTHENTICATED USER ENDPOINTS ====================

    /**
     * Create a new payment intent (requires authentication).
     */
    @PostMapping("/create-payment-intent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createPaymentIntent(
            @Valid @RequestBody PaymentCreateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailAddress(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            // Validate inputs
            stripeService.validateAmount(dto.getAmount());
            stripeService.validateCurrency(dto.getCurrency());

            // Check for duplicate payment using idempotency key
            if (dto.getIdempotencyKey() != null) {
                Optional<Payment> existing = paymentRepository.findByIdempotencyKey(dto.getIdempotencyKey());
                if (existing.isPresent()) {
                    Payment existingPayment = existing.get();
                    if (existingPayment.getStatus() == PaymentStatus.PENDING) {
                        // Return existing payment intent
                        PaymentIntent pi = stripeService
                                .retrievePaymentIntent(existingPayment.getStripePaymentIntentId());
                        return ResponseEntity
                                .ok(PaymentResponseDTO.fromEntityWithSecret(existingPayment, pi.getClientSecret()));
                    }
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Payment already processed with this idempotency key"));
                }
            }

            // Load ride and booking if specified
            Ride ride = null;
            Booking booking = null;

            if (dto.getRideId() != null) {
                ride = rideRepository.findById(dto.getRideId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));
            }

            if (dto.getBookingId() != null) {
                booking = bookingRepository.findById(dto.getBookingId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

                // Verify booking belongs to user
                if (!booking.getPassenger().getUserID().equals(user.getUserID())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Booking does not belong to user");
                }

                // Check if booking already has a successful payment
                Optional<Payment> existingBookingPayment = paymentRepository.findByBooking(booking);
                if (existingBookingPayment.isPresent() &&
                        existingBookingPayment.get().getStatus() == PaymentStatus.SUCCEEDED) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Booking already paid"));
                }

                // Use ride from booking if not specified
                if (ride == null) {
                    ride = booking.getRide();
                }
            }

            // Build metadata for Stripe
            Map<String, String> metadata = new HashMap<>();
            metadata.put("userId", user.getUserID().toString());
            metadata.put("userEmail", user.getEmailAddress());
            if (ride != null) {
                metadata.put("rideId", ride.getRideId().toString());
            }
            if (booking != null) {
                metadata.put("bookingId", booking.getId().toString());
            }

            // Build description
            String description = dto.getDescription();
            if (description == null || description.isBlank()) {
                if (ride != null) {
                    description = "Ride payment: " + ride.getStartingPoint() + " to " + ride.getDestination();
                } else {
                    description = "Yalla payment";
                }
            }

            // Generate idempotency key if not provided
            String idempotencyKey = dto.getIdempotencyKey();
            if (idempotencyKey == null || idempotencyKey.isBlank()) {
                idempotencyKey = UUID.randomUUID().toString();
            }

            // Create Stripe PaymentIntent
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(
                    dto.getAmount(),
                    dto.getCurrency(),
                    description,
                    metadata,
                    idempotencyKey);

            // Save payment record to database
            Payment payment = new Payment();
            payment.setStripePaymentIntentId(paymentIntent.getId());
            payment.setAmount(dto.getAmount());
            payment.setCurrency(dto.getCurrency().toUpperCase());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setUser(user);
            payment.setRide(ride);
            payment.setBooking(booking);
            payment.setDescription(description);
            payment.setIdempotencyKey(idempotencyKey);

            Payment saved = paymentRepository.save(payment);

            logger.info("Payment created: id={}, stripeId={}, user={}",
                    saved.getId(), paymentIntent.getId(), user.getEmailAddress());

            return ResponseEntity.ok(PaymentResponseDTO.fromEntityWithSecret(saved, paymentIntent.getClientSecret()));

        } catch (IllegalArgumentException e) {
            logger.warn("Payment validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (StripeException e) {
            logger.error("Stripe error creating payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Payment service error", "message", e.getMessage()));
        }
    }

    /**
     * Get my payments (authenticated user).
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PaymentResponseDTO>> getMyPayments(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailAddress(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Payment> payments;
        if (status != null && !status.isEmpty()) {
            try {
                PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
                payments = paymentRepository.findByUserAndStatus(user, paymentStatus);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
            }
        } else {
            payments = paymentRepository.findByUserOrderByCreatedAtDesc(user);
        }

        List<PaymentResponseDTO> response = payments.stream()
                .map(PaymentResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific payment by ID (user can only view their own).
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponseDTO> getPayment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailAddress(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        // Check ownership (unless admin)
        boolean isAdmin = user.getUserType() == User.UserType.ADMIN;
        if (!isAdmin && !payment.getUser().getUserID().equals(user.getUserID())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return ResponseEntity.ok(PaymentResponseDTO.fromEntity(payment));
    }

    /**
     * Cancel a pending payment.
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelPayment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailAddress(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        // Check ownership
        if (!payment.getUser().getUserID().equals(user.getUserID())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        // Can only cancel pending payments
        if (payment.getStatus() != PaymentStatus.PENDING) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Only pending payments can be cancelled"));
        }

        try {
            // Cancel on Stripe
            stripeService.cancelPaymentIntent(payment.getStripePaymentIntentId());

            // Update our record
            payment.setStatus(PaymentStatus.CANCELLED);
            paymentRepository.save(payment);

            logger.info("Payment cancelled: id={}", id);
            return ResponseEntity.ok(PaymentResponseDTO.fromEntity(payment));

        } catch (StripeException e) {
            logger.error("Stripe error cancelling payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Failed to cancel payment", "message", e.getMessage()));
        }
    }

    // ==================== WEBHOOK ENDPOINT ====================

    /**
     * Stripe webhook endpoint - handles payment events.
     * This endpoint is NOT authenticated via JWT but uses Stripe signature
     * verification.
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = stripeService.constructWebhookEvent(payload, sigHeader);
        } catch (SignatureVerificationException e) {
            logger.error("Webhook signature verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (IllegalStateException e) {
            logger.error("Webhook configuration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook not configured");
        }

        logger.info("Received Stripe webhook: type={}", event.getType());

        try {
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentSucceeded(event);
                    break;

                case "payment_intent.payment_failed":
                    handlePaymentFailed(event);
                    break;

                case "payment_intent.canceled":
                    handlePaymentCancelled(event);
                    break;

                case "charge.refunded":
                    handleChargeRefunded(event);
                    break;

                default:
                    logger.info("Unhandled webhook event type: {}", event.getType());
            }
        } catch (Exception e) {
            logger.error("Error processing webhook: {}", e.getMessage(), e);
            // Still return 200 to prevent Stripe from retrying
        }

        return ResponseEntity.ok("Received");
    }

    private void handlePaymentSucceeded(Event event) {
        PaymentIntent paymentIntent = stripeService.extractPaymentIntentFromEvent(event);
        if (paymentIntent == null) {
            logger.warn("Could not extract PaymentIntent from event");
            return;
        }

        Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(paymentIntent.getId());
        if (paymentOpt.isEmpty()) {
            logger.warn("Payment not found for PaymentIntent: {}", paymentIntent.getId());
            return;
        }

        Payment payment = paymentOpt.get();
        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setCompletedAt(LocalDateTime.now());

        // Extract charge details if available
        if (paymentIntent.getLatestCharge() != null) {
            payment.setStripeChargeId(paymentIntent.getLatestCharge());
        }

        // Extract payment method details
        if (paymentIntent.getPaymentMethod() != null) {
            try {
                var pm = com.stripe.model.PaymentMethod.retrieve(paymentIntent.getPaymentMethod());
                if (pm.getCard() != null) {
                    payment.setPaymentMethodType("card");
                    payment.setCardBrand(pm.getCard().getBrand());
                    payment.setCardLastFour(pm.getCard().getLast4());
                }
            } catch (StripeException e) {
                logger.warn("Could not retrieve payment method details: {}", e.getMessage());
            }
        }

        paymentRepository.save(payment);
        logger.info("Payment succeeded: id={}, stripeId={}", payment.getId(), paymentIntent.getId());
    }

    private void handlePaymentFailed(Event event) {
        PaymentIntent paymentIntent = stripeService.extractPaymentIntentFromEvent(event);
        if (paymentIntent == null) {
            return;
        }

        Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(paymentIntent.getId());
        if (paymentOpt.isEmpty()) {
            return;
        }

        Payment payment = paymentOpt.get();
        payment.setStatus(PaymentStatus.FAILED);

        // Extract error info
        if (paymentIntent.getLastPaymentError() != null) {
            payment.setErrorMessage(paymentIntent.getLastPaymentError().getMessage());
            payment.setErrorCode(paymentIntent.getLastPaymentError().getCode());
        }

        paymentRepository.save(payment);
        logger.info("Payment failed: id={}, stripeId={}", payment.getId(), paymentIntent.getId());
    }

    private void handlePaymentCancelled(Event event) {
        PaymentIntent paymentIntent = stripeService.extractPaymentIntentFromEvent(event);
        if (paymentIntent == null) {
            return;
        }

        Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(paymentIntent.getId());
        if (paymentOpt.isEmpty()) {
            return;
        }

        Payment payment = paymentOpt.get();
        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);

        logger.info("Payment cancelled: id={}, stripeId={}", payment.getId(), paymentIntent.getId());
    }

    private void handleChargeRefunded(Event event) {
        Charge charge = stripeService.extractChargeFromEvent(event);
        if (charge == null) {
            return;
        }

        String paymentIntentId = charge.getPaymentIntent();
        if (paymentIntentId == null) {
            return;
        }

        Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(paymentIntentId);
        if (paymentOpt.isEmpty()) {
            return;
        }

        Payment payment = paymentOpt.get();
        Long refundedAmount = charge.getAmountRefunded();
        payment.setRefundAmount(refundedAmount);
        payment.setRefundedAt(LocalDateTime.now());

        if (refundedAmount.equals(payment.getAmount())) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }

        paymentRepository.save(payment);
        logger.info("Payment refunded: id={}, refundAmount={}", payment.getId(), refundedAmount);
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * Get all payments (admin only).
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments(
            @RequestParam(required = false) String status) {

        List<Payment> payments;
        if (status != null && !status.isEmpty()) {
            try {
                PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
                payments = paymentRepository.findByStatus(paymentStatus);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
            }
        } else {
            payments = paymentRepository.findAll();
        }

        List<PaymentResponseDTO> response = payments.stream()
                .map(PaymentResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Issue a refund (admin only).
     */
    @PostMapping("/admin/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> issueRefund(@Valid @RequestBody RefundRequestDTO dto) {

        Payment payment = paymentRepository.findById(dto.getPaymentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        // Can only refund successful payments
        if (payment.getStatus() != PaymentStatus.SUCCEEDED &&
                payment.getStatus() != PaymentStatus.PARTIALLY_REFUNDED) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Can only refund successful payments"));
        }

        // Validate refund amount
        Long maxRefundable = payment.getAmount() - (payment.getRefundAmount() != null ? payment.getRefundAmount() : 0);
        Long refundAmount = dto.getAmount() != null ? dto.getAmount() : maxRefundable;

        if (refundAmount > maxRefundable) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Refund amount exceeds maximum refundable: " + maxRefundable));
        }

        try {
            // Create refund on Stripe
            Refund refund = stripeService.createRefund(
                    payment.getStripePaymentIntentId(),
                    dto.getAmount(), // null for full refund
                    dto.getReason());

            // Update payment record
            Long totalRefunded = (payment.getRefundAmount() != null ? payment.getRefundAmount() : 0)
                    + refund.getAmount();
            payment.setRefundAmount(totalRefunded);
            payment.setStripeRefundId(refund.getId());
            payment.setRefundReason(dto.getReason());
            payment.setRefundedAt(LocalDateTime.now());

            if (totalRefunded.equals(payment.getAmount())) {
                payment.setStatus(PaymentStatus.REFUNDED);
            } else {
                payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
            }

            paymentRepository.save(payment);

            logger.info("Refund issued: paymentId={}, refundId={}, amount={}",
                    payment.getId(), refund.getId(), refund.getAmount());

            return ResponseEntity.ok(PaymentResponseDTO.fromEntity(payment));

        } catch (StripeException e) {
            logger.error("Stripe error creating refund: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Refund failed", "message", e.getMessage()));
        }
    }

    /**
     * Get payment statistics (admin only).
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentStatsDTO> getPaymentStats() {
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);

        PaymentStatsDTO stats = new PaymentStatsDTO();
        stats.setTotalPayments(paymentRepository.count());
        stats.setPendingPayments(paymentRepository.countByStatus(PaymentStatus.PENDING));
        stats.setSuccessfulPayments(paymentRepository.countByStatus(PaymentStatus.SUCCEEDED));
        stats.setFailedPayments(paymentRepository.countByStatus(PaymentStatus.FAILED));
        stats.setRefundedPayments(
                paymentRepository.countByStatus(PaymentStatus.REFUNDED) +
                        paymentRepository.countByStatus(PaymentStatus.PARTIALLY_REFUNDED));

        // Calculate revenue (convert cents to dollars)
        Long totalRevenueCents = paymentRepository.sumSuccessfulPaymentsSince(LocalDateTime.MIN);
        Long totalRefundsCents = paymentRepository.sumRefundsSince(LocalDateTime.MIN);

        stats.setTotalRevenue(BigDecimal.valueOf(totalRevenueCents).divide(BigDecimal.valueOf(100)));
        stats.setTotalRefunds(BigDecimal.valueOf(totalRefundsCents).divide(BigDecimal.valueOf(100)));
        stats.setNetRevenue(stats.getTotalRevenue().subtract(stats.getTotalRefunds()));

        // Today's stats
        stats.setPaymentsToday(paymentRepository.countPaymentsSince(todayStart));
        stats.setSuccessfulPaymentsToday(paymentRepository.countSuccessfulPaymentsSince(todayStart));

        Long revenueTodayCents = paymentRepository.sumSuccessfulPaymentsSince(todayStart);
        Long refundsTodayCents = paymentRepository.sumRefundsSince(todayStart);

        stats.setRevenueToday(BigDecimal.valueOf(revenueTodayCents).divide(BigDecimal.valueOf(100)));
        stats.setRefundsToday(BigDecimal.valueOf(refundsTodayCents).divide(BigDecimal.valueOf(100)));

        return ResponseEntity.ok(stats);
    }

    /**
     * Get payments for a specific user (admin only).
     */
    @GetMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Payment> payments = paymentRepository.findByUserOrderByCreatedAtDesc(user);
        List<PaymentResponseDTO> response = payments.stream()
                .map(PaymentResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Get payments for a specific ride (admin only).
     */
    @GetMapping("/admin/ride/{rideId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByRide(@PathVariable Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));

        List<Payment> payments = paymentRepository.findByRide(ride);
        List<PaymentResponseDTO> response = payments.stream()
                .map(PaymentResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }
}
