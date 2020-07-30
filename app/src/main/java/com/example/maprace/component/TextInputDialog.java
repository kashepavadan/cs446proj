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
    private String defaultValue;
    private String inputHint;

    public TextInputDialog() {
        super();
        setPositiveButtonText("Ok");
    }

    public String getDefaultValue() { return defaultValue; }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getInputHint() { return inputHint; }

    public void setInputHint(String inputHint) {
        this.inputHint = inputHint;
    }

    @NonNull
    @Override
    public Dialog onPrepareDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_text_input, null);
        EditText usernameEditText = view.findViewById(R.id.inputTextEdit);

        usernameEditText.setText(getDefaultValue());
        usernameEditText.setHint(getInputHint());

        return getAlertDialogBuilder()
                .setView(view)
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
