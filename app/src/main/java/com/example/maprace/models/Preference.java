package com.example.maprace.models;
import java.util.ArrayList;
import java.util.List;

public class Preference {
    private List<String> category;
    private List<Integer> weight;

    public void init(){
        category = new ArrayList<>();
        weight = new ArrayList<>();
        String[] arr = {"Restaurant", "Bank", "Hotel", "Park", "Mall"};

        for (int i = 0; i < arr.length; i++){
            category.add(arr[i]);
            weight.add(5);
        }
    }
}
