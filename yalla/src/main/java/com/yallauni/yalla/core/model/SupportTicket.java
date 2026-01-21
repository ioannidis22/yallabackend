package com.yallauni.yalla.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Entity representing a support ticket created by users.
 */
@Entity
@Table(name = "support_ticket", indexes = {
        @Index(name = "idx_ticket_user", columnList = "user_id"),
        @Index(name = "idx_ticket_status", columnList = "status"),
        @Index(name = "idx_ticket_created_at", columnList = "created_at")
})
public class SupportTicket {

    // Unique identifier for the support ticket
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who created this ticket
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Subject/title of the support request
    @NotNull
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String subject;

    // Detailed message describing the issue
    @NotNull
    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false, length = 2000)
    private String message;

    // Current status of the ticket (PENDING, IN_PROGRESS, RESOLVED, CLOSED)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status = TicketStatus.PENDING;

    // Category of the support request
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketCategory category = TicketCategory.GENERAL;

    // Admin's response to the ticket
    @Size(max = 2000)
    @Column(name = "admin_response", length = 2000)
    private String adminResponse;

    // ID of the admin who responded
    @Column(name = "responded_by")
    private Long respondedBy;

    // Timestamp when ticket was created
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Timestamp when ticket was last updated
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Timestamp when ticket was resolved
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public enum TicketStatus {
        PENDING, // New ticket, waiting for admin
        IN_PROGRESS, // Admin is working on it
        RESOLVED, // Issue resolved
        CLOSED // Ticket closed
    }

    public enum TicketCategory {
        GENERAL, // General inquiry
        RIDE_ISSUE, // Issues with rides
        PAYMENT, // Payment problems
        ACCOUNT, // Account related issues
        DRIVER_REPORT, // Reporting a driver
        BUG_REPORT, // Technical bugs
        FEATURE_REQUEST // Feature suggestions
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }

    public Long getRespondedBy() {
        return respondedBy;
    }

    public void setRespondedBy(Long respondedBy) {
        this.respondedBy = respondedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
