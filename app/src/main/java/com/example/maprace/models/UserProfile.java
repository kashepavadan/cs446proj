package com.example.maprace.models;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
