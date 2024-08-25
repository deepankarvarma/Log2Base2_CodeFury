package com.hsbc.dao.impl;

import com.hsbc.dao.UserDao;
import com.hsbc.db.DBUtils;
import com.hsbc.exception.user.UserAlreadyExistsException;
import com.hsbc.exception.user.UserNotFoundException;
import com.hsbc.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    private Connection connection;

    public UserDaoImpl() {
        this.connection = DBUtils.getConn();
    }

    @Override
    public User findById(int userId) throws UserNotFoundException {
        String query = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToUser(rs);
            } else {
                throw new UserNotFoundException("User with ID " + userId + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    @Override
    public User findByEmail(String email) throws UserNotFoundException {
        String query = "SELECT * FROM users WHERE user_email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToUser(rs);
            } else {
                throw new UserNotFoundException("User with email " + email + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    @Override
    public List<User> findAllUsers() {
        String query = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        }
        return users;
    }

    @Override
    public void registerUser(User user) throws UserAlreadyExistsException {
        String query = "INSERT INTO users (user_name, user_pswd, user_email, user_phone_number, user_address, user_registration_date, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getAddress());
            ps.setDate(6, Date.valueOf(user.getRegistrationDate()));
            ps.setString(7, user.getRole());  // Set the role
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Duplicate key
                throw new UserAlreadyExistsException("User already exists.");
            }
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    @Override
    public void updateUser(User user) throws UserNotFoundException {
        String query = "UPDATE users SET user_name = ?, user_pswd = ?, user_email = ?, user_phone_number = ?, user_address = ?, user_registration_date = ?, role = ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getAddress());
            ps.setDate(6, Date.valueOf(user.getRegistrationDate()));
            ps.setString(7, user.getRole());  // Update the role
            ps.setInt(8, user.getUserId());
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new UserNotFoundException("User with ID " + user.getUserId() + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(int userId) throws UserNotFoundException {
        String query = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted == 0) {
                throw new UserNotFoundException("User with ID " + userId + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    @Override
    public User getUserByUsername(String username) throws UserNotFoundException {
        String query = "SELECT * FROM users WHERE user_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRowToUser(rs);
            } else {
                throw new UserNotFoundException("User with username " + username + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUserName(rs.getString("user_name"));
        user.setPassword(rs.getString("user_pswd"));
        user.setEmail(rs.getString("user_email"));
        user.setPhoneNumber(rs.getString("user_phone_number"));
        user.setAddress(rs.getString("user_address"));
        user.setRegistrationDate(rs.getDate("user_registration_date").toLocalDate());
        user.setRole(rs.getString("role")); // Map the role
        return user;
    }
}
