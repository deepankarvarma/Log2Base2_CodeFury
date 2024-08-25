package com.hsbc.model;

import java.time.LocalDate;


public class DeliverySchedule {
    private int deliveryScheduleId;
    private Subscription subscription;  // Reference to the Subscription entity
    private LocalDate deliveryDate;

    public DeliverySchedule() {
    }

    public DeliverySchedule(int deliveryScheduleId, Subscription subscription, LocalDate deliveryDate) {
        this.deliveryScheduleId = deliveryScheduleId;
        this.subscription = subscription;
        this.deliveryDate = deliveryDate;
    }

    public int getDeliveryScheduleId() {
        return deliveryScheduleId;
    }

    public void setDeliveryScheduleId(int deliveryScheduleId) {
        this.deliveryScheduleId = deliveryScheduleId;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @Override
    public String toString() {
        return "DeliverySchedule{" +
                "deliveryScheduleId=" + deliveryScheduleId +
                ", subscription=" + subscription +
                ", deliveryDate=" + deliveryDate +
                '}';
    }
}
