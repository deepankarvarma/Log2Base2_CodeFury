package com.hsbc.dao;

import com.hsbc.exception.UserAlreadyExistsException;
import com.hsbc.exception.UserNotFoundException;
import com.hsbc.model.User;

import java.util.List;

public interface UserDao {
    User findById(int userId) throws UserNotFoundException;

    User findByEmail(String email) throws UserNotFoundException;

    List<User> findAllUsers();

    void registerUser(User user) throws UserAlreadyExistsException;

    void updateUser(User user) throws UserNotFoundException;

    void deleteUser(int userId) throws UserNotFoundException;

    User getUserByUsername(String username) throws UserNotFoundException;
}
