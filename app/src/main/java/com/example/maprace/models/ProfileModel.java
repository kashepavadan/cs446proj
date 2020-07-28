package com.example.maprace.models;

import com.example.maprace.ProfileActivity;
import com.example.maprace.dtos.Preference;
import com.example.maprace.dtos.UserProfile;
import com.example.maprace.services.PersistenceService;

public class ProfileModel {
    private final ProfileActivity profileActivity;
    private final PersistenceService persistenceService;

    private UserProfile userProfile;
    private Preference preference;

    public ProfileModel(ProfileActivity profileActivity) {
        this.profileActivity = profileActivity;
        persistenceService = PersistenceService.getInstance();

        refreshUserProfile();
        refreshPreference();
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        persistenceService.saveUserProfile(userProfile);
        refreshUserProfile();
    }

    public void deleteUserProfile() {
        persistenceService.deleteProfile();
    }

    private void refreshUserProfile() {
        userProfile = persistenceService.getUserProfile();
        profileActivity.onUpdateProfile(userProfile);
    }

    public Preference getPreference() {
        return preference;
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

    public void resetProfile() {
        setUserProfile(UserProfile.getDefaultUserProfile(userProfile));
        setPreference(Preference.getDefaultPreference());
    }
}
