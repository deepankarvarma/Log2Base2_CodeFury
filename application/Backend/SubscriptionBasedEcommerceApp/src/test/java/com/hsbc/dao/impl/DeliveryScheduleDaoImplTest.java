package com.hsbc.dao.impl;

import com.hsbc.dao.SubscriptionDao;
import com.hsbc.dao.impl.DeliveryScheduleDaoImpl;
import com.hsbc.exception.delivery.DeliveryScheduleNotFoundException;
import com.hsbc.exception.subscription.SubscriptionNotFoundException;
import com.hsbc.model.DeliverySchedule;
import com.hsbc.model.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeliveryScheduleDaoImplTest {

    private DeliveryScheduleDaoImpl deliveryScheduleDao;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private SubscriptionDao subscriptionDao;

    @BeforeEach
    void setUp() throws SQLException {
        // Create mock objects
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        subscriptionDao = mock(SubscriptionDao.class);

        // Initialize the DeliveryScheduleDaoImpl with the mocked connection and DAO
        deliveryScheduleDao = new DeliveryScheduleDaoImpl(connection);

        // Set up common behavior for mocks
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
    }

    @Test
    void testFindByIdNotFound() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        assertThrows(DeliveryScheduleNotFoundException.class, () -> deliveryScheduleDao.findById(999));
    }


    @Test
    void testAddDeliverySchedule() throws SQLException {
        // Define behavior for the executeUpdate method
        when(preparedStatement.executeUpdate()).thenReturn(1); // Simulate successful update

        DeliverySchedule schedule = new DeliverySchedule();
        schedule.setSubscription(new Subscription()); // Mock subscription setup
        schedule.getSubscription().setSubscriptionId(1);
        schedule.setDeliveryDate(LocalDate.of(2024, 8, 24));

        deliveryScheduleDao.addDeliverySchedule(schedule);

        verify(preparedStatement).setInt(1, 1); // subscription_id
        verify(preparedStatement).setDate(2, Date.valueOf(LocalDate.of(2024, 8, 24))); // delivery_date
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testUpdateDeliverySchedule() throws SQLException, DeliveryScheduleNotFoundException {
        // Define behavior for the executeUpdate method
        when(preparedStatement.executeUpdate()).thenReturn(1); // Simulate successful update

        DeliverySchedule schedule = new DeliverySchedule();
        schedule.setDeliveryScheduleId(1);
        schedule.setSubscription(new Subscription()); // Mock subscription setup
        schedule.getSubscription().setSubscriptionId(1);
        schedule.setDeliveryDate(LocalDate.of(2024, 8, 24));

        deliveryScheduleDao.updateDeliverySchedule(schedule);

        verify(preparedStatement).setInt(1, 1); // subscription_id
        verify(preparedStatement).setDate(2, Date.valueOf(LocalDate.of(2024, 8, 24))); // delivery_date
        verify(preparedStatement).setInt(3, 1); // delivery_schedule_id
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testUpdateDeliveryScheduleNotFound() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(0);

        DeliverySchedule schedule = new DeliverySchedule();
        schedule.setDeliveryScheduleId(999); // Non-existent ID
        schedule.setSubscription(new Subscription()); // Mock subscription setup
        schedule.getSubscription().setSubscriptionId(1);
        schedule.setDeliveryDate(LocalDate.of(2024, 8, 24));

        assertThrows(DeliveryScheduleNotFoundException.class, () -> deliveryScheduleDao.updateDeliverySchedule(schedule));
    }

    @Test
    void testDeleteDeliverySchedule() throws SQLException, DeliveryScheduleNotFoundException {
        // Define behavior for the executeUpdate method
        when(preparedStatement.executeUpdate()).thenReturn(1); // Simulate successful deletion

        deliveryScheduleDao.deleteDeliverySchedule(1);

        verify(preparedStatement).setInt(1, 1); // delivery_schedule_id
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testDeleteDeliveryScheduleNotFound() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(DeliveryScheduleNotFoundException.class, () -> deliveryScheduleDao.deleteDeliverySchedule(999));
    }




}
