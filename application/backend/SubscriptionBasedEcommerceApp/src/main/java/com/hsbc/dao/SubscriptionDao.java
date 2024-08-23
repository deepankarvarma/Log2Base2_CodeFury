package com.hsbc.dao;

import com.hsbc.exception.SubscriptionNotFoundException;
import com.hsbc.exception.UserNotFoundException;
import com.hsbc.model.Subscription;

import java.util.List;


public interface SubscriptionDao {
    Subscription findById(int subscriptionId) throws SubscriptionNotFoundException;

    List<Subscription> findAllSubscriptions();

    void addSubscription(Subscription subscription);

    void updateSubscription(Subscription subscription) throws SubscriptionNotFoundException;

    void deactivateSubscription(int subscriptionId) throws SubscriptionNotFoundException;

    void activateSubscription(int subscriptionId) throws SubscriptionNotFoundException;

    List<Subscription> findSubscriptionsByUser(int userId) throws UserNotFoundException;
}
