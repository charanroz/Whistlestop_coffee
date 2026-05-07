package com.whistlestop_coffee.whistlestop_coffee.controller;

import com.whistlestop_coffee.whistlestop_coffee.dto.PaymentResponse;
import com.whistlestop_coffee.whistlestop_coffee.model.Payment;
import com.whistlestop_coffee.whistlestop_coffee.service.PaymentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Mock payment gateway controller.
 */
@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PaymentController {

    @Autowired
    private PaymentManager paymentManager;

    /**
     * Simulates payment processing.
     * Maps the internal Payment model to a PaymentResponse DTO for frontend safety.
     */
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestParam String orderId) {
        try {
            // call  Manager
            Payment payment = paymentManager.processHorsePayPayment(orderId);

            //  Model transfers to DTO
            PaymentResponse responseDto = new PaymentResponse(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getConfirmedTime(),
                    "SUCCESS"
            );

            //  DTO to front
            return ResponseEntity.ok(responseDto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Payment failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("System error, please try again later.");
        }
    }
}