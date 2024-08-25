package com.hsbc.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class DBUtilsTest {

    private Connection mockConnection;

    @BeforeEach
    void setUp() throws Exception {
        // Initialize the mock connection
        mockConnection = mock(Connection.class);

        // Use reflection to reset the static 'conn' field to null before each test
        Field connField = DBUtils.class.getDeclaredField("conn");
        connField.setAccessible(true);
        connField.set(null, null);
    }

    @Test
    void testGetConn() throws SQLException {
        // Mock DriverManager to return the mock connection
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(any(String.class), any(String.class), any(String.class)))
                    .thenReturn(mockConnection);

            Connection conn = DBUtils.getConn();

            assertNotNull(conn, "Connection should not be null");
            mockedDriverManager.verify(() -> DriverManager.getConnection("jdbc:mysql://localhost:3306/e_commerce_app", "root", "15246"));
        }
    }

    @Test
    void testGetConnWhenAlreadyInitialized() throws SQLException {
        // Mock DriverManager to return the mock connection
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(any(String.class), any(String.class), any(String.class)))
                    .thenReturn(mockConnection);

            // First call to initialize the connection
            Connection firstCall = DBUtils.getConn();
            // Second call should return the same connection instance
            Connection secondCall = DBUtils.getConn();

            assertNotNull(firstCall, "First connection should not be null");
            assertNotNull(secondCall, "Second connection should not be null");
            // Check that only one connection is created
            mockedDriverManager.verify(() -> DriverManager.getConnection(any(String.class), any(String.class), any(String.class)), Mockito.times(1));
        }
    }
}
