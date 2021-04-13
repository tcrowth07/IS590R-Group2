package com.example.demo.dao;

import com.example.demo.model.ApplicationUser;
import com.example.demo.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationUserDao {
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username);

    String insertApplicationUser(UUID id, ApplicationUser user);

    default String insertApplicationUser(ApplicationUser user) {
        UUID id = UUID.randomUUID();
        return insertApplicationUser(id, user);
    }

    List<ApplicationUser> selectAllApplicationUsers();

    Optional<ApplicationUser> selectApplicationUserById(UUID id);

    String deleteApplicationUserById(UUID id);

    ApplicationUser updateApplicationUserById(UUID id, ApplicationUser user);

    Boolean login(String username, String password);

}
