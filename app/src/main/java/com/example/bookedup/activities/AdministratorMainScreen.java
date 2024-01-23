package com.example.bookedup.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.bookedup.R;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.about.AboutUsFragment;
import com.example.bookedup.fragments.accommodations.AccommodationRequestFragment;
import com.example.bookedup.fragments.account.AccountFragment;
//import com.example.bookedup.fragments.admin.user.AdminManageUsersFragment;
import com.example.bookedup.fragments.home.HomeFragment;
import com.example.bookedup.fragments.language.LanguageFragment;
import com.example.bookedup.fragments.reports.ReviewReportFragment;
import com.example.bookedup.fragments.reports.UserReportFragment;
import com.example.bookedup.fragments.reviews.ReviewRequestsFragment;
import com.example.bookedup.fragments.settings.SettingsFragment;
import com.example.bookedup.fragments.users.UsersActivityFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.Review;
import com.example.bookedup.model.ReviewReport;
import com.example.bookedup.model.User;
import com.example.bookedup.model.UserReport;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdministratorMainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    private List<Accommodation> allAccommodations = new ArrayList<Accommodation>();
    private ArrayList<User> reportedUsers = new ArrayList<>();
    private ArrayList<UserReport> userReports = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();
    private ProgressBar progressBar;
    private int count = 0;
    private Timer timer;

    private View darkBackground;
    private View darkBackgroundBottomAppBar;
    private List<Review> reportedReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_main_screen);


        progressBar = findViewById(R.id.loadingProgressBar);
        darkBackground = findViewById(R.id.darkBackground);
        darkBackgroundBottomAppBar = findViewById(R.id.darkBackgroundBottomAppBar);
        progressBar.setVisibility(View.INVISIBLE);
        bottomNavigationView=findViewById(R.id.bottomNavigationViewAdmin);
        drawerLayout=findViewById(R.id.drawer_layoutAdmin);
        NavigationView navigationView=findViewById(R.id.nav_viewAdmin);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar=findViewById(R.id.toolbarAdmin); //Ignore red line errors


        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView.setBackground(null);

        setAllAccommodations();
        setUserReports();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                int itemId=item.getItemId();
                if(itemId==R.id.nav_homeAdmin){
                    openFragment(new HomeFragment());
                    return true;
                }
                else if(itemId==R.id.nav_accommodation){
                    Log.d("AdministratorMainScreen", "all accommodations size " + allAccommodations.size());
                    getLoadPictures(allAccommodations);
                    return true;
                }
                else if(itemId==R.id.nav_users){
                    showBottomDialog();
                    return true;
                }
                else if(itemId==R.id.nav_comments){
                    showBottomDialogForComments();
                    return true;
                }
                return false;
            }
        });


        fragmentManager=getSupportFragmentManager();
        openFragment(new HomeFragment());
    }


    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheets);

        LinearLayout reportsContainer = dialog.findViewById(R.id.userReportsContainer);
        LinearLayout usersActivityContainer = dialog.findViewById(R.id.userActivityContainer);

        reportsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setReportedUsers();
                dialog.dismiss();

            }
        });

        usersActivityContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllUsers();
                dialog.dismiss();

            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void showBottomDialogForComments() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheets_comments);

        LinearLayout reportsContainer = dialog.findViewById(R.id.commentReportsContainer);
        LinearLayout newCommentsContainer = dialog.findViewById(R.id.newCommentContainer);

        reportsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllReportedReviews();
                dialog.dismiss();

            }
        });

        newCommentsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllReviews();
                dialog.dismiss();

            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void setAllReportedReviews(){
        Call<ArrayList<Review>> all = ClientUtils.reviewReportService.getAllReportedReviews();
        all.enqueue(new Callback<ArrayList<Review>>() {
            @Override
            public void onResponse(Call<ArrayList<Review>> call, Response<ArrayList<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AdministratorMainScreen", "Successful response: " + response.body());
                    reportedReviews = response.body();
                    Log.d("AdministratorMainScreen", "SIZEEEEEEEEEEE: " + reportedReviews.size());
                    loadProfilePicturesForComments(reportedReviews, "reported");
                } else {
                    // Log error details
                    Log.d("AdministratorMainScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("AdministratorMainScreen", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Review>> call, Throwable t) {
                Log.d("AdministratorMainScreen", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }


    private void loadProfilePicturesForComments(List<Review> reviews, String action){
        Log.d("AdministratorMainScreen", "ACTION "+ action);
        Map<Long, Bitmap> usersImageMap = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        AtomicInteger loadedImagesCount = new AtomicInteger(0);

        for (Review review : reviews) {
            executorService.execute(() -> {
                try {
                    Call<ResponseBody> photoCall = ClientUtils.photoService.loadPhoto(review.getGuest().getProfilePicture().getId());
                    Response<ResponseBody> response = photoCall.execute();

                    if (response.isSuccessful()) {
                        byte[] photoData = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
                        usersImageMap.put(review.getGuest().getId(), bitmap);

                        if (loadedImagesCount.incrementAndGet() == reviews.size()) {
                            this.runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                darkBackground.setVisibility(View.GONE);
                                darkBackgroundBottomAppBar.setVisibility(View.GONE);
                                if (action.equals("new")) {
                                    openFragment(new ReviewRequestsFragment(reviews, usersImageMap, R.id.frame_layoutAdmin));
                                }else if (action.equals("reported")){
                                    openFragment(new ReviewReportFragment(reportedReviews, R.id.frame_layoutAdmin, usersImageMap));
                                }
                            });
                        }
                    } else {
                        Log.d("AdministratorMainScreen", "Error code " + response.code());
                    }
                } catch (IOException e) {
                    Log.e("AdministratorMainScreen", "Error reading response body: " + e.getMessage());
                }
            });
        }
        executorService.shutdown();

    }

    private void setAllUsers(){
        Call<ArrayList<User>> allUsers = ClientUtils.userService.getUsers();
        allUsers.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AdministratorMainScreen", "Successful response: " + response.body());
                    users = response.body();

                    Map<Long, Integer> numberOfReports = calculateNumberOfUserReports();
                    Map<Long, List<String>> usersReportsReasons = getReportsReasonList();
                    openFragment(new UsersActivityFragment(users, R.id.frame_layoutAdmin, numberOfReports, usersReportsReasons));
                } else {
                    // Log error details
                    Log.d("AdministratorMainScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("AdministratorMainScreen", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                Log.d("AdministratorMainScreen", t.getMessage() != null ? t.getMessage() : "error");
            }
        });

    }

    private void setAllReviews(){
        initiateProgressBar();
        if (progressBar.getVisibility() == View.VISIBLE) {
            darkBackground.setVisibility(View.VISIBLE);
            darkBackgroundBottomAppBar.setVisibility(View.VISIBLE);
        }
        Call<ArrayList<Review>> allReviews = ClientUtils.reviewService.getUnapprovedReviews();
        allReviews.enqueue(new Callback<ArrayList<Review>>() {
            @Override
            public void onResponse(Call<ArrayList<Review>> call, Response<ArrayList<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AdministratorMainScreen", "Successful response: " + response.body());
                    reviews= response.body();
                    loadProfilePicturesForComments(reviews, "new");
                } else {
                    // Log error details
                    Log.d("AdministratorMainScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("AdministratorMainScreen", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Review>> call, Throwable t) {
                Log.d("AdministratorMainScreen", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void updateUser(){
        Call<User> updatedUser = ClientUtils.userService.getUser(LoginScreen.loggedAdmin.getId());
        updatedUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("GuestMainScreen", "Successful response: " + response.body());
                    User refreshedUser = response.body();
                    loadProfilePicture(refreshedUser);

                } else {
                    // Log error details
                    Log.d("GuestMainScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("GuestMainScreen", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("GuestMainScreen", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void initiateProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                darkBackground.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }
        });

        // Postavi brojač na 0
        count = 0;

        // Pokreni Timer za okretanje progres bara
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                count++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(count);
                    }
                });

                if (count == 100) {
                    timer.cancel();
                }
            }
        };
        timer.schedule(timerTask, 0, 100);
    }


    private void getLoadPictures(List<Accommodation> allAccommodations) {
        initiateProgressBar();
        if (progressBar.getVisibility() == View.VISIBLE) {
            darkBackground.setVisibility(View.VISIBLE);
            darkBackgroundBottomAppBar.setVisibility(View.VISIBLE);
        }
        Map<Long, List<Bitmap>> accommodationImageMap = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        AtomicInteger totalImagesToLoad = new AtomicInteger(0);

        for (Accommodation accommodation : allAccommodations) {
            List<Bitmap> photosBitmap = new ArrayList<>();
            for (Photo photo : accommodation.getPhotos()) {
                totalImagesToLoad.incrementAndGet();

                executorService.execute(() -> {
                    try {
                        Call<ResponseBody> photoCall = ClientUtils.photoService.loadPhoto(photo.getId());
                        Response<ResponseBody> response = photoCall.execute();

                        try {
                            if (response.isSuccessful()) {
                                byte[] photoData = response.body().bytes();

                                // Use Glide for efficient image loading
                                Bitmap bitmap = Glide.with(this)
                                        .asBitmap()
                                        .load(photoData)
                                        .override(300, 300)
                                        .submit()
                                        .get();

                                photosBitmap.add(bitmap);

                                int remainingImages = totalImagesToLoad.decrementAndGet();

                                if (remainingImages == 0) {
                                    runOnUiThread(() -> {
                                        progressBar.setVisibility(View.GONE);
                                        darkBackground.setVisibility(View.GONE);
                                        darkBackgroundBottomAppBar.setVisibility(View.GONE);
                                        initializeAccommodationRequestFragment(accommodationImageMap);
                                    });
                                }
                            } else {
                                Log.d("AdministratorMainScreen", "Error code " + response.code());
                            }
                        } finally {
                            response.body().close(); // Close the response body after using it
                        }
                    } catch (IOException | InterruptedException | ExecutionException e) {
                        Log.e("AdministratorMainScreen", "Error processing image: " + e.getMessage());
                    }
                });
            }
            accommodationImageMap.put(accommodation.getId(), photosBitmap);
        }

        executorService.shutdown();
    }

    private void initializeAccommodationRequestFragment(Map<Long, List<Bitmap>> accommodationImages){
        AccommodationRequestFragment accommodationRequestFragment = new AccommodationRequestFragment(accommodationImages);
        Bundle bundle = new Bundle();
        String resultsJson = new Gson().toJson(allAccommodations);
        bundle.putString("resultsJson", resultsJson);

        accommodationRequestFragment.setArguments(bundle);
        openFragment(accommodationRequestFragment);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_account){
            updateUser();
        } else if(itemId == R.id.nav_notifications){
            //Toast.makeText(GuestMainScreen.this,"NOTIFICATIONS clicked",Toast.LENGTH_SHORT).show();
//            openFragment(new NotificationsFragment());
        } else if(itemId == R.id.nav_language){
            //Toast.makeText(GuestMainScreen.this,"LANGUAGE clicked",Toast.LENGTH_SHORT).show();
            openFragment(new LanguageFragment());
        }
        else if(itemId == R.id.nav_settings) {
            //Toast.makeText(GuestMainScreen.this,"SETTINGS clicked",Toast.LENGTH_SHORT).show();
            openFragment(new SettingsFragment());
        }
        else if(itemId == R.id.nav_aboutus){
            //Toast.makeText(GuestMainScreen.this,"ABOUTUS clicked",Toast.LENGTH_SHORT).show();
            openFragment(new AboutUsFragment());
        }
        else if(itemId == R.id.nav_logout){
            Toast.makeText(AdministratorMainScreen.this,"Log out",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void loadProfilePicture(User user){
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        executorService.execute(() -> {
            try {
                Call<ResponseBody> photoCall = ClientUtils.photoService.loadPhoto(user.getProfilePicture().getId());
                Response<ResponseBody> response = photoCall.execute();

                if (response.isSuccessful()) {
                    byte[] photoData = response.body().bytes();
                    Bitmap profilePicture = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);

                    openFragment(new AccountFragment(profilePicture, user));
                } else {
                    Log.d("AdministratorMainScreen", "Error code " + response.code());
                }
            } catch (IOException e) {
                Log.e("AdministratorMainScreen", "Error reading response body: " + e.getMessage());
            }
        });
        executorService.shutdown();
    }

    private void setAllAccommodations() {
        Call<ArrayList<Accommodation>> all = ClientUtils.accommodationService.getAccommodations();
        all.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AdministratorMainScreen", "Successful response: " + response.body());
                    allAccommodations = response.body();
                } else {
                    // Log error details
                    Log.d("AdministratorMainScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("AdministratorMainScreen", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
                Log.d("AdministratorMainScreen", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void setUserReports(){
        Call<ArrayList<UserReport>> reports = ClientUtils.userReportService.getUserReports();
        reports.enqueue(new Callback<ArrayList<UserReport>>() {
            @Override
            public void onResponse(Call<ArrayList<UserReport>> call, Response<ArrayList<UserReport>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AdministratorMainScreen", "Successful response: " + response.body());
                    userReports = response.body();
                } else {
                    // Log error details
                    Log.d("AdministratorMainScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("AdministratorMainScreen", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<UserReport>> call, Throwable t) {
                Log.d("AdministratorMainScreen", t.getMessage() != null ? t.getMessage() : "error");
            }
        });

    }

    private void setReportedUsers(){
        Call<ArrayList<User>> reports = ClientUtils.userReportService.getAllReportedUsers();
        reports.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AdministratorMainScreen", "Successful response: " + response.body());
                    reportedUsers = response.body();
                    openFragment(new UserReportFragment(reportedUsers, R.id.frame_layoutAdmin));
                } else {
                    // Log error details
                    Log.d("AdministratorMainScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("AdministratorMainScreen", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                Log.d("AdministratorMainScreen", t.getMessage() != null ? t.getMessage() : "error");
            }
        });

    }

    private Map<Long, Integer> calculateNumberOfUserReports(){

        Map<Long, Integer> numberOfReports = new HashMap<>();
        for(User user : users){
            int result = 0;
            for(UserReport userReport : userReports){
                if (user.getId().equals(userReport.getReportedUser().getId()) && userReport.isStatus()){;
                    result += 1;
                }
            }
            numberOfReports.put(user.getId(), result);
        }
        return numberOfReports;
    }

    private Map<Long, List<String>> getReportsReasonList(){
        Map<Long, List<String>> userReportsReasons = new HashMap<>();
        for(User user : users){
            List<String> reasons = new ArrayList<>();
            for(UserReport userReport : userReports){
                Log.d("AdministratorMainScreen", "Comparing user " + user.getId() + " with reported user " + userReport.getReportedUser().getId() + " STATUS " + userReport.isStatus());
                if (user.getId().equals(userReport.getReportedUser().getId()) && userReport.isStatus()){
                    Log.d("AdministratorMainScreen", "USAAAAAAAAAAO");
                    reasons.add(userReport.getReason());
                }
            }
            userReportsReasons.put(user.getId(), reasons);
        }
        return userReportsReasons;
    }



    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layoutAdmin, fragment); //fragment_container
        transaction.commit();
    }




}

