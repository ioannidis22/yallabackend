package com.yallauni.yalla.dto.ticket;

/**
 * Data Transfer Object for support ticket statistics.
 * Contains aggregated metrics about tickets in the system.
 * Used by admin dashboard to display ticket analytics.
 */
public class TicketStatsDTO {
    /** Total number of tickets in the system */
    private long totalTickets;
    /** Number of tickets awaiting initial response */
    private long pendingTickets;
    /** Number of tickets currently being worked on */
    private long inProgressTickets;
    /** Number of tickets that have been resolved */
    private long resolvedTickets;
    /** Number of tickets that have been closed */
    private long closedTickets;
    /** Number of tickets created today */
    private long ticketsToday;
    /** Number of tickets resolved today */
    private long resolvedToday;

   
    public long getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(long totalTickets) {
        this.totalTickets = totalTickets;
    }

    public long getPendingTickets() {
        return pendingTickets;
    }

    public void setPendingTickets(long pendingTickets) {
        this.pendingTickets = pendingTickets;
    }

    public long getInProgressTickets() {
        return inProgressTickets;
    }

    public void setInProgressTickets(long inProgressTickets) {
        this.inProgressTickets = inProgressTickets;
    }

    public long getResolvedTickets() {
        return resolvedTickets;
    }

    public void setResolvedTickets(long resolvedTickets) {
        this.resolvedTickets = resolvedTickets;
    }

    public long getClosedTickets() {
        return closedTickets;
    }

    public void setClosedTickets(long closedTickets) {
        this.closedTickets = closedTickets;
    }

    public long getTicketsToday() {
        return ticketsToday;
    }

    public void setTicketsToday(long ticketsToday) {
        this.ticketsToday = ticketsToday;
    }

    public long getResolvedToday() {
        return resolvedToday;
    }

    public void setResolvedToday(long resolvedToday) {
        this.resolvedToday = resolvedToday;
    }
}
