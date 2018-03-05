package com.superaligator.konferencja.models;

/**
 * Created by CI_KMANKA on 2018-03-05.
 */

public class LoginRequest {
    private String username;
    private String password;
    private boolean rememberMe = true;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
