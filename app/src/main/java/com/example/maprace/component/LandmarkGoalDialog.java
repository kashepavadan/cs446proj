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
    private int maxValue;
    private int minValue = 1;

    public LandmarkGoalDialog() {
        super();
        setMessage("Select the number of landmarks you aim to visit:");
        setPositiveButtonText("Confirm");
        setMaxValue(10);
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = getAlertDialogBuilder();
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_landmark_goal, null);
        NumberPicker goalPicker = view.findViewById(R.id.goalPicker);

        goalPicker.setMinValue(minValue);
        goalPicker.setMaxValue(getMaxValue());

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
