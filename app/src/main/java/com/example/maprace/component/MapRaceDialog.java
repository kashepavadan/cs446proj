package com.example.maprace.component;

import android.app.AlertDialog;
import android.view.View;

import androidx.fragment.app.DialogFragment;

public class MapRaceDialog extends DialogFragment {
    protected String title;
    protected String message;
    protected View contentView;

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

    protected AlertDialog.Builder getAlertDialogBuilder() {
        return new AlertDialog.Builder(getActivity());
    }
}
