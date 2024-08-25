package com.hsbc.dao;

import com.hsbc.exception.order.OrderAlreadyExistsException;
import com.hsbc.exception.order.OrderNotFoundException;
import com.hsbc.exception.user.UserNotFoundException;
import com.hsbc.model.Order;

import java.time.LocalDate;
import java.util.List;


public interface OrderDao {
    Order findById(int orderId) throws OrderNotFoundException;

    List<Order> findAllOrders();

    void addOrder(Order order) throws OrderAlreadyExistsException;

    void updateOrder(Order order) throws OrderNotFoundException;

    void cancelOrder(int orderId) throws OrderNotFoundException;

    List<Order> findOrdersByUser(int userId) throws UserNotFoundException;

    List<Order> findOrdersByStatus(Order.DeliveryStatus status);

    Order findOrderBySubscriptionAndDate(int subscriptionId, LocalDate date);

     void updateOrderStatus(int orderId, Order.DeliveryStatus status) throws OrderNotFoundException;

    List<Order> findOrdersByDate(LocalDate date);
}