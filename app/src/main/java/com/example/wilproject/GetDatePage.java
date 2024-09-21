package com.example.wilproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private TextView showDetails;
    private HashMap<String, String> appointments = new HashMap<>();

    private DatabaseReference databaseRef;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_date_page);

        illnesses = findViewById(R.id.illnesses);
        calendarView = findViewById(R.id.calendarView);
        time = findViewById(R.id.Timebtn);
        schedule = findViewById(R.id.submitBtn);
        showDetails = findViewById(R.id.showAppoimentDetails);
        schedule = findViewById(R.id.submitBtn);



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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default to 30 days
            }
        });

        time.setOnClickListener(v -> {
            // Open TimePickerDialog with 30-minute intervals
            TimePickerDialog timePickerDialog = new TimePickerDialog(GetDatePage.this,
                    (TimePicker view, int hourOfDay, int minute) -> {
                        selectedHour = hourOfDay;
                        selectedMin = (minute / 30) * 30; // Round to nearest 30 minutes
                        showDetails.setText(String.format("Selected Time: %02d:%02d", selectedHour, selectedMin));
                    }, selectedHour, selectedMin, true);
            timePickerDialog.show();
        });

        schedule.setOnClickListener(v -> scheduleAppointment(daysToAdd));
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

        String appointmentId = databaseRef.push().getKey();

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
            // Log the appointment ID for debugging purposes
            Log.d("Appointment ID", "Generated ID: " + appointmentId);

            // Store appointment details in Firebase
            HashMap<String, String> appointmentData = new HashMap<>();
            appointmentData.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime()));
            appointmentData.put("time", new SimpleDateFormat("HH:mm", Locale.US).format(calendar.getTime()));
            appointmentData.put("details", "Scheduled Appointment: " + new SimpleDateFormat("EEE, MMM dd, yyyy 'at' hh:mm a", Locale.US).format(calendar.getTime()));

            databaseRef.child(appointmentId).setValue(appointmentData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Appointment saved successfully with ID: " + appointmentId);
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

}



