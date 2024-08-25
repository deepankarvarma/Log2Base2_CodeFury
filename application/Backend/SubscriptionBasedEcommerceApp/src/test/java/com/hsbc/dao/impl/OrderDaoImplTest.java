package com.hsbc.dao.impl;

import com.hsbc.dao.UserDao;
import com.hsbc.exception.order.OrderAlreadyExistsException;
import com.hsbc.exception.order.OrderNotFoundException;
import com.hsbc.exception.user.UserNotFoundException;
import com.hsbc.model.Order;
import com.hsbc.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OrderDaoImplTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private OrderDaoImpl orderDao;
    private UserDao mockUserDao;

    @BeforeEach
    void setUp() throws SQLException, UserNotFoundException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockUserDao = mock(UserDao.class);

        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        orderDao = new OrderDaoImpl();
        orderDao.setConnection(mockConnection);
        // Mock the UserDao used in the OrderDaoImpl
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        orderDao.setConnection(mockConnection);
        // Mock UserDao for the OrderDaoImpl instance
        when(mockUserDao.findById(anyInt())).thenReturn(new User());
    }

    @Test
    void testFindByIdSuccess() throws SQLException, OrderNotFoundException, UserNotFoundException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("order_id")).thenReturn(1);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getDate("order_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0);
        when(mockResultSet.getString("delivery_status")).thenReturn("DELIVERED");

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        Order order = orderDao.findById(1);

        assertNotNull(order);
        assertEquals(1, order.getOrderId());
        assertEquals(100.0, order.getTotalPrice());
        assertEquals(Order.DeliveryStatus.DELIVERED, order.getDeliveryStatus());
    }

    @Test
    void testFindByIdNotFound() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        assertThrows(OrderNotFoundException.class, () -> orderDao.findById(999));
    }

    @Test
    void testFindAllOrders() throws SQLException, UserNotFoundException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("order_id")).thenReturn(1, 2);
        when(mockResultSet.getInt("user_id")).thenReturn(1, 2);
        when(mockResultSet.getDate("order_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0, 200.0);
        when(mockResultSet.getString("delivery_status")).thenReturn("DELIVERED", "PENDING");

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        List<Order> orders = orderDao.findAllOrders();

        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertEquals(100.0, orders.get(0).getTotalPrice());
        assertEquals(200.0, orders.get(1).getTotalPrice());
    }

    @Test
    void testAddOrderSuccess() throws SQLException, OrderAlreadyExistsException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        User user = new User();
        user.setUserId(1);

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setTotalPrice(100.0);
        order.setDeliveryStatus(Order.DeliveryStatus.PENDING);

        orderDao.addOrder(order);

        verify(mockPreparedStatement, times(1)).executeUpdate();
        assertEquals(1, order.getOrderId());
    }

    @Test
    void testAddOrderAlreadyExists() throws SQLException {
        // Simulate the SQLException for a duplicate entry
        SQLException sqlException = new SQLException("Duplicate entry", "23000");
        when(mockPreparedStatement.executeUpdate()).thenThrow(sqlException);

        User user = new User();
        user.setUserId(1);

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setTotalPrice(100.0);
        order.setDeliveryStatus(Order.DeliveryStatus.PENDING);

        // Act & Assert
        OrderAlreadyExistsException thrown = assertThrows(OrderAlreadyExistsException.class, () -> orderDao.addOrder(order));

        // Verify the exception message
        assertTrue(thrown.getMessage().contains("Order already exists"));
    }

    @Test
    void testUpdateOrderSuccess() throws SQLException, OrderNotFoundException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        User user = new User();
        user.setUserId(1);

        Order order = new Order();
        order.setOrderId(1);
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setTotalPrice(150.0);
        order.setDeliveryStatus(Order.DeliveryStatus.DELIVERED);

        orderDao.updateOrder(order);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateOrderNotFound() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        User user = new User();
        user.setUserId(1);

        Order order = new Order();
        order.setOrderId(999);
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setTotalPrice(150.0);
        order.setDeliveryStatus(Order.DeliveryStatus.DELIVERED);

        assertThrows(OrderNotFoundException.class, () -> orderDao.updateOrder(order));
    }

    @Test
    void testCancelOrderSuccess() throws SQLException, OrderNotFoundException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        orderDao.cancelOrder(1);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testCancelOrderNotFound() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(OrderNotFoundException.class, () -> orderDao.cancelOrder(999));
    }

    @Test
    void testFindOrdersByUser() throws SQLException, UserNotFoundException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("order_id")).thenReturn(1, 2);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getDate("order_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0, 200.0);
        when(mockResultSet.getString("delivery_status")).thenReturn("DELIVERED", "PENDING");

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        List<Order> orders = orderDao.findOrdersByUser(1);

        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertEquals(100.0, orders.get(0).getTotalPrice());
        assertEquals(200.0, orders.get(1).getTotalPrice());
    }

    @Test
    void testFindOrdersByStatus() throws SQLException, UserNotFoundException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("order_id")).thenReturn(1, 2);
        when(mockResultSet.getInt("user_id")).thenReturn(1, 2);
        when(mockResultSet.getDate("order_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0, 200.0);
        when(mockResultSet.getString("delivery_status")).thenReturn("DELIVERED", "DELIVERED");

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        List<Order> orders = orderDao.findOrdersByStatus(Order.DeliveryStatus.DELIVERED);

        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertEquals(Order.DeliveryStatus.DELIVERED, orders.get(0).getDeliveryStatus());
        assertEquals(Order.DeliveryStatus.DELIVERED, orders.get(1).getDeliveryStatus());
    }

    @Test
    void testFindOrdersByDate() throws SQLException, UserNotFoundException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("order_id")).thenReturn(1, 2);
        when(mockResultSet.getInt("user_id")).thenReturn(1, 2);
        when(mockResultSet.getDate("order_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0, 200.0);
        when(mockResultSet.getString("delivery_status")).thenReturn("DELIVERED", "PENDING");

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        List<Order> orders = orderDao.findOrdersByDate(LocalDate.now());

        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertEquals(LocalDate.now(), orders.get(0).getOrderDate());
        assertEquals(LocalDate.now(), orders.get(1).getOrderDate());
    }

    @Test
    void testFindOrderBySubscriptionAndDate() throws SQLException, UserNotFoundException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("order_id")).thenReturn(1);
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getDate("order_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockResultSet.getDouble("total_price")).thenReturn(100.0);
        when(mockResultSet.getString("delivery_status")).thenReturn("DELIVERED");

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        Order order = orderDao.findOrderBySubscriptionAndDate(1, LocalDate.now());

        assertNotNull(order);
        assertEquals(1, order.getOrderId());
        assertEquals(LocalDate.now(), order.getOrderDate());
    }

    @Test
    void testUpdateOrderStatusSuccess() throws SQLException, OrderNotFoundException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        orderDao.updateOrderStatus(1, Order.DeliveryStatus.SHIPPED);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateOrderStatusNotFound() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(OrderNotFoundException.class, () -> orderDao.updateOrderStatus(999, Order.DeliveryStatus.SHIPPED));
    }
}
