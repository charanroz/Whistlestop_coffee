package model;

public class Order {
    private int id;
    private String coffeeItem;
    private int quantity;
    private String pickupTime;
    private String status;
    private String cancelReason;

    public Order(int id, String coffeeItem, int quantity, String pickupTime) {
        this.id = id;
        this.coffeeItem = coffeeItem;
        this.quantity = quantity;
        this.pickupTime = pickupTime;
        this.status = "Pending";

    }

    public int getId() {
        return id;
    }

    public String getCoffeeItem() {
        return coffeeItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPickupTime() {
        return pickupTime;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getCancelReason() {
        return cancelReason;
    }
    public void setCancelReason(String reason) {
        this.cancelReason = reason;
    }

}
