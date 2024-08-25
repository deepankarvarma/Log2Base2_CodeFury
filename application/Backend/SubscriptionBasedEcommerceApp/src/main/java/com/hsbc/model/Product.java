package com.hsbc.model;

public class Product {
    private int productId;
    private String productName;
    private String description;
    private double basePrice;
    private int stock;
    private Category category;

    public Product() {
    }

    public Product(int productId, String productName, String description, double basePrice, int stock, Category category) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.basePrice = basePrice;
        this.stock = stock;
        this.category = category;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", basePrice=" + basePrice +
                ", stock=" + stock +
                ", category=" + category +
                '}';
    }
}