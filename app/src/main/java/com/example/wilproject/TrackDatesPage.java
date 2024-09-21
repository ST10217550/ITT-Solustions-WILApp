package com.example.wilproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TrackDatesPage extends AppCompatActivity {

    private CalendarView calendarView;
    private DatabaseReference databaseRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_dates_page);

        calendarView = findViewById(R.id.calendarView);

        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference("appointments").child(userId);

        // Fetch appointments from Firebase and display them
        fetchAppointments();

        // Set a listener when the user selects a date
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);


        if (appointments.containsKey(selectedDate)) {
            String appointmentTime = appointments.get(selectedDate);
            Toast.makeText(TrackDatesPage.this, "Appointment at " + appointmentTime, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(TrackDatesPage.this, "No appointment on this date", Toast.LENGTH_SHORT).show();
        }
    });

   }
    private Map<String, String> appointments = new HashMap<>();

    private void fetchAppointments() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointments.clear();  // Clear old data
                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                    String date = appointmentSnapshot.child("date").getValue(String.class);
                    String time = appointmentSnapshot.child("time").getValue(String.class);
                    if (date != null && time != null) {
                        appointments.put(date, time);
                    }
                }
                Toast.makeText(TrackDatesPage.this, "Appointments loaded", Toast.LENGTH_SHORT).show();
            }
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TrackDatesPage.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }
}