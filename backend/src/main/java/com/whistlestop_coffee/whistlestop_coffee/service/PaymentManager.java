package com.whistlestop_coffee.whistlestop_coffee.service;
import com.whistlestop_coffee.whistlestop_coffee.model.Payment;
import com.whistlestop_coffee.whistlestop_coffee.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentManager {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment processHorsePayPayment(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order Id cannot be empty.");
        }

        LocalDateTime ConfirmedTime = LocalDateTime.now();
        Payment newPayment = new Payment(orderId, ConfirmedTime);
        return paymentRepository.save(newPayment);
    }

    public Optional<Payment> getPaymentById(int id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}