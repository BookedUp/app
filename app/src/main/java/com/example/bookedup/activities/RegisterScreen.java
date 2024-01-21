package com.example.bookedup.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.bookedup.R;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.User;
import com.example.bookedup.model.enums.Role;

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

public class RegisterScreen extends AppCompatActivity {

    private EditText emailAddress, firstName, lastName, country, city, streetAndNumber, mobile;
    private Button continueRegisterButton, selectImageBtn, removeImageBtn;
    private TextView signInTextView;
    private Switch registerSwitch;
    private ImageView profilePictureView;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        emailAddress = findViewById(R.id.register_email_address);
        firstName = findViewById(R.id.register_first_name);
        lastName = findViewById(R.id.register_last_name);
        country = findViewById(R.id.register_country);
        city = findViewById(R.id.register_city);
        streetAndNumber = findViewById(R.id.register_street_and_number);
        mobile = findViewById(R.id.register_phone);
        registerSwitch = findViewById(R.id.register_switch);
        profilePictureView = findViewById(R.id.register_logoImageView);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        removeImageBtn = findViewById(R.id.removeImageBtn);

        continueRegisterButton = findViewById(R.id.continue_register);
        signInTextView = findViewById(R.id.register_signInTextView); // Dodali smo TextView za "Don't have an account? Register"

        continueRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginScreen();
            }
        });

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        removeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfileImage();
            }
        });
    }

    private void openLoginScreen() {
        Intent logInIntent = new Intent(this, LoginScreen.class);
        startActivity(logInIntent);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private void deleteProfileImage() {
        profilePictureView.setImageResource(R.drawable.usx);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == this.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                Glide.with(this)
                        .load(selectedImageUri)
                        .transform(new CenterCrop(), new GranularRoundedCorners(8, 8, 8, 8))
                        .into(profilePictureView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File bitmapToFile(Bitmap bitmap, String filename) {
        try {

            File file = new File(this.getCacheDir(), filename);
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

    private void uploadImage(){
        BitmapDrawable drawable = (BitmapDrawable) profilePictureView.getDrawable();
        Bitmap profilePictureBitmap = drawable.getBitmap();

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
                    Log.d("RegisterScreen", "Photo NEWNEW NEW " + photo);

                    Intent confirmPasswordIntent = new Intent(RegisterScreen.this, ConfirmPasswordScreen.class);
                    confirmPasswordIntent.putExtra("emailAddress", emailAddress.getText().toString());
                    confirmPasswordIntent.putExtra("firstName", firstName.getText().toString());
                    confirmPasswordIntent.putExtra("lastName", lastName.getText().toString());
                    confirmPasswordIntent.putExtra("country", country.getText().toString());
                    confirmPasswordIntent.putExtra("city", city.getText().toString());
                    confirmPasswordIntent.putExtra("streetAndNumber", streetAndNumber.getText().toString());
                    confirmPasswordIntent.putExtra("mobile", mobile.getText().toString());
                    confirmPasswordIntent.putExtra("photo", photo);
                    if(registerSwitch.isChecked()){
                        confirmPasswordIntent.putExtra("role", "Host");
                    } else {
                        confirmPasswordIntent.putExtra("role", "Guest");
                    }

                    startActivity(confirmPasswordIntent);

                } else {
                    Log.d("RegisterScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("RegisterScreen", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("RegisterScreen", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }


}