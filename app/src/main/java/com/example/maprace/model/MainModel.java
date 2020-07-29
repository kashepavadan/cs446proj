package com.example.maprace.model;

import com.example.maprace.MainActivity;
import com.example.maprace.service.PersistenceService;

public class MainModel {
    private final MainActivity mainActivity;
    private final PersistenceService persistenceService;

    public MainModel(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        PersistenceService.init(mainActivity.getApplicationContext());
        persistenceService = PersistenceService.getInstance();

        refreshGameMode();
    }

    public boolean isProfileExist() {
        return persistenceService.isProfileExist();
    }

    private void refreshGameMode() {
        mainActivity.onUpdateGameMode(persistenceService.getGameMode());
    }
}
