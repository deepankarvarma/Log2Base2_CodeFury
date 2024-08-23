package com.hsbc.service;

import com.hsbc.dao.ProductDao;
import com.hsbc.exception.ProductNotFoundException;
import com.hsbc.factory.DaoFactory;
import com.hsbc.model.Product;

import java.util.List;

public class ProductService {

    private ProductDao productDAO;

    public ProductService() {
        this.productDAO = DaoFactory.getProductDao();
    }

    public Product getProductById(int productId) throws ProductNotFoundException {
        return productDAO.findById(productId);
    }

    public List<Product> getAllProducts() {
        return productDAO.findAllProducts();
    }

    public void createProduct(Product product) {
        productDAO.addProduct(product);
    }

    public void updateProduct(Product product) throws ProductNotFoundException {
        productDAO.updateProduct(product);
    }

    public void deleteProduct(int productId) throws ProductNotFoundException {
        productDAO.deleteProduct(productId);
    }

    public List<Product> findProductsByCategory(int categoryId) {
        return productDAO.findProductsByCategory(categoryId);
    }
}

