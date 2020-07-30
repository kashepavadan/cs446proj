package com.example.maprace.model;

import com.example.maprace.ProfileActivity;
import com.example.maprace.data.model.GameMode;
import com.example.maprace.data.model.Preference;
import com.example.maprace.data.model.Records;
import com.example.maprace.data.model.UserProfile;
import com.example.maprace.service.PersistenceService;

public class ProfileModel {
    private final ProfileActivity profileActivity;
    private final PersistenceService persistenceService;

    private UserProfile userProfile;
    private Preference preference;

    public ProfileModel(ProfileActivity profileActivity) {
        this.profileActivity = profileActivity;
        persistenceService = PersistenceService.getInstance();

        refreshUserProfile();
        refreshRecords();
        refreshPreference();
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        persistenceService.saveUserProfile(userProfile);
        refreshUserProfile();
    }

    public void setGameMode(GameMode gameMode) {
        userProfile.setGameMode(gameMode);
        setUserProfile(userProfile);

        // records and preference are dependant on game mode
        refreshRecords();
        refreshPreference();
    }

    public void deleteUserProfile() {
        persistenceService.clearAllData();
    }

    private void refreshUserProfile() {
        userProfile = persistenceService.getUserProfile();
        profileActivity.onUpdateProfile(userProfile);
    }

    public void setRecords(Records records) {
        persistenceService.saveRecords(records);
        refreshRecords();
    }

    private void refreshRecords() {
        Records records = persistenceService.getRecords();
        profileActivity.onUpdateRecords(records);
    }

    public void setPreference(Preference preference) {
        persistenceService.savePreference(preference);
        refreshPreference();
    }

    public void updatePreference(String id, int value) {
        preference.setEntry(id, value);
        setPreference(preference);
    }

    private void refreshPreference() {
        preference = persistenceService.getPreference();
        profileActivity.onUpdatePreference(preference);
    }

    public void resetSettings() {
        setPreference(Preference.getDefaultPreference());
    }

    public void clearRecords() {
        persistenceService.deleteRecords();
        refreshRecords();
    }
}
