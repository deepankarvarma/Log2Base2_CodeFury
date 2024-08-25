package com.hsbc.service;

import com.hsbc.dao.CategoryDao;
import com.hsbc.exception.category.CategoryAlreadyExistsException;
import com.hsbc.exception.category.CategoryNotFoundException;
import com.hsbc.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCategoryById_Success() throws CategoryNotFoundException {
        // Arrange
        Category expectedCategory = new Category( "Electronics",1, "Electronic items");
        when(categoryDao.getCategoryById(1)).thenReturn(expectedCategory);

        // Act
        Category actualCategory = categoryService.getCategoryById(1);

        // Assert
        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    void testGetCategoryById_Failure() throws CategoryNotFoundException {
        // Arrange
        when(categoryDao.getCategoryById(1)).thenThrow(new CategoryNotFoundException("Category not found"));

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(1));
    }

    @Test
    void testGetAllCategories() {
        // Arrange
        List<Category> expectedCategories = new ArrayList<>();
        expectedCategories.add(new Category( "Electronics", 1,"Electronic items"));
        expectedCategories.add(new Category( "Books", 2,"Various books"));
        when(categoryDao.getAllCategories()).thenReturn(expectedCategories);

        // Act
        List<Category> actualCategories = categoryService.getAllCategories();

        // Assert
        assertEquals(expectedCategories, actualCategories);
    }

    @Test
    void testCreateCategory_Success() throws CategoryAlreadyExistsException {
        // Arrange
        Category category = new Category( "Electronics",1, "Electronic items");

        // Act
        categoryService.createCategory(category);

        // Assert
        verify(categoryDao, times(1)).addCategory(category);
    }

    @Test
    void testCreateCategory_Failure() throws CategoryAlreadyExistsException {
        // Arrange
        Category category = new Category( "Electronics",2, "Electronic items");
        doThrow(new CategoryAlreadyExistsException("Category already exists")).when(categoryDao).addCategory(any(Category.class));

        // Act & Assert
        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createCategory(category));
    }

    @Test
    void testUpdateCategory_Success() throws CategoryNotFoundException {
        // Arrange
        Category category = new Category( "Electronics", 1,"Updated description");

        // Act
        categoryService.updateCategory(category);

        // Assert
        verify(categoryDao, times(1)).updateCategory(category);
    }

    @Test
    void testUpdateCategory_Failure() throws CategoryNotFoundException {
        // Arrange
        Category category = new Category( "Electronics", 1,"Updated description");
        doThrow(new CategoryNotFoundException("Category not found")).when(categoryDao).updateCategory(any(Category.class));

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(category));
    }

    @Test
    void testDeleteCategory_Success() throws CategoryNotFoundException {
        // Act
        categoryService.deleteCategory(1);

        // Assert
        verify(categoryDao, times(1)).deleteCategory(1);
    }

    @Test
    void testDeleteCategory_Failure() throws CategoryNotFoundException {
        // Arrange
        doThrow(new CategoryNotFoundException("Category not found")).when(categoryDao).deleteCategory(1);

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(1));
    }
}
