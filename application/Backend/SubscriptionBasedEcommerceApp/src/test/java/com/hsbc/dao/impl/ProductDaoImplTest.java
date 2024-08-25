package com.hsbc.dao.impl;

import com.hsbc.dao.CategoryDao;
import com.hsbc.exception.order.InsufficientStockException;
import com.hsbc.exception.product.ProductNotFoundException;
import com.hsbc.model.Category;
import com.hsbc.model.Product;
import com.hsbc.db.DBUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductDaoImplTest {

    private Connection connection;
    private ProductDaoImpl productDao;
    private CategoryDao categoryDao;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        productDao = new ProductDaoImpl();
        productDao.setConnection(connection);
        categoryDao = mock(CategoryDao.class);
        // Inject the mocked CategoryDao into ProductDaoImpl if needed
    }

    @Test
    public void testFindById_ProductFound() throws SQLException, ProductNotFoundException {
        // Arrange
        int productId = 1;
        Product expectedProduct = new Product();
        expectedProduct.setProductId(productId);
        // Mock ResultSet
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("product_id")).thenReturn(productId);
        when(resultSet.getString("product_name")).thenReturn("Test Product");
        when(resultSet.getString("description")).thenReturn("Test Description");
        when(resultSet.getDouble("base_price")).thenReturn(10.0);
        when(resultSet.getInt("stock")).thenReturn(100);
        when(resultSet.getInt("category_id")).thenReturn(1);

        PreparedStatement statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(connection.prepareStatement(any(String.class))).thenReturn(statement);

        // Act
        Product actualProduct = productDao.findById(productId);

        // Assert
        assertEquals(expectedProduct.getProductId(), actualProduct.getProductId());
    }

    @Test
    public void testFindById_ProductNotFound() throws SQLException {
        // Arrange
        int productId = 1;
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(connection.prepareStatement(any(String.class))).thenReturn(statement);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productDao.findById(productId));
    }

    @Test
    public void testFindAllProducts() throws SQLException {
        // Arrange
        List<Product> expectedProducts = new ArrayList<>();
        Product product = new Product();
        product.setProductId(1);
        expectedProducts.add(product);

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("product_id")).thenReturn(1);
        when(resultSet.getString("product_name")).thenReturn("Test Product");
        when(resultSet.getString("description")).thenReturn("Test Description");
        when(resultSet.getDouble("base_price")).thenReturn(10.0);
        when(resultSet.getInt("stock")).thenReturn(100);
        when(resultSet.getInt("category_id")).thenReturn(1);

        PreparedStatement statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(connection.prepareStatement(any(String.class))).thenReturn(statement);

        // Act
        List<Product> actualProducts = productDao.findAllProducts();

        // Assert
        assertEquals(expectedProducts.size(), actualProducts.size());
    }

    @Test
    public void testAddProduct() throws SQLException {
        // Arrange
        Product product = new Product();
        product.setProductName("New Product");
        product.setDescription("New Description");
        product.setBasePrice(20.0);
        product.setStock(50);
        Category category = new Category();
        category.setCategoryId(1);
        product.setCategory(category);

        PreparedStatement statement = mock(PreparedStatement.class);
        when(statement.executeUpdate()).thenReturn(1);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(1);
        when(statement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(connection.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(statement);

        // Act
        productDao.addProduct(product);

        // Assert
        assertEquals(1, product.getProductId());
    }

    @Test
    public void testUpdateProduct_ProductExists() throws SQLException, ProductNotFoundException {
        // Arrange
        Product product = new Product();
        product.setProductId(1);
        product.setProductName("Updated Product");
        product.setDescription("Updated Description");
        product.setBasePrice(25.0);
        product.setStock(75);
        Category category = new Category();
        category.setCategoryId(2);
        product.setCategory(category);

        PreparedStatement statement = mock(PreparedStatement.class);
        when(statement.executeUpdate()).thenReturn(1);
        when(connection.prepareStatement(any(String.class))).thenReturn(statement);

        // Act
        productDao.updateProduct(product);

        // Assert
        verify(statement).executeUpdate();
    }

    @Test
    public void testUpdateProduct_ProductNotFound() throws SQLException {
        // Arrange
        Product product = new Product();
        product.setProductId(1);
        product.setProductName("Non-existent Product");

        // Initialize Category to avoid NullPointerException
        Category category = new Category();
        category.setCategoryId(1);
        product.setCategory(category);

        // Mock the PreparedStatement and its executeUpdate method to return 0
        PreparedStatement statement = mock(PreparedStatement.class);
        when(statement.executeUpdate()).thenReturn(0);
        when(connection.prepareStatement(any(String.class))).thenReturn(statement);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productDao.updateProduct(product));
    }


    @Test
    public void testDeleteProduct() throws SQLException, ProductNotFoundException {
        // Arrange
        int productId = 1;

        PreparedStatement deleteProductStmt = mock(PreparedStatement.class);
        when(deleteProductStmt.executeUpdate()).thenReturn(1);

        // Mock related deletes
        PreparedStatement deleteOrderItemsStmt = mock(PreparedStatement.class);
        PreparedStatement deleteSubscriptionsStmt = mock(PreparedStatement.class);
        PreparedStatement deleteSubscriptionPlansStmt = mock(PreparedStatement.class);

        when(connection.prepareStatement(any(String.class))).thenReturn(deleteOrderItemsStmt)
                .thenReturn(deleteSubscriptionsStmt)
                .thenReturn(deleteSubscriptionPlansStmt)
                .thenReturn(deleteProductStmt);

        // Act
        productDao.deleteProduct(productId);

        // Assert
        verify(deleteProductStmt).executeUpdate();
    }

    @Test
    public void testDecreaseProductStock_SufficientStock() throws SQLException, InsufficientStockException {
        // Arrange
        int productId = 1;
        int quantity = 5;

        PreparedStatement statement = mock(PreparedStatement.class);
        when(statement.executeUpdate()).thenReturn(1);
        when(connection.prepareStatement(any(String.class))).thenReturn(statement);

        // Act
        productDao.decreaseProductStock(productId, quantity);

        // Assert
        verify(statement).executeUpdate();
    }

    @Test
    public void testDecreaseProductStock_InsufficientStock() throws SQLException {
        // Arrange
        int productId = 1;
        int quantity = 5;

        PreparedStatement statement = mock(PreparedStatement.class);
        when(statement.executeUpdate()).thenReturn(0);
        when(connection.prepareStatement(any(String.class))).thenReturn(statement);

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> productDao.decreaseProductStock(productId, quantity));
    }

    @Test
    public void testFindProductsByCategory() throws SQLException {
        // Arrange
        int categoryId = 1;
        List<Product> expectedProducts = new ArrayList<>();
        Product product = new Product();
        product.setProductId(1);
        expectedProducts.add(product);

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("product_id")).thenReturn(1);
        when(resultSet.getString("product_name")).thenReturn("Test Product");
        when(resultSet.getString("description")).thenReturn("Test Description");
        when(resultSet.getDouble("base_price")).thenReturn(10.0);
        when(resultSet.getInt("stock")).thenReturn(100);
        when(resultSet.getInt("category_id")).thenReturn(categoryId);

        PreparedStatement statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(connection.prepareStatement(any(String.class))).thenReturn(statement);

        // Act
        List<Product> actualProducts = productDao.findProductsByCategory(categoryId);

        // Assert
        assertEquals(expectedProducts.size(), actualProducts.size());
    }
}
