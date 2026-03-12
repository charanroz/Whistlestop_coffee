package service;

import model.Payment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class PaymentManager {
    /**
     * 1.   Used to handle multiple orders
     * 2.   Ensures that each payment_ID is unique
     */
    private List<Payment> orderPaymentTable;
    private int idCounter;


    public PaymentManager() {
        this.orderPaymentTable = new ArrayList<>();
        this.idCounter = 1;
    }

    public Payment processHorsePayPayment(String orderId) {
        //Reject invalid orders
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order Id cannot be empty.");
        }

        //Get current ID, and then increase counter by 1
        LocalDateTime confirmedTime = LocalDateTime.now();
        Payment newPayment = new Payment(idCounter++, orderId, confirmedTime);
        orderPaymentTable.add(newPayment);
        System.out.println("HorsePay payment for: " + orderId + "\n"+"at " + confirmedTime);
        return newPayment;
    }

    public Optional<Payment> getPaymentById(int id) {
        return orderPaymentTable.stream()
                .filter(payment -> payment.getId() == id)
                .findFirst();
    }

    //Defensive Copying
    public List<Payment> getAllPayments() {
        return new ArrayList<>(orderPaymentTable);
    }
}