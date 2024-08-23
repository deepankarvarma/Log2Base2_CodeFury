package com.hsbc.dao;

import com.hsbc.exception.SubscriptionPlanNotFoundException;
import com.hsbc.model.SubscriptionPlan;

import java.util.List;


public interface SubscriptionPlanDao {
    SubscriptionPlan findById(int subscriptionPlanId) throws SubscriptionPlanNotFoundException;

    List<SubscriptionPlan> findAllSubscriptionPlans();

    void addSubscriptionPlan(SubscriptionPlan plan);

    void updateSubscriptionPlan(SubscriptionPlan plan) throws SubscriptionPlanNotFoundException;

    void deactivateSubscriptionPlan(int subscriptionPlanId) throws SubscriptionPlanNotFoundException;

    void activateSubscriptionPlan(int subscriptionPlanId) throws SubscriptionPlanNotFoundException;

    List<SubscriptionPlan> findActivePlansByProduct(int productId);
}
