package com.hsbc.dao.impl;

import com.hsbc.dao.OrderDao;
import com.hsbc.db.DBUtils;
import com.hsbc.exception.OrderNotFoundException;
import com.hsbc.exception.UserNotFoundException;
import com.hsbc.model.Order;

import java.util.ArrayList;
import java.util.List;

import java.sql.*;


public class OrderDaoImpl implements OrderDao {

    private Connection connection;

    public OrderDaoImpl() {
        this.connection = DBUtils.getConn();
    }

    @Override
    public Order findById(int orderId) throws OrderNotFoundException {
        String query = "SELECT * FROM Orders WHERE order_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapRowToOrder(resultSet);
            } else {
                throw new OrderNotFoundException("Order not found with ID: " + orderId);
            }
        } catch (SQLException | UserNotFoundException e) {
            throw new RuntimeException("Error finding Order by ID", e);
        }
    }

    @Override
    public List<Order> findAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM Orders";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                orders.add(mapRowToOrder(resultSet));
            }
        } catch (SQLException | UserNotFoundException e) {
            throw new RuntimeException("Error finding all Orders", e);
        }
        return orders;
    }

    @Override
    public void addOrder(Order order) {
        String query = "INSERT INTO Orders (user_id, order_date, total_price, delivery_status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, order.getUser().getUserId());
            statement.setDate(2, Date.valueOf(order.getOrderDate()));
            statement.setDouble(3, order.getTotalPrice());
            statement.setString(4, order.getDeliveryStatus().name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding Order", e);
        }
    }

    @Override
    public void updateOrder(Order order) throws OrderNotFoundException {
        String query = "UPDATE Orders SET user_id = ?, order_date = ?, total_price = ?, delivery_status = ? WHERE order_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, order.getUser().getUserId());
            statement.setDate(2, Date.valueOf(order.getOrderDate()));
            statement.setDouble(3, order.getTotalPrice());
            statement.setString(4, order.getDeliveryStatus().name());
            statement.setInt(5, order.getOrderId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new OrderNotFoundException("Order not found with ID: " + order.getOrderId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating Order", e);
        }
    }

    @Override
    public void cancelOrder(int orderId) throws OrderNotFoundException {
        String query = "UPDATE Orders SET delivery_status = 'CANCELLED' WHERE order_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new OrderNotFoundException("Order not found with ID: " + orderId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error canceling Order", e);
        }
    }

    @Override
    public List<Order> findOrdersByUser(int userId) throws UserNotFoundException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM Orders WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orders.add(mapRowToOrder(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding Orders by User ID", e);
        }
        return orders;
    }

    @Override
    public List<Order> findOrdersByStatus(Order.DeliveryStatus status) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM Orders WHERE delivery_status = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status.name());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orders.add(mapRowToOrder(resultSet));
            }
        } catch (SQLException | UserNotFoundException e) {
            throw new RuntimeException("Error finding Orders by Status", e);
        }
        return orders;
    }

    private Order mapRowToOrder(ResultSet resultSet) throws SQLException, UserNotFoundException {
        Order order = new Order();
        order.setOrderId(resultSet.getInt("order_id"));
        order.setUser(new UserDaoImpl().findById(resultSet.getInt("user_id")));
        order.setOrderDate(resultSet.getDate("order_date").toLocalDate());
        order.setTotalPrice(resultSet.getDouble("total_price"));
        order.setDeliveryStatus(Order.DeliveryStatus.valueOf(resultSet.getString("delivery_status")));
        return order;
    }
}