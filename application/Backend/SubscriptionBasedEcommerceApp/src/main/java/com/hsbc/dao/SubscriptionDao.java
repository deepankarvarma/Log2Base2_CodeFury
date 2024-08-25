package com.hsbc.dao;

import com.hsbc.exception.subscription.SubscriptionNotFoundException;
import com.hsbc.model.Subscription;

import java.util.List;


public interface SubscriptionDao {
    Subscription findById(int subscriptionId) throws SubscriptionNotFoundException;

    List<Subscription> findAllSubscriptions();

    void addSubscription(Subscription subscription);

    void updateSubscription(Subscription subscription) throws SubscriptionNotFoundException;

    void deactivateSubscription(int subscriptionId) throws SubscriptionNotFoundException;

    void activateSubscription(int subscriptionId) throws SubscriptionNotFoundException;

    List<Subscription> findSubscriptionsByUser(int userId);

    //New method added
    List<Subscription> findActiveSubscriptionsByUser(int userId);

}
