package com.example.wilproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_dates_page);

        calendarView = findViewById(R.id.calendarViewTrack);
        appointmentsTextView = findViewById(R.id.appoiments);

        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference("appointments").child(userId);

        // Fetch all appointment dates from Firebase
        fetchAppointmentDates();

        // Set up a listener to detect when the user selects a date
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            if (appointmentDetailsMap.containsKey(selectedDate)) {
                appointmentsTextView.setText("Appointment on " + selectedDate + ":\n" + appointmentDetailsMap.get(selectedDate));
            } else {
                appointmentsTextView.setText("No appointments on " + selectedDate);
            }
        });

    }

    private void fetchAppointmentDates() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TrackDatesPage", "DataSnapshot contents: " + dataSnapshot.toString());

                if (dataSnapshot.exists()) {
                    appointmentDates.clear();
                    appointmentDetailsMap.clear();
                    StringBuilder appointmentsBuilder = new StringBuilder();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Debugging to see what keys and values are under each snapshot
                        Log.d("TrackDatesPage", "Snapshot Key: " + snapshot.getKey());
                        Log.d("TrackDatesPage", "Snapshot Value: " + snapshot.getValue());

                        String date = snapshot.child("selectedDate").getValue(String.class);
                        String time = snapshot.child("time").getValue(String.class);

                        if (date != null && time != null) {
                            appointmentDates.add(date);
                            String details = "Time: " + time;
                            appointmentDetailsMap.put(date, details);
                            appointmentsBuilder.append("Appointment on: ").append(date).append(" at ").append(time).append("\n");
                        } else {
                            Log.d("TrackDatesPage", "Date or time is missing for this appointment.");
                        }
                    }

                    // Update the TextView with all the appointment details
                    if (appointmentsBuilder.length() > 0) {
                        appointmentsTextView.setText(appointmentsBuilder.toString());
                    } else {
                        appointmentsTextView.setText("No appointments found.");
                    }
                } else {
                    appointmentsTextView.setText("No appointments found.");
                    Log.d("TrackDatesPage", "No data found under the user ID.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TrackDatesPage.this, "Failed to fetch appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void highlightAppointmentDates() {
        // Highlight the dates on the CalendarView
        for (String appointmentDate : appointmentDates) {
            Log.d("TrackDatesPage", "Highlighting appointment date: " + appointmentDate);
            // Here you can implement logic to visually indicate these dates in your UI, if required
            // Note: CalendarView does not support highlighting multiple dates directly
        }
    }
}