package com.example.wilproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

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

    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;


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

        calander = calander.getInstance();

        selectedYear = calander.get(Calendar.YEAR);
        selectedMonth = calander.get(Calendar.MONTH) + 1;
        selectedDay = calander.get(Calendar.DAY_OF_MONTH);


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
}