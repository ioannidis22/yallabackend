package com.yallauni.yalla.controller;

import com.yallauni.yalla.core.model.SupportTicket;
import com.yallauni.yalla.core.model.SupportTicket.TicketStatus;
import com.yallauni.yalla.core.model.SupportTicket.TicketCategory;
import com.yallauni.yalla.core.model.User;
import com.yallauni.yalla.core.model.repository.SupportTicketRepository;
import com.yallauni.yalla.core.model.repository.UserRepository;
import com.yallauni.yalla.dto.ticket.TicketCreateDTO;
import com.yallauni.yalla.dto.ticket.TicketResponseDTO;
import com.yallauni.yalla.dto.ticket.TicketUpdateDTO;
import com.yallauni.yalla.dto.ticket.TicketStatsDTO;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
public class SupportTicketController {

    private final SupportTicketRepository ticketRepository;
    private final UserRepository userRepository;

    public SupportTicketController(SupportTicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    // ==================== USER ENDPOINTS ====================

    /**
     * Create a new support ticket
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TicketResponseDTO> createTicket(
            @Valid @RequestBody TicketCreateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailAddress(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        SupportTicket ticket = new SupportTicket();
        ticket.setUser(user);
        ticket.setSubject(dto.getSubject());
        ticket.setMessage(dto.getMessage());
        ticket.setCategory(dto.getCategory() != null ? dto.getCategory() : TicketCategory.GENERAL);
        ticket.setStatus(TicketStatus.PENDING);
        ticket.setCreatedAt(LocalDateTime.now());

        SupportTicket saved = ticketRepository.save(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketResponseDTO.fromEntity(saved));
    }

    /**
     * Get current user's tickets
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TicketResponseDTO>> getMyTickets(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailAddress(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<TicketResponseDTO> tickets = ticketRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(TicketResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tickets);
    }

    /**
     * Get a specific ticket by ID (user can only view their own tickets)
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TicketResponseDTO> getTicket(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailAddress(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        // Users can only view their own tickets unless they're admin
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !ticket.getUser().getUserID().equals(user.getUserID())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return ResponseEntity.ok(TicketResponseDTO.fromEntity(ticket));
    }

    /**
     * Cancel/close a ticket (user can only cancel their own pending tickets)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> cancelTicket(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmailAddress(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        if (!ticket.getUser().getUserID().equals(user.getUserID())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only cancel your own tickets");
        }

        if (ticket.getStatus() == TicketStatus.RESOLVED || ticket.getStatus() == TicketStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot cancel a ticket that is already resolved or closed");
        }

        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        return ResponseEntity.ok(Map.of("message", "Ticket cancelled successfully"));
    }

    /**
     * Get available ticket categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<TicketCategory>> getCategories() {
        return ResponseEntity.ok(Arrays.asList(TicketCategory.values()));
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * Get all tickets (admin only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets() {
        List<TicketResponseDTO> tickets = ticketRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(TicketResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tickets);
    }

    /**
     * Get tickets by status (admin only)
     */
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByStatus(@PathVariable TicketStatus status) {
        List<TicketResponseDTO> tickets = ticketRepository.findByStatusOrderByCreatedAtAsc(status)
                .stream()
                .map(TicketResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tickets);
    }

    /**
     * Get pending and in-progress tickets (admin queue)
     */
    @GetMapping("/admin/queue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TicketResponseDTO>> getTicketQueue() {
        List<TicketResponseDTO> tickets = ticketRepository.findByStatusInOrderByCreatedAtAsc(
                Arrays.asList(TicketStatus.PENDING, TicketStatus.IN_PROGRESS))
                .stream()
                .map(TicketResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tickets);
    }

    /**
     * Get tickets by category (admin only)
     */
    @GetMapping("/admin/category/{category}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByCategory(@PathVariable TicketCategory category) {
        List<TicketResponseDTO> tickets = ticketRepository.findByCategoryOrderByCreatedAtDesc(category)
                .stream()
                .map(TicketResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tickets);
    }

    /**
     * Update ticket status (admin only)
     */
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponseDTO> updateTicketStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        User admin = userRepository.findByEmailAddress(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        String statusStr = body.get("status");
        if (statusStr == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        try {
            TicketStatus newStatus = TicketStatus.valueOf(statusStr.toUpperCase());
            ticket.setStatus(newStatus);
            ticket.setUpdatedAt(LocalDateTime.now());

            if (newStatus == TicketStatus.RESOLVED) {
                ticket.setResolvedAt(LocalDateTime.now());
                ticket.setRespondedBy(admin.getUserID());
            }

            SupportTicket updated = ticketRepository.save(ticket);
            return ResponseEntity.ok(TicketResponseDTO.fromEntity(updated));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + statusStr);
        }
    }

    /**
     * Respond to a ticket (admin only)
     */
    @PutMapping("/admin/{id}/respond")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponseDTO> respondToTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketUpdateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User admin = userRepository.findByEmailAddress(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        if (dto.getAdminResponse() != null && !dto.getAdminResponse().isBlank()) {
            ticket.setAdminResponse(dto.getAdminResponse());
            ticket.setRespondedBy(admin.getUserID());
        }

        if (dto.getStatus() != null) {
            ticket.setStatus(dto.getStatus());
            if (dto.getStatus() == TicketStatus.RESOLVED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        } else {
            // Auto-set to IN_PROGRESS if admin responds without setting status
            if (ticket.getStatus() == TicketStatus.PENDING) {
                ticket.setStatus(TicketStatus.IN_PROGRESS);
            }
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        SupportTicket updated = ticketRepository.save(ticket);
        return ResponseEntity.ok(TicketResponseDTO.fromEntity(updated));
    }

    /**
     * Resolve a ticket with response (admin convenience endpoint)
     */
    @PutMapping("/admin/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponseDTO> resolveTicket(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        User admin = userRepository.findByEmailAddress(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        String response = body.get("response");
        if (response != null && !response.isBlank()) {
            ticket.setAdminResponse(response);
        }

        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setRespondedBy(admin.getUserID());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setResolvedAt(LocalDateTime.now());

        SupportTicket updated = ticketRepository.save(ticket);
        return ResponseEntity.ok(TicketResponseDTO.fromEntity(updated));
    }

    /**
     * Get ticket statistics (admin only)
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketStatsDTO> getTicketStats() {
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);

        TicketStatsDTO stats = new TicketStatsDTO();
        stats.setTotalTickets(ticketRepository.count());
        stats.setPendingTickets(ticketRepository.countByStatus(TicketStatus.PENDING));
        stats.setInProgressTickets(ticketRepository.countByStatus(TicketStatus.IN_PROGRESS));
        stats.setResolvedTickets(ticketRepository.countByStatus(TicketStatus.RESOLVED));
        stats.setClosedTickets(ticketRepository.countByStatus(TicketStatus.CLOSED));
        stats.setTicketsToday(ticketRepository.countTicketsSince(todayStart));
        stats.setResolvedToday(ticketRepository.countResolvedSince(todayStart));

        return ResponseEntity.ok(stats);
    }

    /**
     * Delete a ticket (admin only - for cleanup)
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteTicket(@PathVariable Long id) {
        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        ticketRepository.delete(ticket);
        return ResponseEntity.ok(Map.of("message", "Ticket deleted successfully"));
    }

    /**
     * Get tickets for a specific user (admin only)
     */
    @GetMapping("/admin/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByUser(@PathVariable Long userId) {
        List<TicketResponseDTO> tickets = ticketRepository.findByUser_UserIDOrderByCreatedAtDesc(userId)
                .stream()
                .map(TicketResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tickets);
    }
}
