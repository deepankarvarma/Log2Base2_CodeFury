package com.hsbc.service;

import com.hsbc.dao.SubscriptionDao;
import com.hsbc.exception.subscription.SubscriptionNotFoundException;
import com.hsbc.exception.user.UserNotFoundException;
import com.hsbc.model.Category;
import com.hsbc.model.Product;
import com.hsbc.model.Subscription;
import com.hsbc.model.SubscriptionPlan;
import com.hsbc.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    @Mock
    private SubscriptionDao subscriptionDao;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize the test user
        testUser = new User(
                1,
                "John Doe",
                "password123",
                "john@example.com",
                "1234567890",
                "123 Main St, Springfield",
                LocalDate.of(2023, 1, 1)
        );
    }

    @Test
    void testGetSubscriptionByIdSuccess() throws SubscriptionNotFoundException {
        Product product = new Product(1, "Product1", "Description", 100.0, 2, new Category( "Fruits",1, "Fresh fruits and vegetables"));

        SubscriptionPlan plan = new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true);
        Subscription subscription = new Subscription(1, testUser, new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true),LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 1), Subscription.Status.ACTIVE);

        when(subscriptionDao.findById(anyInt())).thenReturn(subscription);

        Subscription result = subscriptionService.getSubscriptionById(1);
        assertNotNull(result);
        assertEquals(1, result.getSubscriptionId());
        assertEquals(testUser, result.getUser());
        assertEquals(plan, result.getSubscriptionPlan());
        verify(subscriptionDao).findById(1);
    }

    @Test
    void testGetSubscriptionByIdNotFound() throws SubscriptionNotFoundException {
        when(subscriptionDao.findById(anyInt())).thenThrow(new SubscriptionNotFoundException("Subscription not found"));

        Exception exception = assertThrows(SubscriptionNotFoundException.class, () -> {
            subscriptionService.getSubscriptionById(1);
        });

        assertEquals("Subscription not found", exception.getMessage());
    }

    @Test
    void testGetAllSubscriptions() {
        User user2 = new User(2, "Jane Smith", "password123", "jane@example.com", "0987654321", "456 Elm St, Springfield", LocalDate.of(2023, 2, 1));

        Product product1 = new Product(1, "Product1", "Description", 100.0, 2, new Category( "Fruits",1, "Fresh fruits and vegetables"));
        Product product2 = new Product(2, "Product2", "Description2", 150.0, 5, new Category("Vegetables",2, "Fresh vegetables"));

        SubscriptionPlan plan1 = new SubscriptionPlan(1, product1, SubscriptionPlan.SubscriptionType.DAILY, 1, 5.0, true);
        SubscriptionPlan plan2 = new SubscriptionPlan(2, product2, SubscriptionPlan.SubscriptionType.WEEKLY, 7, 15.0, false);

        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(new Subscription(1, testUser, new SubscriptionPlan(1, product1, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true),LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 1), Subscription.Status.ACTIVE));
        subscriptions.add(new Subscription(12, testUser, new SubscriptionPlan(2, product2, SubscriptionPlan.SubscriptionType.WEEKLY, 7, 5.0, true),LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 1), Subscription.Status.ACTIVE));

        when(subscriptionDao.findAllSubscriptions()).thenReturn(subscriptions);

        List<Subscription> result = subscriptionService.getAllSubscriptions();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(subscriptionDao).findAllSubscriptions();
    }

    @Test
    void testCreateSubscription() {
        Product product = new Product(2, "Product2", "Description2", 150.0, 5, new Category("Vegetables",2, "Fresh vegetables"));
        SubscriptionPlan plan = new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true);
        Subscription subscription = new Subscription(1, testUser, new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true),LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 1), Subscription.Status.ACTIVE);

        doNothing().when(subscriptionDao).addSubscription(any(Subscription.class));

        assertDoesNotThrow(() -> subscriptionService.createSubscription(subscription));
        verify(subscriptionDao).addSubscription(subscription);
    }

    @Test
    void testUpdateSubscriptionSuccess() throws SubscriptionNotFoundException {
        Product product = new Product(2, "Product2", "Description2", 150.0, 5, new Category("Vegetables",2, "Fresh vegetables"));
        SubscriptionPlan plan = new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true);
        Subscription subscription = new Subscription(1, testUser, new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true),LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 1), Subscription.Status.ACTIVE);

        doNothing().when(subscriptionDao).updateSubscription(any(Subscription.class));

        assertDoesNotThrow(() -> subscriptionService.updateSubscription(subscription));
        verify(subscriptionDao).updateSubscription(subscription);
    }

    @Test
    void testUpdateSubscriptionNotFound() throws SubscriptionNotFoundException {
        Product product = new Product(2, "Product2", "Description2", 150.0, 5, new Category("Vegetables",2, "Fresh vegetables"));
        SubscriptionPlan plan = new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true);
        Subscription subscription = new Subscription(1, testUser, new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true),LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 1), Subscription.Status.ACTIVE);

        doThrow(new SubscriptionNotFoundException("Subscription not found")).when(subscriptionDao).updateSubscription(any(Subscription.class));

        Exception exception = assertThrows(SubscriptionNotFoundException.class, () -> {
            subscriptionService.updateSubscription(subscription);
        });

        assertEquals("Subscription not found", exception.getMessage());
    }

    @Test
    void testDeactivateSubscriptionSuccess() throws SubscriptionNotFoundException {
        doNothing().when(subscriptionDao).deactivateSubscription(anyInt());

        assertDoesNotThrow(() -> subscriptionService.deactivateSubscription(1));
        verify(subscriptionDao).deactivateSubscription(1);
    }

    @Test
    void testDeactivateSubscriptionNotFound() throws SubscriptionNotFoundException {
        doThrow(new SubscriptionNotFoundException("Subscription not found")).when(subscriptionDao).deactivateSubscription(anyInt());

        Exception exception = assertThrows(SubscriptionNotFoundException.class, () -> {
            subscriptionService.deactivateSubscription(1);
        });

        assertEquals("Subscription not found", exception.getMessage());
    }

    @Test
    void testActivateSubscriptionSuccess() throws SubscriptionNotFoundException {
        doNothing().when(subscriptionDao).activateSubscription(anyInt());

        assertDoesNotThrow(() -> subscriptionService.activateSubscription(1));
        verify(subscriptionDao).activateSubscription(1);
    }

    @Test
    void testActivateSubscriptionNotFound() throws SubscriptionNotFoundException {
        doThrow(new SubscriptionNotFoundException("Subscription not found")).when(subscriptionDao).activateSubscription(anyInt());

        Exception exception = assertThrows(SubscriptionNotFoundException.class, () -> {
            subscriptionService.activateSubscription(1);
        });

        assertEquals("Subscription not found", exception.getMessage());
    }

    @Test
    void testFindSubscriptionsByUserSuccess() throws UserNotFoundException {
        Product product = new Product(2, "Product2", "Description2", 150.0, 5, new Category("Vegetables",2, "Fresh vegetables"));
        SubscriptionPlan plan = new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true);

        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(new Subscription(1, testUser, new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true),LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 1), Subscription.Status.ACTIVE));

        when(subscriptionDao.findSubscriptionsByUser(anyInt())).thenReturn(subscriptions);

        List<Subscription> result = subscriptionService.findSubscriptionsByUser(1);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(subscriptionDao).findSubscriptionsByUser(1);
    }

    @Test
    void testFindSubscriptionsByUserNotFound() throws UserNotFoundException {
        when(subscriptionDao.findSubscriptionsByUser(anyInt())).thenThrow(new UserNotFoundException("User not found"));

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            subscriptionService.findSubscriptionsByUser(1);
        });

        assertEquals("User not found", exception.getMessage());
    }
}
