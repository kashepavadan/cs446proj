package com.example.maprace.services;

import android.content.Context;

import com.example.maprace.dtos.UserProfile;
import com.example.maprace.dtos.Preference;

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

    private static PersistenceService instance;

    private final Context applicationContext;

    public PersistenceService(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static void init(Context applicationContext) {
        instance = new PersistenceService(applicationContext);
    }

    public static PersistenceService getInstance() {
        return instance;
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

    public UserProfile getUserProfile() {
        Object object = readObject(applicationContext, profileFilename);

        if (object == null) return new UserProfile();
        return (UserProfile) object;
    }

    public void saveUserProfile(UserProfile profile) {
        writeObject(applicationContext,profileFilename, profile);
    }

    public void deleteProfile() {
        deleteFile(applicationContext, profileFilename);
        deleteFile(applicationContext, preferenceFilename);
    }

    public boolean isProfileExist() {
        return isFileExist(applicationContext, profileFilename);
    }

    public Preference getPreference() {
        Object object = readObject(applicationContext, preferenceFilename);

        if (object == null) return new Preference();
        return (Preference) object;
    }

    public void savePreference(Preference pref) {
        writeObject(applicationContext,preferenceFilename, pref);
    }

    public void deletePreference() {
        deleteFile(applicationContext, preferenceFilename);
    }
}
