package com.example.wilproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Spinner;

public class GetDatePage extends AppCompatActivity {

    Spinner illnesses;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_date_page);

        illnesses = findViewById(R.id.illnesses);
    }
}