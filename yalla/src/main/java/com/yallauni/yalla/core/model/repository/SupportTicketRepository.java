package com.yallauni.yalla.core.model.repository;

import com.yallauni.yalla.core.model.SupportTicket;
import com.yallauni.yalla.core.model.SupportTicket.TicketStatus;
import com.yallauni.yalla.core.model.SupportTicket.TicketCategory;
import com.yallauni.yalla.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    // Find tickets by user
    List<SupportTicket> findByUserOrderByCreatedAtDesc(User user);

    List<SupportTicket> findByUser_UserIDOrderByCreatedAtDesc(Long userId);

    // Find tickets by status
    List<SupportTicket> findByStatusOrderByCreatedAtAsc(TicketStatus status);

    // Find tickets by category
    List<SupportTicket> findByCategoryOrderByCreatedAtDesc(TicketCategory category);

    // Find tickets by status and category
    List<SupportTicket> findByStatusAndCategoryOrderByCreatedAtAsc(TicketStatus status, TicketCategory category);

    // Find pending tickets (oldest first - FIFO queue)
    List<SupportTicket> findByStatusInOrderByCreatedAtAsc(List<TicketStatus> statuses);

    // Find tickets by user and status
    List<SupportTicket> findByUserAndStatusOrderByCreatedAtDesc(User user, TicketStatus status);

    // Count tickets by status
    long countByStatus(TicketStatus status);

    // Count tickets by user
    long countByUser(User user);

    // Find all tickets ordered by created date (newest first for admin view)
    List<SupportTicket> findAllByOrderByCreatedAtDesc();

    // Find tickets created in a date range
    List<SupportTicket> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);

    // Statistics queries for admin
    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE t.status = 'PENDING'")
    long countPendingTickets();

    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE t.status = 'IN_PROGRESS'")
    long countInProgressTickets();

    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE t.status = 'RESOLVED'")
    long countResolvedTickets();

    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE t.createdAt >= :since")
    long countTicketsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE t.resolvedAt >= :since")
    long countResolvedSince(@Param("since") LocalDateTime since);
}
