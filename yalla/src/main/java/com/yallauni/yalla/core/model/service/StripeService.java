package com.yallauni.yalla.core.model.service;

// Stripe SDK main class
import com.stripe.Stripe;
// Exception for Stripe API errors (already commented elsewhere)
import com.stripe.exception.StripeException;
// Stripe payment intent model (already commented elsewhere)
import com.stripe.model.PaymentIntent;
// Params builder for payment intent
import com.stripe.param.PaymentIntentCreateParams;
// Injects property value from config
import org.springframework.beans.factory.annotation.Value;
// Marks this class as a Spring service (already commented elsewhere)
import org.springframework.stereotype.Service;

// For running code after bean construction
import jakarta.annotation.PostConstruct;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        // Set Stripe API key after bean is constructed
        Stripe.apiKey = secretKey;
    }

    public PaymentIntent createPaymentIntent(Long amount, String currency) throws StripeException {
        // Create a Stripe payment intent with the given amount and currency
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .build();
        return PaymentIntent.create(params);
    }
}
