package com.example.maprace.models;
import java.io.Serializable;
import java.util.ArrayList;

public class Preference implements Serializable {
    private ArrayList<String> category;
    private ArrayList<Integer> weights;

    public ArrayList<Integer> getWeights(){
        return weights;
    }

    public void setEntryWeight(int index, int value) {
        weights.set(index, value);
    }

    public void init(){
        category = new ArrayList<>();
        weights = new ArrayList<>();
        String[] arr = {"Restaurant", "Bank", "Hotel", "Park", "Mall"};

        for (int i = 0; i < arr.length; i++){
            category.add(arr[i]);
            weights.add(5);
        }
    }

}
