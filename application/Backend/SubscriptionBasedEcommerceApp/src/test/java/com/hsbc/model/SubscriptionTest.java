package com.hsbc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubscriptionTest {

    private Subscription subscription;
    private User user;
    private SubscriptionPlan subscriptionPlan;

    @BeforeEach
    public void setUp() {
        user = new User(
                1,
                "John Doe",
                "password123",
                "john.doe@example.com",
                "123-456-7890",
                "123 Elm Street",
                LocalDate.of(2024, 1, 1)
        );

        subscriptionPlan = new SubscriptionPlan(
                1,
                new Product(
                        1,
                        "Product Name",
                        "Product Description",
                        19.99,
                        100,
                        new Category("Category Name",1, "Category Description")
                ),
                SubscriptionPlan.SubscriptionType.MONTHLY,
                30,
                10.0,
                true
        );

        subscription = new Subscription(
                1,
                user,
                subscriptionPlan,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                Subscription.Status.ACTIVE
        );
    }

    @Test
    public void testDefaultConstructor() {
        Subscription defaultSubscription = new Subscription();
        assertNotNull(defaultSubscription);
    }

    @Test
    public void testParameterizedConstructor() {
        assertEquals(1, subscription.getSubscriptionId());
        assertEquals(user, subscription.getUser());
        assertEquals(subscriptionPlan, subscription.getSubscriptionPlan());
        assertEquals(LocalDate.of(2024, 1, 1), subscription.getStartDate());
        assertEquals(LocalDate.of(2024, 12, 31), subscription.getEndDate());
        assertEquals(Subscription.Status.ACTIVE, subscription.getStatus());
    }

    @Test
    public void testGettersAndSetters() {
        subscription.setSubscriptionId(2);
        subscription.setUser(null); // Setting to null for testing
        subscription.setSubscriptionPlan(null); // Setting to null for testing
        subscription.setStartDate(LocalDate.of(2024, 2, 1));
        subscription.setEndDate(LocalDate.of(2024, 11, 30));
        subscription.setStatus(Subscription.Status.INACTIVE);

        assertEquals(2, subscription.getSubscriptionId());
        assertEquals(null, subscription.getUser());
        assertEquals(null, subscription.getSubscriptionPlan());
        assertEquals(LocalDate.of(2024, 2, 1), subscription.getStartDate());
        assertEquals(LocalDate.of(2024, 11, 30), subscription.getEndDate());
        assertEquals(Subscription.Status.INACTIVE, subscription.getStatus());
    }

    @Test
    public void testToString() {
        String expectedString = "Subscription{" +
                "subscriptionId=" + 1 +
                ", user=" + user +
                ", subscriptionPlan=" + subscriptionPlan +
                ", startDate=" + LocalDate.of(2024, 1, 1) +
                ", endDate=" + LocalDate.of(2024, 12, 31) +
                ", status=" + Subscription.Status.ACTIVE +
                '}';
        assertEquals(expectedString, subscription.toString());
    }
}
