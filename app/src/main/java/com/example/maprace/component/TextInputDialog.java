package com.example.maprace.component;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.maprace.R;

public class TextInputDialog extends MapRaceDialog {
    public TextInputDialog() {
        super();
        setPositiveButtonText("Ok");
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public void setSetting(Object setting) {
        this.setting = (String) setting;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_text_input, null);
        EditText usernameEditText = view.findViewById(R.id.inputTextEdit);

        usernameEditText.setText((String) getDefaultValue());
        usernameEditText.setHint((String) getSetting());

        setContentView(view);

        return getAlertDialogBuilder()
                .setTitle(getTitle())
                .setMessage(getMessage())
                .setView(getContentView())
                .setPositiveButton(getPositiveButtonText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (getOnConfirmListener() != null) {
                            getOnConfirmListener().onConfirm(usernameEditText.getText().toString());
                        }
                    }
                })
                .create();
    }
}
