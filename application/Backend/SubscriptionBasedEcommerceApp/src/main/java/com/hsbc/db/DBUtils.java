package com.hsbc.db;


import java.sql.*;

public class DBUtils {

    static Connection conn;
    static String url = "jdbc:mysql://localhost:3306/e_commerce_app";

    public static Connection getConn() {
        try {
            if (conn == null) {
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                conn = DriverManager.getConnection(url, "root", "15246");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}

