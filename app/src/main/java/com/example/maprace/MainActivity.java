package com.example.maprace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.maprace.data.model.GameMode;
import com.example.maprace.model.MainModel;

public class MainActivity extends AppCompatActivity {
    private MainModel mainModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainModel = new MainModel(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // prompts to create profile when profile does not exist
        if (!mainModel.isProfileExist()) {
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        }
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void navigateProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void navigateHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void onUpdateGameMode(GameMode gameMode) {

    }
}
