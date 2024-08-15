package com.example.wilproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginPage extends AppCompatActivity {

    private Button LoginBtn1;
    private Button RegBtn1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        LoginBtn1 =findViewById(R.id.Login);
        RegBtn1 = findViewById(R.id.LogRegister);

        LoginBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, HomePage.class);
                startActivity(intent);
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