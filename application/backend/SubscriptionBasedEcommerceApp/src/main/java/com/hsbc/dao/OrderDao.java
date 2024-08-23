package com.hsbc.dao;

import com.hsbc.exception.OrderAlreadyExistsException;
import com.hsbc.exception.OrderNotFoundException;
import com.hsbc.exception.UserNotFoundException;
import com.hsbc.model.Order;

import java.util.List;


public interface OrderDao {
    Order findById(int orderId) throws OrderNotFoundException;

    List<Order> findAllOrders();

    void addOrder(Order order) throws OrderAlreadyExistsException;

    void updateOrder(Order order) throws OrderNotFoundException;

    void cancelOrder(int orderId) throws OrderNotFoundException;

    List<Order> findOrdersByUser(int userId) throws UserNotFoundException;

    List<Order> findOrdersByStatus(Order.DeliveryStatus status);
}