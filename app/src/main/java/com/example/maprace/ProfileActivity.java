package com.example.maprace;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maprace.models.UserProfile;
import com.example.maprace.utils.StorageUtils;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView usernameTextView = findViewById(R.id.usernameTextView);

        UserProfile userProfile = StorageUtils.getUserProfile(getApplicationContext());
        usernameTextView.setText(userProfile.getUsername());
    }

}
