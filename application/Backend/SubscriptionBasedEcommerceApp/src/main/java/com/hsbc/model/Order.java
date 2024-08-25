package com.hsbc.model;

import java.time.LocalDate;


public class Order {
    public enum DeliveryStatus {
        PENDING,
        DELIVERED,
        SHIPPED, CANCELLED
    }

    private int orderId;
    private User user;
    private LocalDate orderDate;
    private double totalPrice;
    private DeliveryStatus deliveryStatus;

    public Order() {
    }

    public Order(int orderId, User user, LocalDate orderDate, double totalPrice, DeliveryStatus deliveryStatus) {
        this.orderId = orderId;
        this.user = user;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.deliveryStatus = deliveryStatus;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", user=" + user +
                ", orderDate=" + orderDate +
                ", totalPrice=" + totalPrice +
                ", deliveryStatus=" + deliveryStatus +
                '}';
    }
}
