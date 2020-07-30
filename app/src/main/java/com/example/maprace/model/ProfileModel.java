package com.example.maprace.model;

import com.example.maprace.ProfileActivity;
import com.example.maprace.data.model.GameMode;
import com.example.maprace.service.PersistenceService;

public class ProfileModel {
    private final ProfileActivity profileActivity;
    private final PersistenceService persistenceService;

    public ProfileModel(ProfileActivity profileActivity) {
        this.profileActivity = profileActivity;
        persistenceService = PersistenceService.getInstance();

        refreshUserProfile();
        refreshRecords();
        refreshPreference();
    }

    public void setGameMode(GameMode gameMode) {
        persistenceService.setGameMode(gameMode);
        refreshUserProfile();

        // records and preference are dependant on game mode
        refreshRecords();
        refreshPreference();
    }

    public void deleteUserProfile() {
        persistenceService.clearAllData();
    }

    private void refreshUserProfile() {
        profileActivity.onUpdateProfile(persistenceService.getUsername(), persistenceService.getGameMode());
    }

    private void refreshRecords() {
        profileActivity.onUpdateRecords(persistenceService.getLongestDistance(), persistenceService.getBestTime());
    }

    public void updatePreference(String id, int value) {
        persistenceService.updatePreference(id, value);
        refreshPreference();
    }

    private void refreshPreference() {
        profileActivity.onUpdatePreference(persistenceService.getPreference());
    }

    public void resetSettings() {
        persistenceService.resetPreference();
        refreshPreference();
    }

    public void clearRecords() {
        persistenceService.deleteRecords();
        refreshRecords();
    }

    public void updateUsername(String text) {
        persistenceService.setUsername(text);
        refreshUserProfile();
    }

    public String getUsername() {
        return persistenceService.getUsername();
    }

    public GameMode getGameMode() {
        return persistenceService.getGameMode();
    }
}
