package com.example.bookedup.fragments.account;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
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
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.activities.SplashScreen;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.Address;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountFragment extends Fragment {

    private ImageView profilePic;
    private EditText firstNameInput;
    private EditText phoneInput;
    private EditText addressInput;
    private EditText lastNameInput;
    private EditText passwordInput;
    private Button updateProfileBtn, selectImageBtn, deleteImageBtn;
    private Button deleteProfileBtn;
    private ProgressBar progressBar;
    private Bitmap profilePicture;
    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean imageChanged = false;

    public AccountFragment(Bitmap profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_account, container, false);

        profilePic = view.findViewById(R.id.profile_image_view);
        profilePic.setImageBitmap(profilePicture);

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

        selectImageBtn = view.findViewById(R.id.select_image_button);
        deleteImageBtn = view.findViewById(R.id.delete_image_button);

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

            uploadImage();

        }));


        deleteProfileBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Profile")
                    .setMessage("Are you sure you want to delete your profile?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {

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
                    })
                    .show();
        });

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChanged = true;
                openGallery();
            }
        });

        deleteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfileImage();
            }
        });


        return view;
    }

    private void uploadImage() {
        String[] address = addressInput.getText().toString().split(" ");
        String country = Character.toUpperCase(address[0].charAt(0)) + address[0].substring(1);
        String city = Character.toUpperCase(address[1].charAt(0)) + address[1].substring(1);
        Address oldAddress = LoginScreen.loggedUser.getAddress();
        Address newAddress = new Address(oldAddress.getId(), country, city, oldAddress.getPostalCode(), oldAddress.getStreetAndNumber(), oldAddress.isActive(), oldAddress.getLatitude(), oldAddress.getLongitude());

        BitmapDrawable drawable = (BitmapDrawable) profilePic.getDrawable();
        Bitmap profilePictureBitmap = drawable.getBitmap();

            if (!isImageAlreadyUploaded(profilePictureBitmap)) {

                // Convert Bitmap to File
                String fileName = "us" + System.currentTimeMillis() +".jpg";
                File imageFile = bitmapToFile(profilePictureBitmap, fileName);

                // Convert File to MultipartBody.Part
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", fileName, requestFile);

                // Call the method to upload the image
                Call<ResponseBody> uploadCall = ClientUtils.photoService.uploadPhoto(imagePart);
                uploadCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Photo photo = new Photo("images/" + fileName, "Caption", true);
                            Log.d("AccountFragment", "Photo NEWNEW NEW " + photo);

                            User newUser = new User(LoginScreen.loggedUser.getId(), firstNameInput.getText().toString(), lastNameInput.getText().toString(), newAddress, Integer.parseInt(phoneInput.getText().toString()), LoginScreen.loggedUser.getEmail(), passwordInput.getText().toString(), LoginScreen.loggedUser.blocked(), LoginScreen.loggedUser.isVerified(), LoginScreen.loggedUser.isActive(), photo, LoginScreen.loggedUser.getRole());
                            updateUser(newUser);
                        } else {
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
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("AccountFragment", t.getMessage() != null ? t.getMessage() : "error");
                    }
                });
            } else {
                User newUser = new User(LoginScreen.loggedUser.getId(), firstNameInput.getText().toString(), lastNameInput.getText().toString(), newAddress, Integer.parseInt(phoneInput.getText().toString()), LoginScreen.loggedUser.getEmail(), passwordInput.getText().toString(), LoginScreen.loggedUser.blocked(), LoginScreen.loggedUser.isVerified(), LoginScreen.loggedUser.isActive(), LoginScreen.loggedUser.getProfilePicture(), LoginScreen.loggedUser.getRole());
                updateUser(newUser);
            }

    }

    private void updateUser(User newUser){
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
    }
    private boolean isImageAlreadyUploaded(Bitmap bitmap) {
        if (bitmap.sameAs(profilePicture)){
            return true;
        } else {
            return false;
        }
    }

    private File bitmapToFile(Bitmap bitmap, String filename) {
        try {

            File file = new File(requireContext().getCacheDir(), filename);
            file.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();

            // Write the bytes to the file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                Glide.with(requireContext())
                        .load(selectedImageUri)
                        .transform(new CenterCrop(), new GranularRoundedCorners(8, 8, 8, 8))
                        .into(profilePic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteProfileImage() {
        profilePic.setImageResource(R.drawable.usx);
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