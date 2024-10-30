package com.example.wilproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ProfilePage extends AppCompatActivity {


    private TextView nameTxt, surnameTxt, idNumTxt, emailTxt;
    private EditText phoneNumb, address, city, state, zipCode, allergies, nextName, nextRelation, nextphoneNumb, nextEmail;
    private Button saveDataBtn, homeBtn, dateOfBirth;
    private RadioGroup genderGroup;
    private Spinner illnessesSpinner;

    private DatabaseReference userRef;
    private DatabaseReference userProfileRef;
    private String IDNumb;

    private FirebaseAuth auth;
    private Calendar selectedDate;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        nameTxt = findViewById(R.id.nameTxt);
        surnameTxt = findViewById(R.id.surnameTxt);
        idNumTxt = findViewById(R.id.idNumtxt);
        emailTxt = findViewById(R.id.emailTxt);
        phoneNumb = findViewById(R.id.phoneNumb);
        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        zipCode = findViewById(R.id.zipCode);
        allergies = findViewById(R.id.allergies);
        nextName = findViewById(R.id.nextName);
        nextRelation = findViewById(R.id.nextRelation);
        nextphoneNumb = findViewById(R.id.nextphoneNumb);
        nextEmail = findViewById(R.id.nextEmail);
        genderGroup = findViewById(R.id.gender);
        illnessesSpinner = findViewById(R.id.illnesses);
        saveDataBtn = findViewById(R.id.saveData);
        homeBtn = findViewById(R.id.homeBtn);
        dateOfBirth = findViewById(R.id.DateBtn);

        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity or redirect to login page
            return;
        }

        // Set up Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = currentUser.getUid();
        userRef = database.getReference("users").child(userId);
        userProfileRef = database.getReference("userProfile").child(userId); // Reference to user's profile

        // Load the user's profile data
        loadUserProfileData();

        selectedDate = Calendar.getInstance();
        dateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        // Set up save button listener to save updated user data
        saveDataBtn.setOnClickListener(v -> saveUserProfileData());

        // Home button to navigate to the home page
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePage.this, HomePage.class);
                startActivity(intent);
            }
        });
    }

    private void showDatePickerDialog() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            selectedDate.set(year1, monthOfYear, dayOfMonth);
            String dob = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
            dateOfBirth.setText(dob); // Set the selected date on button text
        }, year, month, day);

        datePickerDialog.show();
    }


    private void loadUserProfileData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user information from Firebase
                    String idNumber = dataSnapshot.child("IDNumb").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String surname = dataSnapshot.child("surname").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    //String profileDetails = "ID number: " + idNumber + "\nName: " + name
                            //+ "\nSurname: " + surname + "\nEmail: " + email;
                    // Set retrieved values in TextViews
                    nameTxt.setText(name);
                    surnameTxt.setText(surname);
                    idNumTxt.setText(idNumber);
                    emailTxt.setText(email);
                } else {
                    Toast.makeText(ProfilePage.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
                Toast.makeText(ProfilePage.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to save user's updated profile data
    private void saveUserProfileData() {
        // Collect data from input fields
        String phone = phoneNumb.getText().toString();
        String userAddress = address.getText().toString();
        String userCity = city.getText().toString();
        String userState = state.getText().toString();
        String zip = zipCode.getText().toString();
        String allergyInfo = allergies.getText().toString();
        String kinName = nextName.getText().toString();
        String kinRelation = nextRelation.getText().toString();
        String kinPhone = nextphoneNumb.getText().toString();
        String kinEmail = nextEmail.getText().toString();
        String dob = dateOfBirth.getText().toString();



        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedGenderId);
        String gender = selectedGender.getText().toString();

        String illness = illnessesSpinner.getSelectedItem().toString();

        // Save data to Firebase under the user's IDNumb path
        userProfileRef.child("dateOfBirth").setValue(dob);
        userProfileRef.child("phone").setValue(phone);
        userProfileRef.child("address").setValue(userAddress);
        userProfileRef.child("city").setValue(userCity);
        userProfileRef.child("state").setValue(userState);
        userProfileRef.child("zipCode").setValue(zip);
        userProfileRef.child("allergies").setValue(allergyInfo);
        userProfileRef.child("kinName").setValue(kinName);
        userProfileRef.child("relationship").setValue(kinRelation);
        userProfileRef.child("kinPhone").setValue(kinPhone);
        userProfileRef.child("kinEmail").setValue(kinEmail);
        userProfileRef.child("gender").setValue(gender);
        userProfileRef.child("illnesses").setValue(illness);

        Toast.makeText(ProfilePage.this, "Profile data saved", Toast.LENGTH_SHORT).show();
    }
}