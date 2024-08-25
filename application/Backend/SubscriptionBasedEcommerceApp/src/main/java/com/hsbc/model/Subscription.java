package com.hsbc.model;

import java.time.LocalDate;


public class Subscription {
    public enum Status {
        ACTIVE,
        INACTIVE
    }

    private int subscriptionId;
    private User user;
    private SubscriptionPlan subscriptionPlan;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;


    public Subscription() {
    }

    public Subscription(int subscriptionId, User user, SubscriptionPlan subscriptionPlan, LocalDate startDate, LocalDate endDate, Status status) {
        this.subscriptionId = subscriptionId;
        this.user = user;
        this.subscriptionPlan = subscriptionPlan;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }


    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Subscription{" +
                "subscriptionId=" + subscriptionId +
                ", user=" + user +
                ", subscriptionPlan=" + subscriptionPlan +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}
