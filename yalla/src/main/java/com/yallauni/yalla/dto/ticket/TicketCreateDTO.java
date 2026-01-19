package com.yallauni.yalla.dto.ticket;

import com.yallauni.yalla.core.model.SupportTicket.TicketCategory;
import jakarta.validation.constraints.*;

public class TicketCreateDTO {

    @NotNull(message = "Subject is required")
    @NotBlank(message = "Subject cannot be blank")
    @Size(min = 5, max = 255, message = "Subject must be between 5 and 255 characters")
    private String subject;

    @NotNull(message = "Message is required")
    @NotBlank(message = "Message cannot be blank")
    @Size(min = 10, max = 2000, message = "Message must be between 10 and 2000 characters")
    private String message;

    private TicketCategory category = TicketCategory.GENERAL;

    // Getters and Setters
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

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }
}
