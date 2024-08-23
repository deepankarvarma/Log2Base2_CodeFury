package com.hsbc.dao.impl;

import com.hsbc.dao.CategoryDao;
import com.hsbc.db.DBUtils;
import com.hsbc.exception.CategoryAlreadyExistsException;
import com.hsbc.exception.CategoryNotFoundException;
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

    @Override
    public void deleteCategory(int categoryId) throws CategoryNotFoundException {
        String query = "DELETE FROM categories WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, categoryId);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted == 0) {
                throw new CategoryNotFoundException("Category with ID " + categoryId + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage());
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
