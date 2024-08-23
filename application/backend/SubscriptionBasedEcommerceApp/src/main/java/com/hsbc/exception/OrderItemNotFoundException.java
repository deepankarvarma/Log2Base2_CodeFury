package com.hsbc.exception;

public class OrderItemNotFoundException extends Exception {
    public OrderItemNotFoundException(String message) {
        super(message);
    }
}
