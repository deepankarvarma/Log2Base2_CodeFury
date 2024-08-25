package com.hsbc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User(
                1,
                "John Doe",
                "password123",
                "john.doe@example.com",
                "123-456-7890",
                "123 Elm Street",
                LocalDate.of(2024, 1, 1)
        );
    }

    @Test
    public void testDefaultConstructor() {
        User defaultUser = new User();
        assertNotNull(defaultUser);
    }

    @Test
    public void testParameterizedConstructor() {
        assertEquals(1, user.getUserId());
        assertEquals("John Doe", user.getUserName());
        assertEquals("password123", user.getPassword());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("123-456-7890", user.getPhoneNumber());
        assertEquals("123 Elm Street", user.getAddress());
        assertEquals(LocalDate.of(2024, 1, 1), user.getRegistrationDate());
        assertEquals("USER", user.getRole());
    }

    @Test
    public void testGettersAndSetters() {
        user.setUserId(2);
        user.setUserName("Jane Smith");
        user.setPassword("newpassword456");
        user.setEmail("jane.smith@example.com");
        user.setPhoneNumber("098-765-4321");
        user.setAddress("456 Oak Avenue");
        user.setRegistrationDate(LocalDate.of(2024, 2, 1));
        user.setRole("ADMIN");

        assertEquals(2, user.getUserId());
        assertEquals("Jane Smith", user.getUserName());
        assertEquals("newpassword456", user.getPassword());
        assertEquals("jane.smith@example.com", user.getEmail());
        assertEquals("098-765-4321", user.getPhoneNumber());
        assertEquals("456 Oak Avenue", user.getAddress());
        assertEquals(LocalDate.of(2024, 2, 1), user.getRegistrationDate());
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    public void testToString() {
        String expectedString = "User{" +
                "userId=" + 1 +
                ", userName='John Doe'" +
                ", password='password123'" +
                ", email='john.doe@example.com'" +
                ", phoneNumber='123-456-7890'" +
                ", address='123 Elm Street'" +
                ", registrationDate=" + LocalDate.of(2024, 1, 1) +
                '}';
        assertEquals(expectedString, user.toString());
    }
}
