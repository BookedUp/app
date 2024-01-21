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
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Address;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.Token;
import com.example.bookedup.model.User;
import com.example.bookedup.model.enums.Role;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmPasswordScreen extends AppCompatActivity {

    private EditText password, confirmPassword;
    private Button registerButton;
    private Address address;
    private String firstName, lastName, emailAddress, userPassword, userConfirmedPassword;
    private Role userRole;
    private Integer mobile;
    private Photo profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password_screen);

        password = findViewById(R.id.confirm_password_password);
        confirmPassword = findViewById(R.id.confirm_password_confirm_password);
        registerButton = findViewById(R.id.register);

        Intent intent = getIntent();
        if (intent != null) {
            emailAddress = intent.getStringExtra("emailAddress");
            firstName = intent.getStringExtra("firstName");
            lastName = intent.getStringExtra("lastName");
            String country = intent.getStringExtra("country");
            String city = intent.getStringExtra("city");
            String streetAndNumber = intent.getStringExtra("streetAndNumber");
            mobile = Integer.parseInt(intent.getStringExtra("mobile"));
            String role = intent.getStringExtra("role");
            profilePicture = (Photo) getIntent().getSerializableExtra("photo");

            address = new Address(country, city, null, streetAndNumber, true, 0, 0);
            if (role.equals("Host")){
                userRole = Role.HOST;
            } else if (role.equals("Guest")){
                userRole = Role.GUEST;
            }
        }



            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userPassword = password.getText().toString();
                    userConfirmedPassword = confirmPassword.getText().toString();
                    if (userPassword.equals(userConfirmedPassword)) {
                        Log.d("ConfirmPasswordScreen", "First Name " + firstName);
                        Log.d("ConfirmPasswordScreen", "Last Name " + lastName);
                        Log.d("ConfirmPasswordScreen", "Address " + address.toString());
                        Log.d("ConfirmPasswordScreen", "Mobile " + mobile);
                        Log.d("ConfirmPasswordScreen", "Password " + userConfirmedPassword);
                        Log.d("ConfirmPasswordScreen", "Role " + userRole);
                        Log.d("ConfirmPasswordScreen", "Profile picture " + profilePicture.toString());
                        User user = new User(firstName, lastName, address, mobile, emailAddress, userConfirmedPassword, false, false, true, profilePicture, userRole);

                        Call<Token> userToken = ClientUtils.userService.register(user);
                        userToken.enqueue(new Callback<Token>() {
                            @Override
                            public void onResponse(Call<Token> call, Response<Token> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Log.d("ConfirmPasswordScreen", "Successful response: " + response.body().getToken());
                                    Token newToken = response.body();
                                    ClientUtils.setAuthToken(newToken.getToken());
                                    Toast.makeText(ConfirmPasswordScreen.this, "You have successfully registered!", Toast.LENGTH_SHORT).show();
                                    Intent loginIntent = new Intent(ConfirmPasswordScreen.this, LoginScreen.class);
                                    startActivity(loginIntent);
                                } else {
                                    Log.d("ConfirmPasswordScreen", "Unsuccessful response: " + response.code());
                                    try {
                                        Log.d("ConfirmPasswordScreen", "Error Body: " + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Token> call, Throwable t) {
                                Log.d("ConfirmPasswordScreen", "EROOR " + t.getMessage() != null ? t.getMessage() : "error");
                            }
                        });
                    } else {
                        Toast.makeText(ConfirmPasswordScreen.this,"Password mismatches!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}