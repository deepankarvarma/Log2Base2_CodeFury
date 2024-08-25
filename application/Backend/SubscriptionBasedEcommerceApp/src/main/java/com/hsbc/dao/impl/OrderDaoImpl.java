package com.hsbc.dao.impl;

import com.hsbc.dao.OrderDao;
import com.hsbc.db.DBUtils;
import com.hsbc.exception.order.OrderAlreadyExistsException;
import com.hsbc.exception.order.OrderNotFoundException;
import com.hsbc.exception.user.UserNotFoundException;
import com.hsbc.model.Order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class OrderDaoImpl implements OrderDao {

    private Connection connection;

    // Constructor initializes the database connection using DBUtils
    public OrderDaoImpl() {
        this.connection = DBUtils.getConn();
    }

    // Method to set a different connection, useful for testing or different environments
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    // Retrieves an Order by its ID, throwing an exception if not found
    @Override
    public Order findById(int orderId) throws OrderNotFoundException {
        String query = "SELECT * FROM Orders WHERE order_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapRowToOrder(resultSet); // Maps the result set to an Order object
            } else {
                throw new OrderNotFoundException("Order not found with ID: " + orderId);
            }
        } catch (SQLException | UserNotFoundException e) {
            throw new RuntimeException("Error finding Order by ID", e);
        }
    }

    // Retrieves all Orders from the database
    @Override
    public List<Order> findAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM Orders";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                orders.add(mapRowToOrder(resultSet)); // Adds each order to the list
            }
        } catch (SQLException | UserNotFoundException e) {
            throw new RuntimeException("Error finding all Orders", e);
        }
        return orders;
    }

    // Adds a new Order to the database, throwing an exception if it already exists
    @Override
    public void addOrder(Order order) throws OrderAlreadyExistsException {
        String query = "INSERT INTO orders (user_id, order_date, total_price, delivery_status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Set the order details in the prepared statement
            statement.setInt(1, order.getUser().getUserId());
            statement.setDate(2, Date.valueOf(order.getOrderDate()));
            statement.setDouble(3, order.getTotalPrice());
            statement.setString(4, order.getDeliveryStatus().name());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            // Check for duplicate entry SQLState (MySQL: "23000")
            if ("23000".equals(e.getSQLState())) {
                throw new OrderAlreadyExistsException("Order already exists: " + e.getMessage());
            }
            throw new RuntimeException("Error adding order: " + e.getMessage(), e);
        }
    }

    // Updates an existing Order in the database, throwing an exception if not found
    @Override
    public void updateOrder(Order order) throws OrderNotFoundException {
        String query = "UPDATE Orders SET user_id = ?, order_date = ?, total_price = ?, delivery_status = ? WHERE order_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set the updated order details in the prepared statement
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

    // Cancels an Order by setting its status to 'CANCELLED'
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

    // Retrieves all Orders associated with a specific user by their user ID
    @Override
    public List<Order> findOrdersByUser(int userId) throws UserNotFoundException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM Orders WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orders.add(mapRowToOrder(resultSet)); // Adds each order to the list
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding Orders by User ID", e);
        }
        return orders;
    }

    // Retrieves all Orders that have a specific delivery status
    @Override
    public List<Order> findOrdersByStatus(Order.DeliveryStatus status) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM Orders WHERE delivery_status = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status.name());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orders.add(mapRowToOrder(resultSet)); // Adds each order to the list
            }
        } catch (SQLException | UserNotFoundException e) {
            throw new RuntimeException("Error finding Orders by Status", e);
        }
        return orders;
    }

    // Retrieves all Orders placed on a specific date
    @Override
    public List<Order> findOrdersByDate(LocalDate date) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE order_date = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(date));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                orders.add(mapRowToOrder(resultSet)); // Adds each order to the list
            }
        } catch (SQLException | UserNotFoundException e) {
            throw new RuntimeException("Error finding Orders by date", e);
        }
        return orders;
    }

    // Retrieves an Order based on its associated subscription and date
    @Override
    public Order findOrderBySubscriptionAndDate(int subscriptionId, LocalDate date) {
        Order order = null;
        String query = "SELECT o.* FROM orders o "
                + "JOIN order_items oi ON o.order_id = oi.order_id "
                + "JOIN subscription_plans sp ON oi.product_id = sp.product_id "
                + "JOIN subscriptions s ON sp.subscription_plan_id = s.subscription_plan_id "
                + "WHERE s.subscription_id = ? AND o.order_date = ? AND s.subscription_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, subscriptionId);
            statement.setDate(2, Date.valueOf(date));
            statement.setInt(3, subscriptionId); // Ensures correct subscription
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                order = mapRowToOrder(resultSet);
            }
        } catch (SQLException | UserNotFoundException e) {
            throw new RuntimeException("Error finding order by subscription and date", e);
        }
        return order;
    }

    // Updates the delivery status of an Order by its ID
    @Override
    public void updateOrderStatus(int orderId, Order.DeliveryStatus status) throws OrderNotFoundException {
        String query = "UPDATE Orders SET delivery_status = ? WHERE order_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status.name());
            statement.setInt(2, orderId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new OrderNotFoundException("Order not found with ID: " + orderId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order status", e);
        }
    }

    // Maps a ResultSet row to an Order object
    private Order mapRowToOrder(ResultSet resultSet) throws SQLException, UserNotFoundException {
        Order order = new Order();
        order.setOrderId(resultSet.getInt("order_id"));
        order.setUser(new UserDaoImpl().findById(resultSet.getInt("user_id"))); // Fetch and set the associated User
        order.setOrderDate(resultSet.getDate("order_date").toLocalDate());
        order.setTotalPrice(resultSet.getDouble("total_price"));
        order.setDeliveryStatus(Order.DeliveryStatus.valueOf(resultSet.getString("delivery_status")));
        return order;
    }
}
