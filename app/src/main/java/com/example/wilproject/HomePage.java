package com.example.wilproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {

    private String IDNumb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        CardView profile = findViewById(R.id.profile);


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, ProfilePage.class);
                IDNumb = getIntent().getStringExtra("IDNumb");
                startActivity(intent);
            }
        });

        CardView getDate = findViewById(R.id.getDate);
        getDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, GetDatePage.class);
                //IDNumb = getIntent().getStringExtra("IDNumb");
                startActivity(intent);
            }
        });

        CardView reschedule = findViewById(R.id.reschedule);
        reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, ReschedulePage.class);
                //IDNumb = getIntent().getStringExtra("IDNumb");
                startActivity(intent);
            }
        });

        CardView track = findViewById(R.id.track);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, TrackDatesPage.class);
                //IDNumb = getIntent().getStringExtra("IDNumb");
                startActivity(intent);
            }
        });
    }
}