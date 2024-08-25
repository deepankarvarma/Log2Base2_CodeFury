package com.hsbc.service;

import com.hsbc.dao.SubscriptionDao;
import com.hsbc.exception.subscription.SubscriptionNotFoundException;
import com.hsbc.exception.user.UserNotFoundException;
import com.hsbc.factory.DaoFactory;
import com.hsbc.model.Subscription;

import java.util.List;

public class SubscriptionService {

    private SubscriptionDao subscriptionDAO;

    public SubscriptionService() {
        this.subscriptionDAO = DaoFactory.getSubscriptionDao();
    }

    public Subscription getSubscriptionById(int subscriptionId) throws SubscriptionNotFoundException {
        return subscriptionDAO.findById(subscriptionId);
    }

    public List<Subscription> getAllSubscriptions() {
        return subscriptionDAO.findAllSubscriptions();
    }

    public void createSubscription(Subscription subscription) {
        subscriptionDAO.addSubscription(subscription);
    }

    public void updateSubscription(Subscription subscription) throws SubscriptionNotFoundException {
        subscriptionDAO.updateSubscription(subscription);
    }

    public void deactivateSubscription(int subscriptionId) throws SubscriptionNotFoundException {
        subscriptionDAO.deactivateSubscription(subscriptionId);
    }

    public void activateSubscription(int subscriptionId) throws SubscriptionNotFoundException {
        subscriptionDAO.activateSubscription(subscriptionId);
    }

    public List<Subscription> findSubscriptionsByUser(int userId) throws UserNotFoundException {
        return subscriptionDAO.findSubscriptionsByUser(userId);
    }

    public List<Subscription> findActiveSubscriptionsByUser(int userId) {
        return subscriptionDAO.findActiveSubscriptionsByUser(userId);
    }
}