//package com.hsbc.dao.impl;
//
//import com.hsbc.dao.UserDao;
//import com.hsbc.dao.impl.UserDaoImpl;
//import com.hsbc.exception.user.UserAlreadyExistsException;
//import com.hsbc.exception.user.UserNotFoundException;
//import com.hsbc.model.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.sql.*;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserDaoImplTest {
//
//    private UserDaoImpl userDao;
//    private Connection connection;
//    private PreparedStatement preparedStatement;
//    private ResultSet resultSet;
//
//    @BeforeEach
//    void setUp() throws SQLException {
//        connection = mock(Connection.class);
//        preparedStatement = mock(PreparedStatement.class);
//        resultSet = mock(ResultSet.class);
//
//        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
//        userDao = new UserDaoImpl(); // Use the default constructor
//        // Inject the mocked connection
//        userDao.setConnection(connection);
//    }
//
//    @Test
//    void testFindById() throws SQLException, UserNotFoundException {
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//        when(resultSet.next()).thenReturn(true);
//        when(resultSet.getInt("user_id")).thenReturn(1);
//        when(resultSet.getString("user_name")).thenReturn("John Doe");
//        when(resultSet.getString("user_pswd")).thenReturn("password");
//        when(resultSet.getString("user_email")).thenReturn("john@example.com");
//        when(resultSet.getString("user_phone_number")).thenReturn("1234567890");
//        when(resultSet.getString("user_address")).thenReturn("123 Elm Street");
//        when(resultSet.getDate("user_registration_date")).thenReturn(Date.valueOf(LocalDate.now()));
//        when(resultSet.getString("role")).thenReturn("USER");
//
//        User expectedUser = new User();
//        expectedUser.setUserId(1);
//        expectedUser.setUserName("John Doe");
//        expectedUser.setPassword("password");
//        expectedUser.setEmail("john@example.com");
//        expectedUser.setPhoneNumber("1234567890");
//        expectedUser.setAddress("123 Elm Street");
//        expectedUser.setRegistrationDate(LocalDate.now());
//        expectedUser.setRole("USER");
//
//        User result = userDao.findById(1);
//
//        assertEquals(expectedUser, result);
//    }
//
//    @Test
//    void testFindByEmail() throws SQLException, UserNotFoundException {
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//        when(resultSet.next()).thenReturn(true);
//        when(resultSet.getInt("user_id")).thenReturn(1);
//        when(resultSet.getString("user_name")).thenReturn("John Doe");
//        when(resultSet.getString("user_pswd")).thenReturn("password");
//        when(resultSet.getString("user_email")).thenReturn("john@example.com");
//        when(resultSet.getString("user_phone_number")).thenReturn("1234567890");
//        when(resultSet.getString("user_address")).thenReturn("123 Elm Street");
//        when(resultSet.getDate("user_registration_date")).thenReturn(Date.valueOf(LocalDate.now()));
//        when(resultSet.getString("role")).thenReturn("USER");
//
//        User expectedUser = new User();
//        expectedUser.setUserId(1);
//        expectedUser.setUserName("John Doe");
//        expectedUser.setPassword("password");
//        expectedUser.setEmail("john@example.com");
//        expectedUser.setPhoneNumber("1234567890");
//        expectedUser.setAddress("123 Elm Street");
//        expectedUser.setRegistrationDate(LocalDate.now());
//        expectedUser.setRole("USER");
//
//        User result = userDao.findByEmail("john@example.com");
//
//        assertEquals(expectedUser, result);
//    }
//
//    @Test
//    void testFindAllUsers() throws SQLException {
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//        when(resultSet.next()).thenReturn(true).thenReturn(false); // one result
//
//        when(resultSet.getInt("user_id")).thenReturn(1);
//        when(resultSet.getString("user_name")).thenReturn("John Doe");
//        when(resultSet.getString("user_pswd")).thenReturn("password");
//        when(resultSet.getString("user_email")).thenReturn("john@example.com");
//        when(resultSet.getString("user_phone_number")).thenReturn("1234567890");
//        when(resultSet.getString("user_address")).thenReturn("123 Elm Street");
//        when(resultSet.getDate("user_registration_date")).thenReturn(Date.valueOf(LocalDate.now()));
//        when(resultSet.getString("role")).thenReturn("USER");
//
//        User expectedUser = new User();
//        expectedUser.setUserId(1);
//        expectedUser.setUserName("John Doe");
//        expectedUser.setPassword("password");
//        expectedUser.setEmail("john@example.com");
//        expectedUser.setPhoneNumber("1234567890");
//        expectedUser.setAddress("123 Elm Street");
//        expectedUser.setRegistrationDate(LocalDate.now());
//        expectedUser.setRole("USER");
//
//        List<User> users = userDao.findAllUsers();
//
//        assertEquals(1, users.size());
//        assertEquals(expectedUser, users.get(0));
//    }
//
//    @Test
//    void testUpdateUser() throws SQLException, UserNotFoundException {
//        when(preparedStatement.executeUpdate()).thenReturn(1);
//
//        User user = new User();
//        user.setUserId(1);
//        user.setUserName("John Doe");
//        user.setPassword("password");
//        user.setEmail("john@example.com");
//        user.setPhoneNumber("1234567890");
//        user.setAddress("123 Elm Street");
//        user.setRegistrationDate(LocalDate.now());
//        user.setRole("USER");
//
//        userDao.updateUser(user);
//
//        verify(preparedStatement).setString(1, "John Doe");
//        verify(preparedStatement).setString(2, "password");
//        verify(preparedStatement).setString(3, "john@example.com");
//        verify(preparedStatement).setString(4, "1234567890");
//        verify(preparedStatement).setString(5, "123 Elm Street");
//        verify(preparedStatement).setDate(6, Date.valueOf(LocalDate.now()));
//        verify(preparedStatement).setString(7, "USER");
//        verify(preparedStatement).setInt(8, 1);
//    }
//
//    @Test
//    void testDeleteUser() throws SQLException, UserNotFoundException {
//        when(preparedStatement.executeUpdate()).thenReturn(1);
//
//        userDao.deleteUser(1);
//
//        verify(preparedStatement).setInt(1, 1);
//    }
//
//    @Test
//    void testGetUserByUsername() throws SQLException, UserNotFoundException {
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//        when(resultSet.next()).thenReturn(true);
//        when(resultSet.getInt("user_id")).thenReturn(1);
//        when(resultSet.getString("user_name")).thenReturn("John Doe");
//        when(resultSet.getString("user_pswd")).thenReturn("password");
//        when(resultSet.getString("user_email")).thenReturn("john@example.com");
//        when(resultSet.getString("user_phone_number")).thenReturn("1234567890");
//        when(resultSet.getString("user_address")).thenReturn("123 Elm Street");
//        when(resultSet.getDate("user_registration_date")).thenReturn(Date.valueOf(LocalDate.now()));
//        when(resultSet.getString("role")).thenReturn("USER");
//
//        User expectedUser = new User();
//        expectedUser.setUserId(1);
//        expectedUser.setUserName("John Doe");
//        expectedUser.setPassword("password");
//        expectedUser.setEmail("john@example.com");
//        expectedUser.setPhoneNumber("1234567890");
//        expectedUser.setAddress("123 Elm Street");
//        expectedUser.setRegistrationDate(LocalDate.now());
//        expectedUser.setRole("USER");
//
//        User result = userDao.getUserByUsername("John Doe");
//
//        assertEquals(expectedUser, result);
//    }
//}

package com.hsbc.dao.impl;

import com.hsbc.dao.UserDao;
import com.hsbc.exception.user.UserAlreadyExistsException;
import com.hsbc.exception.user.UserNotFoundException;
import com.hsbc.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class UserDaoImplTest {

    private UserDaoImpl userDao;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        userDao = new UserDaoImpl();
        userDao.setConnection(connection);
    }

    @Test
    public void testFindById_UserFound() throws SQLException, UserNotFoundException {
        // Arrange
        User expectedUser = new User();
        expectedUser.setUserId(1);
        expectedUser.setUserName("John Doe");
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("user_id")).thenReturn(expectedUser.getUserId());
        when(resultSet.getString("user_name")).thenReturn(expectedUser.getUserName());

        // Act
        User user = userDao.findById(1);

        // Assert
        assertNotNull(user);
        assertEquals(expectedUser.getUserId(), user.getUserId());
        assertEquals(expectedUser.getUserName(), user.getUserName());
    }

    @Test
    public void testFindById_UserNotFound() throws SQLException {
        // Arrange
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userDao.findById(1));
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() throws SQLException {
        // Arrange
        User user = new User();
        user.setUserName("testUser");
        user.setPassword("testPassword");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setAddress("Test Address");
        user.setRegistrationDate(LocalDate.now());  // Set a valid registration date
        user.setRole("USER");

        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);

        // Simulate a SQLException with the correct SQLState for a duplicate key in MySQL ("23000")
        doThrow(new SQLException("Duplicate key", "23000")).when(preparedStatement).executeUpdate();

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> userDao.registerUser(user));
    }

//    @Test
//    public void testUpdateUser_UserNotFound() throws SQLException {
//        // Arrange
//        User user = new User();
//        user.setUserId(1);
//        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
//        when(preparedStatement.executeUpdate()).thenReturn(0);
//
//        // Act & Assert
//        assertThrows(UserNotFoundException.class, () -> userDao.updateUser(user));
//    }

    @Test
    public void testUpdateUser_UserNotFound() throws SQLException {
        // Arrange
        User user = new User();
        user.setUserId(1);
        user.setUserName("John Doe");
        user.setPassword("password123");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("1234567890");
        user.setAddress("123 Main St");
        user.setRegistrationDate(LocalDate.now());
        user.setRole("USER");

        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userDao.updateUser(user));
    }

    @Test
    public void testDeleteUser_UserNotFound() throws SQLException {
        // Arrange
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userDao.deleteUser(1));
    }

    @Test
    public void testFindAllUsers() throws SQLException {
        // Arrange
        List<User> expectedUsers = new ArrayList<>();
        User user = new User();
        user.setUserId(1);
        user.setUserName("John Doe");
        expectedUsers.add(user);

        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("user_id")).thenReturn(user.getUserId());
        when(resultSet.getString("user_name")).thenReturn(user.getUserName());

        // Act
        List<User> users = userDao.findAllUsers();

        // Assert
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(expectedUsers.get(0).getUserId(), users.get(0).getUserId());
    }

    @Test
    public void testFindByEmail_UserFound() throws SQLException, UserNotFoundException {
        // Arrange
        User expectedUser = new User();
        expectedUser.setUserId(1);
        expectedUser.setEmail("john.doe@example.com");
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("user_id")).thenReturn(expectedUser.getUserId());
        when(resultSet.getString("user_email")).thenReturn(expectedUser.getEmail());

        // Act
        User user = userDao.findByEmail("john.doe@example.com");

        // Assert
        assertNotNull(user);
        assertEquals(expectedUser.getEmail(), user.getEmail());
    }

    @Test
    public void testFindByEmail_UserNotFound() throws SQLException {
        // Arrange
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userDao.findByEmail("nonexistent@example.com"));
    }

    @Test
    public void testGetUserByUsername_UserFound() throws SQLException, UserNotFoundException {
        // Arrange
        User expectedUser = new User();
        expectedUser.setUserId(1);
        expectedUser.setUserName("John Doe");
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("user_id")).thenReturn(expectedUser.getUserId());
        when(resultSet.getString("user_name")).thenReturn(expectedUser.getUserName());

        // Act
        User user = userDao.getUserByUsername("John Doe");

        // Assert
        assertNotNull(user);
        assertEquals(expectedUser.getUserId(), user.getUserId());
        assertEquals(expectedUser.getUserName(), user.getUserName());
    }

    @Test
    public void testGetUserByUsername_UserNotFound() throws SQLException {
        // Arrange
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userDao.getUserByUsername("nonexistentuser"));
    }
}
