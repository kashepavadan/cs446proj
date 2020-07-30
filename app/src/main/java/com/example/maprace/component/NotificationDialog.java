package com.example.maprace.component;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NotificationDialog extends MapRaceDialog {
    private String buttonText;
    private DialogInterface.OnClickListener onClickListener;

    public NotificationDialog() {
        super();
        setButtonText("Ok");
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public DialogInterface.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return getAlertDialogBuilder()
                .setTitle(getTitle())
                .setMessage(getMessage())
                .setView(getContentView())
                .setPositiveButton(getButtonText(), getOnClickListener())
                .create();
    }
}
