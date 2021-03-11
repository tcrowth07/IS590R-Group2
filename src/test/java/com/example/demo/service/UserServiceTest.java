package com.example.demo.service;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class UserServiceTest {

    private final UserDao userDao = Mockito.mock(UserDao.class);

    @Test
    @DisplayName("Test should pass when a string with the user's name is returned")
    void addUser() {
        UserService userService = new UserService(userDao);
        User testUser = new User(UUID.randomUUID(), "test", "test@gmail.com");
//        Mockito.when(userDao.insertUser(testUser.getId(), testUser));
//        Assertions.assertEquals("string", userService.addUser(testUser));
//        Assertions.assertEquals(2, 2);
        System.out.println(userService.addUser((testUser)));
        Assertions.assertEquals(userService.addUser(testUser), "User " + testUser.getName() + " was added.");
    }

    @Test
    @DisplayName("Test should pass when an array of all users in database is returned")
    void shouldGetAllUsers() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void updateUser() {
    }
}