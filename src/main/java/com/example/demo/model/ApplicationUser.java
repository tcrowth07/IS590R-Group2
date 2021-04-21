package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.example.demo.security.ApplicationUserRole.ADMIN;

public class ApplicationUser implements UserDetails {

    private final UUID id;
    private final String name;
    private final String username;
    private final String password;
//    private final String email; //Do we need this?
    private final String role;
    private final Set<? extends GrantedAuthority> grantedAuthorities = ADMIN.getGrantedAuthorities();
    private final boolean isAccountNonExpired;
    private final boolean isAccountNonLocked;
    private final boolean isCredentialsNonExpired;
    private final boolean isEnabled;
    //public ApplicationUser(UUID id, Set<? extends GrantedAuthority> grantedAuthorities, String name, String username, String password, boolean isAccountNonExpired, boolean isAccountNonLocked, boolean isCredentialsNonExpired, boolean isEnabled) {
    public ApplicationUser(UUID id, String role, @JsonProperty("name") String name, @JsonProperty("username") String username, @JsonProperty("password") String password, boolean isAccountNonExpired, boolean isAccountNonLocked, boolean isCredentialsNonExpired, boolean isEnabled) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
    }

    //I don't think this needs an override because the UserDetails interface doesn't have the getName method
    public UUID getId() {return id;}

    public String getName() {
        return name;
    }

    //@Override
    public String getRole(){
        return role;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
