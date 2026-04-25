package com.example.spectraoil;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ===== BLUETOOTH INIT =====
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        requestPermissions();

        // ===== UI =====
        ConstraintLayout btnLoginEmail = findViewById(R.id.btn_login_email);
        ConstraintLayout btnLoginGoogle = findViewById(R.id.btn_login_google);
        ConstraintLayout btnInfo = findViewById(R.id.btn_info);
        TextView btnGuest = findViewById(R.id.btn_guest);

        btnLoginEmail.setOnClickListener(v -> {
            Toast.makeText(this, "Login...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, Login.class));
        });

        btnLoginGoogle.setOnClickListener(v ->
                Toast.makeText(this, "Login With Google", Toast.LENGTH_SHORT).show()
        );

        btnInfo.setOnClickListener(v ->
                Toast.makeText(this, "Info", Toast.LENGTH_SHORT).show()
        );

        btnGuest.setOnClickListener(v -> {

            // ✅ Check Bluetooth before entering Home
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!bluetoothAdapter.isEnabled()) {
                Toast.makeText(this, "Please turn ON Bluetooth", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Guest mode", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, Home.class));
        });
    }

    // ===== PERMISSIONS =====
    private void requestPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT
                        },
                        100);
            }
        }
    }
}