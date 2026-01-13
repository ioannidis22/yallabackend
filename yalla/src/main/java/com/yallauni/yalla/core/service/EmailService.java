package com.yallauni.yalla.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 */
@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final boolean emailEnabled;

    public EmailService(
            final JavaMailSender mailSender,
            @Value("${app.email.from:noreply@yallauni.com}") final String fromEmail,
            @Value("${app.email.enabled:false}") final boolean emailEnabled) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.emailEnabled = emailEnabled;
    }

    /**
     * Sends a password reset email with the reset code.
     */
    public void sendPasswordResetEmail(final String toEmail, final String userName, final String resetCode) {
        final String subject = "Yalla Uni - Password Reset Code";
        final String body = String.format(
                """
                        Hi %s,

                        You requested to reset your password. Use this code to reset it:

                        %s

                        This code expires in 30 minutes.

                        If you didn't request this, please ignore this email.

                        - The Yalla Uni Team
                        """,
                userName != null ? userName : "there",
                resetCode);

        if (emailEnabled) {
            try {
                final SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(toEmail);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                LOGGER.info("Password reset email sent to: {}", toEmail);
            } catch (Exception e) {
                LOGGER.error("Failed to send password reset email to: {}", toEmail, e);
            }
        } else {
            // Log the code for development/testing
            LOGGER.info("=== PASSWORD RESET CODE (email disabled) ===");
            LOGGER.info("Email: {}", toEmail);
            LOGGER.info("Reset Code: {}", resetCode);
            LOGGER.info("=============================================");
        }
    }
}
