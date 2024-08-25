//package hsbc.dao.impl;
//
//import com.hsbc.dao.impl.CategoryDaoImpl;
//import com.hsbc.exception.category.CategoryAlreadyExistsException;
//import com.hsbc.exception.category.CategoryNotFoundException;
//import com.hsbc.model.Category;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class CategoryDaoImplTest {
//
//    private CategoryDaoImpl categoryDao;
//    private Connection connection;
//    private PreparedStatement preparedStatement;
//    private ResultSet resultSet;
//
//    @BeforeEach
//    void setUp() throws SQLException {
//        // Create mock objects
//        connection = mock(Connection.class);
//        preparedStatement = mock(PreparedStatement.class);
//        resultSet = mock(ResultSet.class);
//
//        // Initialize the CategoryDaoImpl with the mocked connection
//        categoryDao = new CategoryDaoImpl(connection);
//
//        // Set up common behavior for mocks
//        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
//    }
//
//    @Test
//    void testGetCategoryById() throws SQLException, CategoryNotFoundException {
//        // Define behavior for the ResultSet mock
//        when(resultSet.next()).thenReturn(true);
//        when(resultSet.getInt("category_id")).thenReturn(1);
//        when(resultSet.getString("category_name")).thenReturn("Electronics");
//        when(resultSet.getString("category_description")).thenReturn("Devices and gadgets");
//
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//
//        // Call the method under test
//        Category category = categoryDao.getCategoryById(1);
//
//        // Verify the result
//        assertNotNull(category);
//        assertEquals(1, category.getCategoryId());
//        assertEquals("Electronics", category.getCategoryName());
//        assertEquals("Devices and gadgets", category.getCategoryDescription());
//    }
//
//    @Test
//    void testGetCategoryByIdNotFound() throws SQLException {
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//        when(resultSet.next()).thenReturn(false);
//
//        assertThrows(CategoryNotFoundException.class, () -> categoryDao.getCategoryById(999));
//    }
//
//    @Test
//    void testGetAllCategories() throws SQLException {
//        // Define behavior for the ResultSet mock
//        when(resultSet.next()).thenReturn(true).thenReturn(false);
//        when(resultSet.getInt("category_id")).thenReturn(1);
//        when(resultSet.getString("category_name")).thenReturn("Electronics");
//        when(resultSet.getString("category_description")).thenReturn("Devices and gadgets");
//
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//
//        // Call the method under test
//        List<Category> categories = categoryDao.getAllCategories();
//
//        // Verify the result
//        assertNotNull(categories);
//        assertEquals(1, categories.size());
//        assertEquals("Electronics", categories.get(0).getCategoryName());
//    }
//
//    @Test
//    void testAddCategory() throws SQLException, CategoryAlreadyExistsException {
//        // Define behavior for the executeUpdate method
//        when(preparedStatement.executeUpdate()).thenReturn(1); // Simulate successful update
//
//        Category category = new Category();
//        category.setCategoryName("Furniture");
//        category.setCategoryDescription("A category for furniture");
//
//        categoryDao.addCategory(category);
//
//        verify(preparedStatement).setString(1, "Furniture");
//        verify(preparedStatement).setString(2, "A category for furniture");
//        verify(preparedStatement).executeUpdate();
//    }
//
//
//    @Test
//    void testAddCategoryAlreadyExists() throws SQLException {
//        SQLException sqlException = new SQLException("Duplicate key", "23505");
//        when(preparedStatement.executeUpdate()).thenThrow(sqlException);
//
//        Category category = new Category();
//        category.setCategoryName("Furniture");
//        category.setCategoryDescription("A category for furniture");
//
//        assertThrows(CategoryAlreadyExistsException.class, () -> categoryDao.addCategory(category));
//    }
//
//    @Test
//    void testUpdateCategory() throws SQLException, CategoryNotFoundException {
//        when(preparedStatement.executeUpdate()).thenReturn(1);
//
//        Category category = new Category();
//        category.setCategoryId(1);
//        category.setCategoryName("Updated Name");
//        category.setCategoryDescription("Updated Description");
//
//        categoryDao.updateCategory(category);
//
//        verify(preparedStatement).setString(1, "Updated Name");
//        verify(preparedStatement).setString(2, "Updated Description");
//        verify(preparedStatement).setInt(3, 1);
//        verify(preparedStatement).executeUpdate();
//    }
//
//    @Test
//    void testUpdateCategoryNotFound() throws SQLException {
//        when(preparedStatement.executeUpdate()).thenReturn(0);
//
//        Category category = new Category();
//        category.setCategoryId(999);
//        category.setCategoryName("Non-existent Category");
//
//        assertThrows(CategoryNotFoundException.class, () -> categoryDao.updateCategory(category));
//    }
//
//    @Test
//    void testDeleteCategory() throws SQLException, CategoryNotFoundException {
//        when(preparedStatement.executeUpdate()).thenReturn(1);
//
//        categoryDao.deleteCategory(1);
//
//        verify(preparedStatement).setInt(1, 1);
//        verify(preparedStatement).executeUpdate();
//    }
//
//    @Test
//    void testDeleteCategoryNotFound() throws SQLException {
//        when(preparedStatement.executeUpdate()).thenReturn(0);
//
//        assertThrows(CategoryNotFoundException.class, () -> categoryDao.deleteCategory(999));
//    }
//}

package com.hsbc.dao.impl;

import com.hsbc.exception.category.CategoryAlreadyExistsException;
import com.hsbc.exception.category.CategoryNotFoundException;
import com.hsbc.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CategoryDaoImplTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private CategoryDaoImpl categoryDao;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        categoryDao = new CategoryDaoImpl(mockConnection);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void testGetCategoryByIdSuccess() throws SQLException, CategoryNotFoundException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("category_id")).thenReturn(1);
        when(mockResultSet.getString("category_name")).thenReturn("Electronics");
        when(mockResultSet.getString("category_description")).thenReturn("All electronic items");

        Category category = categoryDao.getCategoryById(1);

        assertNotNull(category);
        assertEquals(1, category.getCategoryId());
        assertEquals("Electronics", category.getCategoryName());
        assertEquals("All electronic items", category.getCategoryDescription());
    }

    @Test
    void testGetCategoryByIdNotFound() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        assertThrows(CategoryNotFoundException.class, () -> {
            categoryDao.getCategoryById(999);
        });
    }

    @Test
    void testGetAllCategoriesSuccess() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("category_id")).thenReturn(1, 2);
        when(mockResultSet.getString("category_name")).thenReturn("Electronics", "Clothing");
        when(mockResultSet.getString("category_description")).thenReturn("All electronic items", "Clothes and accessories");

        List<Category> categories = categoryDao.getAllCategories();

        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertEquals("Electronics", categories.get(0).getCategoryName());
        assertEquals("Clothing", categories.get(1).getCategoryName());
    }

    @Test
    void testAddCategorySuccess() throws SQLException, CategoryAlreadyExistsException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Category category = new Category("New Category", 0, "New Category Description");
        categoryDao.addCategory(category);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testAddCategoryAlreadyExists() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Duplicate entry", "23505"));

        Category category = new Category("Existing Category", 0, "Existing Category Description");

        assertThrows(CategoryAlreadyExistsException.class, () -> {
            categoryDao.addCategory(category);
        });
    }

    @Test
    void testUpdateCategorySuccess() throws SQLException, CategoryNotFoundException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Category category = new Category("Updated Category", 0, "Updated Description");
        categoryDao.updateCategory(category);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateCategoryNotFound() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        Category category = new Category("Nonexistent Category", 1, "Nonexistent Description");

        assertThrows(CategoryNotFoundException.class, () -> {
            categoryDao.updateCategory(category);
        });
    }

    @Test
    void testDeleteCategorySuccess() throws SQLException, CategoryNotFoundException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // No associated products found
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        categoryDao.deleteCategory(1);

        verify(mockPreparedStatement, times(1)).executeUpdate();
        verify(mockConnection, times(1)).commit();
    }

    @Test
    void testDeleteCategoryNotFound() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(CategoryNotFoundException.class, () -> {
            categoryDao.deleteCategory(999);
        });
    }

    @Test
    void testDeleteCategoryWithDependencies() throws SQLException, CategoryNotFoundException {
        // Mock different PreparedStatements for each query
        PreparedStatement mockSelectProductsStmt = mock(PreparedStatement.class);
        PreparedStatement mockDeleteOrderItemsStmt = mock(PreparedStatement.class);
        PreparedStatement mockDeleteSubscriptionsStmt = mock(PreparedStatement.class);
        PreparedStatement mockDeleteSubscriptionPlansStmt = mock(PreparedStatement.class);
        PreparedStatement mockDeleteProductStmt = mock(PreparedStatement.class);
        PreparedStatement mockDeleteCategoryStmt = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(any(String.class)))
                .thenReturn(mockSelectProductsStmt)
                .thenReturn(mockDeleteOrderItemsStmt)
                .thenReturn(mockDeleteSubscriptionsStmt)
                .thenReturn(mockDeleteSubscriptionPlansStmt)
                .thenReturn(mockDeleteProductStmt)
                .thenReturn(mockDeleteCategoryStmt);

        // Mock ResultSet to simulate products found for the category
        when(mockSelectProductsStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false); // Simulate at least one product associated with the category

        // Mock the deletion of the category
        when(mockDeleteCategoryStmt.executeUpdate()).thenReturn(1); // Simulate successful deletion

        // Call the method under test
        categoryDao.deleteCategory(2);

        // Verify each delete operation was performed once per associated product
        verify(mockDeleteOrderItemsStmt, times(1)).executeUpdate();
        verify(mockDeleteSubscriptionsStmt, times(1)).executeUpdate();
        verify(mockDeleteSubscriptionPlansStmt, times(1)).executeUpdate();
        verify(mockDeleteProductStmt, times(1)).executeUpdate();
        verify(mockDeleteCategoryStmt, times(1)).executeUpdate();

        verify(mockConnection, times(1)).commit();
    }


}
