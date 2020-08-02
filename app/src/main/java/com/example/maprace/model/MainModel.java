package com.example.maprace.model;

import com.example.maprace.MainActivity;
import com.example.maprace.service.PersistenceService;

public class MainModel {
    private final MainActivity mainActivity;
    private final PersistenceService persistenceService;

    public MainModel(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        
        persistenceService = PersistenceService.getInstance(mainActivity.getApplicationContext());

        refreshGameMode();
    }

    public boolean isProfileExist() {
        return persistenceService.isProfileExist();
    }

    private void refreshGameMode() {
        mainActivity.onUpdateGameMode(persistenceService.getGameMode());
    }
}
