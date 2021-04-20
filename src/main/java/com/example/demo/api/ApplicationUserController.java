package com.example.demo.api;

import com.example.demo.model.ApplicationUser;
import com.example.demo.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

//@Controller

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "api/v1/user")
@RestController
public class ApplicationUserController {

    private final ApplicationUserService applicationUserService;

    @Autowired
    public ApplicationUserController(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

//    @GetMapping("login")
//    public String getLoginView() {
//        return "login";
//    }
//
//    @GetMapping("journals")
//    public String getJournals() {
//        return "journals";
//    }

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String addUser(@RequestBody ApplicationUser user) {
            return applicationUserService.addApplicationUser(user);
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public List<ApplicationUser> getAllUsers() {

        System.out.println("In Get All Users Function");
        return applicationUserService.selectAllApplicationUsers();
    }

    @GetMapping(path = "/{id}")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ApplicationUser getUserById(@PathVariable("id") UUID id){
        return applicationUserService.getApplicationUserById(id).orElse(null);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
//    @PreAuthorize("hasAuthority('user:write')")
    public @ResponseBody String deleteApplicationUserById(@PathVariable("id") UUID id){
        applicationUserService.deleteApplicationUser(id);
        return "Deleted";
    }

//    @PreAuthorize("hasAuthority('user:write')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody String updateApplicationUser(@PathVariable("id") UUID id, @RequestBody ApplicationUser userToUpdate) {
        applicationUserService.updateApplicationUser(id, userToUpdate);
        return "Updated";
    }



}
