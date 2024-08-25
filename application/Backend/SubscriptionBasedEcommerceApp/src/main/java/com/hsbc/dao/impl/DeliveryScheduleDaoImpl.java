package com.hsbc.dao.impl;

import com.hsbc.dao.DeliveryScheduleDao;
import com.hsbc.db.DBUtils;
import com.hsbc.exception.delivery.DeliveryScheduleNotFoundException;
import com.hsbc.exception.subscription.SubscriptionNotFoundException;
import com.hsbc.model.DeliverySchedule;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DeliveryScheduleDaoImpl implements DeliveryScheduleDao {

    private Connection connection;

    // Default constructor initializes the connection using DBUtils
    public DeliveryScheduleDaoImpl() {
        this.connection = DBUtils.getConn();
    }

    // Constructor that accepts a connection, useful for testing or dependency injection
    public DeliveryScheduleDaoImpl(Connection connection) {
        this.connection = connection;
    }

    // Retrieves a DeliverySchedule by its ID, throws exception if not found
    @Override
    public DeliverySchedule findById(int scheduleId) throws DeliveryScheduleNotFoundException {
        String query = "SELECT * FROM Delivery_Schedules WHERE delivery_schedule_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, scheduleId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapRowToDeliverySchedule(resultSet); // Maps the result set to a DeliverySchedule object
            } else {
                throw new DeliveryScheduleNotFoundException("DeliverySchedule not found with ID: " + scheduleId);
            }
        } catch (SQLException | SubscriptionNotFoundException e) {
            throw new RuntimeException("Error finding DeliverySchedule by ID", e);
        }
    }

    // Retrieves all DeliverySchedules from the database
    @Override
    public List<DeliverySchedule> findAllSchedules() {
        List<DeliverySchedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM Delivery_Schedules";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                schedules.add(mapRowToDeliverySchedule(resultSet)); // Adds each schedule to the list
            }
        } catch (SQLException | SubscriptionNotFoundException e) {
            throw new RuntimeException("Error finding all DeliverySchedules", e);
        }
        return schedules;
    }

    // Adds a new DeliverySchedule to the database
    @Override
    public void addDeliverySchedule(DeliverySchedule schedule) {
        String query = "INSERT INTO Delivery_Schedules (subscription_id, delivery_date) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Sets the details of the delivery schedule in the prepared statement
            statement.setInt(1, schedule.getSubscription().getSubscriptionId());
            statement.setDate(2, Date.valueOf(schedule.getDeliveryDate()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding DeliverySchedule", e);
        }
    }

    // Updates an existing DeliverySchedule in the database, throws exception if not found
    @Override
    public void updateDeliverySchedule(DeliverySchedule schedule) throws DeliveryScheduleNotFoundException {
        String query = "UPDATE Delivery_Schedules SET subscription_id = ?, delivery_date = ? WHERE delivery_schedule_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Sets the updated delivery schedule details in the prepared statement
            statement.setInt(1, schedule.getSubscription().getSubscriptionId());
            statement.setDate(2, Date.valueOf(schedule.getDeliveryDate()));
            statement.setInt(3, schedule.getDeliveryScheduleId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DeliveryScheduleNotFoundException("DeliverySchedule not found with ID: " + schedule.getDeliveryScheduleId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating DeliverySchedule", e);
        }
    }

    // Deletes a DeliverySchedule by its ID, throws exception if not found
    @Override
    public void deleteDeliverySchedule(int scheduleId) throws DeliveryScheduleNotFoundException {
        String query = "DELETE FROM Delivery_Schedules WHERE delivery_schedule_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, scheduleId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DeliveryScheduleNotFoundException("DeliverySchedule not found with ID: " + scheduleId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting DeliverySchedule", e);
        }
    }

    // Finds all DeliverySchedules associated with a specific order by the order ID
    @Override
    public List<DeliverySchedule> findSchedulesByOrder(int orderId) {
        List<DeliverySchedule> schedules = new ArrayList<>();
        String query = "SELECT ds.* FROM Delivery_Schedules ds " +
                "JOIN Orders o ON ds.subscription_id = o.subscription_id WHERE o.order_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                schedules.add(mapRowToDeliverySchedule(resultSet)); // Adds each schedule to the list
            }
        } catch (SQLException | SubscriptionNotFoundException e) {
            throw new RuntimeException("Error finding DeliverySchedules by Order ID", e);
        }
        return schedules;
    }

    // Finds all DeliverySchedules for a specific delivery date
    @Override
    public List<DeliverySchedule> findSchedulesByDate(LocalDate deliveryDate) {
        List<DeliverySchedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM delivery_schedules WHERE delivery_date = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(deliveryDate));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                schedules.add(mapRowToDeliverySchedule(resultSet)); // Adds each schedule to the list
            }
        } catch (SQLException | SubscriptionNotFoundException e) {
            System.out.println("Error fetching DeliverySchedules: " + e.getMessage());
            throw new RuntimeException("Error finding DeliverySchedules by Date", e);
        }
        return schedules;
    }

    // Helper method to map a ResultSet row to a DeliverySchedule object
    private DeliverySchedule mapRowToDeliverySchedule(ResultSet resultSet) throws SQLException, SubscriptionNotFoundException {
        DeliverySchedule schedule = new DeliverySchedule();
        schedule.setDeliveryScheduleId(resultSet.getInt("delivery_schedule_id"));
        int subscriptionId = resultSet.getInt("subscription_id");
        schedule.setSubscription(new SubscriptionDaoImpl().findById(subscriptionId)); // Fetch and set the associated Subscription
        schedule.setDeliveryDate(resultSet.getDate("delivery_date").toLocalDate());
        return schedule;
    }
}
