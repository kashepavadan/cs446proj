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

public class NumberPickerDialog extends MapRaceDialog {
    private int minValue;
    private int maxValue;

    public NumberPickerDialog() {
        super();
        setMessage("Select the number of landmarks you aim to visit:");
        setPositiveButtonText("Confirm");
    }

    private int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    private int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @NonNull
    @Override
    protected Dialog onPrepareDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = getAlertDialogBuilder();
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_landmark_goal, null);
        NumberPicker goalPicker = view.findViewById(R.id.goalPicker);

        goalPicker.setMinValue(getMinValue());
        goalPicker.setMaxValue(getMaxValue());

        return builder
                .setView(view)
                .setPositiveButton(getPositiveButtonText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (getOnConfirmListener() != null) {
                            getOnConfirmListener().onConfirm(goalPicker.getValue());
                        }
                    }
                })
                .create();
    }
}
