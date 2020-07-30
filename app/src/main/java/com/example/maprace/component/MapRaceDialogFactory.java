package com.example.maprace.component;

public class MapRaceDialogFactory {
    public static MapRaceDialog getNotificationDialog() {
        return new NotificationDialog();
    }

    public static MapRaceDialog getConfirmationDialog() {
        return new ConfirmationDialog();
    }

    public static MapRaceDialog getTextInputDialog(String defaultValue, String hint) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setDefaultValue(defaultValue);
        textInputDialog.setInputHint(hint);

        return textInputDialog;
    }

    public static MapRaceDialog getNumberPickerDialog(int minValue, int maxValue) {
        NumberPickerDialog numberPickerDialog = new NumberPickerDialog();
        numberPickerDialog.setMinValue(minValue);
        numberPickerDialog.setMaxValue(maxValue);

        return numberPickerDialog;
    }
}
