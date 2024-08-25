package com.hsbc.dao.impl;

import com.hsbc.dao.ProductDao;
import com.hsbc.exception.product.ProductNotFoundException;
import com.hsbc.exception.subscription.SubscriptionPlanNotFoundException;
import com.hsbc.model.Product;
import com.hsbc.model.SubscriptionPlan;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SubscriptionPlanDaoImplTest {

    private SubscriptionPlanDaoImpl subscriptionPlanDao;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @Mock
    private ProductDao mockProductDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        // Injecting mockConnection and mockProductDao into SubscriptionPlanDaoImpl using the constructor
        subscriptionPlanDao = new SubscriptionPlanDaoImpl(mockConnection, mockProductDao);
    }

    @Test(expected = SubscriptionPlanNotFoundException.class)
    public void testFindById_NotFound() throws Exception {
        // Setup
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Execution
        subscriptionPlanDao.findById(1);

        // Verification
        // Exception expected
    }

    @Test
    public void testAddSubscriptionPlan_Success() throws Exception {
        // Setup
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setProduct(new Product());
        plan.setSubscriptionType(SubscriptionPlan.SubscriptionType.MONTHLY);
        plan.setIntervalDays(30);
        plan.setDiscountRate(10.0);
        plan.setActive(true);

        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        // Execution
        subscriptionPlanDao.addSubscriptionPlan(plan);

        // Verification
        verify(mockConnection).prepareStatement(anyString(), anyInt());
        verify(mockPreparedStatement).executeUpdate();
        assertEquals(1, plan.getSubscriptionPlanId());
    }

    @Test(expected = SubscriptionPlanNotFoundException.class)
    public void testUpdateSubscriptionPlan_NotFound() throws Exception {
        // Setup
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setSubscriptionPlanId(1);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // Execution
        subscriptionPlanDao.updateSubscriptionPlan(plan);

        // Verification
        // Exception expected
    }

    @Test
    public void testUpdateSubscriptionPlan_Success() throws Exception {
        // Setup
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setSubscriptionPlanId(1);
        plan.setProduct(new Product());
        plan.setSubscriptionType(SubscriptionPlan.SubscriptionType.MONTHLY);
        plan.setIntervalDays(30);
        plan.setDiscountRate(10.0);
        plan.setActive(true);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Execution
        subscriptionPlanDao.updateSubscriptionPlan(plan);

        // Verification
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).setInt(1, plan.getProduct().getProductId());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testDeactivateSubscriptionPlan_Success() throws Exception {
        // Setup
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Execution
        subscriptionPlanDao.deactivateSubscriptionPlan(1);

        // Verification
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test(expected = SubscriptionPlanNotFoundException.class)
    public void testDeactivateSubscriptionPlan_NotFound() throws Exception {
        // Setup
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // Execution
        subscriptionPlanDao.deactivateSubscriptionPlan(1);

        // Verification
        // Exception expected
    }

    @Test
    public void testActivateSubscriptionPlan_Success() throws Exception {
        // Setup
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Execution
        subscriptionPlanDao.activateSubscriptionPlan(1);

        // Verification
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeUpdate();
    }
}
