package com.example.wilproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilePage extends AppCompatActivity {
    FirebaseAuth auth;
    DatabaseReference usersRef;
    FirebaseUser user;

    Button btnEdit, btnSave, btnLogOut;
    EditText editFirstName, regSurname, regIDnum, eTextEmail, addres1, addres2, fileNum;
    Spinner illness;
    RadioButton femaleBtn, maleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize UI components
        editFirstName = findViewById(R.id.editFirstName);
        regSurname = findViewById(R.id.regSurname);
        regIDnum = findViewById(R.id.regIDnum);
        eTextEmail = findViewById(R.id.eTextEmail);
        addres1 = findViewById(R.id.addres1);
        addres2 = findViewById(R.id.addres2);
        fileNum = findViewById(R.id.fileNum);
        illness = findViewById(R.id.illness);
        femaleBtn = findViewById(R.id.femaleBtn);
        maleBtn = findViewById(R.id.maleBtn);
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnLogOut = findViewById(R.id.btnLogOut);

        // Set up Spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.type,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        illness.setAdapter(adapter);

        // Load user profile data
        loadUserProfile();

        // Edit button click handler
        btnEdit.setOnClickListener(v -> {
            editFirstName.setEnabled(true);
            regSurname.setEnabled(true);
            regIDnum.setEnabled(true);
            eTextEmail.setEnabled(true);
            addres1.setEnabled(true);
            addres2.setEnabled(true);
            fileNum.setEnabled(true);
            btnSave.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
        });

        // Save button click handler
        btnSave.setOnClickListener(v -> {
            saveUserProfile();
        });

        // Log Out button click handler
        btnLogOut.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserProfile() {
        String userId = user.getUid();
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserProfileC userProfile = dataSnapshot.getValue(UserProfileC.class);
                    if (userProfile != null) {
                        editFirstName.setText(userProfile.firstName);
                        regSurname.setText(userProfile.lastName);
                        regIDnum.setText(userProfile.idNumber);
                        eTextEmail.setText(userProfile.email);
                        addres1.setText(userProfile.address1);
                        addres2.setText(userProfile.address2);
                        fileNum.setText(userProfile.fileNumber);

                        // Set Spinner and RadioButton values
                        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) illness.getAdapter();
                        int illnessPosition = adapter.getPosition(userProfile.illness);
                        illness.setSelection(illnessPosition);

                        // Example for RadioButtons
                        if ("Female".equals(userProfile.gender)) {
                            femaleBtn.setChecked(true);
                        } else if ("Male".equals(userProfile.gender)) {
                            maleBtn.setChecked(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfilePage.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserProfile() {
        String firstName = editFirstName.getText().toString().trim();
        String surname = regSurname.getText().toString().trim();
        String idNumber = regIDnum.getText().toString().trim();
        String email = eTextEmail.getText().toString().trim();
        String address1 = addres1.getText().toString().trim();
        String address2 = addres2.getText().toString().trim();
        String fileNumber = fileNum.getText().toString().trim();
        String illnessText = illness.getSelectedItem().toString();
        String gender = femaleBtn.isChecked() ? "Female" : "Male";

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(surname) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        UserProfileC userProfile = new UserProfileC(firstName, surname, idNumber, email, address1, address2, fileNumber, illnessText, gender);
        usersRef.child(userId).setValue(userProfile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfilePage.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfilePage.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}