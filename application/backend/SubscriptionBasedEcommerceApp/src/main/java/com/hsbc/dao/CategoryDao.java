package com.hsbc.dao;

import com.hsbc.exception.CategoryAlreadyExistsException;
import com.hsbc.exception.CategoryNotFoundException;
import com.hsbc.model.Category;

import java.util.List;

public interface CategoryDao {
    Category getCategoryById(int categoryId) throws CategoryNotFoundException;

    List<Category> getAllCategories();

    void addCategory(Category category) throws CategoryAlreadyExistsException;

    void updateCategory(Category category) throws CategoryNotFoundException;

    void deleteCategory(int categoryId) throws CategoryNotFoundException;
}


