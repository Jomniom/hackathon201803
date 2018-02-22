package com.superaligator.konferencja.models;


import android.support.annotation.NonNull;

public class LoginResponse extends Response {
    @NonNull
    public String userId;
    @NonNull
    public String email;
    @NonNull
    public String token;
}
