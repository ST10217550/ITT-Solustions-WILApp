package com.example.wilproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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
import java.util.HashMap;
import java.util.Locale;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.navigation.NavigationBarView;


public class GetDatePage extends AppCompatActivity {

    private Spinner illnesses;
    private CalendarView calendarView;
    private Button time;
    private Button schedule;
    private Calendar calendar;
    private int daysToAdd = 30;
    private int selectedHour = 12;
    private int selectedMin = 0;
    private String status= "pending";
    private TextView showDetails;
    private HashMap<String, String> appointments = new HashMap<>();

    private DatabaseReference databaseRef;
    private FirebaseAuth auth;

    private String IDNumb;
    BottomNavigationView bottomNavigationView;

    private static final String CHANNEL_ID = "appointment_channel";

    @SuppressLint("MissingInflatedId")
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
        //bottomNavigationView = findViewById(R.id.bottomMenu);

        NavigationBarView bottomNavigationView = findViewById(R.id.bottomMenu);


        /*bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Toast.makeText(GetDatePage.this, "Home selected", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GetDatePage.this, HomePage.class));
                        return true;

                    case R.id.profile:
                        Toast.makeText(GetDatePage.this, "Profile selected", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GetDatePage.this, ProfilePage.class));
                        return true;

                    case R.id.trackDate:
                        Toast.makeText(GetDatePage.this, "Track selected", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GetDatePage.this, TrackDatesPage.class));
                        return true;

                    case R.id.reschedule:
                        Toast.makeText(GetDatePage.this, "Reschedule selected", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GetDatePage.this, ReschedulePage.class));
                        return true;

                    case R.id.getDate:
                        Toast.makeText(GetDatePage.this, "Schedule selected", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GetDatePage.this, GetDatePage.class));
                        return true;

                    default:
                        return false;
                }
            }
        });*/

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
                        daysToAdd =28;
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

    }

    private void storeAppointmentId(String appointmentId) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppointmentPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("appointmentId", appointmentId);
        editor.apply();
    }

    private void scheduleAppointment(int daysToAdd) {
        calendar = Calendar.getInstance(); // Reset to current date
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
        showDetails = findViewById(R.id.showAppoimentDetails);

        // Adjust the date if it falls on a weekend
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 2); // Move to Monday
        } else if (dayOfWeek == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to Monday
        }

        // Set the selected time
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMin);

        UserProfile userProfile = new UserProfile();

        String appointmentId = auth.getCurrentUser().getUid();

        // Format the selected date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        String selectedDate = dateFormat.format(calendar.getTime());
        String selectedTime = timeFormat.format(calendar.getTime());

        // Check if the selected time slot is already booked
        if (appointments.containsKey(selectedDate) && appointments.get(selectedDate).equals(selectedTime)) {
            Toast.makeText(this, "This time slot is already booked. Please select a different time.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (appointmentId != null) {

            storeAppointmentId(appointmentId);
            Log.d("Appointment ID", appointmentId);

            // Get the selected illness
            String selectedIllness = illnesses.getSelectedItem().toString();

            // Prepare data to be stored in Firebase
            HashMap<String, String> appointmentData = new HashMap<>();
            appointmentData.put("IDNumb", appointmentId); // Store IDNumb
            appointmentData.put("selectedDate", selectedDate); // Store the date
            appointmentData.put("time", selectedTime); // Store the time
            appointmentData.put("illness", selectedIllness); // Store the selected illness
            appointmentData.put("status", status);
            appointmentData.put("details", "Scheduled Appointment: " + new SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm a", Locale.US).format(calendar.getTime()));

            // Store appointment details in Firebase
            databaseRef.child(appointmentId).setValue(appointmentData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Appointment saved successfully with ID: " + appointmentId);
                            sendNotification("Appointment Scheduled", "Your appointment is scheduled for " + selectedDate + " at " + selectedTime);
                            scheduleNotificationAlarm(daysToAdd - 3, selectedDate, selectedTime);
                            Toast.makeText(GetDatePage.this, "Appointment saved successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GetDatePage.this, "Failed to save appointment.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Failed to generate appointment ID.", Toast.LENGTH_SHORT).show();
        }

        // Book the appointment
        appointments.put(selectedDate, selectedTime);

        // Update CalendarView to show the scheduled date
        calendarView.setDate(calendar.getTimeInMillis(), true, true);

        // Display the scheduled appointment in TextView
        String appointmentDetails = "Scheduled Appointment: " + new SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm a", Locale.US).format(calendar.getTime());
        showDetails.setText(appointmentDetails);

        Toast.makeText(this, "Appointment scheduled on " + calendar.getTime().toString(), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    private void createAppointmentNotification(String appointmentDetails) {
        Intent intent = new Intent(this, ReschedulePage.class); // Opens ReschedulePage when tapped
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "appointment_channel")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Appointment Reminder")
                .setContentText(appointmentDetails)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    @SuppressLint("MissingPermission")
    private void sendNotification(String title, String message) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_circle_notifications_24) // replace with your icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Appointment Notifications";
            String description = "Channel for appointment notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotificationAlarm(int daysBefore, String date, String time) {
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.add(Calendar.DAY_OF_MONTH, daysBefore);

        Context context = this; // or getContext() if inside a Fragment
        int requestCode = 0;

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("appointmentDetails", "Your appointment is in 3 days on " + date + " at " + time);
        // Modify the PendingIntent creation line
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE // or use FLAG_MUTABLE if necessary
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent1 = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent1); // This prompts the user to grant permission
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
            }
        }



    }


}



