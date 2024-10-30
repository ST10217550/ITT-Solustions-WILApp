package com.example.wilproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TrackDatesPage extends AppCompatActivity {

    private CalendarView calendarView;
    private FirebaseAuth auth;
    private DatabaseReference databaseRef;
    private Calendar calendar;
    private ArrayList<String> appointmentDates = new ArrayList<>(); // List to store all appointment dates
    private TextView appointmentsTextView;
    private Map<String, String> appointmentDetailsMap = new HashMap<>();
    private String userId;

    private ImageButton home;
    private ImageButton profile;
    private ImageButton getDate;
    private ImageButton reschedule;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_dates_page);

        calendarView = findViewById(R.id.calendarViewTrack);
        appointmentsTextView = findViewById(R.id.appoiments);
        home = findViewById(R.id.button_home);
        profile = findViewById(R.id.button_profile);
        getDate = findViewById(R.id.button_track);
        reschedule = findViewById(R.id.button_reschedule);


        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            databaseRef = FirebaseDatabase.getInstance().getReference("appointments").child(userId);
            checkForAppointment();
        } else {
            appointmentsTextView.setText("User not logged in.");
        }

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackDatesPage.this, HomePage.class);
                startActivity(intent);

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackDatesPage.this, ProfilePage.class);
                startActivity(intent);
            }
        });
        getDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackDatesPage.this, GetDatePage.class);
                startActivity(intent);
            }
        });

        reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackDatesPage.this, ReschedulePage.class);
                startActivity(intent);
            }
        });

    }

    private void checkForAppointment() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String selectedDate = dataSnapshot.child("selectedDate").getValue(String.class);
                    String time = dataSnapshot.child("time").getValue(String.class);
                    String illness = dataSnapshot.child("illness").getValue(String.class);
                    String status = dataSnapshot.child("status").getValue(String.class);

                    String appointmentDetails = "Illness: " + illness + "\n" +
                            "Date: " + selectedDate + "\n" +
                            "Time: " + time + "\n" +
                            "Status: " + status;

                    appointmentsTextView.setText(appointmentDetails);

                    // Parse selectedDate to set it on the CalendarView
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    try {
                        Date date = dateFormat.parse(selectedDate);
                        if (date != null) {
                            calendarView.setDate(date.getTime(), true, true); // Move to appointment date
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(TrackDatesPage.this, "Error parsing appointment date.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    appointmentsTextView.setText("No appointment found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TrackDatesPage.this, "Error retrieving appointment.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}