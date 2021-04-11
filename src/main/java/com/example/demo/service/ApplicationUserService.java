package com.example.demo.service;

import com.example.demo.dao.ApplicationUserDao;
import com.example.demo.dao.UserDao;
import com.example.demo.model.ApplicationUser;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationUserService implements UserDetailsService {

    private final ApplicationUserDao applicationUserDao;

    @Autowired
    public ApplicationUserService(@Qualifier("applicationUserPostgres") ApplicationUserDao applicationUserDao) {
        this.applicationUserDao = applicationUserDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return applicationUserDao.selectApplicationUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", username)));
    }


    public String addApplicationUser(ApplicationUser user) {
        return applicationUserDao.insertApplicationUser(user);
    }

    public List<ApplicationUser> getAllApplicationUsers() {
        return applicationUserDao.selectAllApplicationUsers();
    }

    public Optional<ApplicationUser> getApplicationUserById(UUID id) {
        return applicationUserDao.selectApplicationUserById(id);
    }

    public String deleteApplicationUser(UUID id){
        return applicationUserDao.deleteApplicationUserById(id);
    }

    public ApplicationUser updateApplicationUser(UUID id, ApplicationUser newUser){
        return applicationUserDao.updateApplicationUserById(id, newUser);
    }





}
