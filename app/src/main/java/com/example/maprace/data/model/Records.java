package com.example.maprace.data.model;

import java.io.Serializable;

public class Records implements Serializable {
    private Float longestDistance = null;
    private Long bestTime = null;

    public static Records getDefaultRecords() {
        return new Records();
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
