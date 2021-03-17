package com.example.demo.service;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserServiceTest {

    private final UserDao userDao = Mockito.mock(UserDao.class);

    @Test
    @DisplayName("Test should pass when a string with the user's name is returned")
    void addUser() {
        UserService userService = new UserService(userDao);
        User testUser = new User(UUID.randomUUID(), "test", "test@gmail.com");
        Mockito.when(userDao.insertUser(testUser))
                .thenReturn("User " + testUser.getName() + " was added");

        String insertUserResponse = userService.addUser(testUser);
        Assertions.assertEquals("User " + testUser.getName() + " was added", insertUserResponse);
    }

    @Test
    @DisplayName("Test should pass when an array of all users in database is returned")
    void shouldGetAllUsers() {
    }

    @Test
    @DisplayName("Test should pass when a User with given id is returned")
    void shouldGetUserById() {
        UserService userService = new UserService(userDao);
        User expectedUserResponse = new User(UUID.fromString("5e4259af-00eb-486e-82aa-8921ed0f1f6d"), "joe", "joe@gmail.com");
        Mockito.when(userService.getUserById(UUID.fromString("5e4259af-00eb-486e-82aa-8921ed0f1f6d")))
                .thenReturn(Optional.of(expectedUserResponse));

        Optional<User> actualUserResponse = userService.getUserById(UUID.fromString("5e4259af-00eb-486e-82aa-8921ed0f1f6d"));
        Assertions.assertEquals(actualUserResponse, Optional.of(expectedUserResponse));
    }

    @Test
    void deleteUser() {
    }

    @Test
    void updateUser() {
    }
}