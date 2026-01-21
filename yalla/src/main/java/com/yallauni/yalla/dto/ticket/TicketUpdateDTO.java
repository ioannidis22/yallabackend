package com.yallauni.yalla.dto.ticket;

import com.yallauni.yalla.core.model.SupportTicket.TicketStatus;
import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for updating a support ticket.
 * Used by admins to update ticket status and add responses.
 */
public class TicketUpdateDTO {
    /** New status for the ticket (PENDING, IN_PROGRESS, RESOLVED, CLOSED) */
    private TicketStatus status;

    /** Admin's response to the ticket (max 2000 characters) */
    @Size(max = 2000, message = "Admin response cannot exceed 2000 characters")
    private String adminResponse;

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }
}
