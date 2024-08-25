package com.hsbc.dao;

import com.hsbc.exception.product.ProductNotFoundException;
import com.hsbc.exception.order.InsufficientStockException;
import com.hsbc.model.Product;

import java.util.List;

public interface ProductDao {
    Product findById(int productId) throws ProductNotFoundException;

    List<Product> findAllProducts();

    void addProduct(Product product);

    void updateProduct(Product product) throws ProductNotFoundException;

    void deleteProduct(int productId) throws ProductNotFoundException;

    List<Product> findProductsByCategory(int categoryId);

    void decreaseProductStock(int productId, int quantity) throws InsufficientStockException;


//    Product getProductByName(String name) throws ProductNotFoundException;

}
