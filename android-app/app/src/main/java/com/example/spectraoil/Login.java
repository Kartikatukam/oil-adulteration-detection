package com.example.spectraoil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class Login extends AppCompatActivity {

    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);

        MaterialButton btnLogin = findViewById(R.id.loginBtn);

        btnLogin.setOnClickListener(v -> {

            String userName = username.getText().toString().trim();
            String passWord = password.getText().toString().trim();

            // ✅ Empty check
            if (userName.isEmpty() || passWord.isEmpty()) {
                Toast.makeText(this, "Enter username & password", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Simple login check
            if (!userName.equals("ajaygawande") || !passWord.equals("Mahi@123")) {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Login.this, Home.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);
                finish(); // optional: prevents going back to login
            }
        });
    }
}