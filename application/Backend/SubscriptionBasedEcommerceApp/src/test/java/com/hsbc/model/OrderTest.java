package com.hsbc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderTest {

    private Order order;
    private User user;

    @BeforeEach
    public void setUp() {
        // Initialize a User object for testing purposes
        user = new User(); // Assuming a default constructor for User
        // Initialize an Order object before each test
        order = new Order(1, user, LocalDate.of(2024, 8, 24), 100.0, Order.DeliveryStatus.PENDING);
    }

    @Test
    public void testDefaultConstructor() {
        // Test the default constructor
        Order defaultOrder = new Order();
        assertNotNull(defaultOrder);
    }

    @Test
    public void testParameterizedConstructor() {
        // Test the parameterized constructor
        assertEquals(1, order.getOrderId());
        assertEquals(user, order.getUser());
        assertEquals(LocalDate.of(2024, 8, 24), order.getOrderDate());
        assertEquals(100.0, order.getTotalPrice());
        assertEquals(Order.DeliveryStatus.PENDING, order.getDeliveryStatus());
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        User newUser = new User(); // Assuming a default constructor for User
        LocalDate newOrderDate = LocalDate.of(2024, 12, 25);
        double newTotalPrice = 200.0;
        Order.DeliveryStatus newStatus = Order.DeliveryStatus.DELIVERED;

        order.setOrderId(2);
        order.setUser(newUser);
        order.setOrderDate(newOrderDate);
        order.setTotalPrice(newTotalPrice);
        order.setDeliveryStatus(newStatus);

        assertEquals(2, order.getOrderId());
        assertEquals(newUser, order.getUser());
        assertEquals(newOrderDate, order.getOrderDate());
        assertEquals(newTotalPrice, order.getTotalPrice());
        assertEquals(newStatus, order.getDeliveryStatus());
    }

    @Test
    public void testToString() {
        // Test the toString method
        String expectedString = "Order{" +
                "orderId=1, " +
                "user=" + user +
                ", orderDate=2024-08-24, " +
                "totalPrice=100.0, " +
                "deliveryStatus=PENDING" +
                '}';
        assertEquals(expectedString, order.toString());
    }
}
