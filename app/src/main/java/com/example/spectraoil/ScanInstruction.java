package com.example.spectraoil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScanInstruction extends AppCompatActivity {

    TextView oilTypeText;
    Button startScanButton;

    String oilType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_instruction);

        startScanButton = findViewById(R.id.startScanButton);

        // Receive oil type from previous activity
        oilType = getIntent().getStringExtra("oilType");



        // Start scanning when button clicked
        startScanButton.setOnClickListener(v -> {

            Intent intent = new Intent(ScanInstruction.this, Scanning.class);
            intent.putExtra("oilType", oilType);
            startActivity(intent);

        });
    }
}