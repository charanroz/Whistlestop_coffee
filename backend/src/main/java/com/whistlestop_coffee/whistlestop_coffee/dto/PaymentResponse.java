package com.whistlestop_coffee.whistlestop_coffee.dto;

import java.time.LocalDateTime;

public class PaymentResponse {

    private int paymentId;
    private String orderId;
    private LocalDateTime confirmedTime;
    private String paymentStatus;

    // conduct
    public PaymentResponse() {}

    public PaymentResponse(int paymentId, String orderId, LocalDateTime confirmedTime, String paymentStatus) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.confirmedTime = confirmedTime;
        this.paymentStatus = paymentStatus;
    }

    //  Getter && Setter
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getConfirmedTime() {
        return confirmedTime;
    }

    public void setConfirmedTime(LocalDateTime confirmedTime) {
        this.confirmedTime = confirmedTime;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}