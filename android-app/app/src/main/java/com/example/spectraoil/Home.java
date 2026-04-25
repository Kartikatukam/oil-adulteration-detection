package com.example.spectraoil;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

public class Home extends AppCompatActivity {

    TextView connectTxt, deviceTxt;
    ConstraintLayout connectBtn, btnStartTest;

    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        connectTxt = findViewById(R.id.connectTxt);
        deviceTxt = findViewById(R.id.deviceTxt);

        connectBtn = findViewById(R.id.connectBtn);
        btnStartTest = findViewById(R.id.btnStartTest);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        connectBtn.setOnClickListener(v -> checkPermissions());

        btnStartTest.setOnClickListener(v ->
                startActivity(new Intent(Home.this, SelectOil.class)));
    }

    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            bluetoothPermissionLauncher.launch(new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
            });

        } else {
            connectDevice();
        }
    }

    ActivityResultLauncher<String[]> bluetoothPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> connectDevice());

    private void connectDevice() {

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Toast.makeText(this, "Scanning for ESP32...", Toast.LENGTH_SHORT).show();

        // ✅ THIS IS THE FIX
        BLEManager.getInstance().startScan(this);
    }
}