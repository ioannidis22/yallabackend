package com.yallauni.yalla.controller;

// Exception for Stripe API errors
import com.stripe.exception.StripeException;
// Stripe payment intent model
import com.stripe.model.PaymentIntent;
// Service for Stripe logic
import com.yallauni.yalla.core.model.service.StripeService;


// Used for HTTP responses (already commented elsewhere)
import org.springframework.http.ResponseEntity;
// Spring REST controller annotations (already commented elsewhere)
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments") // All endpoints in this controller start with /api/payments
public class PaymentController {

    private final StripeService stripeService;

    
    public PaymentController(StripeService stripeService) {
        // Inject the Stripe service
        this.stripeService = stripeService;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestParam Long amount, @RequestParam String currency) {
        // Create a Stripe payment intent and return the client secret
        try {
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(amount, currency);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("clientSecret", paymentIntent.getClientSecret());
            return ResponseEntity.ok(responseData);
        } catch (StripeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
