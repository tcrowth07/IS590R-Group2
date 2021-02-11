package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class User {

    private final UUID id;
    private final String name;
    private final String email;
    //private final String password;

    //@JsonProperty("password") String password
    public User(@JsonProperty("id") UUID id,
                @JsonProperty("name") String name,
                @JsonProperty("email") String email
                ) {
        this.id = id;
        this.name = name;
        this.email = email;
        //this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {return email;}

    //public String getPassword() {return  password;}
}
