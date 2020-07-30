package com.example.maprace.component;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NotificationDialog extends MapRaceDialog {
    public NotificationDialog() {
        super();
        setPositiveButtonText("Ok");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return getAlertDialogBuilder()
                .setTitle(getTitle())
                .setMessage(getMessage())
                .setView(getContentView())
                .setPositiveButton(getPositiveButtonText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (getOnConfirmListener() != null) {
                            getOnConfirmListener().onConfirm(null);
                        }
                    }
                })
                .create();
    }
}
