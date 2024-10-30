package com.example.wilproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterPage extends AppCompatActivity {

    private EditText regName;
    private EditText regSurname;
    private EditText emailReg;
    private EditText passwordReg;
    private EditText comfirmPass;
    private EditText idNum;
    private Button regBtn;
    private Button loginBtn;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference usersRef;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);


        regName = findViewById(R.id.regName);
        regSurname = findViewById(R.id.regSurname);
        emailReg = findViewById(R.id.emailReg);
        passwordReg = findViewById(R.id.passwordReg);
        idNum = findViewById(R.id.idNum);
        regBtn = findViewById(R.id.regBtn1);
        loginBtn = findViewById(R.id.LoginBtn1);
        comfirmPass = findViewById(R.id.confirmpasswordReg);


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                database = FirebaseDatabase.getInstance();
                usersRef = database.getReference("users");

                String IDNumb = idNum.getText().toString();
                String firstName = regName.getText().toString();
                String lastName = regSurname.getText().toString();
                String emailText = emailReg.getText().toString();
                String passwordText = passwordReg.getText().toString();


                if(TextUtils.isEmpty(IDNumb)){
                    Toast.makeText(RegisterPage.this, "Enter Id Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(IDNumb.length() < 10 ){
                    Toast.makeText(RegisterPage.this, "Invalid ID number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(firstName)){
                    Toast.makeText(RegisterPage.this, "Enter your Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(lastName)){
                    Toast.makeText(RegisterPage.this, "Enter your Surmane", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(emailText)) {
                    Toast.makeText(RegisterPage.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(passwordText)) {
                    Toast.makeText(RegisterPage.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (passwordText.length() < 6) {
                    Toast.makeText(RegisterPage.this, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();

                                // Create a HashMap to store user details
                                HashMap<String, Object> userData = new HashMap<>();
                                userData.put("IDNumb", IDNumb);
                                userData.put("name", firstName);
                                userData.put("surname", lastName);
                                userData.put("email", emailText);
                                userData.put("password", passwordText);

                                // Store user data in Firebase
                                usersRef.child(userId).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterPage.this, "Account created.",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterPage.this, LoginPage.class);
                                            intent.putExtra("IDNumb", IDNumb);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterPage.this, "Failed to save user data.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Authentication failed.";
                            Toast.makeText(RegisterPage.this, errorMessage,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }

        });
    }


}
