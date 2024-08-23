package com.hsbc.dao;

import com.hsbc.exception.OrderItemNotFoundException;
import com.hsbc.model.OrderItem;

import java.util.List;

public interface OrderItemDao {
    OrderItem findById(int orderItemId) throws OrderItemNotFoundException;

    List<OrderItem> findAllOrderItems();

    void addOrderItem(OrderItem orderItem);

    void updateOrderItem(OrderItem orderItem) throws OrderItemNotFoundException;

    void deleteOrderItem(int orderItemId) throws OrderItemNotFoundException;

    List<OrderItem> findOrderItemsByOrder(int orderId);
}