package com.hsbc.service;

import com.hsbc.dao.OrderItemDao;
import com.hsbc.exception.order.OrderItemNotFoundException;
import com.hsbc.model.OrderItem;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class OrderItemServiceTest {

    private OrderItemService orderItemService;

    @Mock
    private OrderItemDao mockOrderItemDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        orderItemService = new OrderItemService(mockOrderItemDao);
    }

    @Test
    public void testGetOrderItemById_Success() throws Exception {
        OrderItem mockOrderItem = new OrderItem();
        when(mockOrderItemDao.findById(anyInt())).thenReturn(mockOrderItem);

        OrderItem orderItem = orderItemService.getOrderItemById(1);
        assertNotNull(orderItem);
        verify(mockOrderItemDao).findById(1);
    }

    @Test(expected = OrderItemNotFoundException.class)
    public void testGetOrderItemById_NotFound() throws Exception {
        when(mockOrderItemDao.findById(anyInt())).thenThrow(new OrderItemNotFoundException("OrderItem not found"));

        orderItemService.getOrderItemById(1);
    }

    @Test
    public void testCreateOrderItem_Success() {
        OrderItem orderItem = new OrderItem();
        orderItemService.createOrderItem(orderItem);

        verify(mockOrderItemDao).addOrderItem(orderItem);
    }

    @Test
    public void testUpdateOrderItem_Success() throws Exception {
        OrderItem orderItem = new OrderItem();
        orderItemService.updateOrderItem(orderItem);

        verify(mockOrderItemDao).updateOrderItem(orderItem);
    }

    @Test(expected = OrderItemNotFoundException.class)
    public void testUpdateOrderItem_NotFound() throws Exception {
        doThrow(new OrderItemNotFoundException("OrderItem not found")).when(mockOrderItemDao).updateOrderItem(any(OrderItem.class));

        OrderItem orderItem = new OrderItem();
        orderItemService.updateOrderItem(orderItem);
    }

    @Test
    public void testDeleteOrderItem_Success() throws Exception {
        orderItemService.deleteOrderItem(1);

        verify(mockOrderItemDao).deleteOrderItem(1);
    }

    @Test(expected = OrderItemNotFoundException.class)
    public void testDeleteOrderItem_NotFound() throws Exception {
        doThrow(new OrderItemNotFoundException("OrderItem not found")).when(mockOrderItemDao).deleteOrderItem(anyInt());

        orderItemService.deleteOrderItem(1);
    }

    @Test
    public void testFindOrderItemsByOrder_Success() {
        List<OrderItem> mockOrderItems = new ArrayList<>();
        mockOrderItems.add(new OrderItem());

        when(mockOrderItemDao.findOrderItemsByOrder(anyInt())).thenReturn(mockOrderItems);

        List<OrderItem> orderItems = orderItemService.findOrderItemsByOrder(1);
        assertNotNull(orderItems);
        assertEquals(1, orderItems.size());
        verify(mockOrderItemDao).findOrderItemsByOrder(1);
    }

    // Additional tests for other methods can be added here...
}
