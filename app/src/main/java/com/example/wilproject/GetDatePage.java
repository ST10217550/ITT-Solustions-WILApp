package com.example.wilproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


public class GetDatePage extends AppCompatActivity {

    private Spinner illnesses;
    private CalendarView calendarView;
    private Button time;
    private Button schedule;
    private Calendar calendar;
    private int daysToAdd = 30;
    private int selectedHour = 12;
    private int selectedMin = 0;
    private String status = "pending";
    private TextView showDetails;
    private HashMap<String, String> appointments = new HashMap<>();

    private DatabaseReference databaseRef;
    private FirebaseAuth auth;

    private String IDNumb;

    private ImageButton home;
    private ImageButton profile;
    private ImageButton track;
    private ImageButton reschedule;


    private static final String CHANNEL_ID = "appointment_channel";

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_date_page);
        FirebaseApp.initializeApp(this);

        illnesses = findViewById(R.id.illnesses);
        calendarView = findViewById(R.id.calendarViewTrack);
        time = findViewById(R.id.Timebtn);
        schedule = findViewById(R.id.submitBtn);
        showDetails = findViewById(R.id.showAppoimentDetails);
        schedule = findViewById(R.id.submitBtn);
        home = findViewById(R.id.button_home);
        profile = findViewById(R.id.button_profile);
        track = findViewById(R.id.button_track);
        reschedule = findViewById(R.id.button_reschedule);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();  // Firebase UID
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Retrieve IDNumb from the user's profile
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Assuming IDNumb is stored under the "IDNumb" field
                        IDNumb = dataSnapshot.child("IDNumb").getValue(String.class);

                        if (IDNumb != null) {
                            Log.d("GetDatePage", "Retrieved IDNumb: " + IDNumb);
                        } else {
                            Log.e("GetDatePage", "IDNumb not found for the user.");
                            Toast.makeText(GetDatePage.this, "Failed to retrieve user ID.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("GetDatePage", "User data does not exist.");
                        Toast.makeText(GetDatePage.this, "User profile not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("GetDatePage", "Database error: " + databaseError.getMessage());
                    Toast.makeText(GetDatePage.this, "Database error occurred.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("GetDatePage", "User not logged in.");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }


        calendar = Calendar.getInstance();

        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid(); // Get the currently logged-in user's ID
        databaseRef = FirebaseDatabase.getInstance().getReference("appointments").child(userId); // Appointments for the specific user


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        illnesses.setAdapter(adapter);

        illnesses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                switch (selectedOption) {
                    case "HIV/AIDS":
                        daysToAdd = 25;
                        break;
                    case "Hypertension":
                        daysToAdd = 40;
                        break;
                    case "Heart Disease":
                        daysToAdd = 30;
                        break;
                    case "Diabetes":
                        daysToAdd = 30;
                        break;
                    case "Epilepsy":
                        daysToAdd = 21;
                        break;
                    case "Arthitis":
                        daysToAdd = 60;
                        break;
                    case "TB":
                        daysToAdd = 60;
                        break;
                    case "More Than 1":
                        daysToAdd = 28;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default to 30 days
            }
        });

        time.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(GetDatePage.this,
                    (TimePicker view, int hourOfDay, int minute) -> {
                        // Check if the selected time is between 7 AM and 3 PM
                        if (hourOfDay < 7 || hourOfDay >= 15) {
                            Toast.makeText(GetDatePage.this, "You can only schedule appointments between 7 AM and 3 PM. Please select a valid time.", Toast.LENGTH_SHORT).show();
                            return; // Prevent scheduling outside of the allowed time range
                        }
                        selectedHour = hourOfDay;
                        selectedMin = (minute / 30) * 30; // Round to the nearest 30 minutes
                        showDetails.setText(String.format("Selected Time: %02d:%02d", selectedHour, selectedMin));
                    }, selectedHour, selectedMin, true);
            timePickerDialog.show();
        });

        schedule.setOnClickListener(v -> {
            scheduleAppointment(daysToAdd);
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetDatePage.this, HomePage.class);
                startActivity(intent);

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetDatePage.this, ProfilePage.class);
                startActivity(intent);
            }
        });
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetDatePage.this, TrackDatesPage.class);
                startActivity(intent);
            }
        });

        reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetDatePage.this, ReschedulePage.class);
                startActivity(intent);
            }
        });


    }

    private void storeAppointmentId(String appointmentId) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppointmentPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("appointmentId", appointmentId);
        editor.apply();
    }

    private void scheduleAppointment(int daysToAdd) {
        // Get the currently logged-in user's ID
        String userId = auth.getCurrentUser().getUid();

        if (userId == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return; // Exit if user is not logged in
        }

        // Initialize calendar to today's date and clear time to midnight
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Log.d("Debug", "Initial date (midnight): " + calendar.getTime().toString());

        // Add the specified days to set the appointment date
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
        Log.d("Debug", "Date after adding days: " + calendar.getTime().toString());

        // Adjust the date if it falls on a weekend
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 2); // Move to Monday
        } else if (dayOfWeek == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to Monday
        }

        Log.d("Debug", "Date after weekend adjustment: " + calendar.getTime().toString());

        // Set the selected time
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMin);

        Log.d("Debug", "Final date with time: " + calendar.getTime().toString());

        // Format the selected date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        String selectedDate = dateFormat.format(calendar.getTime());
        String selectedTime = timeFormat.format(calendar.getTime());

        Log.d("Debug", "Formatted date: " + selectedDate + " and time: " + selectedTime);

        // Get the selected illness
        String selectedIllness = illnesses.getSelectedItem().toString();

        // Prepare data to be stored in Firebase
        HashMap<String, String> appointmentData = new HashMap<>();
        appointmentData.put("IDNumb", userId);
        appointmentData.put("selectedDate", selectedDate);
        appointmentData.put("time", selectedTime);
        appointmentData.put("illness", selectedIllness);
        appointmentData.put("status", status);
        appointmentData.put("details", "Scheduled Appointment: " + new SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm a", Locale.US).format(calendar.getTime()));

        // Save appointment data under the user's ID in Firebase
        databaseRef.setValue(appointmentData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Appointment saved successfully for user: " + userId);
                        sendImmediateNotification(selectedDate, selectedTime);  // Notify immediately
                        scheduleNotification(userId);
                        Toast.makeText(GetDatePage.this, "Appointment saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(GetDatePage.this, "Failed to save appointment.", Toast.LENGTH_SHORT).show();
                    }
                });

        // Update CalendarView to show the scheduled date
        calendarView.setDate(calendar.getTimeInMillis(), true, true);

        // Display the scheduled appointment in TextView
        String appointmentDetails = "Scheduled Appointment: " + new SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm a", Locale.US).format(calendar.getTime());
        showDetails.setText(appointmentDetails);

        Toast.makeText(this, "Appointment scheduled on " + calendar.getTime().toString(), Toast.LENGTH_SHORT).show();
    }


    // Immediate notification to confirm appointment scheduling
    private void sendImmediateNotification(String date, String time) {
        Log.d("GetDatePage", "Sending immediate notification...");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Appointment Scheduled")
                .setContentText("Your appointment is set for " + date + " at " + time + ".")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(2, builder.build());  // Unique notification ID for immediate notification
    }

    // Schedule notification 3 days before the appointment
    private void scheduleNotification(String userId) {
        databaseRef = FirebaseDatabase.getInstance().getReference("appointments").child(userId);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String date = snapshot.child("date").getValue(String.class);
                String time = snapshot.child("time").getValue(String.class);

                if (date != null && time != null) {
                    calendar = parseDateTime(date, time);
                    calendar.add(Calendar.DAY_OF_MONTH, -3);  // Set for 3 days before the appointment

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(GetDatePage.this, NotificationReceiver.class);
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(GetDatePage.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    if (alarmManager != null) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        Toast.makeText(GetDatePage.this, "Notification scheduled!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GetDatePage.this, "Failed to schedule notification", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Calendar parseDateTime(String date, String time) {
        Calendar cal = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date dateTime = sdf.parse(date + " " + time);
            if (dateTime != null) {
                cal.setTime(dateTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cal;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Appointment Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for appointment reminders");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}



