package com.example.maprace.data.model;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private String username;
    private GameMode gameMode;
    private int maxDistance;

    private static final int DEFAULT_MAX_DISTANCE = 5;
    private static final GameMode DEFAULT_GAME_MODE = GameMode.WALK;

    public static UserProfile newInstance(String username) {
        UserProfile newProfile = new UserProfile();

        newProfile.setUsername(username);
        newProfile.setGameMode(DEFAULT_GAME_MODE);
        newProfile.setMaxDistance(DEFAULT_MAX_DISTANCE);

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

    public void resetGameMode() {this.gameMode = DEFAULT_GAME_MODE; }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void resetMaxDistance() {
        this.maxDistance = DEFAULT_MAX_DISTANCE;
    }
}
