package com.example.maprace;

import android.widget.SeekBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.maprace.dtos.Preference;

public class PreferenceEntryView extends CardView {
    public PreferenceEntryView(ProfileActivity profileActivity, Preference.Entry preferenceEntry) {
        super(profileActivity);

        inflate(profileActivity, R.layout.view_preference_entry, this);

        TextView preferenceName = findViewById(R.id.preferenceName);
        TextView preferenceValue = findViewById(R.id.preferenceValue);
        SeekBar preferenceSeekbar = findViewById(R.id.preferenceSeekBar);

        preferenceName.setText(preferenceEntry.getId());
        preferenceValue.setText(Integer.toString(preferenceEntry.getValue()));
        preferenceSeekbar.setProgress(preferenceEntry.getValue());

        preferenceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                preferenceValue.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                profileActivity.onPreferenceEntryChange(preferenceEntry.getId(), seekBar.getProgress());
            }
        });
    }
}
