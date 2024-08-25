package com.hsbc.service;

import com.hsbc.dao.CategoryDao;
import com.hsbc.exception.category.CategoryAlreadyExistsException;
import com.hsbc.exception.category.CategoryNotFoundException;
import com.hsbc.factory.DaoFactory;
import com.hsbc.model.Category;

import java.util.List;

public class CategoryService {

    private CategoryDao categoryDAO;

    public CategoryService() {
        this.categoryDAO = DaoFactory.getCategoryDao();
    }

    public Category getCategoryById(int categoryId) throws CategoryNotFoundException {
        return categoryDAO.getCategoryById(categoryId);
    }

    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    public void createCategory(Category category) throws CategoryAlreadyExistsException {
        categoryDAO.addCategory(category);
    }

    public void updateCategory(Category category) throws CategoryNotFoundException {
        categoryDAO.updateCategory(category);
    }

    public void deleteCategory(int categoryId) throws CategoryNotFoundException {
        categoryDAO.deleteCategory(categoryId);
    }
}