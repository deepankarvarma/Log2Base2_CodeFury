package com.hsbc.dao.impl;

import com.hsbc.dao.SubscriptionPlanDao;
import com.hsbc.db.DBUtils;

import com.hsbc.exception.SubscriptionPlanNotFoundException;
import com.hsbc.model.SubscriptionPlan;
import com.hsbc.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class SubscriptionPlanDaoImpl implements SubscriptionPlanDao {

    private static final String INSERT_PLAN = "INSERT INTO SubscriptionPlans (product_id, subscription_type, interval_days, discount_rate, is_active) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_PLAN = "UPDATE SubscriptionPlans SET product_id = ?, subscription_type = ?, interval_days = ?, discount_rate = ?, is_active = ? WHERE subscription_plan_id = ?";
    private static final String DEACTIVATE_PLAN = "UPDATE SubscriptionPlans SET is_active = false WHERE subscription_plan_id = ?";
    private static final String ACTIVATE_PLAN = "UPDATE SubscriptionPlans SET is_active = true WHERE subscription_plan_id = ?";
    private static final String SELECT_PLAN_BY_ID = "SELECT * FROM SubscriptionPlans WHERE subscription_plan_id = ?";
    private static final String SELECT_ALL_PLANS = "SELECT * FROM SubscriptionPlans";
    private static final String SELECT_ACTIVE_PLANS_BY_PRODUCT = "SELECT * FROM SubscriptionPlans WHERE product_id = ? AND is_active = true";

    private Connection connection;

    public SubscriptionPlanDaoImpl() {
        this.connection = DBUtils.getConn();
    }

    @Override
    public SubscriptionPlan findById(int subscriptionPlanId) throws SubscriptionPlanNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_PLAN_BY_ID)) {

            statement.setInt(1, subscriptionPlanId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapRowToSubscriptionPlan(resultSet);
            } else {
                throw new SubscriptionPlanNotFoundException("Subscription Plan not found with ID: " + subscriptionPlanId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding subscription plan by ID", e);
        }
    }

    @Override
    public List<SubscriptionPlan> findAllSubscriptionPlans() {
        List<SubscriptionPlan> plans = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_PLANS);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                plans.add(mapRowToSubscriptionPlan(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all subscription plans", e);
        }
        return plans;
    }

    @Override
    public void addSubscriptionPlan(SubscriptionPlan plan) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PLAN, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, plan.getProduct().getProductId());
            statement.setString(2, plan.getSubscriptionType().name());
            statement.setInt(3, plan.getIntervalDays());
            statement.setDouble(4, plan.getDiscountRate());
            statement.setBoolean(5, plan.isActive());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Adding subscription plan failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    plan.setSubscriptionPlanId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Adding subscription plan failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding subscription plan", e);
        }
    }

    @Override
    public void updateSubscriptionPlan(SubscriptionPlan plan) throws SubscriptionPlanNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_PLAN)) {

            statement.setInt(1, plan.getProduct().getProductId());
            statement.setString(2, plan.getSubscriptionType().name());
            statement.setInt(3, plan.getIntervalDays());
            statement.setDouble(4, plan.getDiscountRate());
            statement.setBoolean(5, plan.isActive());
            statement.setInt(6, plan.getSubscriptionPlanId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SubscriptionPlanNotFoundException("Subscription Plan not found with ID: " + plan.getSubscriptionPlanId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating subscription plan", e);
        }
    }

    @Override
    public void deactivateSubscriptionPlan(int subscriptionPlanId) throws SubscriptionPlanNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement(DEACTIVATE_PLAN)) {

            statement.setInt(1, subscriptionPlanId);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SubscriptionPlanNotFoundException("Subscription Plan not found with ID: " + subscriptionPlanId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating subscription plan", e);
        }
    }

    @Override
    public void activateSubscriptionPlan(int subscriptionPlanId) throws SubscriptionPlanNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement(ACTIVATE_PLAN)) {

            statement.setInt(1, subscriptionPlanId);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SubscriptionPlanNotFoundException("Subscription Plan not found with ID: " + subscriptionPlanId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error activating subscription plan", e);
        }
    }

    @Override
    public List<SubscriptionPlan> findActivePlansByProduct(int productId) {
        List<SubscriptionPlan> plans = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ACTIVE_PLANS_BY_PRODUCT)) {

            statement.setInt(1, productId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                plans.add(mapRowToSubscriptionPlan(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active subscription plans by product", e);
        }
        return plans;
    }

    private SubscriptionPlan mapRowToSubscriptionPlan(ResultSet resultSet) throws SQLException {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setSubscriptionPlanId(resultSet.getInt("subscription_plan_id"));
        plan.setIntervalDays(resultSet.getInt("interval_days"));
        plan.setDiscountRate(resultSet.getDouble("discount_rate"));
        plan.setActive(resultSet.getBoolean("is_active"));

        Product product = new Product();
        product.setProductId(resultSet.getInt("product_id"));
        plan.setProduct(product);

        plan.setSubscriptionType(SubscriptionPlan.SubscriptionType.valueOf(resultSet.getString("subscription_type")));

        return plan;
    }
}