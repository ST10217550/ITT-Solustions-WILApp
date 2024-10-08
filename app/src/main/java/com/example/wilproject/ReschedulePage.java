package com.example.wilproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class ReschedulePage extends AppCompatActivity {

    private DatePickerDialog ReschDate;
    private TimePickerDialog ReschTime;
    private Calendar calander;
    private CalendarView calendarView;
    private TextView newshowDetails;
    private Button dateBtn;
    private Button timeBtn;
    private Button reSchedule;
    private int selectedhrs;
    private int selectedmin;
    private TextView preAppointment;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private DatabaseReference databaseRef;
    private FirebaseAuth auth;
    private String appointmentId;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule_page);

        dateBtn = findViewById(R.id.DateBtn);
        timeBtn = findViewById(R.id.retimeBtn);
        newshowDetails = findViewById(R.id.reDetails);
        reSchedule = findViewById(R.id.rescheduleBtn);
        calendarView = findViewById(R.id.calendarView3);
        preAppointment = findViewById(R.id.preAppointDetails);

        calander = calander.getInstance();

        selectedYear = calander.get(Calendar.YEAR);
        selectedMonth = calander.get(Calendar.MONTH) + 1;
        selectedDay = calander.get(Calendar.DAY_OF_MONTH);

        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid(); // Get the currently logged-in user's ID
        databaseRef = FirebaseDatabase.getInstance().getReference("appointments").child(userId);

        appointmentId = getStoredAppointmentId();
        if (appointmentId == null) {
            Toast.makeText(this, "No appointment ID found!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
            return;
        }

        Log.d("ReschedulePage", "Appointment ID: " + appointmentId);
        fetchCurrentAppointment();

        initDatePicker();
        dateBtn.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(ReschedulePage.this,
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        // Set the selected year, month, and day
                        selectedYear = year;
                        selectedMonth = month + 1; // Add 1 to get the correct month
                        selectedDay = dayOfMonth;


                    }, selectedYear, selectedMonth - 1, selectedDay); // Subtract 1 for proper month indexing

            // Show the date picker dialog
            datePickerDialog.show();
        });


        initTimePicker();
        timeBtn.setOnClickListener(v ->{
            TimePickerDialog timePickerDialog = new TimePickerDialog(ReschedulePage.this,
                    (TimePicker view, int hourOfDay, int minute) -> {
                        selectedhrs = hourOfDay;
                        selectedmin = (minute / 30) * 30; // Round to nearest 30 minutes



                    }, selectedhrs, selectedmin, true);
            timePickerDialog.show();
        });


        reSchedule.setOnClickListener(v -> {
            updateDetails();
            updateCalendarView();
        });



    }

    private void updateDetails() {
        String formattedDate = String.format("Selected Date: %02d/%02d/%d", selectedDay, selectedMonth, selectedYear);
        String formattedTime = String.format("Selected Time: %02d:%02d", selectedhrs, selectedmin);

        // Update the TextView with both date and time
        newshowDetails.setText(formattedDate + "\n" + formattedTime);
    }

    private void updateCalendarView() {
        // Create a Calendar instance and set it to the selected date
        Calendar updatedCalendar = Calendar.getInstance();
        updatedCalendar.set(selectedYear, selectedMonth - 1, selectedDay); // Months are 0-indexed

        // Update the CalendarView to show the selected date
        calendarView.setDate(updatedCalendar.getTimeInMillis(), true, true);
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                dateBtn.setText(year +"/" + month + "/" + dayOfMonth);
            }
        };
        calander = calander.getInstance();
        int year1 = calander.get(calander.YEAR);
        int month1 = calander.get(calander.MONTH);
        int day1 = calander.get(calander.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;
        ReschDate = new DatePickerDialog(this,style,dateSetListener, year1,month1,day1);
        ReschDate.getDatePicker().setMinDate(calander.getTimeInMillis()+86400000);
    }



    private void initTimePicker(){
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeBtn.setText(hourOfDay + ":" + minute);
            }
        };
        calander = calander.getInstance();
        int hrs = calander.get(calander.HOUR);
        int min = calander.get(calander.MINUTE);

        int style = AlertDialog.THEME_HOLO_DARK;
        ReschTime = new TimePickerDialog(this, style, timeSetListener, hrs, min, true);

    }

    private String getStoredAppointmentId() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppointmentPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("appointmentId", null); // Default is null if not found
    }


    private void fetchCurrentAppointment() {
        databaseRef.child(appointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String date = snapshot.child("date").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);

                    // Parse the fetched date and time
                    String[] dateParts = date.split("-");
                    selectedYear = Integer.parseInt(dateParts[0]);
                    selectedMonth = Integer.parseInt(dateParts[1]) - 1; // Month is zero-based
                    selectedDay = Integer.parseInt(dateParts[2]);

                    String[] timeParts = time.split(":");
                    selectedhrs = Integer.parseInt(timeParts[0]);
                    selectedmin = Integer.parseInt(timeParts[1]);

                    // Update UI with current details

                    String formattedDate = String.format("Selected Date: %02d/%02d/%d", selectedDay, selectedMonth, selectedYear);
                    String formattedTime = String.format("Selected Time: %02d:%02d", selectedhrs, selectedmin);

                    // Update the TextView with both date and time
                    preAppointment.setText(formattedDate + "\n" + formattedTime);
                    updateCalendarView();
                } else {
                    Toast.makeText(ReschedulePage.this, "Appointment not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReschedulePage.this, "Failed to fetch appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAppointment() {
        String newDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
        String newTime = String.format("%02d:%02d", selectedhrs, selectedmin);

        // Update the appointment details in Firebase
        databaseRef.child(appointmentId).child("date").setValue(newDate);
        databaseRef.child(appointmentId).child("time").setValue(newTime)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ReschedulePage.this, "Appointment updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ReschedulePage.this, "Failed to update appointment.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}