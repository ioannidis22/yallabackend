package com.yallauni.yalla.dto.ticket;

public class TicketStatsDTO {

    private long totalTickets;
    private long pendingTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private long closedTickets;
    private long ticketsToday;
    private long resolvedToday;

    // Getters and Setters
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
