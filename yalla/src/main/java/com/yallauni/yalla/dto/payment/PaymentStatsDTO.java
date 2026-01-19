package com.yallauni.yalla.dto.payment;

import java.math.BigDecimal;

/**
 * DTO for payment statistics (admin dashboard).
 */
public class PaymentStatsDTO {

    private long totalPayments;
    private long pendingPayments;
    private long successfulPayments;
    private long failedPayments;
    private long refundedPayments;

    // Amounts in display format (dollars, not cents)
    private BigDecimal totalRevenue;
    private BigDecimal totalRefunds;
    private BigDecimal netRevenue;

    // Today's stats
    private long paymentsToday;
    private long successfulPaymentsToday;
    private BigDecimal revenueToday;
    private BigDecimal refundsToday;

    // Getters and Setters
    public long getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(long totalPayments) {
        this.totalPayments = totalPayments;
    }

    public long getPendingPayments() {
        return pendingPayments;
    }

    public void setPendingPayments(long pendingPayments) {
        this.pendingPayments = pendingPayments;
    }

    public long getSuccessfulPayments() {
        return successfulPayments;
    }

    public void setSuccessfulPayments(long successfulPayments) {
        this.successfulPayments = successfulPayments;
    }

    public long getFailedPayments() {
        return failedPayments;
    }

    public void setFailedPayments(long failedPayments) {
        this.failedPayments = failedPayments;
    }

    public long getRefundedPayments() {
        return refundedPayments;
    }

    public void setRefundedPayments(long refundedPayments) {
        this.refundedPayments = refundedPayments;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalRefunds() {
        return totalRefunds;
    }

    public void setTotalRefunds(BigDecimal totalRefunds) {
        this.totalRefunds = totalRefunds;
    }

    public BigDecimal getNetRevenue() {
        return netRevenue;
    }

    public void setNetRevenue(BigDecimal netRevenue) {
        this.netRevenue = netRevenue;
    }

    public long getPaymentsToday() {
        return paymentsToday;
    }

    public void setPaymentsToday(long paymentsToday) {
        this.paymentsToday = paymentsToday;
    }

    public long getSuccessfulPaymentsToday() {
        return successfulPaymentsToday;
    }

    public void setSuccessfulPaymentsToday(long successfulPaymentsToday) {
        this.successfulPaymentsToday = successfulPaymentsToday;
    }

    public BigDecimal getRevenueToday() {
        return revenueToday;
    }

    public void setRevenueToday(BigDecimal revenueToday) {
        this.revenueToday = revenueToday;
    }

    public BigDecimal getRefundsToday() {
        return refundsToday;
    }

    public void setRefundsToday(BigDecimal refundsToday) {
        this.refundsToday = refundsToday;
    }
}
