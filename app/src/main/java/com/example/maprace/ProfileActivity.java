package com.example.maprace;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maprace.models.Preference;
import com.example.maprace.models.UserProfile;
import com.example.maprace.utils.StorageUtils;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ArrayList<TextView> texts = new ArrayList<>();
    private ArrayList<SeekBar> sliders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView usernameTextView = findViewById(R.id.usernameTextView);

        UserProfile userProfile = StorageUtils.getUserProfile(getApplicationContext());
        usernameTextView.setText(userProfile.getUsername());
        getElements();
        addSliderListener();
        restoreSetting();
    }

    private void restoreSetting() {
        Preference pref = StorageUtils.getPreference(getApplicationContext());
        ArrayList<Integer> weights = pref.getWeights();
        if (null != weights) {
            for (int i = 0; i < sliders.size(); i++) {
                int w = weights.get(i);
                sliders.get(i).setProgress(w);
                texts.get(i).setText(Integer.toString(w));
            }
        }
    }

    private void getElements(){
        sliders.add((SeekBar) findViewById(R.id.restaurantBar));
        sliders.add((SeekBar) findViewById(R.id.bankBar));
        sliders.add((SeekBar) findViewById(R.id.hotelBar));
        sliders.add((SeekBar) findViewById(R.id.parkBar));
        sliders.add((SeekBar) findViewById(R.id.mallBar));

        texts.add((TextView) findViewById(R.id.restaurantText));
        texts.add((TextView) findViewById(R.id.bankText));
        texts.add((TextView) findViewById(R.id.hotelText));
        texts.add((TextView) findViewById(R.id.parkText));
        texts.add((TextView) findViewById(R.id.mallText));
    }

    private void addSliderListener(){
        for (int i = 0; i < sliders.size(); i++){
            SeekBar seekbar = sliders.get(i);
            final TextView tv = texts.get(i);
            final int finalI = i;
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    tv.setText(Integer.toString(progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Preference pref = StorageUtils.getPreference(getApplicationContext());
                    pref.setEntryWeight(finalI, seekBar.getProgress());
                    StorageUtils.savePreference(getApplicationContext(), pref);
                }
            });
        }
    }

}
