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
    public interface TextInputDialogListener {
        void onConfirm(String text);
    }

    private String defaultValue;
    private String inputHint;
    private String confirmButtonText;
    private TextInputDialogListener textInputDialogListener;

    public TextInputDialog() {
        super();
        setConfirmButtonText("Ok");
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getInputHint() {
        return inputHint;
    }

    public void setInputHint(String inputHint) {
        this.inputHint = inputHint;
    }

    public String getConfirmButtonText() {
        return confirmButtonText;
    }

    public void setConfirmButtonText(String confirmButtonText) {
        this.confirmButtonText = confirmButtonText;
    }

    public TextInputDialogListener getTextInputDialogListener() {
        return textInputDialogListener;
    }

    public void setTextInputDialogListener(TextInputDialogListener textInputDialogListener) {
        this.textInputDialogListener = textInputDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_text_input, null);
        EditText usernameEditText = view.findViewById(R.id.inputTextEdit);

        usernameEditText.setText(getDefaultValue());
        usernameEditText.setHint(getInputHint());

        setContentView(view);

        return getAlertDialogBuilder()
                .setTitle(getTitle())
                .setMessage(getMessage())
                .setView(getContentView())
                .setPositiveButton(getConfirmButtonText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getTextInputDialogListener().onConfirm(usernameEditText.getText().toString());
                    }
                })
                .create();
    }
}
