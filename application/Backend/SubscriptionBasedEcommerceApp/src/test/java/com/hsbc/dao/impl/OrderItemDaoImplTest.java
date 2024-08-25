package com.hsbc.dao.impl;

import com.hsbc.dao.impl.OrderDaoImpl;
import com.hsbc.dao.impl.OrderItemDaoImpl;
import com.hsbc.dao.impl.ProductDaoImpl;
import com.hsbc.exception.order.OrderItemNotFoundException;
import com.hsbc.exception.order.OrderNotFoundException;
import com.hsbc.exception.product.ProductNotFoundException;
import com.hsbc.model.Order;
import com.hsbc.model.OrderItem;
import com.hsbc.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderItemDaoImplTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private OrderDaoImpl orderDao;

    @Mock
    private ProductDaoImpl productDao;

    @InjectMocks
    private OrderItemDaoImpl orderItemDao;

    @BeforeEach
    void setUp() throws SQLException {
        // Mocking the Connection's behavior
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
    }


    @Test
    void testFindByIdNotFound() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        assertThrows(OrderItemNotFoundException.class, () -> orderItemDao.findById(999));
    }


    @Test
    void testAddOrderItem() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(new Order()); // Mock order setup
        orderItem.getOrder().setOrderId(1);
        orderItem.setProduct(new Product()); // Mock product setup
        orderItem.getProduct().setProductId(1);
        orderItem.setQuantity(2);
        orderItem.setPrice(20.0);

        orderItemDao.addOrderItem(orderItem);

        verify(preparedStatement).setInt(1, 1); // order_id
        verify(preparedStatement).setInt(2, 1); // product_id
        verify(preparedStatement).setInt(3, 2); // quantity
        verify(preparedStatement).setDouble(4, 20.0); // price
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testUpdateOrderItem() throws SQLException, OrderItemNotFoundException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(1);
        orderItem.setOrder(new Order()); // Mock order setup
        orderItem.getOrder().setOrderId(1);
        orderItem.setProduct(new Product()); // Mock product setup
        orderItem.getProduct().setProductId(1);
        orderItem.setQuantity(2);
        orderItem.setPrice(20.0);

        orderItemDao.updateOrderItem(orderItem);

        verify(preparedStatement).setInt(1, 1); // order_id
        verify(preparedStatement).setInt(2, 1); // product_id
        verify(preparedStatement).setInt(3, 2); // quantity
        verify(preparedStatement).setDouble(4, 20.0); // price
        verify(preparedStatement).setInt(5, 1); // order_item_id
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testUpdateOrderItemNotFound() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(0);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(999); // Non-existent ID
        orderItem.setOrder(new Order()); // Mock order setup
        orderItem.getOrder().setOrderId(1);
        orderItem.setProduct(new Product()); // Mock product setup
        orderItem.getProduct().setProductId(1);
        orderItem.setQuantity(2);
        orderItem.setPrice(20.0);

        assertThrows(OrderItemNotFoundException.class, () -> orderItemDao.updateOrderItem(orderItem));
    }

    @Test
    void testDeleteOrderItem() throws SQLException, OrderItemNotFoundException {
        when(preparedStatement.executeUpdate()).thenReturn(1);

        orderItemDao.deleteOrderItem(1);

        verify(preparedStatement).setInt(1, 1); // order_item_id
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testDeleteOrderItemNotFound() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(OrderItemNotFoundException.class, () -> orderItemDao.deleteOrderItem(999));
    }

}
