package com.example.demo.api;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping(value = "api/v1/user")
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void addUser(@RequestBody User user) {
        userService.addUser(user);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path = "{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public User getUserById(@PathVariable("id") UUID id){
        return userService.getUserById(id).orElse(null);
    }

    @DeleteMapping(path = "{id}")
    @PreAuthorize("hasAuthority('user:write')")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void deleteUserById(@PathVariable("id") UUID id){
        userService.deleteUser(id);
    }

//    @RequestMapping(value = "/{authorizationUrl}", method = RequestMethod.PUT)
    @PutMapping(path = "{id}")
    @PreAuthorize("hasAuthority('user:write')")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void updateUser(@PathVariable("id") UUID id, @RequestBody User userToUpdate) {
        userService.updateUser(id, userToUpdate);
    }

}
