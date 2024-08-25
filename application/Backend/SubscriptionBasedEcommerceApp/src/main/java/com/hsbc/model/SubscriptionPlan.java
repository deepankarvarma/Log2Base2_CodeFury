package com.hsbc.model;


import java.util.Objects;

public class SubscriptionPlan {
    public enum SubscriptionType {
        DAILY,
        ALTERNATE_DAY,
        WEEKLY,
        BI_WEEKLY,
        MONTHLY
    }

    private int subscriptionPlanId;
    private Product product;
    private SubscriptionType subscriptionType;
    private int intervalDays;
    private double discountRate;
    private boolean isActive;

    public SubscriptionPlan() {
    }

    public SubscriptionPlan(int subscriptionPlanId, Product product, SubscriptionType subscriptionType, int intervalDays, double discountRate, boolean isActive) {
        this.subscriptionPlanId = subscriptionPlanId;
        this.product = product;
        this.subscriptionType = subscriptionType;
        this.intervalDays = intervalDays;
        this.discountRate = discountRate;
        this.isActive = isActive;
    }

    public int getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(int subscriptionPlanId) {
        this.subscriptionPlanId = subscriptionPlanId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public int getIntervalDays() {
        return intervalDays;
    }

    public void setIntervalDays(int intervalDays) {
        this.intervalDays = intervalDays;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionPlan that = (SubscriptionPlan) o;
        return subscriptionPlanId == that.subscriptionPlanId &&
                intervalDays == that.intervalDays &&
                Double.compare(that.discountRate, discountRate) == 0 &&
                isActive == that.isActive &&
                subscriptionType == that.subscriptionType &&
                Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriptionPlanId, product, subscriptionType, intervalDays, discountRate, isActive);
    }

    @Override
    public String toString() {
        return "SubscriptionPlan{" +
                "subscriptionPlanId=" + subscriptionPlanId +
                ", product=" + product +
                ", subscriptionType=" + subscriptionType +
                ", intervalDays=" + intervalDays +
                ", discountRate=" + discountRate +
                ", isActive=" + isActive +
                '}';
    }
}


