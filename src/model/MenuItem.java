package model;

public class MenuItem {
    private int id;
    private String name;
    private boolean hasSize;
    private double priceRegular;
    private double priceLarge;
    private boolean isAvailable;

    public MenuItem(int id, String name, boolean hasSize, double priceRegular, double priceLarge) {
        this.id = id;
        this.name = name;
        this.hasSize = hasSize;
        this.priceRegular = priceRegular;
        this.priceLarge = priceLarge;
        this.isAvailable = true; // Default to true as per database logic
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasSize() {
        return hasSize;
    }

    public double getPriceRegular() {
        return priceRegular;
    }

    public double getPriceLarge() {
        return priceLarge;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setPriceRegular(double priceRegular) {
        this.priceRegular = priceRegular;
    }

    public void setPriceLarge(double priceLarge) {
        if (this.hasSize) {
            this.priceLarge = priceLarge;
        }
    }
}