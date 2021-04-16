package com.example.demo.api;

import com.example.demo.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("login")
@RestController
public class LoginController {

    private final ApplicationUserService applicationUserService;

    @Autowired
    public LoginController(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    @PostMapping("login")
    public Boolean getLoginView(@RequestBody String username, String password) {
        return applicationUserService.login(username, password);
    }

}
