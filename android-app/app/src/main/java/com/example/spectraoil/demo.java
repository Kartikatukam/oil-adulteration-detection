package com.example.spectraoil;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class demo extends AppCompatActivity {

    TextView userName, passWord;
    TextView connectTxt, deviceTxt, batteryText;
    View batteryFill, batteryContainer;
    ConstraintLayout connectBtn;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;
    InputStream inputStream;
    Thread receiveThread;

    ConstraintLayout btnStartTest;

    private final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // =============================
    // HISTORY VARIABLES
    // =============================

    RecyclerView historyRecycler;
    ArrayList<HistoryItem> historyList;
    HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // =============================
        // Initialize Views
        // =============================


        connectTxt = findViewById(R.id.connectTxt);
        deviceTxt = findViewById(R.id.deviceTxt);
        batteryText = findViewById(R.id.batteryText);
        batteryFill = findViewById(R.id.batteryFill);
        batteryContainer = findViewById(R.id.batteryContainer);
        connectBtn = findViewById(R.id.connectBtn);

        btnStartTest = findViewById(R.id.btnStartTest);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // =============================
        // HISTORY SETUP
        // =============================

        historyRecycler = findViewById(R.id.historyRecycler);

        historyList = new ArrayList<>();

        historyAdapter = new HistoryAdapter(this, historyList);

        historyRecycler.setLayoutManager(new LinearLayoutManager(this));
        historyRecycler.setAdapter(historyAdapter);




        // =============================
        // RECEIVE SCAN RESULT
        // =============================

        Intent intent = getIntent();

        String oilType = intent.getStringExtra("oilType");
        String status = intent.getStringExtra("status");
        int icon = intent.getIntExtra("icon", 0);

        if (oilType != null) {

            historyList.add(
                    new HistoryItem(oilType, status, icon)
            );

            historyAdapter.notifyDataSetChanged();
        }

        // =============================
        // DEFAULT UI
        // =============================

        connectTxt.setText("Connect");
        deviceTxt.setText("Device");
        batteryText.setText("Battery: 0%");

        batteryContainer.post(() -> updateBatteryUI(0));

        // =============================
        // BUTTON ACTIONS
        // =============================

        connectBtn.setOnClickListener(v -> checkPermissions());

        btnStartTest.setOnClickListener(v -> {
            startActivity(new Intent(demo.this, SelectOil.class));
        });
    }

    // =============================
    // PERMISSION CHECK
    // =============================

    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            bluetoothPermissionLauncher.launch(new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
            });

        } else {

            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    ActivityResultLauncher<String[]> bluetoothPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {

                        Boolean scanGranted = result.getOrDefault(
                                Manifest.permission.BLUETOOTH_SCAN, false);
                        Boolean connectGranted = result.getOrDefault(
                                Manifest.permission.BLUETOOTH_CONNECT, false);

                        if (scanGranted && connectGranted) {
                            enableBluetooth();
                        } else {
                            Toast.makeText(this, "Bluetooth Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    });

    ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {

                        Boolean fine = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarse = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION, false);

                        if (fine || coarse) {
                            enableBluetooth();
                        } else {
                            Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    });

    // =============================
    // ENABLE BLUETOOTH
    // =============================

    private void enableBluetooth() {

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth Not Supported", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableBtIntent);
        } else {
            connectDevice();
        }
    }

    ActivityResultLauncher<Intent> enableBluetoothLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (bluetoothAdapter.isEnabled()) {
                            connectDevice();
                        } else {
                            Toast.makeText(this, "Bluetooth is OFF", Toast.LENGTH_SHORT).show();
                        }
                    });

    // =============================
    // CONNECT TO ESP32
    // =============================

    private void connectDevice() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Bluetooth Connect Permission Required", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : pairedDevices) {

            if (device.getName().equals("ESP32_BATTERY")) {

                try {

                    bluetoothAdapter.cancelDiscovery();

                    socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    socket.connect();

                    inputStream = socket.getInputStream();

                    connectTxt.setText("Connected");
                    deviceTxt.setText("SpectraOil");

                    startReceivingData();

                    Toast.makeText(this, "Connected to ESP32", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }

        Toast.makeText(this, "ESP32 Not Paired", Toast.LENGTH_SHORT).show();
    }

    // =============================
    // RECEIVE BATTERY DATA
    // =============================

    private void startReceivingData() {

        receiveThread = new Thread(() -> {

            byte[] buffer = new byte[1024];
            int bytes;

            StringBuilder dataBuffer = new StringBuilder();

            while (true) {
                try {

                    bytes = inputStream.read(buffer);

                    String received = new String(buffer, 0, bytes);

                    dataBuffer.append(received);

                    if (received.contains("\n")) {

                        String[] values = dataBuffer.toString().split("\n");

                        for (String value : values) {

                            value = value.trim();

                            if (!value.isEmpty()) {

                                int percent = Integer.parseInt(value);

                                runOnUiThread(() -> updateBatteryUI(percent));
                            }
                        }

                        dataBuffer.setLength(0);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        });

        receiveThread.start();
    }

    // =============================
    // UPDATE BATTERY UI
    // =============================

    private void updateBatteryUI(int percent) {

        batteryText.setText("Battery: " + percent + "%");

        int containerWidth = batteryContainer.getWidth();
        int usableWidth = containerWidth - 10;

        int fillWidth = (usableWidth * percent) / 100;

        ViewGroup.LayoutParams params = batteryFill.getLayoutParams();
        params.width = fillWidth;
        batteryFill.setLayoutParams(params);

        if (percent <= 20) {
            batteryFill.setBackgroundColor(Color.RED);
        } else if (percent <= 50) {
            batteryFill.setBackgroundColor(Color.YELLOW);
        } else {
            batteryFill.setBackgroundColor(Color.GREEN);
        }
    }
}