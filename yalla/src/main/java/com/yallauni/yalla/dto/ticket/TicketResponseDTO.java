package com.yallauni.yalla.dto.ticket;

import com.yallauni.yalla.core.model.SupportTicket;
import com.yallauni.yalla.core.model.SupportTicket.TicketStatus;
import com.yallauni.yalla.core.model.SupportTicket.TicketCategory;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for returning ticket information in API responses.
 * Includes user info, ticket details, and admin response.
 */
public class TicketResponseDTO {

    // Ticket unique identifier
    private Long id;
    // ID of the user who created the ticket
    private Long userId;
    // Email of the ticket creator
    private String userEmail;
    // Full name of the ticket creator
    private String userName;
    // Subject/title of the ticket
    private String subject;
    // Detailed message content
    private String message;
    // Current ticket status (PENDING, IN_PROGRESS, RESOLVED, CLOSED)
    private TicketStatus status;
    // Ticket category (GENERAL, RIDE_ISSUE, PAYMENT, etc.)
    private TicketCategory category;
    // Admin's response to the ticket
    private String adminResponse;
    // ID of the admin who responded
    private Long respondedBy;
    // Timestamp when ticket was created
    private LocalDateTime createdAt;
    // Timestamp when ticket was last updated
    private LocalDateTime updatedAt;
    // Timestamp when ticket was resolved
    private LocalDateTime resolvedAt;

    // Default constructor
    public TicketResponseDTO() {
    }

    // Μethod to create from entity
    public static TicketResponseDTO fromEntity(SupportTicket ticket) {
        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(ticket.getId());
        dto.setUserId(ticket.getUser().getUserID());
        dto.setUserEmail(ticket.getUser().getEmailAddress());
        dto.setUserName(ticket.getUser().getFirstName() + " " + ticket.getUser().getLastName());
        dto.setSubject(ticket.getSubject());
        dto.setMessage(ticket.getMessage());
        dto.setStatus(ticket.getStatus());
        dto.setCategory(ticket.getCategory());
        dto.setAdminResponse(ticket.getAdminResponse());
        dto.setRespondedBy(ticket.getRespondedBy());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        dto.setResolvedAt(ticket.getResolvedAt());
        return dto;
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
