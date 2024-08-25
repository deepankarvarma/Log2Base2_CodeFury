package com.hsbc.service;

import com.hsbc.dao.OrderItemDao;
import com.hsbc.exception.order.OrderItemNotFoundException;
import com.hsbc.factory.DaoFactory;
import com.hsbc.model.OrderItem;

import java.util.List;

public class OrderItemService {

    private OrderItemDao orderItemDAO;

    public OrderItemService() {
        this.orderItemDAO = DaoFactory.getOrderItemDao();
    }
    // Constructor for test use
    public OrderItemService(OrderItemDao orderItemDAO) {
        this.orderItemDAO = orderItemDAO;
    }

    public OrderItem getOrderItemById(int orderItemId) throws OrderItemNotFoundException {
        return orderItemDAO.findById(orderItemId);
    }

    public List<OrderItem> getAllOrderItems() {
        return orderItemDAO.findAllOrderItems();
    }

    public void createOrderItem(OrderItem orderItem) {
        orderItemDAO.addOrderItem(orderItem);
    }

    public void updateOrderItem(OrderItem orderItem) throws OrderItemNotFoundException {
        orderItemDAO.updateOrderItem(orderItem);
    }

    public void deleteOrderItem(int orderItemId) throws OrderItemNotFoundException {
        orderItemDAO.deleteOrderItem(orderItemId);
    }

    public List<OrderItem> findOrderItemsByOrder(int orderId) {
        return orderItemDAO.findOrderItemsByOrder(orderId);
    }
}