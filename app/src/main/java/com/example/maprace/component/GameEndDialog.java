package com.example.maprace.component;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.maprace.GameActivity;


public class GameEndDialog extends DialogFragment {
    private final GameActivity gameActivity;

    public GameEndDialog(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage("Congratulations! You've reached the goal!")
                .setTitle("Map Race")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameActivity.finish();
                    }
                });

        return dialogBuilder.create();
    }
}
