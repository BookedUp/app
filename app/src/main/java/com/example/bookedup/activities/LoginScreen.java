package com.example.bookedup.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
        registerTextView = findViewById(R.id.login_registerTextView); // Dodali smo TextView za "Don't have an account? Register"
        forgotPasswordTextView = findViewById(R.id.login_forgotPasswordTextView); // Dodali smo TextView za "Don't have an account? Register"

        // Postavljanje klika na dugme za potvrdu unosa
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(LoginScreen.this, GuestMainScreen.class);
                startActivity(homeIntent);
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
