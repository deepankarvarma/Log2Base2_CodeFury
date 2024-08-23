package com.hsbc.service;

import com.hsbc.dao.UserDao;
import com.hsbc.exception.UserAlreadyExistsException;
import com.hsbc.exception.UserNotFoundException;
import com.hsbc.factory.DaoFactory;
import com.hsbc.model.User;

import java.util.List;

public class UserService {

    private UserDao userDAO;

    public UserService() {
        this.userDAO = DaoFactory.getUserDao();
    }

    public User getUserById(int userId) throws UserNotFoundException {
        return userDAO.findById(userId);
    }

    public User getUserByEmail(String email) throws UserNotFoundException {
        return userDAO.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userDAO.findAllUsers();
    }

    public void registerUser(User user) throws UserAlreadyExistsException {
        userDAO.registerUser(user);
    }

    public void updateUser(User user) throws UserNotFoundException {
        userDAO.updateUser(user);
    }

    public void deleteUser(int userId) throws UserNotFoundException {
        userDAO.deleteUser(userId);
    }

    public User getUserByUsername(String username) throws UserNotFoundException {
        return userDAO.getUserByUsername(username);
    }
}