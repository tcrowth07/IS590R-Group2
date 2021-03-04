package com.example.demo.service;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(@Qualifier("UserPostgres") UserDao userDao) {
        this.userDao = userDao;
    }

    public String addUser(User User) {
        return userDao.insertUser(User);
    }

    public List<User> getAllUsers() {
        return userDao.selectAllUsers();
    }

    public Optional<User> getUserById(UUID id) {
        return userDao.selectUserById(id);
    }

    public String deleteUser(UUID id){
        return userDao.deleteUserById(id);
    }

    public User updateUser(UUID id, User newUser){
        return userDao.updateUserById(id, newUser);
    }
}
