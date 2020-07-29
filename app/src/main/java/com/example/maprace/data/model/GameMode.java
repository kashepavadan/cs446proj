package com.example.maprace.data.model;

import java.io.Serializable;

public enum GameMode implements Serializable {
    WALK("WALK"),
    BIKE("BIKE"),
    CAR("CAR");

    private final String value;

    GameMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
