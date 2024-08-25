package com.hsbc.dao.impl;

import com.hsbc.dao.ProductDao;
import com.hsbc.db.DBUtils;
import com.hsbc.exception.order.InsufficientStockException;
import com.hsbc.exception.product.ProductNotFoundException;
import com.hsbc.model.Category;
import com.hsbc.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao {

    // SQL queries for various operations on the Products table
    private static final String INSERT_PRODUCT = "INSERT INTO Products (product_name, description, base_price, stock, category_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_PRODUCT = "UPDATE Products SET product_name = ?, description = ?, base_price = ?, stock = ?, category_id = ? WHERE product_id = ?";
    private static final String DELETE_PRODUCT = "DELETE FROM Products WHERE product_id = ?";
    private static final String SELECT_PRODUCT_BY_ID = "SELECT * FROM Products WHERE product_id = ?";
    private static final String SELECT_ALL_PRODUCTS = "SELECT * FROM Products";
    private static final String SELECT_PRODUCTS_BY_CATEGORY = "SELECT * FROM Products WHERE category_id = ?";

    private Connection connection;

    // Constructor to initialize the database connection using DBUtils
    public ProductDaoImpl() {
        this.connection = DBUtils.getConn();
    }

    // Finds a Product by its ID, throws an exception if not found
    @Override
    public Product findById(int productId) throws ProductNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_PRODUCT_BY_ID)) {
            statement.setInt(1, productId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapRowToProduct(resultSet); // Map the result set to a Product object
            } else {
                throw new ProductNotFoundException("Product not found with ID: " + productId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by ID", e);
        }
    }

    // Retrieves all Products from the database
    @Override
    public List<Product> findAllProducts() {
        List<Product> products = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_PRODUCTS);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapRowToProduct(resultSet)); // Add each product to the list
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all products", e);
        }
        return products;
    }

    // Adds a new Product to the database
    @Override
    public void addProduct(Product product) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_PRODUCT, Statement.RETURN_GENERATED_KEYS)) {
            // Set the product details in the prepared statement
            statement.setString(1, product.getProductName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getBasePrice());
            statement.setInt(4, product.getStock());
            statement.setInt(5, product.getCategory().getCategoryId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Adding product failed, no rows affected.");
            }

            // Retrieve the generated product ID
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

    // Updates an existing Product in the database
    @Override
    public void updateProduct(Product product) throws ProductNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_PRODUCT)) {
            // Set the updated product details in the prepared statement
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
     * The following method handles the deletion of a product and all related data,
     * such as order items, subscriptions, and subscription plans.
     * Integrity: This approach ensures that no orphaned records remain by deleting all linked data.
     * Performance: Deleting a product with many linked records may take time.
     * Order History: This will remove the product from order history by deleting the associated order items.
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

            // Finally, delete the product itself
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

    // Finds all Products in a specific category
    @Override
    public List<Product> findProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_PRODUCTS_BY_CATEGORY)) {
            statement.setInt(1, categoryId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                products.add(mapRowToProduct(resultSet)); // Add each product to the list
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by category", e);
        }
        return products;
    }

    // Decreases the stock of a Product by a given quantity
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

    // Maps a ResultSet row to a Product object
    private Product mapRowToProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setProductId(resultSet.getInt("product_id"));
        product.setProductName(resultSet.getString("product_name"));
        product.setDescription(resultSet.getString("description"));
        product.setBasePrice(resultSet.getDouble("base_price"));
        product.setStock(resultSet.getInt("stock"));

        // Fetch and set the associated Category
        Category category = new CategoryDaoImpl().getCategoryById(resultSet.getInt("category_id"));
        product.setCategory(category);

        return product;
    }

    // Setter for injecting a mock connection, useful in unit tests
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
