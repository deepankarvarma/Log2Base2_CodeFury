package com.hsbc.service;

import com.hsbc.dao.UserDao;
import com.hsbc.exception.user.UserAlreadyExistsException;
import com.hsbc.exception.user.UserNotFoundException;
import com.hsbc.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize the test user
        testUser = new User(
                1,
                "John Doe",
                "password123",
                "john@example.com",
                "1234567890",
                "123 Main St, Springfield",
                LocalDate.of(2023, 1, 1)
        );
    }

    @Test
    void testGetUserByIdSuccess() throws UserNotFoundException {
        when(userDao.findById(anyInt())).thenReturn(testUser);

        User result = userService.getUserById(1);
        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("John Doe", result.getUserName());
        verify(userDao).findById(1);
    }

    @Test
    void testGetUserByIdNotFound() throws UserNotFoundException {
        when(userDao.findById(anyInt())).thenThrow(new UserNotFoundException("User not found"));

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(1);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetUserByEmailSuccess() throws UserNotFoundException {
        when(userDao.findByEmail(anyString())).thenReturn(testUser);

        User result = userService.getUserByEmail("john@example.com");
        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        verify(userDao).findByEmail("john@example.com");
    }

    @Test
    void testGetUserByEmailNotFound() throws UserNotFoundException {
        when(userDao.findByEmail(anyString())).thenThrow(new UserNotFoundException("User not found"));

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByEmail("john@example.com");
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        User user2 = new User(
                2,
                "Jane Smith",
                "password123",
                "jane@example.com",
                "0987654321",
                "456 Elm St, Springfield",
                LocalDate.of(2023, 2, 1)
        );

        List<User> users = new ArrayList<>();
        users.add(testUser);
        users.add(user2);

        when(userDao.findAllUsers()).thenReturn(users);

        List<User> result = userService.getAllUsers();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userDao).findAllUsers();
    }

    @Test
    void testRegisterUserSuccess() throws UserAlreadyExistsException {
        doNothing().when(userDao).registerUser(any(User.class));

        assertDoesNotThrow(() -> userService.registerUser(testUser));
        verify(userDao).registerUser(testUser);
    }

    @Test
    void testRegisterUserAlreadyExists() {
        doThrow(new UserAlreadyExistsException("User already exists")).when(userDao).registerUser(any(User.class));

        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUser(testUser);
        });

        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void testUpdateUserSuccess() throws UserNotFoundException {
        doNothing().when(userDao).updateUser(any(User.class));

        assertDoesNotThrow(() -> userService.updateUser(testUser));
        verify(userDao).updateUser(testUser);
    }

    @Test
    void testUpdateUserNotFound() throws UserNotFoundException {
        doThrow(new UserNotFoundException("User not found")).when(userDao).updateUser(any(User.class));

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(testUser);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testDeleteUserSuccess() throws UserNotFoundException {
        doNothing().when(userDao).deleteUser(anyInt());

        assertDoesNotThrow(() -> userService.deleteUser(1));
        verify(userDao).deleteUser(1);
    }

    @Test
    void testDeleteUserNotFound() throws UserNotFoundException {
        doThrow(new UserNotFoundException("User not found")).when(userDao).deleteUser(anyInt());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(1);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetUserByUsernameSuccess() throws UserNotFoundException {
        when(userDao.getUserByUsername(anyString())).thenReturn(testUser);

        User result = userService.getUserByUsername("John Doe");
        assertNotNull(result);
        assertEquals("John Doe", result.getUserName());
        verify(userDao).getUserByUsername("John Doe");
    }

    @Test
    void testGetUserByUsernameNotFound() throws UserNotFoundException {
        when(userDao.getUserByUsername(anyString())).thenThrow(new UserNotFoundException("User not found"));

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByUsername("John Doe");
        });

        assertEquals("User not found", exception.getMessage());
    }
}
