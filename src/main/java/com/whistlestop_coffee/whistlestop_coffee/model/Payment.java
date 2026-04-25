package model;
import java.time.LocalDateTime;

public class Payment {
    /**
     *  id:Auto-generated unique ID for each payment record (payment_id)
     *  orderId:Display name of the item
     *  paidAt:Timestamp of when the payment was confirmed by HorsePay
     */
    private int id;
    private String orderId;
    private LocalDateTime paidAt;


    //framework, default constructor
    public Payment() {}

    /**
     *Constructor with parameters
     * This is for new payment.
     * without payment_id, it will be auto-generated.
     */
    public Payment(String orderId, LocalDateTime paidAt) {
        this.orderId = orderId;
        this.paidAt = paidAt;
    }

    /**
     * Constructor with all parameters.
     * This is for loading payment detail.
     */

    public Payment(int id, String orderId, LocalDateTime paidAt) {
        this.id = id;
        this.orderId = orderId;
        this.paidAt = paidAt;
    }

    //Getters && Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public LocalDateTime getPaidAt() {
        return paidAt;
    }
    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }


    @Override
    public String toString() {
        return " Payment:" +
                "\n PaymentId=" + id +
                "\n OrderId='" + orderId +
                "\n paidAt=" + paidAt ;
    }
}