package com.example.wilproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_dates_page);

        calendarView = findViewById(R.id.calendarViewTrack);
        appointmentsTextView = findViewById(R.id.appoiments); // Reference to the TextView

        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid(); // Get the currently logged-in user's ID
        databaseRef = FirebaseDatabase.getInstance().getReference("appointments").child(userId);

        // Fetch all appointment dates from Firebase
        fetchAppointmentDates();

    }

    private void fetchAppointmentDates() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    appointmentDates.clear(); // Clear the list before adding new dates
                    StringBuilder appointmentsBuilder = new StringBuilder(); // To store appointment details for TextView

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String date = snapshot.child("date").getValue(String.class);
                        String time = snapshot.child("time").getValue(String.class);

                        if (date != null && time != null) {
                            appointmentDates.add(date);

                            // Add appointment details to the StringBuilder
                            appointmentsBuilder.append("Appointment on: ").append(date).append(" at ").append(time).append("\n");
                        }
                    }

                    // Update the TextView with the appointment details
                    appointmentsTextView.setText(appointmentsBuilder.toString());

                    // Highlight the fetched dates on the CalendarView
                    highlightAppointmentDates();
                } else {
                    Toast.makeText(TrackDatesPage.this, "No appointments found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TrackDatesPage.this, "Failed to fetch appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void highlightAppointmentDates() {
        // Iterate through all appointment dates and set them on the CalendarView
        calendarView.setDate(calendar.getInstance().getTimeInMillis()); // Start with today's date

        for (String appointmentDate : appointmentDates) {
            try {
                // Convert the appointment date (in String format) to milliseconds
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Calendar appointmentCalendar = calendar.getInstance();
                appointmentCalendar.setTime(dateFormat.parse(appointmentDate)); // Parse the date

                // Highlight or mark the date on the CalendarView
                calendarView.setDate(appointmentCalendar.getTimeInMillis(), true, true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}