package com.hsbc.service;

import com.hsbc.dao.OrderDao;

import com.hsbc.exception.*;
import com.hsbc.factory.DaoFactory;
import com.hsbc.model.Order;

import java.util.List;

public class OrderService {

    private OrderDao orderDAO;

    public OrderService() {
        this.orderDAO = DaoFactory.getOrderDao();
    }

    public Order getOrderById(int orderId) throws OrderNotFoundException {
        return orderDAO.findById(orderId);
    }

    public List<Order> getAllOrders() {
        return orderDAO.findAllOrders();
    }

    public void createOrder(Order order) throws OrderAlreadyExistsException {
        orderDAO.addOrder(order);
    }

    public void updateOrder(Order order) throws OrderNotFoundException {
        orderDAO.updateOrder(order);
    }

    public void cancelOrder(int orderId) throws OrderNotFoundException {
        orderDAO.cancelOrder(orderId);
    }

    public List<Order> findOrdersByUser(int userId) throws UserNotFoundException {
        return orderDAO.findOrdersByUser(userId);
    }

    public List<Order> findOrdersByStatus(Order.DeliveryStatus status) {
        return orderDAO.findOrdersByStatus(status);
    }
}