package com.example.spectraoil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Result extends AppCompatActivity {

    TextView headerTitle, statusText, purityPer, purityText;
    ImageView statusIcon;
    MaterialButton saveButton;

    String oilType;
    float adulteration;

    String statusMessage;
    String qualityMessage;
    int iconRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        headerTitle = findViewById(R.id.headerTitle);
        statusText = findViewById(R.id.statusText);
        purityPer = findViewById(R.id.purityPer);
        purityText = findViewById(R.id.purityText);
        statusIcon = findViewById(R.id.statusIcon);
        saveButton = findViewById(R.id.saveButton);

        oilType = getIntent().getStringExtra("oilType");
        adulteration = getIntent().getFloatExtra("purity", 0);

        if (oilType == null) oilType = "Unknown Oil";

        headerTitle.setText(oilType + " Analysis");

        // ✅ formatted adulteration
        purityPer.setText(String.format("%.2f%% Adulterated", adulteration));

        // ===== STATUS LOGIC =====
        if (0 <= adulteration && adulteration <= 5) {
            iconRes = R.drawable.ic_check_green;
            statusMessage = "SAFE TO CONSUME";
            qualityMessage = "Excellent Quality Oil";
        }
        else if (5 < adulteration && adulteration <= 30) {
            iconRes = R.drawable.ic_check_yellow;
            statusMessage = "CONSUME WITH CAUTION";
            qualityMessage = "Moderate Quality Oil";
        }
        else if (30 < adulteration && adulteration <= 60) {
            iconRes = R.drawable.ic_check_orange;
            statusMessage = "RISKY TO CONSUME";
            qualityMessage = "Poor Quality Oil";
        }
        else {
            iconRes = R.drawable.ic_cross;
            statusMessage = "VERY RISKY TO CONSUME";
            qualityMessage = "Very Poor Quality Oil";
        }

        statusIcon.setImageResource(iconRes);
        statusText.setText(statusMessage);
        purityText.setText(qualityMessage);

        // ===== SAVE TO HISTORY =====
        saveButton.setOnClickListener(v -> {

            Intent intent = new Intent(Result.this, Home.class);

            intent.putExtra("oilType", oilType);
            intent.putExtra("status", statusMessage);
            intent.putExtra("icon", iconRes);

            startActivity(intent);
            finish(); // ✅ important
        });
    }
}