package com.example.demo.api;

import com.example.demo.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "login")
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
