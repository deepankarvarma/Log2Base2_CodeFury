package com.hsbc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubscriptionPlanTest {

    private SubscriptionPlan subscriptionPlan;
    private Product product;

    @BeforeEach
    public void setUp() {
        // Initialize Product object for testing purposes
        product = new Product(
                1,
                "Product Name",
                "Product Description",
                19.99,
                100,
                new Category( "Category Name",1, "Category Description")
        );

        subscriptionPlan = new SubscriptionPlan(
                1,
                product,
                SubscriptionPlan.SubscriptionType.MONTHLY,
                30,
                10.0,
                true
        );
    }

    @Test
    public void testDefaultConstructor() {
        SubscriptionPlan defaultPlan = new SubscriptionPlan();
        assertNotNull(defaultPlan);
    }

    @Test
    public void testParameterizedConstructor() {
        assertEquals(1, subscriptionPlan.getSubscriptionPlanId());
        assertEquals(product, subscriptionPlan.getProduct());
        assertEquals(SubscriptionPlan.SubscriptionType.MONTHLY, subscriptionPlan.getSubscriptionType());
        assertEquals(30, subscriptionPlan.getIntervalDays());
        assertEquals(10.0, subscriptionPlan.getDiscountRate());
        assertEquals(true, subscriptionPlan.isActive());
    }

    @Test
    public void testGettersAndSetters() {
        subscriptionPlan.setSubscriptionPlanId(2);
        subscriptionPlan.setProduct(null); // Setting to null for testing
        subscriptionPlan.setSubscriptionType(SubscriptionPlan.SubscriptionType.WEEKLY);
        subscriptionPlan.setIntervalDays(7);
        subscriptionPlan.setDiscountRate(5.0);
        subscriptionPlan.setActive(false);

        assertEquals(2, subscriptionPlan.getSubscriptionPlanId());
        assertEquals(null, subscriptionPlan.getProduct());
        assertEquals(SubscriptionPlan.SubscriptionType.WEEKLY, subscriptionPlan.getSubscriptionType());
        assertEquals(7, subscriptionPlan.getIntervalDays());
        assertEquals(5.0, subscriptionPlan.getDiscountRate());
        assertEquals(false, subscriptionPlan.isActive());
    }

    @Test
    public void testToString() {
        String expectedString = "SubscriptionPlan{" +
                "subscriptionPlanId=" + 1 +
                ", product=" + product +
                ", subscriptionType=" + SubscriptionPlan.SubscriptionType.MONTHLY +
                ", intervalDays=" + 30 +
                ", discountRate=" + 10.0 +
                ", isActive=" + true +
                '}';
        assertEquals(expectedString, subscriptionPlan.toString());
    }
}
