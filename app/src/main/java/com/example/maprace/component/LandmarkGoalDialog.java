package com.example.maprace.component;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import com.example.maprace.R;

public class LandmarkGoalDialog extends MapRaceDialog {
    private int minValue = 1;

    public LandmarkGoalDialog() {
        super();
        setMessage("Select the number of landmarks you aim to visit:");
        setPositiveButtonText("Confirm");
        setSetting(new Integer(10));
    }

    @Override
    public void setSetting(Object setting) {
        this.setting = ((Integer) setting).intValue();

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = getAlertDialogBuilder();
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_landmark_goal, null);
        NumberPicker goalPicker = view.findViewById(R.id.goalPicker);

        goalPicker.setMinValue(minValue);
        goalPicker.setMaxValue(((Integer) getSetting()).intValue());

        return builder.setTitle(getTitle())
                .setMessage(getMessage())
                .setView(view)
                .setPositiveButton(getPositiveButtonText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (getOnConfirmListener() != null) {
                            getOnConfirmListener().onConfirm(new Integer(goalPicker.getValue()));
                        }
                    }
                })
                .create();
    }
}
