package com.hsbc.service;

import com.hsbc.dao.SubscriptionPlanDao;
import com.hsbc.exception.subscription.SubscriptionPlanNotFoundException;
import com.hsbc.model.Category;
import com.hsbc.model.Product;
import com.hsbc.model.SubscriptionPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class SubscriptionPlanServiceTest {

    @Mock
    private SubscriptionPlanDao subscriptionPlanDao;

    @InjectMocks
    private SubscriptionPlanService subscriptionPlanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSubscriptionPlanByIdSuccess() throws SubscriptionPlanNotFoundException {
        Product product = new Product(1, "Product1", "Description", 100.0, 2, new Category( "Fruits",1, "Fresh fruits and vegetables"));
        SubscriptionPlan plan = new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true);

        when(subscriptionPlanDao.findById(anyInt())).thenReturn(plan);

        SubscriptionPlan result = subscriptionPlanService.getSubscriptionPlanById(1);
        assertNotNull(result);
        assertEquals(1, result.getSubscriptionPlanId());
        assertEquals(product, result.getProduct());
        assertEquals(SubscriptionPlan.SubscriptionType.MONTHLY, result.getSubscriptionType());
        verify(subscriptionPlanDao).findById(1);
    }

    @Test
    void testGetSubscriptionPlanByIdNotFound() throws SubscriptionPlanNotFoundException {
        when(subscriptionPlanDao.findById(anyInt())).thenThrow(new SubscriptionPlanNotFoundException("Subscription Plan not found"));

        Exception exception = assertThrows(SubscriptionPlanNotFoundException.class, () -> {
            subscriptionPlanService.getSubscriptionPlanById(1);
        });

        assertEquals("Subscription Plan not found", exception.getMessage());
    }

    @Test
    void testGetAllSubscriptionPlans() {
        Product product1 = new Product(1, "Product1", "Description1", 100.0, 2, new Category( "Fruits", 1,"Fresh fruits and vegetables"));
        Product product2 = new Product(2, "Product2", "Description2", 150.0, 5, new Category("Vegetables",2, "Fresh vegetables"));

        List<SubscriptionPlan> plans = new ArrayList<>();
        plans.add(new SubscriptionPlan(1, product1, SubscriptionPlan.SubscriptionType.DAILY, 1, 5.0, true));
        plans.add(new SubscriptionPlan(2, product2, SubscriptionPlan.SubscriptionType.WEEKLY, 7, 15.0, false));

        when(subscriptionPlanDao.findAllSubscriptionPlans()).thenReturn(plans);

        List<SubscriptionPlan> result = subscriptionPlanService.getAllSubscriptionPlans();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(subscriptionPlanDao).findAllSubscriptionPlans();
    }

    @Test
    void testCreateSubscriptionPlan() {
        Product product = new Product(1, "Product1", "Description", 100.0, 2, new Category( "Fruits", 1,"Fresh fruits and vegetables"));
        SubscriptionPlan plan = new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true);

        doNothing().when(subscriptionPlanDao).addSubscriptionPlan(any(SubscriptionPlan.class));

        assertDoesNotThrow(() -> subscriptionPlanService.createSubscriptionPlan(plan));
        verify(subscriptionPlanDao).addSubscriptionPlan(plan);
    }

    @Test
    void testUpdateSubscriptionPlanSuccess() throws SubscriptionPlanNotFoundException {
        Product product = new Product(1, "Product1", "Description", 100.0, 2, new Category("Fruits",1, "Fresh fruits and vegetables"));
        SubscriptionPlan plan = new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true);

        doNothing().when(subscriptionPlanDao).updateSubscriptionPlan(any(SubscriptionPlan.class));

        assertDoesNotThrow(() -> subscriptionPlanService.updateSubscriptionPlan(plan));
        verify(subscriptionPlanDao).updateSubscriptionPlan(plan);
    }

    @Test
    void testUpdateSubscriptionPlanNotFound() throws SubscriptionPlanNotFoundException {
        Product product = new Product(1, "Product1", "Description", 100.0, 2, new Category( "Fruits", 1,"Fresh fruits and vegetables"));
        SubscriptionPlan plan = new SubscriptionPlan(1, product, SubscriptionPlan.SubscriptionType.MONTHLY, 30, 10.0, true);

        doThrow(new SubscriptionPlanNotFoundException("Subscription Plan not found")).when(subscriptionPlanDao).updateSubscriptionPlan(any(SubscriptionPlan.class));

        Exception exception = assertThrows(SubscriptionPlanNotFoundException.class, () -> {
            subscriptionPlanService.updateSubscriptionPlan(plan);
        });

        assertEquals("Subscription Plan not found", exception.getMessage());
    }

    @Test
    void testDeactivateSubscriptionPlanSuccess() throws SubscriptionPlanNotFoundException {
        doNothing().when(subscriptionPlanDao).deactivateSubscriptionPlan(anyInt());

        assertDoesNotThrow(() -> subscriptionPlanService.deactivateSubscriptionPlan(1));
        verify(subscriptionPlanDao).deactivateSubscriptionPlan(1);
    }

    @Test
    void testDeactivateSubscriptionPlanNotFound() throws SubscriptionPlanNotFoundException {
        doThrow(new SubscriptionPlanNotFoundException("Subscription Plan not found")).when(subscriptionPlanDao).deactivateSubscriptionPlan(anyInt());

        Exception exception = assertThrows(SubscriptionPlanNotFoundException.class, () -> {
            subscriptionPlanService.deactivateSubscriptionPlan(1);
        });

        assertEquals("Subscription Plan not found", exception.getMessage());
    }

    @Test
    void testActivateSubscriptionPlanSuccess() throws SubscriptionPlanNotFoundException {
        doNothing().when(subscriptionPlanDao).activateSubscriptionPlan(anyInt());

        assertDoesNotThrow(() -> subscriptionPlanService.activateSubscriptionPlan(1));
        verify(subscriptionPlanDao).activateSubscriptionPlan(1);
    }

    @Test
    void testActivateSubscriptionPlanNotFound() throws SubscriptionPlanNotFoundException {
        doThrow(new SubscriptionPlanNotFoundException("Subscription Plan not found")).when(subscriptionPlanDao).activateSubscriptionPlan(anyInt());

        Exception exception = assertThrows(SubscriptionPlanNotFoundException.class, () -> {
            subscriptionPlanService.activateSubscriptionPlan(1);
        });

        assertEquals("Subscription Plan not found", exception.getMessage());
    }

    @Test
    void testFindActivePlansByProduct() {
        Product product1 = new Product(1, "Product1", "Description1", 100.0, 2, new Category( "Fruits",1, "Fresh fruits and vegetables"));
        List<SubscriptionPlan> plans = new ArrayList<>();
        plans.add(new SubscriptionPlan(1, product1, SubscriptionPlan.SubscriptionType.DAILY, 1, 5.0, true));

        when(subscriptionPlanDao.findActivePlansByProduct(anyInt())).thenReturn(plans);

        List<SubscriptionPlan> result = subscriptionPlanService.findActivePlansByProduct(1);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(subscriptionPlanDao).findActivePlansByProduct(1);
    }
}
