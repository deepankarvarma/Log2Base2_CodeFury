package com.hsbc.dao.impl;

import com.hsbc.dao.SubscriptionPlanDao;
import com.hsbc.dao.UserDao;
import com.hsbc.exception.subscription.SubscriptionNotFoundException;
import com.hsbc.exception.subscription.SubscriptionPlanNotFoundException;
import com.hsbc.exception.user.UserNotFoundException;
import com.hsbc.model.Subscription;
import com.hsbc.model.User;
import com.hsbc.model.SubscriptionPlan;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class SubscriptionDaoImplTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private SubscriptionPlanDao mockSubscriptionPlanDao;

    @InjectMocks
    private SubscriptionDaoImpl subscriptionDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subscriptionDao = new SubscriptionDaoImpl(mockConnection, mockUserDao, mockSubscriptionPlanDao);
    }

    @Test
    public void testFindById_Success() throws Exception {
        // Setup
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("subscription_id")).thenReturn(1);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getInt("subscription_plan_id")).thenReturn(1);
        when(mockResultSet.getDate("start_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockResultSet.getDate("end_date")).thenReturn(Date.valueOf(LocalDate.now().plusDays(10)));
        when(mockResultSet.getString("status")).thenReturn("ACTIVE");

        when(mockUserDao.findById(anyInt())).thenReturn(new User());
        when(mockSubscriptionPlanDao.findById(anyInt())).thenReturn(new SubscriptionPlan());

        // Execution
        Subscription result = subscriptionDao.findById(1);

        // Verification
        assertNotNull(result);
        assertEquals(1, result.getSubscriptionId());
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test(expected = SubscriptionNotFoundException.class)
    public void testFindById_NotFound() throws Exception {
        // Setup
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Execution
        subscriptionDao.findById(1);

        // Verification
        // Exception expected
    }

    @Test
    public void testFindAllSubscriptions_Success() throws Exception {
        // Setup
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("subscription_id")).thenReturn(1, 2);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getInt("subscription_plan_id")).thenReturn(1);
        when(mockResultSet.getDate("start_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockResultSet.getDate("end_date")).thenReturn(Date.valueOf(LocalDate.now().plusDays(10)));
        when(mockResultSet.getString("status")).thenReturn("ACTIVE");

        when(mockUserDao.findById(anyInt())).thenReturn(new User());
        when(mockSubscriptionPlanDao.findById(anyInt())).thenReturn(new SubscriptionPlan());

        // Execution
        List<Subscription> result = subscriptionDao.findAllSubscriptions();

        // Verification
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test(expected = SubscriptionNotFoundException.class)
    public void testUpdateSubscription_NotFound() throws Exception {
        // Setup
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(1);

        // Setting up a User object to avoid NullPointerException
        User user = new User();
        user.setUserId(1);
        subscription.setUser(user);

        // Setting up a SubscriptionPlan object to avoid NullPointerException
        SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
        subscriptionPlan.setSubscriptionPlanId(1);
        subscription.setSubscriptionPlan(subscriptionPlan);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // Simulate no rows being updated

        // Execution
        subscriptionDao.updateSubscription(subscription);

        // Verification
        // Exception expected
    }

    @Test
    public void testDeactivateSubscription_Success() throws Exception {
        // Setup
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Execution
        subscriptionDao.deactivateSubscription(1);

        // Verification
        verify(mockConnection).prepareStatement(contains("UPDATE Subscriptions SET status = 'INACTIVE' WHERE subscription_id = ?"));
        verify(mockConnection).prepareStatement(contains("DELETE FROM delivery_schedules WHERE subscription_id = ? AND delivery_date > ?"));

        verify(mockPreparedStatement, times(2)).setInt(1, 1);
        verify(mockPreparedStatement, times(2)).executeUpdate();
    }



    @Test
    public void testFindActiveSubscriptionsByUser_Success() throws Exception {
        // Setup
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("subscription_id")).thenReturn(1, 2);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getInt("subscription_plan_id")).thenReturn(1);
        when(mockResultSet.getDate("start_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockResultSet.getDate("end_date")).thenReturn(Date.valueOf(LocalDate.now().plusDays(10)));
        when(mockResultSet.getString("status")).thenReturn("ACTIVE");

        when(mockUserDao.findById(anyInt())).thenReturn(new User());
        when(mockSubscriptionPlanDao.findById(anyInt())).thenReturn(new SubscriptionPlan());

        // Execution
        List<Subscription> result = subscriptionDao.findActiveSubscriptionsByUser(1);

        // Verification
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
