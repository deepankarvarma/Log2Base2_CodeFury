package com.hsbc.exception.order;

public class OrderItemNotFoundException extends Exception {
    public OrderItemNotFoundException(String message) {
        super(message);
    }
}
