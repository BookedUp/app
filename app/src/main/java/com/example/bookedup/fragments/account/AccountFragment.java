package com.example.bookedup.fragments.account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.bookedup.R;
import com.example.bookedup.activities.GuestMainScreen;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.activities.SplashScreen;
import com.example.bookedup.adapters.PopularAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.home.HomeFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Address;
import com.example.bookedup.model.User;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountFragment extends Fragment {

    ImageView profilePic;
    EditText firstNameInput;
    EditText phoneInput;
    EditText addressInput;
    EditText lastNameInput;
    EditText passwordInput;
    Button updateProfileBtn;
    Button deleteProfileBtn;
    ProgressBar progressBar;

    public AccountFragment() {}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_account, container, false);

        profilePic = view.findViewById(R.id.profile_image_view);

        String imageUrl = LoginScreen.loggedUser.getProfilePicture().getUrl();
        Glide.with(requireContext()).load(imageUrl)
                .transform(new CenterCrop(), new GranularRoundedCorners(40, 40, 40, 40))
                .into(profilePic);

        firstNameInput = view.findViewById(R.id.profile_first_name);
        firstNameInput.setText(LoginScreen.loggedUser.getFirstName());

        lastNameInput = view.findViewById(R.id.profile_last_name);
        lastNameInput.setText(LoginScreen.loggedUser.getLastName());

        phoneInput = view.findViewById(R.id.profile_phone);
        phoneInput.setText(String.valueOf(LoginScreen.loggedUser.getPhone()));

        addressInput = view.findViewById(R.id.profile_address);
        addressInput.setText(LoginScreen.loggedUser.getAddress().getCountry() + " " + LoginScreen.loggedUser.getAddress().getCity());

        passwordInput = view.findViewById(R.id.profile_password);
        passwordInput.setText(LoginScreen.loggedUser.getPassword());

        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());

        ImageView eyeImageView = view.findViewById(R.id.pass_visibility);
        eyeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        deleteProfileBtn = view.findViewById(R.id.profile_delete_btn);

        progressBar = view.findViewById(R.id.profile_progress_bar);

        String[] address = addressInput.getText().toString().split(" ");
        String country = Character.toUpperCase(address[0].charAt(0)) + address[0].substring(1);
        String city = Character.toUpperCase(address[1].charAt(0)) + address[1].substring(1);

        updateProfileBtn.setOnClickListener((v -> {
            Address oldAddress = LoginScreen.loggedUser.getAddress();
            Address newAddress = new Address(oldAddress.getId(), country, city, oldAddress.getPostalCode(), oldAddress.getStreetAndNumber(), oldAddress.isActive(), oldAddress.getLatitude(), oldAddress.getLongitude());
            User newUser = new User(LoginScreen.loggedUser.getId(), firstNameInput.getText().toString(), lastNameInput.getText().toString(), newAddress, Integer.parseInt(phoneInput.getText().toString()), LoginScreen.loggedUser.getEmail(), passwordInput.getText().toString(), LoginScreen.loggedUser.isBlocked(), LoginScreen.loggedUser.isVerified(), LoginScreen.loggedUser.isActive(), LoginScreen.loggedUser.getProfilePicture(), LoginScreen.loggedUser.getRole());
            Call<User> updatedUser = ClientUtils.userService.updateUser(newUser, LoginScreen.loggedUser.getId());
            updatedUser.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.d("AccountFragment", "Successful response: " + response.body());
                            User newUser = response.body();
                            Toast.makeText(getActivity(),"Successful changes",Toast.LENGTH_SHORT).show();
                            Log.d("AccountFragment", "New User " + newUser.toString());
                        } else {
                            Log.d("AccountFragment", "Response body is null");
                        }
                    }  else {
                        // Log error details
                        Log.d("AccountFragment", "Unsuccessful response: " + response.code());
                        try {
                            Log.d("AccountFragment", "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d("AccountFragment", t.getMessage() != null?t.getMessage():"error");
                }
            });
        }));


        deleteProfileBtn.setOnClickListener(v -> {
            // Create and show the dialog
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Profile")
                    .setMessage("Are you sure you want to delete your profile?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // User clicked "Yes"
                        // Perform the deletion operation here

                        Call<User> deletedUser = ClientUtils.userService.deleteUser(LoginScreen.loggedUser.getId());
                        deletedUser.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Deletion successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), SplashScreen.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Log.d("AccountFragment", "Unsuccessful response: " + response.code());
                                    try {
                                        Log.d("AccountFragment", "Error Body: " + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Log.d("AccountFragment", t.getMessage() != null ? t.getMessage() : "error");
                            }
                        });
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        // User clicked "No"
                        // Do nothing or handle accordingly
                    })
                    .show();
        });

//
//        logoutBtn.setOnClickListener((v)->{
//            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        FirebaseUtil.logout();
//                        Intent intent = new Intent(getContext(),SplashActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                    }
//                }
//            });
//
//
//
//        });
//
//        profilePic.setOnClickListener((v)->{
//            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
//                    .createIntent(new Function1<Intent, Unit>() {
//                        @Override
//                        public Unit invoke(Intent intent) {
//                            imagePickLauncher.launch(intent);
//                            return null;
//                        }
//                    });
//        });

        return view;
    }


    private void togglePasswordVisibility() {
        EditText passwordEditText = requireView().findViewById(R.id.profile_password);

        if (passwordEditText.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            passwordEditText.setTransformationMethod(null);
        } else {
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }


}