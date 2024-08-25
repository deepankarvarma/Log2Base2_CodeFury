package com.hsbc.dao.impl;

import com.hsbc.dao.CategoryDao;
import com.hsbc.db.DBUtils;
import com.hsbc.exception.category.CategoryAlreadyExistsException;
import com.hsbc.exception.category.CategoryNotFoundException;
import com.hsbc.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CategoryDaoImpl implements CategoryDao {

    private Connection connection;

    public CategoryDaoImpl() {
        this.connection = DBUtils.getConn();
    }

    @Override
    public Category getCategoryById(int categoryId) throws CategoryNotFoundException {
        String query = "SELECT * FROM categories WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToCategory(rs);
            } else {
                throw new CategoryNotFoundException("Category with ID " + categoryId + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    @Override
    public List<Category> getAllCategories() {
        String query = "SELECT * FROM categories";
        List<Category> categories = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(mapRowToCategory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        }
        return categories;
    }

    @Override
    public void addCategory(Category category) throws CategoryAlreadyExistsException {
        String query = "INSERT INTO categories (category_name, category_description) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getCategoryDescription());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Duplicate key
                throw new CategoryAlreadyExistsException("Category already exists.");
            }
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    @Override
    public void updateCategory(Category category) throws CategoryNotFoundException {
        String query = "UPDATE categories SET category_name = ?, category_description = ? WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getCategoryDescription());
            ps.setInt(3, category.getCategoryId());
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new CategoryNotFoundException("Category with ID " + category.getCategoryId() + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    /*
    * Considerations:
    * Integrity: This approach assumes that deleting category is a rare operation and handles all linked data to avoid orphaned records.
    * Performance: Deleting a category with many associated products and dependencies could be slow due to the cascading deletions. However, it ensures the integrity of your database.
    * Order History: As with the product deletion, deleting all related data (including orders and subscriptions) means that we won’t be able to view past orders related to those products.
    * */

    @Override
    public void deleteCategory(int categoryId) throws CategoryNotFoundException {
        try {
            connection.setAutoCommit(false); // Start transaction

            // First, find all products associated with this category
            String selectProductsQuery = "SELECT product_id FROM products WHERE category_id = ?";
            try (PreparedStatement selectProductsStmt = connection.prepareStatement(selectProductsQuery)) {
                selectProductsStmt.setInt(1, categoryId);
                ResultSet resultSet = selectProductsStmt.executeQuery();

                while (resultSet.next()) {
                    int productId = resultSet.getInt("product_id");

                    // Delete related order items
                    String deleteOrderItemsQuery = "DELETE FROM order_items WHERE product_id = ?";
                    try (PreparedStatement deleteOrderItemsStmt = connection.prepareStatement(deleteOrderItemsQuery)) {
                        deleteOrderItemsStmt.setInt(1, productId);
                        deleteOrderItemsStmt.executeUpdate();
                    }

                    // Delete related subscriptions
                    String deleteSubscriptionsQuery = "DELETE FROM subscriptions WHERE subscription_plan_id IN (SELECT subscription_plan_id FROM subscription_plans WHERE product_id = ?)";
                    try (PreparedStatement deleteSubscriptionsStmt = connection.prepareStatement(deleteSubscriptionsQuery)) {
                        deleteSubscriptionsStmt.setInt(1, productId);
                        deleteSubscriptionsStmt.executeUpdate();
                    }

                    // Delete related subscription plans
                    String deleteSubscriptionPlansQuery = "DELETE FROM subscription_plans WHERE product_id = ?";
                    try (PreparedStatement deleteSubscriptionPlansStmt = connection.prepareStatement(deleteSubscriptionPlansQuery)) {
                        deleteSubscriptionPlansStmt.setInt(1, productId);
                        deleteSubscriptionPlansStmt.executeUpdate();
                    }

                    // Delete the product itself
                    String deleteProductQuery = "DELETE FROM products WHERE product_id = ?";
                    try (PreparedStatement deleteProductStmt = connection.prepareStatement(deleteProductQuery)) {
                        deleteProductStmt.setInt(1, productId);
                        deleteProductStmt.executeUpdate();
                    }
                }
            }

            // Finally, delete the category itself
            String deleteCategoryQuery = "DELETE FROM categories WHERE category_id = ?";
            try (PreparedStatement deleteCategoryStmt = connection.prepareStatement(deleteCategoryQuery)) {
                deleteCategoryStmt.setInt(1, categoryId);
                int affectedRows = deleteCategoryStmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new CategoryNotFoundException("Category not found with ID: " + categoryId);
                }
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback transaction if any error occurs
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Error rolling back transaction", rollbackEx);
            }
            throw new RuntimeException("Error deleting category", e);
        } finally {
            try {
                connection.setAutoCommit(true); // Reset auto-commit mode
            } catch (SQLException e) {
                throw new RuntimeException("Error resetting auto-commit mode", e);
            }
        }
    }


    private Category mapRowToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setCategoryDescription(rs.getString("category_description"));
        return category;
    }
}
