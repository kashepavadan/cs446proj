package com.example.maprace.utils;

import android.content.Context;

import com.example.maprace.models.UserProfile;
import com.example.maprace.models.Preference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class StorageUtils {
    private static final String profileFilename = "profile";
    private static final String preferenceFilename = "preference";

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

    public static UserProfile getUserProfile(Context applicationContext) {
        Object object = readObject(applicationContext, profileFilename);

        if (object == null) return new UserProfile();
        return (UserProfile) object;
    }

    public static void saveUserProfile(Context applicationContext, UserProfile profile) {
        writeObject(applicationContext,profileFilename, profile);
    }

    public static void deleteProfile(Context applicationContext) {
        deleteFile(applicationContext, profileFilename);
    }

    public static boolean isProfileExist(Context applicationContext) {
        return isFileExist(applicationContext, profileFilename);
    }

    public static Preference getPreference(Context applicationContext) {
        Object object = readObject(applicationContext, preferenceFilename);

        if (object == null) return new Preference();
        return (Preference) object;
    }

    public static void savePreference(Context applicationContext, Preference pref) {
        writeObject(applicationContext,preferenceFilename, pref);
    }
}
