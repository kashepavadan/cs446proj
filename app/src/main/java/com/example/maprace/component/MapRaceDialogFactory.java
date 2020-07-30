package com.example.maprace.component;

import com.example.maprace.component.ConfirmationDialog;
import com.example.maprace.component.MapRaceDialog;
import com.example.maprace.component.TextInputDialog;
import com.example.maprace.component.NotificationDialog;
import com.example.maprace.component.LandmarkGoalDialog;

public class MapRaceDialogFactory {
    private static final String NOTIFICATION_DIALOG="NOTIFICATION DIALOG";
    private static final String LANDMARK_GOAL_DIALOG="LANDMARK GOAL DIALOG";
    private static final String CONFIRMATION_DIALOG="CONFIRMATION DIALOG";
    private static final String TEXT_INPUT_DIALOG="TEXT INPUT DIALOG";

    public MapRaceDialog getDialog(String type){
        if(type == null){
            return null;
        }
        if(type.equals(NOTIFICATION_DIALOG)){
            return new NotificationDialog();
        } else if(type.equals(LANDMARK_GOAL_DIALOG)){
            return new LandmarkGoalDialog();
        } else if(type.equals(CONFIRMATION_DIALOG)){
            return new ConfirmationDialog();
        } else if(type.equals(TEXT_INPUT_DIALOG)){
            return new TextInputDialog();
        }

        return null;
    }
}
