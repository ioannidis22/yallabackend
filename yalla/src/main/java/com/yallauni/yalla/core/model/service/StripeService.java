package com.yallauni.yalla.core.model.service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Enhanced Stripe Service with comprehensive payment operations.
 */
@Service
public class StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

    // Supported currencies
    private static final List<String> SUPPORTED_CURRENCIES = Arrays.asList(
            "USD", "EUR", "GBP", "CAD", "AUD", "JPY", "CHF", "INR", "AED", "SAR");

    // Payment limits (in cents)
    private static final long MIN_AMOUNT = 50; // 50 cents minimum
    private static final long MAX_AMOUNT = 99999999; // $999,999.99 maximum

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    @Value("${stripe.publishable.key:}")
    private String publishableKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
        logger.info("Stripe API initialized");
    }

    /**
     * Get the publishable key for frontend use.
     */
    public String getPublishableKey() {
        return publishableKey;
    }

    /**
     * Check if webhook verification is enabled.
     */
    public boolean isWebhookVerificationEnabled() {
        return webhookSecret != null && !webhookSecret.isEmpty();
    }

    /**
     * Validate payment amount.
     */
    public void validateAmount(Long amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        if (amount < MIN_AMOUNT) {
            throw new IllegalArgumentException("Minimum payment amount is " + MIN_AMOUNT + " cents");
        }
        if (amount > MAX_AMOUNT) {
            throw new IllegalArgumentException("Maximum payment amount is " + MAX_AMOUNT + " cents");
        }
    }

    /**
     * Validate currency code.
     */
    public void validateCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
        String upperCurrency = currency.toUpperCase();
        if (!SUPPORTED_CURRENCIES.contains(upperCurrency)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency +
                    ". Supported: " + String.join(", ", SUPPORTED_CURRENCIES));
        }
    }

    /**
     * Create a payment intent with validation and metadata.
     */
    public PaymentIntent createPaymentIntent(Long amount, String currency, String description,
            Map<String, String> metadata, String idempotencyKey) throws StripeException {

        validateAmount(amount);
        validateCurrency(currency);

        logger.info("Creating PaymentIntent: amount={}, currency={}", amount, currency.toUpperCase());

        PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency.toLowerCase())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build());

        if (description != null && !description.isBlank()) {
            builder.setDescription(description);
        }

        if (metadata != null && !metadata.isEmpty()) {
            builder.putAllMetadata(metadata);
        }

        PaymentIntentCreateParams params = builder.build();

        PaymentIntent paymentIntent;
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            paymentIntent = PaymentIntent.create(params,
                    com.stripe.net.RequestOptions.builder()
                            .setIdempotencyKey(idempotencyKey)
                            .build());
        } else {
            paymentIntent = PaymentIntent.create(params);
        }

        logger.info("PaymentIntent created: id={}", paymentIntent.getId());
        return paymentIntent;
    }

    /**
     * Simple payment intent creation (backward compatible).
     */
    public PaymentIntent createPaymentIntent(Long amount, String currency) throws StripeException {
        return createPaymentIntent(amount, currency, null, null, null);
    }

    /**
     * Retrieve a payment intent by ID.
     */
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        logger.info("Retrieving PaymentIntent: id={}", paymentIntentId);
        return PaymentIntent.retrieve(paymentIntentId);
    }

    /**
     * Cancel a payment intent.
     */
    public PaymentIntent cancelPaymentIntent(String paymentIntentId) throws StripeException {
        logger.info("Cancelling PaymentIntent: id={}", paymentIntentId);
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return paymentIntent.cancel();
    }

    /**
     * Create a refund for a payment.
     */
    public Refund createRefund(String paymentIntentId, Long amount, String reason) throws StripeException {
        logger.info("Creating refund: paymentIntentId={}, amount={}, reason={}",
                paymentIntentId, amount, reason);

        RefundCreateParams.Builder builder = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId);

        if (amount != null) {
            builder.setAmount(amount); // Partial refund
        }
        // Full refund if amount is null

        if (reason != null && !reason.isBlank()) {
            // Map to Stripe refund reason
            RefundCreateParams.Reason stripeReason;
            switch (reason.toLowerCase()) {
                case "duplicate":
                    stripeReason = RefundCreateParams.Reason.DUPLICATE;
                    break;
                case "fraudulent":
                    stripeReason = RefundCreateParams.Reason.FRAUDULENT;
                    break;
                default:
                    stripeReason = RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER;
            }
            builder.setReason(stripeReason);
        }

        RefundCreateParams params = builder.build();
        Refund refund = Refund.create(params);

        logger.info("Refund created: id={}, amount={}", refund.getId(), refund.getAmount());
        return refund;
    }

    /**
     * Create a full refund.
     */
    public Refund createFullRefund(String paymentIntentId, String reason) throws StripeException {
        return createRefund(paymentIntentId, null, reason);
    }

    /**
     * Retrieve a refund by ID.
     */
    public Refund retrieveRefund(String refundId) throws StripeException {
        logger.info("Retrieving Refund: id={}", refundId);
        return Refund.retrieve(refundId);
    }

    /**
     * Verify webhook signature and construct event.
     */
    public Event constructWebhookEvent(String payload, String sigHeader) throws SignatureVerificationException {
        if (!isWebhookVerificationEnabled()) {
            throw new IllegalStateException("Webhook secret not configured");
        }

        logger.info("Verifying webhook signature");
        return Webhook.constructEvent(payload, sigHeader, webhookSecret);
    }

    /**
     * Extract PaymentIntent from webhook event.
     */
    public PaymentIntent extractPaymentIntentFromEvent(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        if (deserializer.getObject().isPresent()) {
            StripeObject stripeObject = deserializer.getObject().get();
            if (stripeObject instanceof PaymentIntent) {
                return (PaymentIntent) stripeObject;
            }
        }
        return null;
    }

    /**
     * Extract Charge from webhook event.
     */
    public Charge extractChargeFromEvent(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        if (deserializer.getObject().isPresent()) {
            StripeObject stripeObject = deserializer.getObject().get();
            if (stripeObject instanceof Charge) {
                return (Charge) stripeObject;
            }
        }
        return null;
    }

    /**
     * Check if a currency is supported.
     */
    public boolean isCurrencySupported(String currency) {
        return currency != null && SUPPORTED_CURRENCIES.contains(currency.toUpperCase());
    }

    /**
     * Get list of supported currencies.
     */
    public List<String> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }
}
