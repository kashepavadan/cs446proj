package com.example.maprace;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        gameModeRadioGroup = findViewById(R.id.gameModeRadioGroup);

        gameModeRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        profileModel = new ProfileModel(this);
    }

    public void onUpdateProfile(String username, GameMode gameMode) {
        usernameTextView.setText(username);

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
        profileModel.resetSettings();
    }

    public void onDeleteProfile(View view) {
        profileModel.deleteUserProfile();
        finish();
    }
}
