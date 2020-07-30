package com.example.maprace.component;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import androidx.fragment.app.DialogFragment;

public class MapRaceDialog extends DialogFragment {
    protected String title;
    protected String message;
    protected View contentView;
    protected String positiveButtonText;
    protected String negativeButtonText;
    protected DialogInterface.OnClickListener onNegativeClickListener;
    protected LandmarkGoalDialog.OnConfirmListener onConfirmListener;

    public interface OnConfirmListener {
        void onConfirm(Object obj);
    }

    public MapRaceDialog() {
        super();
        setTitle("Map Race");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public String getPositiveButtonText() {
        return positiveButtonText;
    }

    public void setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
    }

    public String getNegativeButtonText() {
        return negativeButtonText;
    }

    public void setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    public MapRaceDialog.OnConfirmListener getOnConfirmListener() {
        return onConfirmListener;
    }

    public void setOnConfirmListener(MapRaceDialog.OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public DialogInterface.OnClickListener getOnNegativeClickListener() {
        return onNegativeClickListener;
    }

    public void setOnNegativeClickListener(DialogInterface.OnClickListener onNegativeClickListener) {
        this.onNegativeClickListener = onNegativeClickListener;
    }

    protected AlertDialog.Builder getAlertDialogBuilder() {
        return new AlertDialog.Builder(getActivity());
    }
}