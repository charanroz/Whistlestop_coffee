package com.whistlestop_coffee.whistlestop_coffee.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;


@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String orderId;
    private LocalDateTime paidAt;
    private LocalDateTime confirmedTime;

    public Payment() {}

    public Payment(String orderId, LocalDateTime paidAt) {
        this.orderId = orderId;
        this.paidAt = paidAt;
        this.confirmedTime = paidAt;
    }

    public Payment(int id, String orderId, LocalDateTime paidAt) {
        this.id = id;
        this.orderId = orderId;
        this.paidAt = paidAt;
        this.confirmedTime = paidAt;
    }
    public LocalDateTime getConfirmedTime() {
        return confirmedTime;
    }

    public void setConfirmedTime(LocalDateTime confirmedTime) {
        this.confirmedTime = confirmedTime;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    @Override
    public String toString() {
        return " Payment:" +
                "\n PaymentId=" + id +
                "\n OrderId='" + orderId +
                "\n paidAt=" + paidAt;
    }
}
