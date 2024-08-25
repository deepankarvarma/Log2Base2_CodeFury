package com.hsbc.service;

import com.hsbc.dao.ProductDao;
import com.hsbc.exception.product.ProductNotFoundException;
import com.hsbc.model.Category;
import com.hsbc.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductByIdSuccess() throws ProductNotFoundException {
        Product product = new Product(1, "Product1", "Description", 100.0,2, new Category("Fruits", 1, "Fresh fruits and vegetables"));
        when(productDao.findById(anyInt())).thenReturn(product);

        Product result = productService.getProductById(1);
        assertNotNull(result);
        assertEquals(1, result.getProductId());
        assertEquals("Product1", result.getProductName());
        verify(productDao).findById(1);
    }

    @Test
    void testGetProductByIdNotFound() throws ProductNotFoundException {
        when(productDao.findById(anyInt())).thenThrow(new ProductNotFoundException("Product not found"));

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(1);
        });

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "Product1", "Description", 100.0,2, new Category("Fruits", 1, "Fresh fruits and vegetables")));
        products.add(new Product(2, "Product2", "Description", 120.0,5, new Category("readytoeat", 2, "ready to eat meals")));

        when(productDao.findAllProducts()).thenReturn(products);

        List<Product> result = productService.getAllProducts();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productDao).findAllProducts();
    }

    @Test
    void testCreateProduct() {
        Product product = new Product(1, "Product1", "Description", 100.0,2, new Category("Fruits", 1, "Fresh fruits and vegetables"));

        doNothing().when(productDao).addProduct(any(Product.class));

        assertDoesNotThrow(() -> productService.createProduct(product));
        verify(productDao).addProduct(product);
    }

    @Test
    void testUpdateProductSuccess() throws ProductNotFoundException {
        Product product = new Product(1, "Product1", "Description", 100.0,2, new Category("Fruits", 1, "Fresh fruits and vegetables"));

        doNothing().when(productDao).updateProduct(any(Product.class));

        assertDoesNotThrow(() -> productService.updateProduct(product));
        verify(productDao).updateProduct(product);
    }

    @Test
    void testUpdateProductNotFound() throws ProductNotFoundException {
        Product product = new Product(1, "Product1", "Description", 100.0,2, new Category("Fruits", 1, "Fresh fruits and vegetables"));

        doThrow(new ProductNotFoundException("Product not found")).when(productDao).updateProduct(any(Product.class));

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(product);
        });

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void testDeleteProductSuccess() throws ProductNotFoundException {
        doNothing().when(productDao).deleteProduct(anyInt());

        assertDoesNotThrow(() -> productService.deleteProduct(1));
        verify(productDao).deleteProduct(1);
    }

    @Test
    void testDeleteProductNotFound() throws ProductNotFoundException {
        doThrow(new ProductNotFoundException("Product not found")).when(productDao).deleteProduct(anyInt());

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct(1);
        });

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void testFindProductsByCategory() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "Product1", "Description", 100.0,2, new Category("Fruits", 1, "Fresh fruits and vegetables")));
        products.add(new Product(2, "Product2", "Description", 120.0,5, new Category("readytoeat", 2, "ready to eat meals")));


        when(productDao.findProductsByCategory(anyInt())).thenReturn(products);

        List<Product> result = productService.findProductsByCategory(1);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productDao).findProductsByCategory(1);
    }
}
