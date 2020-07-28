package com.example.maprace.dtos;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private String username;
    private Float longestDistance = null;
    private Long bestTime = null;

    public static UserProfile getDefaultUserProfile(UserProfile profile) {
        UserProfile newProfile = new UserProfile();
        newProfile.setUsername(profile.getUsername());

        return newProfile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Float getLongestDistance() {
        return longestDistance;
    }

    public void setLongestDistance(Float longestDistance) {
        this.longestDistance = longestDistance;
    }

    public Long getBestTime() {
        return bestTime;
    }

    public void setBestTime(Long bestTime) {
        this.bestTime = bestTime;
    }
}
