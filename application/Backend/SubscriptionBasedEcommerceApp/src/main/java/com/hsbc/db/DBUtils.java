package com.hsbc.db;

import java.sql.*;

public class DBUtils {

    // Static variable to hold the database connection
    static Connection conn;

    // Database URL specifying the connection details, change it accordingly.
    static String url = "jdbc:mysql://localhost:3306/e_commerce_app";
    static String username = "root";
    static String password = "15246";

    /**
     * Retrieves the database connection. If the connection does not exist, it establishes a new one.
     *
     * @return Connection to the database
     */
    public static Connection getConn() {
        try {
            // If the connection is null, create a new one
            if (conn == null) {
                // Registering the MySQL JDBC driver explicitly
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                // Establishing the connection using the provided URL, username, and password
                conn = DriverManager.getConnection(url, username, password);
            }
        } catch (SQLException e) {
            // Log the error message in case of an SQL exception
            System.out.println(e.getMessage());
        }
        // Return the existing or newly created connection
        return conn;
    }
}
