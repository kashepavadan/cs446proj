package com.example.maprace;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maprace.dtos.Preference;
import com.example.maprace.dtos.UserProfile;
import com.example.maprace.models.ProfileModel;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ProfileActivity extends AppCompatActivity {
    private LinearLayout preferencesContainer;
    private TextView usernameTextView;
    private TextView longestDistanceTextView;
    private TextView bestTimeTextView;

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

        profileModel = new ProfileModel(this);
    }

    public void onUpdateProfile(UserProfile userProfile) {
        usernameTextView.setText(userProfile.getUsername());
        longestDistanceTextView.setText(userProfile.getLongestDistance() != null ?
                String.format(Locale.getDefault(), "%.2f km", userProfile.getLongestDistance() / 1000) : "N/A");
        bestTimeTextView.setText(getTimeString(userProfile.getBestTime()));
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

    public void onResetProfile(View view) {
        profileModel.resetProfile();
    }

    public void onDeleteProfile(View view) {
        profileModel.deleteUserProfile();
        finish();
    }
}
