package com.hsbc.service;

import com.hsbc.dao.SubscriptionPlanDao;
import com.hsbc.exception.subscription.SubscriptionPlanNotFoundException;
import com.hsbc.factory.DaoFactory;
import com.hsbc.model.SubscriptionPlan;

import java.util.List;

public class SubscriptionPlanService {

    private SubscriptionPlanDao subscriptionPlanDAO;

    public SubscriptionPlanService() {
        this.subscriptionPlanDAO = DaoFactory.getSubscriptionPlanDao();
    }

    public SubscriptionPlan getSubscriptionPlanById(int subscriptionPlanId) throws SubscriptionPlanNotFoundException {
        return subscriptionPlanDAO.findById(subscriptionPlanId);
    }

    public List<SubscriptionPlan> getAllSubscriptionPlans() {
        return subscriptionPlanDAO.findAllSubscriptionPlans();
    }

    public void createSubscriptionPlan(SubscriptionPlan plan) {
        subscriptionPlanDAO.addSubscriptionPlan(plan);
    }

    public void updateSubscriptionPlan(SubscriptionPlan plan) throws SubscriptionPlanNotFoundException {
        subscriptionPlanDAO.updateSubscriptionPlan(plan);
    }

    public void deactivateSubscriptionPlan(int subscriptionPlanId) throws SubscriptionPlanNotFoundException {
        subscriptionPlanDAO.deactivateSubscriptionPlan(subscriptionPlanId);
    }

    public void activateSubscriptionPlan(int subscriptionPlanId) throws SubscriptionPlanNotFoundException {
        subscriptionPlanDAO.activateSubscriptionPlan(subscriptionPlanId);
    }

    public List<SubscriptionPlan> findActivePlansByProduct(int productId) {
        return subscriptionPlanDAO.findActivePlansByProduct(productId);
    }
}