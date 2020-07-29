package com.example.maprace.data.model;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private String username;
    private GameMode gameMode;

    public static UserProfile newInstance(String username) {
        UserProfile newProfile = new UserProfile();

        newProfile.setUsername(username);
        newProfile.setGameMode(GameMode.WALK);

        return newProfile;
    }

    public static UserProfile getDefaultUserProfile(UserProfile profile) {
        return newInstance(profile.getUsername());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
}
