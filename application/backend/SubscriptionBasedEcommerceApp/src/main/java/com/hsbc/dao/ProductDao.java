package com.hsbc.dao;

import com.hsbc.exception.ProductNotFoundException;
import com.hsbc.model.Product;

import java.util.List;

public interface ProductDao {
    Product findById(int productId) throws ProductNotFoundException;

    List<Product> findAllProducts();

    void addProduct(Product product);

    void updateProduct(Product product) throws ProductNotFoundException;

    void deleteProduct(int productId) throws ProductNotFoundException;

    List<Product> findProductsByCategory(int categoryId);

//    Product getProductByName(String name) throws ProductNotFoundException;

}
