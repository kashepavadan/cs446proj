package com.example.maprace.component;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MapRaceDialog extends DialogFragment {
    public interface OnConfirmListener {
        void onConfirm(Object obj);
    }

    protected String title;
    protected String message;
    protected String positiveButtonText;
    protected String negativeButtonText;
    protected boolean canceledOnTouchOutside;
    protected DialogInterface.OnClickListener onPositiveClickListener;
    protected DialogInterface.OnClickListener onNegativeClickListener;
    protected OnConfirmListener onConfirmListener;

    public MapRaceDialog() {
        super();
        setTitle("Map Race");
        setCanceledOnTouchOutside(true);
    }

    protected String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    protected String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    protected String getPositiveButtonText() {
        return positiveButtonText;
    }

    protected void setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
    }

    protected String getNegativeButtonText() {
        return negativeButtonText;
    }

    protected void setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    public boolean isCanceledOnTouchOutside() {
        return canceledOnTouchOutside;
    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }

    public DialogInterface.OnClickListener getOnPositiveClickListener() {
        return onPositiveClickListener;
    }

    public void setOnPositiveClickListener(DialogInterface.OnClickListener onPositiveClickListener) {
        this.onPositiveClickListener = onPositiveClickListener;
    }

    public DialogInterface.OnClickListener getOnNegativeClickListener() {
        return onNegativeClickListener;
    }

    public void setOnNegativeClickListener(DialogInterface.OnClickListener onNegativeClickListener) {
        this.onNegativeClickListener = onNegativeClickListener;
    }

    public OnConfirmListener getOnConfirmListener() {
        return onConfirmListener;
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    protected AlertDialog.Builder getAlertDialogBuilder() {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getTitle())
                .setMessage(getMessage())
                .setPositiveButton(getPositiveButtonText(), getOnPositiveClickListener())
                .setNegativeButton(getNegativeButtonText(), getOnNegativeClickListener());
    }

    protected Dialog onPrepareDialog(@Nullable Bundle savedInstanceState) {
        return getAlertDialogBuilder().create();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = onPrepareDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside());

        return dialog;
    }
}