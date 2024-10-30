package com.example.wilproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
    private String userId;
    private String IDNumb;
    private Spinner illnesses;

    private ImageButton home;
    private ImageButton profile;
    private ImageButton track;
    private ImageButton getDate;

    private static final String CHANNEL_ID = "appointment_channel";


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
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
        home = findViewById(R.id.button_home);
        profile = findViewById(R.id.button_profile);
        track = findViewById(R.id.button_track);
        getDate = findViewById(R.id.button_reschedule);

        calander = calander.getInstance();

        selectedYear = calander.get(Calendar.YEAR);
        selectedMonth = calander.get(Calendar.MONTH) + 1;
        selectedDay = calander.get(Calendar.DAY_OF_MONTH);

        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid(); // Get the currently logged-in user's ID
        databaseRef = FirebaseDatabase.getInstance().getReference("appointments").child(userId);


        fetchAppointmentDetails();


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
            rescheduleAppointment();
            // Proceed to update details and update the appointment
            updateDetails();
            updateCalendarView();
             // Call the method to update the appointment in Firebase
            createNotificationChannel();
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReschedulePage.this, HomePage.class);
                startActivity(intent);

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReschedulePage.this, ProfilePage.class);
                startActivity(intent);
            }
        });
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReschedulePage.this, TrackDatesPage.class);
                startActivity(intent);
            }
        });

        getDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReschedulePage.this, ReschedulePage.class);
                startActivity(intent);
            }
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
                // Ensure hour is between 15 (3 PM) and 17 (5 PM)
                if (hourOfDay < 15 || hourOfDay > 17) {
                    Toast.makeText(ReschedulePage.this, "Please select a time between 3 PM and 5 PM.", Toast.LENGTH_SHORT).show();
                    return; // Do not update the time if it's out of bounds
                }
                selectedhrs = hourOfDay;
                selectedmin = (minute / 30) * 30; // Round to nearest 30 minutes
                timeBtn.setText(String.format("%02d:%02d", selectedhrs, selectedmin));
            }
        };

        calander = calander.getInstance();
        int hrs = 15; // Default to 3 PM
        int min = 0; // Default to 00 minutes

        int style = AlertDialog.THEME_HOLO_DARK;
        ReschTime = new TimePickerDialog(this, style, timeSetListener, hrs, min, true);
    }



    private void fetchAppointmentDetails() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    IDNumb = dataSnapshot.child("IDNumb").getValue(String.class);
                    String selectedDate = dataSnapshot.child("selectedDate").getValue(String.class);
                    String time = dataSnapshot.child("time").getValue(String.class);
                    String illness = dataSnapshot.child("illness").getValue(String.class);
                    String details = dataSnapshot.child("details").getValue(String.class);

                    // Update the UI with the fetched details
                    preAppointment.setText("Date: " + selectedDate + ", Time: " + time + ", Details: " + details);
                } else {
                    Toast.makeText(ReschedulePage.this, "No appointment found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReschedulePage.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Method to update the appointment in Firebase
    private void rescheduleAppointment() {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Error: User ID is not available.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Set up the calendar with the selected hour and minute
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, selectedhrs);
        calendar.set(Calendar.MINUTE, selectedmin);

        // Format the selected date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        String selectedDate = dateFormat.format(calendar.getTime());
        String selectedTime = timeFormat.format(calendar.getTime());

        // Prepare updated data with only the required fields
        HashMap<String, Object> updatedData = new HashMap<>();
        updatedData.put("selectedDate", selectedDate); // Updated date
        updatedData.put("time", selectedTime); // Updated time
        updatedData.put("details", "Rescheduled Appointment: " + new SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm a", Locale.US).format(calendar.getTime()));

        // Update only the selected fields in the Firebase database
        databaseRef.child("appointments").child(userId) // Path under appointments/userId
                .updateChildren(updatedData) // Use updateChildren for partial updates
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ReschedulePage.this, "Appointment rescheduled successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ReschedulePage.this, "Failed to reschedule appointment.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @SuppressLint({"ScheduleExactAlarm", "MissingPermission"})
    private void sendRescheduleNotification(String date, String time) {
        // Intent to open the ReschedulePage when the notification is tapped
        Intent intent = new Intent(this, ReschedulePage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Appointment Rescheduled")
                .setContentText("Your appointment has been rescheduled to " + date + " at " + time)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Appointment Channel";
            String description = "Channel for appointment notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}