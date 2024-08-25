package com.hsbc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CategoryTest {

    private Category category;

    @BeforeEach
    public void setUp() {
        // Initialize a Category object before each test
        category = new Category("Fruits", 1, "Fresh fruits and vegetables");
    }

    @Test
    public void testDefaultConstructor() {
        // Test the default constructor
        Category defaultCategory = new Category();
        assertNotNull(defaultCategory);
    }

    @Test
    public void testParameterizedConstructor() {
        // Test the parameterized constructor
        assertEquals("Fruits", category.getCategoryName());
        assertEquals(1, category.getCategoryId());
        assertEquals("Fresh fruits and vegetables", category.getCategoryDescription());
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        category.setCategoryName("Vegetables");
        category.setCategoryId(2);
        category.setCategoryDescription("Fresh and organic vegetables");

        assertEquals("Vegetables", category.getCategoryName());
        assertEquals(2, category.getCategoryId());
        assertEquals("Fresh and organic vegetables", category.getCategoryDescription());
    }

    @Test
    public void testToString() {
        // Test the toString method
        String expectedString = "Category{" +
                "categoryId=1, " +
                "categoryName='Fruits', " +
                "categoryDescription='Fresh fruits and vegetables'" +
                '}';
        assertEquals(expectedString, category.toString());
    }
}
