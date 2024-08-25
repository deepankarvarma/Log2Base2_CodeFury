package com.hsbc.dao.impl;

import com.hsbc.dao.OrderItemDao;
import com.hsbc.db.DBUtils;
import com.hsbc.exception.order.OrderItemNotFoundException;
import com.hsbc.exception.order.OrderNotFoundException;
import com.hsbc.exception.product.ProductNotFoundException;
import com.hsbc.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDaoImpl implements OrderItemDao {

    private Connection connection;

    // Constructor initializes the database connection using DBUtils
    public OrderItemDaoImpl() {
        this.connection = DBUtils.getConn();
    }

    // Retrieves an OrderItem by its ID, throwing an exception if not found
    @Override
    public OrderItem findById(int orderItemId) throws OrderItemNotFoundException {
        String query = "SELECT * FROM Order_Items WHERE order_item_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderItemId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapRowToOrderItem(resultSet); // Maps the result set to an OrderItem object
            } else {
                throw new OrderItemNotFoundException("OrderItem not found with ID: " + orderItemId);
            }
        } catch (SQLException | OrderNotFoundException | ProductNotFoundException e) {
            throw new RuntimeException("Error finding OrderItem by ID", e);
        }
    }

    // Retrieves all OrderItems from the database
    @Override
    public List<OrderItem> findAllOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();
        String query = "SELECT * FROM Order_Items";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                orderItems.add(mapRowToOrderItem(resultSet)); // Adds each order item to the list
            }
        } catch (SQLException | OrderNotFoundException | ProductNotFoundException e) {
            throw new RuntimeException("Error finding all OrderItems", e);
        }
        return orderItems;
    }

    // Adds a new OrderItem to the database
    @Override
    public void addOrderItem(OrderItem orderItem) {
        String query = "INSERT INTO Order_Items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set the order item details in the prepared statement
            statement.setInt(1, orderItem.getOrder().getOrderId());
            statement.setInt(2, orderItem.getProduct().getProductId());
            statement.setInt(3, orderItem.getQuantity());
            statement.setDouble(4, orderItem.getPrice());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding OrderItem", e);
        }
    }

    // Updates an existing OrderItem in the database, throwing an exception if not found
    @Override
    public void updateOrderItem(OrderItem orderItem) throws OrderItemNotFoundException {
        String query = "UPDATE Order_Items SET order_id = ?, product_id = ?, quantity = ?, price = ? WHERE order_item_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set the updated order item details in the prepared statement
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

    // Deletes an OrderItem from the database by its ID, throwing an exception if not found
    @Override
    public void deleteOrderItem(int orderItemId) throws OrderItemNotFoundException {
        String query = "DELETE FROM Order_Items WHERE order_item_id = ?";
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

    // Retrieves all OrderItems for a specific order by the order's ID
    @Override
    public List<OrderItem> findOrderItemsByOrder(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String query = "SELECT * FROM Order_Items WHERE order_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orderItems.add(mapRowToOrderItem(resultSet)); // Adds each order item to the list
            }
        } catch (SQLException | OrderNotFoundException | ProductNotFoundException e) {
            throw new RuntimeException("Error finding OrderItems by Order ID", e);
        }
        return orderItems;
    }

    // Maps a ResultSet row to an OrderItem object
    private OrderItem mapRowToOrderItem(ResultSet resultSet) throws SQLException, OrderNotFoundException, ProductNotFoundException {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(resultSet.getInt("order_item_id"));
        orderItem.setOrder(new OrderDaoImpl().findById(resultSet.getInt("order_id"))); // Fetch and set the associated Order
        orderItem.setProduct(new ProductDaoImpl().findById(resultSet.getInt("product_id"))); // Fetch and set the associated Product
        orderItem.setQuantity(resultSet.getInt("quantity"));
        orderItem.setPrice(resultSet.getDouble("price"));
        return orderItem;
    }
}