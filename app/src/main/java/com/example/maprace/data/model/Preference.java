package com.example.maprace.data.model;
import com.example.maprace.model.GameModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Preference implements Serializable {
    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 10;
    private static final int DEFAULT_VALUE = 5;

    public static class Entry implements Serializable {
        private final String id;
        private int value;

        public Entry(String id, int value) {

            this.id = id;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    private final List<Entry> entries;

    public Preference() {
        entries = new ArrayList<>();
    }

    public static Preference getDefaultPreference() {
        List<Entry> entries = new ArrayList<>();

        for (String id : GameModel.poiTypes) {
            entries.add(new Entry(id, DEFAULT_VALUE));
        }

        return new Preference(entries);
    }

    public Preference(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntry(String id, int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) return;

        for (Entry entry : entries) {
            if (entry.getId().equals(id)) {
                entry.setValue(value);
            }
        }
    }
}
