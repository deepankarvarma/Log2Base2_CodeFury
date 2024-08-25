package com.hsbc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductTest {

    private Product product;
    private Category category;

    @BeforeEach
    public void setUp() {
        // Initialize Category object for testing purposes
        category = new Category("Fruits", 1, "Fresh and organic fruits");
        // Initialize a Product object before each test
        product = new Product(1, "Apple", "Fresh red apple", 0.5, 100, category);
    }

    @Test
    public void testDefaultConstructor() {
        // Test the default constructor
        Product defaultProduct = new Product();
        assertNotNull(defaultProduct);
    }

    @Test
    public void testParameterizedConstructor() {
        // Test the parameterized constructor
        assertEquals(1, product.getProductId());
        assertEquals("Apple", product.getProductName());
        assertEquals("Fresh red apple", product.getDescription());
        assertEquals(0.5, product.getBasePrice());
        assertEquals(100, product.getStock());
        assertEquals(category, product.getCategory());
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        int newProductId = 2;
        String newProductName = "Banana";
        String newDescription = "Fresh yellow banana";
        double newBasePrice = 0.3;
        int newStock = 150;
        Category newCategory = new Category("Vegetables", 2, "Fresh and organic vegetables");

        product.setProductId(newProductId);
        product.setProductName(newProductName);
        product.setDescription(newDescription);
        product.setBasePrice(newBasePrice);
        product.setStock(newStock);
        product.setCategory(newCategory);

        assertEquals(newProductId, product.getProductId());
        assertEquals(newProductName, product.getProductName());
        assertEquals(newDescription, product.getDescription());
        assertEquals(newBasePrice, product.getBasePrice());
        assertEquals(newStock, product.getStock());
        assertEquals(newCategory, product.getCategory());
    }

    @Test
    public void testToString() {
        // Test the toString method
        String expectedString = "Product{" +
                "productId=" + 1 +
                ", productName='Apple'" +
                ", description='Fresh red apple'" +
                ", basePrice=" + 0.5 +
                ", stock=" + 100 +
                ", category=" + category +
                '}';
        assertEquals(expectedString, product.toString());
    }
}
