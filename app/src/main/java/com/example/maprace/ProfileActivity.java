package com.example.maprace;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maprace.component.MapRaceDialog;
import com.example.maprace.component.MapRaceDialogFactory;
import com.example.maprace.component.PreferenceEntryView;
import com.example.maprace.component.ShareButton;
import com.example.maprace.data.model.GameMode;
import com.example.maprace.data.model.Preference;
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
    private Button maxDistanceButton;

    private static final int TOP_MAX_DISTANCE = 100;

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
        maxDistanceButton = findViewById(R.id.maxDistanceButton);
        longestDistanceTextView = findViewById(R.id.longestDistance);
        bestTimeTextView = findViewById(R.id.bestTime);
        shareButtons = new ShareButton[]{ findViewById(R.id.twitterButton), findViewById(R.id.facebookButton)};
        gameModeRadioGroup = findViewById(R.id.gameModeRadioGroup);
        profileModel = new ProfileModel(this);
    }

    public void onUpdateProfile(String username, GameMode gameMode, int maxDistance) {
        usernameTextView.setText(username);
        maxDistanceButton.setText(String.valueOf(maxDistance));

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

    public void onUpdateRecords(Float longestDistance, Long bestTime) {
        longestDistanceTextView.setText(longestDistance != null ?
                String.format(Locale.getDefault(), "%.2f km", longestDistance / 1000) : "N/A");
        bestTimeTextView.setText(getTimeString(bestTime));

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
        MapRaceDialog confirmationDialog = MapRaceDialogFactory.getConfirmationDialog();

        confirmationDialog.setMessage("Are you sure you want to reset the settings?");
        confirmationDialog.setOnPositiveClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                profileModel.resetSettings();
            }
        });
        confirmationDialog.show(getSupportFragmentManager(), "resetSettingsConfirmationDialog");
    }

    public void onDeleteProfile(View view) {
        MapRaceDialog confirmationDialog = MapRaceDialogFactory.getConfirmationDialog();

        confirmationDialog.setMessage("Are you sure you want to delete your profile?");
        confirmationDialog.setOnPositiveClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                profileModel.deleteUserProfile();
                finish();
            }
        });
        confirmationDialog.show(getSupportFragmentManager(), "deleteProfileConfirmationDialog");
    }

    public void onClearRecords(View view) {
        MapRaceDialog confirmationDialog = MapRaceDialogFactory.getConfirmationDialog();
        String gameMode = profileModel.getGameMode().getValue();

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
        MapRaceDialog textInputDialog = MapRaceDialogFactory.getTextInputDialog(profileModel.getUsername(), "Username");

        textInputDialog.setMessage("Please enter a new username:");
        textInputDialog.setOnConfirmListener(new MapRaceDialog.OnConfirmListener() {
            @Override
            public void onConfirm(Object text) {
                profileModel.updateUsername((String) text);
            }
        });

        textInputDialog.show(getSupportFragmentManager(), "editUsernameDialog");
    }

    public void onEditMaxDistance(View view) {
        MapRaceDialog distanceDialog = MapRaceDialogFactory.getNumberPickerDialog(1,TOP_MAX_DISTANCE);

        distanceDialog.setMessage("Please select the maximum radius for landmark generation");
        distanceDialog.setOnConfirmListener(new MapRaceDialog.OnConfirmListener() {
            @Override
            public void onConfirm(Object obj) {
                profileModel.updateMaxDistance(((Integer) obj).intValue());
            }
        });

        distanceDialog.show(getSupportFragmentManager(), "editMaxDistanceDialog");
    }

    public void onExitProfile(View v) {
        ProfileActivity.super.onBackPressed();
    }
}
