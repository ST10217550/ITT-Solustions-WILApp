package com.example.wilproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    private Button LoginBtn1;
    private Button RegBtn1;
    private EditText username;
    private EditText passwordLog;
    private String IDNumb;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        LoginBtn1 =findViewById(R.id.Login);
        RegBtn1 = findViewById(R.id.LogRegister);
        username = findViewById(R.id.username);
        passwordLog = findViewById(R.id.LogPass);


        LoginBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();

                String usernameLog = username.getText().toString();
                String passwordText = passwordLog.getText().toString();

                if (TextUtils.isEmpty(usernameLog)) {
                    Toast.makeText(LoginPage.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(usernameLog).matches()) {
                    Toast.makeText(LoginPage.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(passwordText)) {
                    Toast.makeText(LoginPage.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(usernameLog, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    assert currentUser != null;
                                    String userId = currentUser.getUid();





                                    Toast.makeText(LoginPage.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginPage.this, HomePage.class);
                                    IDNumb = getIntent().getStringExtra("IDNumb");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginPage.this, "Username or Password is incorrect!.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        RegBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(intent);
            }
        });
    }
}