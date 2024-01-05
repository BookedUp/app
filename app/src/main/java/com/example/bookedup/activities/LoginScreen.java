package com.example.bookedup.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookedup.R;

public class LoginScreen extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button loginButton;
    private TextView registerTextView;

    private TextView forgotPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Inicijalizacija UI elemenata
        loginButton = findViewById(R.id.login_loginButton);
        registerTextView = findViewById(R.id.login_registerTextView);
        forgotPasswordTextView = findViewById(R.id.login_forgotPasswordTextView);

        // Initialize username and password EditText views
        username = findViewById(R.id.login_username); // replace with the actual ID from your layout
        password = findViewById(R.id.login_password); // replace with the actual ID from your layout

        // Postavljanje klika na dugme za potvrdu unosa
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredUsername = username.getText().toString().trim();
                String enteredPassword = password.getText().toString().trim();

                // Check the credentials and open the appropriate activity
                if (enteredUsername.equals("jovan.jovanovic@example.com") && enteredPassword.equals("jovanpass")) {
                    Intent adminIntent = new Intent(LoginScreen.this, AdministratorMainScreen.class);
                    startActivity(adminIntent);
                } else if (enteredUsername.equals("ana.anic@example.com") && enteredPassword.equals("anapass")) {
                    Intent hostIntent = new Intent(LoginScreen.this, HostMainScreen.class);
                    startActivity(hostIntent);
                } else if (enteredUsername.equals("jovana.jovanovic@example.com") && enteredPassword.equals("jovanapass")) {
                    Intent guestIntent = new Intent(LoginScreen.this, GuestMainScreen.class);
                    startActivity(guestIntent);
                } else {
                    // If credentials don't match, show a Toast
                    Toast.makeText(LoginScreen.this, "Bad credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Postavljanje klika na TextView za registraciju
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prelazak na RegisterScreenActivity
                openRegisterScreen();
            }
        });

        // Postavljanje klika na TextView za registraciju
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prelazak na RegisterScreenActivity
                openForgotPasswordScreen();
            }
        });
    }

    // Dodajte ovu funkciju za otvaranje ekrana za registraciju
    private void openRegisterScreen() {
        Intent registerIntent = new Intent(this, RegisterScreen.class);
        startActivity(registerIntent);
    }

    private void openForgotPasswordScreen() {
        Intent forgotPasswordIntent = new Intent(this, ForgotPasswordScreen.class);
        startActivity(forgotPasswordIntent);
    }

    // Ostatak vašeg koda...
}
