package com.hsbc.dao.impl;


import com.hsbc.dao.ProductDao;
import com.hsbc.db.DBUtils;
import com.hsbc.exception.order.InsufficientStockException;
import com.hsbc.exception.product.ProductNotFoundException;
import com.hsbc.model.Category;
import com.hsbc.model.Product;

import java.util.ArrayList;
import java.util.List;


import java.sql.*;


public class ProductDaoImpl implements ProductDao {

    private static final String INSERT_PRODUCT = "INSERT INTO Products (product_name, description, base_price, stock, category_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_PRODUCT = "UPDATE Products SET product_name = ?, description = ?, base_price = ?, stock = ?, category_id = ? WHERE product_id = ?";
    private static final String DELETE_PRODUCT = "DELETE FROM Products WHERE product_id = ?";
    private static final String SELECT_PRODUCT_BY_ID = "SELECT * FROM Products WHERE product_id = ?";
    private static final String SELECT_ALL_PRODUCTS = "SELECT * FROM Products";
    private static final String SELECT_PRODUCTS_BY_CATEGORY = "SELECT * FROM Products WHERE category_id = ?";


    private Connection connection;

    public ProductDaoImpl() {
        this.connection = DBUtils.getConn();
    }

    @Override
    public Product findById(int productId) throws ProductNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_PRODUCT_BY_ID)) {

            statement.setInt(1, productId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapRowToProduct(resultSet);
            } else {
                throw new ProductNotFoundException("Product not found with ID: " + productId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by ID", e);
        }
    }

    @Override
    public List<Product> findAllProducts() {
        List<Product> products = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_PRODUCTS);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapRowToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all products", e);
        }
        return products;
    }

    @Override
    public void addProduct(Product product) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PRODUCT, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, product.getProductName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getBasePrice());
            statement.setInt(4, product.getStock());
            statement.setInt(5, product.getCategory().getCategoryId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Adding product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setProductId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Adding product failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding product", e);
        }
    }

    @Override
    public void updateProduct(Product product) throws ProductNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_PRODUCT)) {

            statement.setString(1, product.getProductName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getBasePrice());
            statement.setInt(4, product.getStock());
            statement.setInt(5, product.getCategory().getCategoryId());
            statement.setInt(6, product.getProductId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new ProductNotFoundException("Product not found with ID: " + product.getProductId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product", e);
        }
    }

    /*
    Considerations for the below fucntion:

    Integrity: This approach assumes that deleting the product is a rare operation and handles all linked data to avoid orphaned records.
    Performance: If there are a large number of linked records, this operation might take some time.
    Order History: Since the orders are linked via order_items, deleting those items will unfortunately remove the trace of the product in any order history.
     */

    @Override
    public void deleteProduct(int productId) throws ProductNotFoundException {
        try {
            connection.setAutoCommit(false); // Start transaction

            // Delete related order items
            String deleteOrderItemsQuery = "DELETE FROM order_items WHERE product_id = ?";
            try (PreparedStatement deleteOrderItemsStmt = connection.prepareStatement(deleteOrderItemsQuery)) {
                deleteOrderItemsStmt.setInt(1, productId);
                deleteOrderItemsStmt.executeUpdate();
            }

            // Delete related subscriptions
            String deleteSubscriptionsQuery = "DELETE FROM subscriptions WHERE subscription_plan_id IN (SELECT subscription_plan_id FROM subscription_plans WHERE product_id = ?)";
            try (PreparedStatement deleteSubscriptionsStmt = connection.prepareStatement(deleteSubscriptionsQuery)) {
                deleteSubscriptionsStmt.setInt(1, productId);
                deleteSubscriptionsStmt.executeUpdate();
            }

            // Delete related subscription plans
            String deleteSubscriptionPlansQuery = "DELETE FROM subscription_plans WHERE product_id = ?";
            try (PreparedStatement deleteSubscriptionPlansStmt = connection.prepareStatement(deleteSubscriptionPlansQuery)) {
                deleteSubscriptionPlansStmt.setInt(1, productId);
                deleteSubscriptionPlansStmt.executeUpdate();
            }

            // Finally, delete the product
            String deleteProductQuery = "DELETE FROM products WHERE product_id = ?";
            try (PreparedStatement deleteProductStmt = connection.prepareStatement(deleteProductQuery)) {
                deleteProductStmt.setInt(1, productId);
                int affectedRows = deleteProductStmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new ProductNotFoundException("Product not found with ID: " + productId);
                }
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback transaction if any error occurs
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Error rolling back transaction", rollbackEx);
            }
            throw new RuntimeException("Error deleting product", e);
        } finally {
            try {
                connection.setAutoCommit(true); // Reset auto-commit mode
            } catch (SQLException e) {
                throw new RuntimeException("Error resetting auto-commit mode", e);
            }
        }
    }


    @Override
    public List<Product> findProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_PRODUCTS_BY_CATEGORY)) {

            statement.setInt(1, categoryId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                products.add(mapRowToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by category", e);
        }
        return products;
    }

    @Override
    public void decreaseProductStock(int productId, int quantity) throws InsufficientStockException {
        String query = "UPDATE Products SET stock = stock - ? WHERE product_id = ? AND stock >= ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new InsufficientStockException("Not enough stock for product ID: " + productId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product stock", e);
        }
    }


    private Product mapRowToProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setProductId(resultSet.getInt("product_id"));
        product.setProductName(resultSet.getString("product_name"));
        product.setDescription(resultSet.getString("description"));
        product.setBasePrice(resultSet.getDouble("base_price"));
        product.setStock(resultSet.getInt("stock"));

//        Category category = new Category();
//        category.setCategoryId(resultSet.getInt("category_id"));
//        product.setCategory(category);

        Category category = new CategoryDaoImpl().getCategoryById(resultSet.getInt("category_id"));
        product.setCategory(category);

        return product;
    }
}

