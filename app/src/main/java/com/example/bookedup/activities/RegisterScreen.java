package com.example.bookedup.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bookedup.R;

public class RegisterScreen extends AppCompatActivity {

    private EditText emailAddress;
    private EditText firstName;
    private EditText lastName;

    private EditText address;

    private EditText mobile;
    private Button continueRegisterButton;
    private TextView signInTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        // Inicijalizacija UI elemenata
        continueRegisterButton = findViewById(R.id.continue_register);
        signInTextView = findViewById(R.id.register_signInTextView); // Dodali smo TextView za "Don't have an account? Register"

        // Postavljanje klika na dugme za potvrdu unosa
        continueRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ovde dodajte kod za proveru unetih podataka i eventualni prelazak na HomeScreen
                // Primer: Kada su podaci ispravni, prelazi se na HomeScreen
                Intent confirmPasswordIntent = new Intent(RegisterScreen.this, ConfirmPasswordScreen.class);
                startActivity(confirmPasswordIntent);
            }
        });

        // Postavljanje klika na TextView za registraciju
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prelazak na RegisterScreenActivity
                openLoginScreen();
            }
        });
    }

    // Dodajte ovu funkciju za otvaranje ekrana za registraciju
    private void openLoginScreen() {
        Intent logInIntent = new Intent(this, LoginScreen.class);
        startActivity(logInIntent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Dodajte kod koji će se izvršiti kada aktivnost postane vidljiva korisniku
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Dodajte kod koji će se izvršiti kada se aktivnost restartuje
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Dodajte kod koji će se izvršiti kada aktivnost ponovno postane interaktivna
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Dodajte kod koji će se izvršiti kada aktivnost izgubi fokus
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Dodajte kod koji će se izvršiti kada aktivnost više nije vidljiva korisniku
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dodajte kod koji će se izvršiti kada aktivnost bude uništena
    }
}