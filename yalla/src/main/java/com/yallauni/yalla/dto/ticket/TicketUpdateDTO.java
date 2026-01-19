package com.yallauni.yalla.dto.ticket;

import com.yallauni.yalla.core.model.SupportTicket.TicketStatus;
import jakarta.validation.constraints.*;

public class TicketUpdateDTO {

    private TicketStatus status;

    @Size(max = 2000, message = "Admin response cannot exceed 2000 characters")
    private String adminResponse;

    // Getters and Setters
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
