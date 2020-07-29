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
        return getFilename(preferenceFilename);
    }

    private String getRecordsFilename() {
        return getFilename(recordsFilename);
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    private static Object readObject(Context applicationContext, String filename) {
        Object object = null;

        try {
            FileInputStream fileInputStream = applicationContext.openFileInput(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            object = objectInputStream.readObject();
            fileInputStream.close();
        } catch (FileNotFoundException ignored) {

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return object;
    }

    private static void writeObject(Context applicationContext, String filename, Object object) {
        try {
            FileOutputStream fileOutputStream = applicationContext.openFileOutput(filename, 0);
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
        String[] files = new String[] {recordsFilename, preferenceFilename};

        deleteFile(applicationContext, profileFilename);

        for (GameMode mode : modes) {
            for (String file : files) {
                deleteFile(applicationContext, getFilename(file, mode.getValue()));
            }
        }
    }

    public UserProfile getUserProfile() {
        Object object = readObject(applicationContext, getProfileFilename());

        if (object == null) return new UserProfile();
        return (UserProfile) object;
    }

    public void saveUserProfile(UserProfile profile) {
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

    public void savePreference(Preference pref) {
        writeObject(applicationContext, getPreferenceFilename(), pref);
    }

    public void deletePreference() {
        deleteFile(applicationContext, getPreferenceFilename());
    }

    public Records getRecords() {
        Object object = readObject(applicationContext, getRecordsFilename());

        if (object == null) return Records.getDefaultRecords();
        return (Records) object;
    }

    public void saveRecords(Records records) {
        writeObject(applicationContext, getRecordsFilename(), records);
    }

    public void deleteRecords() {
        deleteFile(applicationContext, getRecordsFilename());
    }

    public GameMode getGameMode() {
        return getUserProfile().getGameMode();
    }
}
