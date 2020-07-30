package com.example.maprace.service;

import android.content.Context;

import com.example.maprace.data.model.GameMode;
import com.example.maprace.data.model.Records;
import com.example.maprace.data.model.UserProfile;
import com.example.maprace.data.model.Preference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class PersistenceService {
    private static final String profileFilename = "profile";
    private static final String preferenceFilename = "preference";
    private static final String recordsFilename = "records";

    private static PersistenceService instance;

    private final Context applicationContext;
    private GameMode mode;

    public PersistenceService(Context applicationContext) {
        this.applicationContext = applicationContext;
        mode = null;
    }

    public static void init(Context applicationContext) {
        instance = new PersistenceService(applicationContext);
        instance.setMode(instance.getGameMode());
    }

    public static PersistenceService getInstance() {
        return instance;
    }

    private String getFilename(String prefix, String suffix) {
        return prefix.concat("_").concat(suffix);
    }

    private String getFilename(String prefix) {
        if (mode == null) return prefix;
        return getFilename(prefix, mode.getValue());
    }

    private String getProfileFilename() {
        return profileFilename;
    }

    private String getPreferenceFilename() {
        return preferenceFilename;
    }

    private String getRecordsFilename() {
        return getFilename(recordsFilename);
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    private static Object readObject(Context applicationContext, String filename) {
        Object object = null;

        try (FileInputStream fileInputStream = applicationContext.openFileInput(filename)) {
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            object = objectInputStream.readObject();
        } catch (FileNotFoundException ignored) {

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return object;
    }

    private static void writeObject(Context applicationContext, String filename, Object object) {
        try (FileOutputStream fileOutputStream = applicationContext.openFileOutput(filename, 0)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
        } catch (FileNotFoundException ignored) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isFileExist(Context applicationContext, String filename) {
        File file = new File(applicationContext.getFilesDir(), filename);
        return file.exists();
    }

    private static void deleteFile(Context applicationContext, String filename) {
        File file = new File(applicationContext.getFilesDir(), filename);
        file.delete();
    }

    public void clearAllData() {
        GameMode[] modes = new GameMode[] {GameMode.WALK, GameMode.BIKE, GameMode.CAR};
        String[] files = new String[] {recordsFilename};

        deleteFile(applicationContext, profileFilename);
        deleteFile(applicationContext, preferenceFilename);

        for (GameMode mode : modes) {
            deleteFile(applicationContext, getFilename(recordsFilename, mode.getValue()));
        }
    }

    private UserProfile getUserProfile() {
        Object object = readObject(applicationContext, getProfileFilename());

        if (object == null) return new UserProfile();
        return (UserProfile) object;
    }

    private void saveUserProfile(UserProfile profile) {
        setMode(profile.getGameMode());
        writeObject(applicationContext, getProfileFilename(), profile);
    }

    public void deleteProfile() {
        deleteFile(applicationContext, getProfileFilename());
        deleteFile(applicationContext, getPreferenceFilename());
    }

    public boolean isProfileExist() {
        return isFileExist(applicationContext, getProfileFilename());
    }

    public Preference getPreference() {
        Object object = readObject(applicationContext, getPreferenceFilename());

        if (object == null) return Preference.getDefaultPreference();
        return (Preference) object;
    }

    private void savePreference(Preference pref) {
        writeObject(applicationContext, getPreferenceFilename(), pref);
    }

    public void deletePreference() {
        deleteFile(applicationContext, getPreferenceFilename());
    }

    private Records getRecords() {
        Object object = readObject(applicationContext, getRecordsFilename());

        if (object == null) return Records.getDefaultRecords();
        return (Records) object;
    }

    private void saveRecords(Records records) {
        writeObject(applicationContext, getRecordsFilename(), records);
    }

    public void deleteRecords() {
        deleteFile(applicationContext, getRecordsFilename());
    }

    public GameMode getGameMode() {
        return getUserProfile().getGameMode();
    }

    public void updateRecords(float distanceWalked, long elapsedTime) {
        Records records = getRecords();

        boolean shouldSave = false;

        if (records.getLongestDistance() == null || records.getLongestDistance() < distanceWalked) {
            records.setLongestDistance(distanceWalked);
            shouldSave = true;
        }

        if (records.getBestTime() == null || records.getBestTime() > elapsedTime) {
            records.setBestTime(elapsedTime);
            shouldSave = true;
        }

        if (shouldSave) saveRecords(records);
    }

    public void first_time_register(String username) {
        UserProfile profile = UserProfile.newInstance(username);
        saveUserProfile(profile);

        Preference userPref = Preference.getDefaultPreference();
        savePreference(userPref);

        Records records = Records.getDefaultRecords();
        saveRecords(records);
    }

    public String getUsername() {
        return getUserProfile().getUsername();
    }

    public void setUsername(String username) {
        UserProfile profile = getUserProfile();
        profile.setUsername(username);
        saveUserProfile(profile);
    }

    public Float getLongestDistance() {
        return getRecords().getLongestDistance();
    }

    public Long getBestTime() {
        return getRecords().getBestTime();
    }

    public void setGameMode(GameMode gameMode) {
        UserProfile profile = getUserProfile();
        profile.setGameMode(gameMode);
        saveUserProfile(profile);
    }

    public void resetPreference() {
        savePreference(Preference.getDefaultPreference());
    }

    public void updatePreference(String id, int value) {
        Preference preference = getPreference();
        preference.setEntry(id, value);
        savePreference(preference);
    }
}
