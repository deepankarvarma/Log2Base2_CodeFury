package com.hsbc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DeliveryScheduleTest {

    private DeliverySchedule deliverySchedule;
    private Subscription subscription;

    @BeforeEach
    public void setUp() {
        // Initialize a Subscription object for testing purposes
        subscription = new Subscription(); // Assuming a default constructor for Subscription
        // Initialize a DeliverySchedule object before each test
        deliverySchedule = new DeliverySchedule(1, subscription, LocalDate.of(2024, 8, 24));
    }

    @Test
    public void testDefaultConstructor() {
        // Test the default constructor
        DeliverySchedule defaultDeliverySchedule = new DeliverySchedule();
        assertNotNull(defaultDeliverySchedule);
    }

    @Test
    public void testParameterizedConstructor() {
        // Test the parameterized constructor
        assertEquals(1, deliverySchedule.getDeliveryScheduleId());
        assertEquals(subscription, deliverySchedule.getSubscription());
        assertEquals(LocalDate.of(2024, 8, 24), deliverySchedule.getDeliveryDate());
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        Subscription newSubscription = new Subscription(); // Assuming a default constructor for Subscription
        LocalDate newDeliveryDate = LocalDate.of(2024, 12, 25);

        deliverySchedule.setDeliveryScheduleId(2);
        deliverySchedule.setSubscription(newSubscription);
        deliverySchedule.setDeliveryDate(newDeliveryDate);

        assertEquals(2, deliverySchedule.getDeliveryScheduleId());
        assertEquals(newSubscription, deliverySchedule.getSubscription());
        assertEquals(newDeliveryDate, deliverySchedule.getDeliveryDate());
    }

    @Test
    public void testToString() {
        // Test the toString method
        String expectedString = "DeliverySchedule{" +
                "deliveryScheduleId=1, " +
                "subscription=" + subscription +
                ", deliveryDate=2024-08-24" +
                '}';
        assertEquals(expectedString, deliverySchedule.toString());
    }
}
