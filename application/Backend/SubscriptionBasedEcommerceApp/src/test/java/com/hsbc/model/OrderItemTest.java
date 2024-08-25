package com.hsbc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderItemTest {

    private OrderItem orderItem;
    private Order order;
    private Product product;

    @BeforeEach
    public void setUp() {
        // Initialize Order and Product objects for testing purposes
        order = new Order(); // Assuming a default constructor for Order
        product = new Product(); // Assuming a default constructor for Product
        // Initialize an OrderItem object before each test
        orderItem = new OrderItem(1, order, product, 5, 50.0);
    }

    @Test
    public void testDefaultConstructor() {
        // Test the default constructor
        OrderItem defaultOrderItem = new OrderItem();
        assertNotNull(defaultOrderItem);
    }

    @Test
    public void testParameterizedConstructor() {
        // Test the parameterized constructor
        assertEquals(1, orderItem.getOrderItemId());
        assertEquals(order, orderItem.getOrder());
        assertEquals(product, orderItem.getProduct());
        assertEquals(5, orderItem.getQuantity());
        assertEquals(50.0, orderItem.getPrice());
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        Order newOrder = new Order(); // Assuming a default constructor for Order
        Product newProduct = new Product(); // Assuming a default constructor for Product
        int newQuantity = 10;
        double newPrice = 100.0;

        orderItem.setOrderItemId(2);
        orderItem.setOrder(newOrder);
        orderItem.setProduct(newProduct);
        orderItem.setQuantity(newQuantity);
        orderItem.setPrice(newPrice);

        assertEquals(2, orderItem.getOrderItemId());
        assertEquals(newOrder, orderItem.getOrder());
        assertEquals(newProduct, orderItem.getProduct());
        assertEquals(newQuantity, orderItem.getQuantity());
        assertEquals(newPrice, orderItem.getPrice());
    }

    @Test
    public void testToString() {
        // Test the toString method
        String expectedString = "OrderItem{" +
                "orderItemId=" + 1 +
                ", order=" + order +
                ", product=" + product +
                ", quantity=" + 5 +
                ", price=" + 50.0 +
                '}';
        assertEquals(expectedString, orderItem.toString());
    }
}
