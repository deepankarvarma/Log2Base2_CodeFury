package com.hsbc.service;

import com.hsbc.dao.OrderDao;

import com.hsbc.dao.OrderItemDao;
import com.hsbc.dao.ProductDao;
import com.hsbc.exception.order.InsufficientStockException;
import com.hsbc.exception.order.OrderAlreadyExistsException;
import com.hsbc.exception.order.OrderNotFoundException;
import com.hsbc.exception.user.UserNotFoundException;
import com.hsbc.factory.DaoFactory;
import com.hsbc.model.Order;
import com.hsbc.model.OrderItem;

import java.time.LocalDate;
import java.util.List;

public class OrderService {

    private OrderDao orderDAO;
    private OrderItemDao orderItemDAO;
    private ProductDao productDAO;

    public OrderService() {
        this.orderDAO = DaoFactory.getOrderDao();
        this.orderItemDAO = DaoFactory.getOrderItemDao();
        this.productDAO = DaoFactory.getProductDao();
    }

    // Constructor for test use
    public OrderService(OrderDao orderDAO, OrderItemDao orderItemDAO, ProductDao productDAO) {
        this.orderDAO = orderDAO;
        this.orderItemDAO = orderItemDAO;
        this.productDAO = productDAO;
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

    public Order findOrderBySubscriptionAndDate(int subscriptionId, LocalDate date) {
        return orderDAO.findOrderBySubscriptionAndDate(subscriptionId, date);
    }

    public List<Order> findOrdersByDate(LocalDate date) {
        return orderDAO.findOrdersByDate(date);
    }

    public void markOrderAsDelivered(int orderId) throws OrderNotFoundException, InsufficientStockException {
        Order order = orderDAO.findById(orderId);
        if (order.getDeliveryStatus() == Order.DeliveryStatus.PENDING) {
            List<OrderItem> items = orderItemDAO.findOrderItemsByOrder(orderId);
            for (OrderItem item : items) {
                productDAO.decreaseProductStock(item.getProduct().getProductId(), item.getQuantity());
            }
            orderDAO.updateOrderStatus(orderId, Order.DeliveryStatus.DELIVERED);
        } else {
            throw new IllegalStateException("Order is not in a 'PENDING' state.");
        }
    }

    public void updateOrderStatus(int orderId, Order.DeliveryStatus status) throws OrderNotFoundException {
        orderDAO.updateOrderStatus(orderId, status);
    }
}