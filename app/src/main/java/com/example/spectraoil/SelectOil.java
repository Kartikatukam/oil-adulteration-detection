package com.example.spectraoil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SelectOil extends AppCompatActivity {

    CardView cardSunflower, cardGroundnut, cardSafflower, cardSesame;
    RadioButton rbSunflower, rbGroundnut, rbSafflower, rbSesame;
    Button btnNext;

    String selectedOil = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_oil);

        cardSunflower = findViewById(R.id.cardSunflower);
        cardGroundnut = findViewById(R.id.cardGroundnut);
        cardSafflower = findViewById(R.id.cardSafflower);
        cardSesame = findViewById(R.id.cardSesame);

        rbSunflower = findViewById(R.id.rbSunflower);
        rbGroundnut = findViewById(R.id.rbGroundnut);
        rbSafflower = findViewById(R.id.rbSafflower);
        rbSesame = findViewById(R.id.rbSesame);

        btnNext = findViewById(R.id.btnNext);

        // Card click
        cardSunflower.setOnClickListener(v -> selectOil(1));
        cardGroundnut.setOnClickListener(v -> selectOil(2));
        cardSafflower.setOnClickListener(v -> selectOil(3));
        cardSesame.setOnClickListener(v -> selectOil(4));

        // Radio click
        rbSunflower.setOnClickListener(v -> selectOil(1));
        rbGroundnut.setOnClickListener(v -> selectOil(2));
        rbSafflower.setOnClickListener(v -> selectOil(3));
        rbSesame.setOnClickListener(v -> selectOil(4));

        // Next button
        btnNext.setOnClickListener(v -> {

            if (selectedOil.equals("")) {

                Toast.makeText(SelectOil.this,
                        "Please select oil type",
                        Toast.LENGTH_SHORT).show();

            } else {

                Intent intent = new Intent(SelectOil.this, ScanInstruction.class);
                intent.putExtra("oilType", selectedOil);
                startActivity(intent);

            }

        });
    }

    private void selectOil(int oil) {

        // reset radio buttons
        rbSunflower.setChecked(false);
        rbGroundnut.setChecked(false);
        rbSafflower.setChecked(false);
        rbSesame.setChecked(false);

        // reset card colors
        cardSunflower.setCardBackgroundColor(Color.WHITE);
        cardGroundnut.setCardBackgroundColor(Color.WHITE);
        cardSafflower.setCardBackgroundColor(Color.WHITE);
        cardSesame.setCardBackgroundColor(Color.WHITE);

        if (oil == 1) {
            rbSunflower.setChecked(true);
            cardSunflower.setCardBackgroundColor(Color.parseColor("#C8E6C9"));
            selectedOil = "Sunflower Oil";
        }

        if (oil == 2) {
            rbGroundnut.setChecked(true);
            cardGroundnut.setCardBackgroundColor(Color.parseColor("#C8E6C9"));
            selectedOil = "Groundnut Oil";
        }

        if (oil == 3) {
            rbSafflower.setChecked(true);
            cardSafflower.setCardBackgroundColor(Color.parseColor("#C8E6C9"));
            selectedOil = "Safflower Oil";
        }

        if (oil == 4) {
            rbSesame.setChecked(true);
            cardSesame.setCardBackgroundColor(Color.parseColor("#C8E6C9"));
            selectedOil = "Sesame Oil";
        }


        btnNext.setEnabled(true);
    }
}