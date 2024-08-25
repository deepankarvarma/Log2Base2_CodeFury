package com.hsbc.dao.impl;

import com.hsbc.dao.SubscriptionDao;
import com.hsbc.db.DBUtils;
import com.hsbc.exception.subscription.SubscriptionNotFoundException;
import com.hsbc.exception.subscription.SubscriptionPlanNotFoundException;
import com.hsbc.exception.user.UserNotFoundException;
import com.hsbc.model.Subscription;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;


public class SubscriptionDaoImpl implements SubscriptionDao {

    private static final String SELECT_SUBSCRIPTION_BY_ID = "SELECT * FROM Subscriptions WHERE subscription_id = ?";
    private static final String SELECT_ALL_SUBSCRIPTIONS = "SELECT * FROM subscriptions";
    private static final String INSERT_SUBSCRIPTION = "INSERT INTO Subscriptions (user_id, subscription_plan_id, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SUBSCRIPTION = "UPDATE Subscriptions SET user_id = ?, subscription_plan_id = ?, start_date = ?, end_date = ?, status = ? WHERE subscription_id = ?";
    private static final String DEACTIVATE_SUBSCRIPTION = "UPDATE Subscriptions SET status = 'INACTIVE' WHERE subscription_id = ?";
    private static final String ACTIVATE_SUBSCRIPTION = "UPDATE Subscriptions SET status = 'ACTIVE' WHERE subscription_id = ?";
    private static final String SELECT_SUBSCRIPTIONS_BY_USER = "SELECT * FROM Subscriptions WHERE user_id = ?";
    private static final String SELECT_ACTIVE_SUBSCRIPTIONS_BY_USER = "SELECT * FROM subscriptions WHERE user_id = ? AND status = 'ACTIVE'";


    private Connection connection;

    public SubscriptionDaoImpl() {
        this.connection = DBUtils.getConn();
    }

    @Override
    public Subscription findById(int subscriptionId) throws SubscriptionNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_SUBSCRIPTION_BY_ID)) {

            statement.setInt(1, subscriptionId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapRowToSubscription(resultSet);
            } else {
                throw new SubscriptionNotFoundException("Subscription not found with ID: " + subscriptionId);
            }
        } catch (SQLException | UserNotFoundException | SubscriptionPlanNotFoundException e) {
            throw new RuntimeException("Error finding Subscription by ID", e);
        }
    }

    @Override
    public List<Subscription> findAllSubscriptions() {
        List<Subscription> subscriptions = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SUBSCRIPTIONS)) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                subscriptions.add(mapRowToSubscription(resultSet));
            }
        } catch (SQLException | UserNotFoundException | SubscriptionPlanNotFoundException e) {
            throw new RuntimeException("Error finding all Subscriptions", e);
        }
        return subscriptions;
    }

    @Override
    public void addSubscription(Subscription subscription) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SUBSCRIPTION, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, subscription.getUser().getUserId());
            statement.setInt(2, subscription.getSubscriptionPlan().getSubscriptionPlanId());
            statement.setDate(3, Date.valueOf(subscription.getStartDate()));
            statement.setDate(4, Date.valueOf(subscription.getEndDate()));
            statement.setString(5, subscription.getStatus().name());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Adding subscription failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    subscription.setSubscriptionId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Adding subscription failed, no ID obtained.");
                }
            }
            // After adding the subscription, generate delivery schedules based on the subscription
            generateDeliverySchedules(subscription, connection);

        } catch (SQLException e) {
            throw new RuntimeException("Error adding Subscription", e);
        }
    }

    @Override
    public void updateSubscription(Subscription subscription) throws SubscriptionNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SUBSCRIPTION)) {

            statement.setInt(1, subscription.getUser().getUserId());
            statement.setInt(2, subscription.getSubscriptionPlan().getSubscriptionPlanId());
            statement.setDate(3, Date.valueOf(subscription.getStartDate()));
            statement.setDate(4, Date.valueOf(subscription.getEndDate()));
            statement.setString(5, subscription.getStatus().name());
            statement.setInt(6, subscription.getSubscriptionId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SubscriptionNotFoundException("Subscription not found with ID: " + subscription.getSubscriptionId());
            }

            // Update the delivery schedules accordingly if the subscription dates or status change
            deleteDeliverySchedulesBySubscription(subscription.getSubscriptionId(), connection);
            generateDeliverySchedules(subscription, connection);

        } catch (SQLException e) {
            throw new RuntimeException("Error updating Subscription", e);
        }
    }

    @Override
    public void deactivateSubscription(int subscriptionId) throws SubscriptionNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement(DEACTIVATE_SUBSCRIPTION)) {

            statement.setInt(1, subscriptionId);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SubscriptionNotFoundException("Subscription not found with ID: " + subscriptionId);
            }

            // deleting future delivery dates
            deleteFutureDeliverySchedulesBySubscription(subscriptionId, connection);

        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating Subscription", e);
        }
    }

    @Override
    public void activateSubscription(int subscriptionId) throws SubscriptionNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement(ACTIVATE_SUBSCRIPTION)) {

            statement.setInt(1, subscriptionId);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SubscriptionNotFoundException("Subscription not found with ID: " + subscriptionId);
            }

            // Additional logic to handle activation-related tasks could be added
            // come back to it later

        } catch (SQLException e) {
            throw new RuntimeException("Error activating Subscription", e);
        }
    }

    @Override
    public List<Subscription> findSubscriptionsByUser(int userId) {
        List<Subscription> subscriptions = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_SUBSCRIPTIONS_BY_USER)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                subscriptions.add(mapRowToSubscription(resultSet));
            }
        } catch (SQLException | SubscriptionPlanNotFoundException | UserNotFoundException e) {
            throw new RuntimeException("Error finding Subscriptions by User ID", e);
        }
        return subscriptions;
    }

    ////New method added
    @Override
    public List<Subscription> findActiveSubscriptionsByUser(int userId) {
        List<Subscription> subscriptions = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ACTIVE_SUBSCRIPTIONS_BY_USER)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                subscriptions.add(mapRowToSubscription(resultSet));
            }
        } catch (SQLException | UserNotFoundException | SubscriptionPlanNotFoundException e) {
            throw new RuntimeException("Error finding active subscriptions by user", e);
        }
        return subscriptions;
    }


    private Subscription mapRowToSubscription(ResultSet resultSet) throws SQLException, UserNotFoundException, SubscriptionPlanNotFoundException {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(resultSet.getInt("subscription_id"));
        subscription.setUser(new UserDaoImpl().findById(resultSet.getInt("user_id")));

        int planId = resultSet.getInt("subscription_plan_id");
        try {
            subscription.setSubscriptionPlan(new SubscriptionPlanDaoImpl().findById(planId));
        } catch (SubscriptionPlanNotFoundException e) {
            System.out.println("Failed to find subscription plan with ID: " + planId);
            throw e;
        }

        subscription.setStartDate(resultSet.getDate("start_date").toLocalDate());
        subscription.setEndDate(resultSet.getDate("end_date").toLocalDate());
        subscription.setStatus(Subscription.Status.valueOf(resultSet.getString("status")));

        return subscription;
    }


    private void generateDeliverySchedules(Subscription subscription, Connection connection) throws SQLException {
        // Logic to generate delivery schedules based on subscription plan type
        LocalDate currentDate = subscription.getStartDate();
        int intervalDays = subscription.getSubscriptionPlan().getIntervalDays();

        while (!currentDate.isAfter(subscription.getEndDate())) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO Delivery_Schedules (subscription_id, delivery_date) VALUES (?, ?)")) {

                statement.setInt(1, subscription.getSubscriptionId());
                statement.setDate(2, Date.valueOf(currentDate));

                statement.executeUpdate();
            }

            currentDate = currentDate.plusDays(intervalDays);
        }
    }

    private void deleteDeliverySchedulesBySubscription(int subscriptionId, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM Delivery_Schedules WHERE subscription_id = ?")) {
            statement.setInt(1, subscriptionId);
            statement.executeUpdate();
        }
    }

    private void deleteFutureDeliverySchedulesBySubscription(int subscriptionId, Connection connection) throws SQLException {
        String deleteQuery = "DELETE FROM delivery_schedules WHERE subscription_id = ? AND delivery_date > ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setInt(1, subscriptionId);
            statement.setDate(2, Date.valueOf(LocalDate.now())); // Use the current date to determine future schedules
            statement.executeUpdate();
        }
    }
}