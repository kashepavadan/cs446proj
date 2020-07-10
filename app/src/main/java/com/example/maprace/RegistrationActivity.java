package com.example.maprace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.maprace.models.UserProfile;
import com.example.maprace.utils.StorageUtils;

public class RegistrationActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        submitButton = findViewById(R.id.submitButton);
        usernameEditText = findViewById(R.id.usernameEditText);

        submitButton.setEnabled(false);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) submitButton.setEnabled(false);
                else submitButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void handleSubmit(View view) {
        UserProfile profile = new UserProfile();
        profile.setUsername(usernameEditText.getText().toString());
        StorageUtils.saveUserProfile(getApplicationContext(), profile);

        finish();
    }
}
