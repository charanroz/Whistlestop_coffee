package model;

import java.math.BigDecimal;

public class OrderItem {
    private int id;    // PK: id INT(30)
    private int orderId;    // FK: order_id INT(30)
    private int menuItemId;    // FK: menu_item_id INT(30)
    private String size;    // size VAR(15) DEFAULT 'Regular'
    private int quantity;    // quantity INT(30) DEFAULT 1
    private BigDecimal unitPrice;    // unit_price DECIMAL(5,2)

    /**
     * Used for creating new order items
     * Including Menu item and Quantity
     */
    public OrderItem(int menuItemId, String size, int quantity, BigDecimal unitPrice) {
        this.menuItemId = menuItemId;
        this.size = (size != null) ? size : "Regular";
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getter and Setter methods
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getMenuItemId() { return menuItemId; }
    public void setMenuItemId(int menuItemId) { this.menuItemId = menuItemId; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    /**
     * Calculate the subtotal amount of this individual item
     */
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "menuItemId=" + menuItemId +
                ", size='" + size + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=£" + unitPrice +
                ", subtotal=£" + getSubtotal() +
                '}';
    }
}