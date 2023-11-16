package com.example.bookedup.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bookedup.R;

public class ConfirmPasswordScreen extends AppCompatActivity {

    private EditText password;
    private EditText confirmPassword;
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password_screen);

        // Inicijalizacija UI elemenata
        registerButton = findViewById(R.id.register);

        // Postavljanje klika na dugme za potvrdu unosa
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ovde dodajte kod za proveru unetih podataka i eventualni prelazak na HomeScreen
                // Primer: Kada su podaci ispravni, prelazi se na HomeScreen
                Intent homeIntent = new Intent(ConfirmPasswordScreen.this, GuestMainScreen.class);
                startActivity(homeIntent);
            }
        });
    }
}