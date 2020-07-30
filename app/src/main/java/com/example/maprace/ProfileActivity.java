package com.example.maprace;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maprace.component.ConfirmationDialog;
import com.example.maprace.component.PreferenceEntryView;
import com.example.maprace.component.ShareButton;
import com.example.maprace.component.TextInputDialog;
import com.example.maprace.data.model.GameMode;
import com.example.maprace.data.model.Preference;
import com.example.maprace.data.model.Records;
import com.example.maprace.data.model.UserProfile;
import com.example.maprace.model.ProfileModel;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ProfileActivity extends AppCompatActivity {
    private LinearLayout preferencesContainer;
    private TextView usernameTextView;
    private TextView longestDistanceTextView;
    private TextView bestTimeTextView;
    private ShareButton[] shareButtons;
    private RadioGroup gameModeRadioGroup;
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (profileModel == null) return;

            GameMode gameMode = GameMode.WALK;

            switch (checkedId) {
                case R.id.bikeModeRadio:
                    gameMode = GameMode.BIKE;
                    break;

                case R.id.carModeRadio:
                    gameMode = GameMode.CAR;
                    break;

                default:
                    break;
            }

            profileModel.setGameMode(gameMode);
        }
    };

    private ProfileModel profileModel;

    private static String getTimeString(Long milliseconds) {
        if (milliseconds == null) return "N/A";

        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds - TimeUnit.HOURS.toMillis(hours));

        return String.format(Locale.getDefault(), "%2d hrs %2d mins", hours, minutes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        preferencesContainer = findViewById(R.id.preferencesContainer);
        usernameTextView = findViewById(R.id.usernameTextView);
        longestDistanceTextView = findViewById(R.id.longestDistance);
        bestTimeTextView = findViewById(R.id.bestTime);
        shareButtons = new ShareButton[]{ findViewById(R.id.twitterButton), findViewById(R.id.facebookButton)};
        gameModeRadioGroup = findViewById(R.id.gameModeRadioGroup);
        profileModel = new ProfileModel(this);
    }

    public void onUpdateProfile(UserProfile userProfile) {
        usernameTextView.setText(userProfile.getUsername());

        GameMode gameMode = userProfile.getGameMode();
        if (gameMode == null) return;

        int id = R.id.walkModeRadio;;

        switch (gameMode) {
            case BIKE:
                id = R.id.bikeModeRadio;
                break;

            case CAR:
                id = R.id.carModeRadio;
                break;

            default:
                break;
        }

        RadioButton radioButton = gameModeRadioGroup.findViewById(id);
        gameModeRadioGroup.setOnCheckedChangeListener(null);
        radioButton.setChecked(true);
        gameModeRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public void onUpdateRecords(Records records) {
        longestDistanceTextView.setText(records.getLongestDistance() != null ?
                String.format(Locale.getDefault(), "%.2f km", records.getLongestDistance() / 1000) : "N/A");
        bestTimeTextView.setText(getTimeString(records.getBestTime()));

        for (ShareButton button: shareButtons) {
            button.setLongestDistance(longestDistanceTextView.getText().toString());
            button.setBestTime(bestTimeTextView.getText().toString());
        }
    }

    public void onUpdatePreference(Preference preference) {
        List<Preference.Entry> preferenceEntries = preference.getEntries();

        preferencesContainer.removeAllViews();

        for (Preference.Entry entry : preferenceEntries) {
            preferencesContainer.addView(new PreferenceEntryView(this, entry));
        }
    }

    public void onPreferenceEntryChange(String id, int value) {
        if (profileModel == null) return;
        profileModel.updatePreference(id, value);
    }

    public void onResetSettings(View view) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog();

        confirmationDialog.setMessage("Are you sure you want to reset the settings?");
        confirmationDialog.setOnPositiveClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                profileModel.resetSettings();
            }
        });
        confirmationDialog.show(getSupportFragmentManager(), "resetSettingsConfirmationDialog");
    }

    public void onDeleteProfile(View view) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog();

        confirmationDialog.setMessage("Are you sure you want to delete your profile?");
        confirmationDialog.setOnPositiveClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                profileModel.deleteUserProfile();
                finish();
            }
        });
        confirmationDialog.show(getSupportFragmentManager(), "deleteProfileConfirmationDialog");
    }

    public void onClearRecords(View view) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog();
        String gameMode = profileModel.getUserProfile().getGameMode().getValue();

        confirmationDialog.setMessage(String.format("Are you sure you want to clear the records for %s mode?", gameMode));
        confirmationDialog.setOnPositiveClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                profileModel.clearRecords();
            }
        });
        confirmationDialog.show(getSupportFragmentManager(), "clearRecordsConfirmationDialog");
    }

    public void onEditUsername(View view) {
        TextInputDialog textInputDialog = new TextInputDialog();

        textInputDialog.setMessage("Please enter a new username:");
        textInputDialog.setDefaultValue(profileModel.getUserProfile().getUsername());
        textInputDialog.setInputHint("Username");
        textInputDialog.setTextInputDialogListener(new TextInputDialog.TextInputDialogListener() {
            @Override
            public void onConfirm(String text) {
                profileModel.updateUsername(text);
            }
        });

        textInputDialog.show(getSupportFragmentManager(), "editUsernameDialog");
    }
}
