package com.example.spectraoil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Arrays;

public class Scanning extends AppCompatActivity {


    CircularProgressIndicator progressIndicator;
    MaterialButton cancelButton;

    Handler handler = new Handler();

    int progressValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        progressIndicator = findViewById(R.id.circularProgress);
        cancelButton = findViewById(R.id.cancelButton);

        // INIT MODEL
        OilPredictionModel.init(this);

        startScanning();

        cancelButton.setOnClickListener(v -> finish());
    }

    private void startScanning() {

        Toast.makeText(this, "Scanning Oil Sample...", Toast.LENGTH_SHORT).show();

        // 🔥 START SENSOR READING
        BLEManager.getInstance().startReading();

        handler.postDelayed(scanRunnable, 100);
    }

    Runnable scanRunnable = new Runnable() {

        @Override
        public void run() {

            progressValue += 2;
            progressIndicator.setProgress(progressValue);

            if (progressValue < 100) {
                handler.postDelayed(this, 100);
            } else {
                processSensorData();
            }
        }
    };

    private void processSensorData() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                float[] sensorData = readSensorData();

                if (sensorData == null) {
                    Log.d("BLE_DATA", "Waiting for data...");
                    handler.postDelayed(this, 500);
                    return;
                }

                // 🔥 LOG INPUT
                Log.d("MODEL_INPUT", Arrays.toString(sensorData));



                Log.d("MODEL", "Running prediction...");

                float purity = OilPredictionModel.predict(sensorData);

                Log.d("MODEL", "Output: " + purity);

                if (purity < 0) {
                    showError();
                    return;
                }

                Intent intent = new Intent(Scanning.this, Result.class);
                intent.putExtra("purity", purity);

                startActivity(intent);
                finish();
            }
        }, 500);
    }

    private float[] readSensorData() {

        try {

            String received = BLEManager.latestData;

            Log.d("BLE_DATA", "Received: " + received);

            if (received == null) return null;

            if (!received.startsWith("#")) {
                Log.d("BLE_DATA", "Invalid format");
                return null;
            }

            String[] parts = received.split(",");

            Log.d("BLE_DATA", "Parts length: " + parts.length);

            if (parts.length < 12) return null;

            float[] data = new float[12];

            for (int i = 1; i < 13; i++) {
                data[i - 1] = Float.parseFloat(parts[i]);
            }

            Log.d("BLE_DATA", "Parsed successfully");

            return data;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void showError() {

        progressIndicator.setIndicatorColor(
                getResources().getColor(android.R.color.holo_red_dark)
        );

        Toast.makeText(this, "Sensor error. Try again.", Toast.LENGTH_LONG).show();
    }


}
