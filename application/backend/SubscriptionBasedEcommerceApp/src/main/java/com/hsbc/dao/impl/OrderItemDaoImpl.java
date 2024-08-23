package com.hsbc.dao.impl;

import com.hsbc.dao.OrderItemDao;
import com.hsbc.db.DBUtils;
import com.hsbc.exception.OrderItemNotFoundException;
import com.hsbc.exception.OrderNotFoundException;
import com.hsbc.exception.ProductNotFoundException;
import com.hsbc.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDaoImpl implements OrderItemDao {

    private Connection connection;

    public OrderItemDaoImpl() {
        this.connection = DBUtils.getConn();
    }

    @Override
    public OrderItem findById(int orderItemId) throws OrderItemNotFoundException {
        String query = "SELECT * FROM OrderItems WHERE order_item_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderItemId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapRowToOrderItem(resultSet);
            } else {
                throw new OrderItemNotFoundException("OrderItem not found with ID: " + orderItemId);
            }
        } catch (SQLException | OrderNotFoundException | ProductNotFoundException e) {
            throw new RuntimeException("Error finding OrderItem by ID", e);
        }
    }

    @Override
    public List<OrderItem> findAllOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();
        String query = "SELECT * FROM OrderItems";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                orderItems.add(mapRowToOrderItem(resultSet));
            }
        } catch (SQLException | OrderNotFoundException | ProductNotFoundException e) {
            throw new RuntimeException("Error finding all OrderItems", e);
        }
        return orderItems;
    }

    @Override
    public void addOrderItem(OrderItem orderItem) {
        String query = "INSERT INTO OrderItems (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderItem.getOrder().getOrderId());
            statement.setInt(2, orderItem.getProduct().getProductId());
            statement.setInt(3, orderItem.getQuantity());
            statement.setDouble(4, orderItem.getPrice());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding OrderItem", e);
        }
    }

    @Override
    public void updateOrderItem(OrderItem orderItem) throws OrderItemNotFoundException {
        String query = "UPDATE OrderItems SET order_id = ?, product_id = ?, quantity = ?, price = ? WHERE order_item_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderItem.getOrder().getOrderId());
            statement.setInt(2, orderItem.getProduct().getProductId());
            statement.setInt(3, orderItem.getQuantity());
            statement.setDouble(4, orderItem.getPrice());
            statement.setInt(5, orderItem.getOrderItemId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new OrderItemNotFoundException("OrderItem not found with ID: " + orderItem.getOrderItemId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating OrderItem", e);
        }
    }

    @Override
    public void deleteOrderItem(int orderItemId) throws OrderItemNotFoundException {
        String query = "DELETE FROM OrderItems WHERE order_item_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderItemId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new OrderItemNotFoundException("OrderItem not found with ID: " + orderItemId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting OrderItem", e);
        }
    }

    @Override
    public List<OrderItem> findOrderItemsByOrder(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String query = "SELECT * FROM OrderItems WHERE order_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orderItems.add(mapRowToOrderItem(resultSet));
            }
        } catch (SQLException | OrderNotFoundException | ProductNotFoundException e) {
            throw new RuntimeException("Error finding OrderItems by Order ID", e);
        }
        return orderItems;
    }

    private OrderItem mapRowToOrderItem(ResultSet resultSet) throws SQLException, OrderNotFoundException, ProductNotFoundException {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(resultSet.getInt("order_item_id"));
        orderItem.setOrder(new OrderDaoImpl().findById(resultSet.getInt("order_id")));
        orderItem.setProduct(new ProductDaoImpl().findById(resultSet.getInt("product_id")));
        orderItem.setQuantity(resultSet.getInt("quantity"));
        orderItem.setPrice(resultSet.getDouble("price"));
        return orderItem;
    }
}