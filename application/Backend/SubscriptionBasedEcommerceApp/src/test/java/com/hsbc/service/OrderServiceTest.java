package com.hsbc.service;

import com.hsbc.dao.OrderDao;
import com.hsbc.dao.OrderItemDao;
import com.hsbc.dao.ProductDao;
import com.hsbc.exception.order.InsufficientStockException;
import com.hsbc.exception.order.OrderAlreadyExistsException;
import com.hsbc.exception.order.OrderNotFoundException;
import com.hsbc.exception.user.UserNotFoundException;
import com.hsbc.model.Order;
import com.hsbc.model.OrderItem;
import com.hsbc.model.Product;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private OrderDao mockOrderDao;

    @Mock
    private OrderItemDao mockOrderItemDao;

    @Mock
    private ProductDao mockProductDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        orderService = new OrderService(mockOrderDao, mockOrderItemDao, mockProductDao);
    }

    @Test
    public void testGetOrderById_Success() throws Exception {
        Order mockOrder = new Order();
        when(mockOrderDao.findById(anyInt())).thenReturn(mockOrder);

        Order order = orderService.getOrderById(1);
        assertNotNull(order);
        verify(mockOrderDao).findById(1);
    }

    @Test(expected = OrderNotFoundException.class)
    public void testGetOrderById_NotFound() throws Exception {
        when(mockOrderDao.findById(anyInt())).thenThrow(new OrderNotFoundException("Order not found"));

        orderService.getOrderById(1);
    }

    @Test
    public void testCreateOrder_Success() throws Exception {
        Order order = new Order();
        orderService.createOrder(order);

        verify(mockOrderDao).addOrder(order);
    }

    @Test(expected = OrderAlreadyExistsException.class)
    public void testCreateOrder_AlreadyExists() throws Exception {
        doThrow(new OrderAlreadyExistsException("Order already exists")).when(mockOrderDao).addOrder(any(Order.class));

        Order order = new Order();
        orderService.createOrder(order);
    }

    @Test
    public void testMarkOrderAsDelivered_Success() throws Exception {
        // Setup
        Order mockOrder = new Order();
        mockOrder.setDeliveryStatus(Order.DeliveryStatus.PENDING);

        Product mockProduct = new Product();
        mockProduct.setProductId(1); // Set a valid product ID

        OrderItem mockOrderItem = new OrderItem();
        mockOrderItem.setQuantity(2);
        mockOrderItem.setProduct(mockProduct); // Set the product in the order item

        List<OrderItem> mockOrderItems = new ArrayList<>();
        mockOrderItems.add(mockOrderItem);

        when(mockOrderDao.findById(anyInt())).thenReturn(mockOrder);
        when(mockOrderItemDao.findOrderItemsByOrder(anyInt())).thenReturn(mockOrderItems);

        // Execution
        orderService.markOrderAsDelivered(1);

        // Verification
        verify(mockProductDao).decreaseProductStock(anyInt(), anyInt());
        verify(mockOrderDao).updateOrderStatus(1, Order.DeliveryStatus.DELIVERED);
    }

    @Test(expected = InsufficientStockException.class)
    public void testMarkOrderAsDelivered_InsufficientStock() throws Exception {
        Order mockOrder = new Order();
        mockOrder.setDeliveryStatus(Order.DeliveryStatus.PENDING);

        OrderItem mockOrderItem = new OrderItem();
        mockOrderItem.setQuantity(2);

        List<OrderItem> mockOrderItems = new ArrayList<>();
        mockOrderItems.add(mockOrderItem);

        when(mockOrderDao.findById(anyInt())).thenReturn(mockOrder);
        when(mockOrderItemDao.findOrderItemsByOrder(anyInt())).thenReturn(mockOrderItems);
        doThrow(new InsufficientStockException("Insufficient stock")).when(mockProductDao).decreaseProductStock(anyInt(), anyInt());

        orderService.markOrderAsDelivered(1);
    }

}