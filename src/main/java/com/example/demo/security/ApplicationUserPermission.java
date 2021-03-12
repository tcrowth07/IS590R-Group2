package com.example.demo.security;

public enum ApplicationUserPermission {
    USER_READ("ApplicationUser:read"),
    USER_WRITE("ApplicationUser:write"),
    JOURNAL_READ("journal:read"),
    JOURNAL_WRITE("journal:write");

    private final String permission;

    ApplicationUserPermission(String permission){
        this.permission = permission;
    }

    public String getPermission(){
        return permission;
    }
}
