package com.example.bookedup.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookedup.R;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.accommodations.AccommodationListFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Admin;
import com.example.bookedup.model.Guest;
import com.example.bookedup.model.Host;
import com.example.bookedup.model.JwtUtils;
import com.example.bookedup.model.LoginRequest;
import com.example.bookedup.model.Notification;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.Review;
import com.example.bookedup.model.Token;
import com.example.bookedup.model.User;
import com.example.bookedup.model.enums.Role;
import com.example.bookedup.services.AdminService;
import com.example.bookedup.services.UserService;
import com.example.bookedup.utils.SharedViewModel;

import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button loginButton;
    private TextView registerTextView;
    private TextView forgotPasswordTextView;
    public static User loggedUser;
    public static Guest loggedGuest;
    public static Host loggedHost;
    public static Admin loggedAdmin;
    private Long userId;
    private List<Accommodation> mostPopularAccommodations = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        loginButton = findViewById(R.id.login_loginButton);
        registerTextView = findViewById(R.id.login_registerTextView);
        forgotPasswordTextView = findViewById(R.id.login_forgotPasswordTextView);

        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);

        ImageView eyeImageView = findViewById(R.id.login_togglePassword);
        eyeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredUsername = username.getText().toString().trim();
                String enteredPassword = password.getText().toString().trim();
                Log.d("LoginScreen", "Entered Username: " + enteredUsername);
                Log.d("LoginScreen", "Entered Password: " + enteredPassword);

                LoginRequest loginRequest = new LoginRequest(enteredUsername, enteredPassword);
                Log.d("LoginScreen", "Token prvi: ");

                ClientUtils.userService.login(loginRequest).enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        Log.d("LoginScreen", "Token LOGIN: ");
                        if (response.isSuccessful()) {
                            Token authResponse = response.body();
                            Log.d("LoginScreen", "Token RESPONSE: " + authResponse.getToken());
                            ClientUtils.setAuthToken(authResponse.getToken());
                            try {
                                userId = handleLoginSuccess(authResponse.getToken());
                                if (userId != 0){
                                    openUserActivity(userId);
                                } else {
                                    Log.d("LoginScreen", "Los user");
                                }

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            Toast.makeText(LoginScreen.this,"The user with this data does not exist. Try again!",Toast.LENGTH_SHORT).show();
                            Log.d("LoginScreen", "Unsuccessful response: " + response.code());
                            try {
                                Log.d("LoginScreen", "Response je neuspesan" + response.errorBody().string());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
                        Log.e("LoginScreen", "Network error: " + t.getMessage());
                    }
                });
            }
        });
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterScreen();
            }
        });
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgotPasswordScreen();
            }
        });

    }

    private void togglePasswordVisibility() {
        Log.d("AccountFragment", "USAAAAAAAAAAAAAAAAO");
        EditText passwordEditText = findViewById(R.id.login_password);

        if (passwordEditText.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            passwordEditText.setTransformationMethod(null);
        } else {
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }


    private void openUserActivity(long userId){
        ClientUtils.userService.getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("LoginScreen", "Token GET USER: ");
                if (response.isSuccessful()) {
                    loggedUser = response.body();
//                    Log.d("LoginScreen", "User: " + loggedUser.toString());
//                    startPolling();
                    if (loggedUser.getRole().equals(Role.GUEST)){
                        findGuest(loggedUser);
                    } else if (loggedUser.getRole().equals(Role.HOST)){
                        findHost(loggedUser);
                    } else if (loggedUser.getRole().equals(Role.ADMIN)){
                        findAdmin(loggedUser);
                    }
                } else {
                    Log.d("LoginScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("LoginScreen", "Response je neuspesan" + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Handle network errors
            }
        });
    }
    private long handleLoginSuccess(String token) throws JSONException {
        if (token != null && !token.isEmpty()) {
            // Decode the JWT token
            Log.d("LoginScreen", "User token: " + token);
            if (ClientUtils.getAuthToken().equals(token)) {
                 return JwtUtils.getUserIdFromToken(token);
            }
        } else {
            Log.d("LoginScreen", "Token is empty or null ");
        }
        return 0;
    }

    private void openRegisterScreen() {
        Intent registerIntent = new Intent(this, RegisterScreen.class);
        startActivity(registerIntent);
    }

    private void openForgotPasswordScreen() {
        Intent forgotPasswordIntent = new Intent(this, ForgotPasswordScreen.class);
        startActivity(forgotPasswordIntent);
    }

//    private void setMostPopularAccommodations(Intent intent){
//        Call<ArrayList<Accommodation>> mostPopular = ClientUtils.accommodationService.getMostPopular();
//        mostPopular.enqueue(new Callback<ArrayList<Accommodation>>() {
//            @Override
//            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
//                if (response.isSuccessful()) {
//                    if (response.body() != null) {
//                        Log.d("HomeFragment", "Successful response: " + response.body());
//                        mostPopularAccommodations = response.body();
//                        getLoadPictures(mostPopularAccommodations, intent);
//                    } else {
//                        Log.d("HomeFragment", "Response body is null");
//                    }
//                }  else {
//                    // Log error details
//                    Log.d("HomeFragment", "Unsuccessful response: " + response.code());
//                    try {
//                        Log.d("HomeFragment", "Error Body: " + response.errorBody().string());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
//                Log.d("HomeFragment", t.getMessage() != null?t.getMessage():"error");
//            }
//        });
//    }

//    private void getLoadPictures(List<Accommodation> mostPopularAccommodations, Intent intent) {
//        Map<Long, List<Bitmap>> accommodationImageMap = new ConcurrentHashMap<>();
//        ExecutorService executorService = Executors.newFixedThreadPool(8);
//        AtomicInteger totalImagesToLoad = new AtomicInteger(0);
//
//        for (Accommodation accommodation : mostPopularAccommodations) {
//            List<Bitmap> photosBitmap = new ArrayList<>();
//            for(Photo photo : accommodation.getPhotos()) {
//                totalImagesToLoad.incrementAndGet();
//                executorService.execute(() -> {
//                    try {
//                        Call<ResponseBody> photoCall = ClientUtils.photoService.loadPhoto(photo.getId());
//                        Response<ResponseBody> response = photoCall.execute();
//                        try {
//
//                            if (response.isSuccessful()) {
//                                byte[] photoData = response.body().bytes();
//
//                                Bitmap bitmap = Glide.with(this)
//                                        .asBitmap()
//                                        .load(photoData)
//                                        .override(300, 300)
//                                        .submit()
//                                        .get();
//                                photosBitmap.add(bitmap);
//
//                                int remainingImages = totalImagesToLoad.decrementAndGet();
//
//                                if (remainingImages == 0) {
//                                    SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
//                                    sharedViewModel.setMostPopularAccommodations(mostPopularAccommodations);
//                                    sharedViewModel.setAccommodationImageMap(accommodationImageMap);
//                                    runOnUiThread(() -> {
//                                        if (!isFinishing()) {
//                                            startActivity(intent);
//                                        }
//                                    });
//
//                                }
//                            } else {
//                                Log.d("HomeFragment", "Error code " + response.code());
//                            }
//                        } catch (ExecutionException e) {
//                            throw new RuntimeException(e);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        } finally {
//                            response.body().close(); // Close the response body after using it
//                        }
//                    } catch (IOException e) {
//                        Log.e("HomeFragment", "Error reading response body: " + e.getMessage());
//                    }
//                });
//            }
//            accommodationImageMap.put(accommodation.getId(), photosBitmap);
//        }
//        executorService.shutdown();
//    }


    private void findGuest(User loggedUser){
        Log.d("LoginScreen", "USAO1 ");
        Log.d("LoginScreen", "id " + loggedUser.getId());

        ClientUtils.guestService.getGuest(loggedUser.getId()).enqueue(new Callback<Guest>() {
            @Override
            public void onResponse(Call<Guest> call, Response<Guest> response) {
                Log.d("LoginScreen", "USAO2 ");
                if (response.isSuccessful()) {
                    loggedGuest = response.body();
                    Log.d("LoginScreen", "User: " + loggedGuest.toString());
                    Intent guestIntent = new Intent(LoginScreen.this, GuestMainScreen.class);
//                    setMostPopularAccommodations(guestIntent);
                    startActivity(guestIntent);
                } else {
                    Log.d("LoginScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("LoginScreen", "Response je neuspesan" + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Guest> call, Throwable t) {
                // Handle network errors
            }
        });
    }

    private void findHost(User loggedUser){
        Log.d("LoginScreen", "USAO1 ");
        Log.d("LoginScreen", "id " + loggedUser.getId());

        ClientUtils.hostService.getHost(loggedUser.getId()).enqueue(new Callback<Host>() {
            @Override
            public void onResponse(Call<Host> call, Response<Host> response) {
                Log.d("LoginScreen", "USAO2 ");
                if (response.isSuccessful()) {
                    loggedHost = response.body();
                    Log.d("LoginScreen", "Host: " + loggedHost.toString());
                    Intent hostIntent = new Intent(LoginScreen.this, HostMainScreen.class);
//                    setMostPopularAccommodations(hostIntent);
                    startActivity(hostIntent);
                } else {
                    Log.d("LoginScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("LoginScreen", "Response je neuspesan" + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Host> call, Throwable t) {
                // Handle network errors
            }
        });

    }

    private void findAdmin(User loggedUser){
        Log.d("LoginScreen", "USAO1 ");
        Log.d("LoginScreen", "id " + loggedUser.getId());

        ClientUtils.adminService.getById(loggedUser.getId()).enqueue(new Callback<Admin>() {
            @Override
            public void onResponse(Call<Admin> call, Response<Admin> response) {
                Log.d("LoginScreen", "USAO2 ");
                if (response.isSuccessful()) {
                    loggedAdmin = response.body();
                    Log.d("LoginScreen", "Admin: " + loggedAdmin.toString());
                    Intent adminIntent = new Intent(LoginScreen.this, AdministratorMainScreen.class);
//                    setMostPopularAccommodations(adminIntent);
                    startActivity(adminIntent);
                } else {
                    Log.d("LoginScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("LoginScreen", "Response je neuspesan" + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Admin> call, Throwable t) {
                // Handle network errors
            }
        });

    }
}
