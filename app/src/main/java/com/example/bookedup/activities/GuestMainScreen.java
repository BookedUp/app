package com.example.bookedup.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookedup.R;
//import com.example.bookedup.adapters.CategoryAdapter;
//import com.example.bookedup.adapters.PopularAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.about.AboutUsFragment;
import com.example.bookedup.fragments.accommodations.AccommodationListFragment;
import com.example.bookedup.fragments.accommodations.AccommodationRequestFragment;
import com.example.bookedup.fragments.accommodations.FavouriteAccommodationFragment;
import com.example.bookedup.fragments.account.AccountFragment;
import com.example.bookedup.fragments.home.HomeFragment;
import com.example.bookedup.fragments.language.LanguageFragment;
import com.example.bookedup.fragments.notifications.NotificationsFragment;
import com.example.bookedup.fragments.reservations.ReservationListFragment;
import com.example.bookedup.fragments.reservations.ReservationRequestFragment;
import com.example.bookedup.fragments.settings.SettingsFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Category;
import com.example.bookedup.model.Notification;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.Review;
import com.example.bookedup.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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


public class GuestMainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    private List<Reservation> myReservations = new ArrayList<Reservation>();
    private List<Accommodation> favourites = new ArrayList<>();
    private ArrayList<Notification> notifications = new ArrayList<>();
    private boolean reservationsIsClicked = false;
//    private List<Accommodation> mostPopularAccommodations = new ArrayList<>();
//    private Map<Long, List<Bitmap>> accommodationImages = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_main_screen);
        checkForNewNotifications();

//        Intent intent = getIntent();
//
//        if (intent != null) {
//            mostPopularAccommodations = (List<Accommodation>) intent.getSerializableExtra("mostPopularAccommodations");
//            accommodationImages = (Map<Long, List<Bitmap>>) intent.getSerializableExtra("photosBitmap");
//
//            // Use the received lists...
//        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar = findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView.setBackground(null);
        setMyReservations();
       bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               int itemId = item.getItemId();
               if (itemId == R.id.nav_home){
                   openFragment(new HomeFragment());
                   return true;
               }
               else if(itemId == R.id.nav_reservations) {
                   reservationsIsClicked = true;
                   setMyReservations();
                   return true;
               }
               else if(itemId == R.id.nav_favorites){
                   reservationsIsClicked = false;
                   favourites = LoginScreen.loggedGuest.getFavourites();
                   if (!favourites.isEmpty()) {
                       getLoadPictures(favourites, myReservations);
                   } else {
                       Toast.makeText(GuestMainScreen.this,"No results!",Toast.LENGTH_SHORT).show();
                   }
                   return true;
               }
               else if(itemId == R.id.nav_account){
                   updateUser();
                   return true;
               }
               return false;
           }
       });
       fragmentManager = getSupportFragmentManager();
       openFragment(new HomeFragment());
    }


    private void checkForNewNotifications() {
        if (LoginScreen.loggedGuest != null) {
            Call<ArrayList<Notification>> allNotifications = ClientUtils.notificationService.getEnabledNotificationsByUserId(LoginScreen.loggedGuest.getId());
            allNotifications.enqueue(new Callback<ArrayList<Notification>>() {
                @Override
                public void onResponse(Call<ArrayList<Notification>> call, Response<ArrayList<Notification>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("LoginScreen", "Successful response: " + response.body());
                        notifications = response.body();
                        Collections.sort(notifications, (n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));

                        Notification latestNotification = findLatestNotification(notifications);

                        if (latestNotification != null) {
                            Log.d("LoginScreen", "Latest Notification: " + latestNotification.toString());
                            showNotificationPopup(latestNotification);

                        }
                    } else {
                        Log.d("LoginScreen", "Unsuccessful response: " + response.code());
                        try {
                            Log.d("LoginScreen", "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Notification>> call, Throwable t) {
                    Log.d("LoginScreen", t.getMessage() != null ? t.getMessage() : "error");
                }
            });
        }
    }


    private Notification findLatestNotification(ArrayList<Notification> notificationList) {
        if (notificationList != null && !notificationList.isEmpty()) {
            Notification latestNotification = notificationList.get(0);

            for (Notification notification : notificationList) {
                if (notification.getTimestamp().after(latestNotification.getTimestamp())) {
                    latestNotification = notification;
                }
            }

            return latestNotification;
        }

        return null;
    }

    private void showNotificationPopup(Notification notification) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (!notification.getTitle().isEmpty()) {
            builder.setTitle(notification.getTitle());
        }else {
            builder.setTitle("New Notification");
        }
        builder.setMessage("Message: " + notification.getMessage() +
                "\nTimestamp: " + notification.getTimestamp());

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadProfilePicture(User user ){
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        executorService.execute(() -> {
            try {
                Log.d("GuestMainScreen", "Nova slika " + user.getProfilePicture().getUrl());
                Call<ResponseBody> photoCall = ClientUtils.photoService.loadPhoto(user.getProfilePicture().getId());
                Response<ResponseBody> response = photoCall.execute();

                if (response.isSuccessful()) {
                    byte[] photoData = response.body().bytes();
                    Bitmap profilePicture = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
                    openFragment(new AccountFragment(profilePicture));
                } else {
                    Log.d("GuestMainScreen", "Error code " + response.code());
                }
            } catch (IOException e) {
                Log.e("GuestMainScreen", "Error reading response body: " + e.getMessage());
            }
        });
        executorService.shutdown();
    }


    private void updateUser(){
        Call<User> updatedUser = ClientUtils.userService.getUser(LoginScreen.loggedGuest.getId());
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

    private void setMyReservations() {
        Call<ArrayList<Reservation>> reservations = ClientUtils.reservationService.getReservationsByGuestId(LoginScreen.loggedGuest.getId());
        reservations.enqueue(new Callback<ArrayList<Reservation>>() {
            @Override
            public void onResponse(Call<ArrayList<Reservation>> call, Response<ArrayList<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("GuestMainScreen", "Successful response: " + response.body());
                    myReservations = response.body();
                    if(!myReservations.isEmpty()) {
                        getLoadPictures(favourites, myReservations);
                    } else {
                        Toast.makeText(GuestMainScreen.this,"No results!",Toast.LENGTH_SHORT).show();
                    }
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
            public void onFailure(Call<ArrayList<Reservation>> call, Throwable t) {
                Log.d("GuestMainScreen", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }


    private void getLoadPictures(List<Accommodation> favourites, List<Reservation> myReservations) {
        Map<Long, List<Bitmap>> accommodationImageMap = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        AtomicInteger totalImagesToLoad = new AtomicInteger(0);

        List<Accommodation> choosenAccommodations = findChoosenAccommodations(favourites, myReservations);

        for (Accommodation accommodation : choosenAccommodations) {
            List<Bitmap> photosBitmap = new ArrayList<>();
            for(Photo photo : accommodation.getPhotos()) {
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
                                    // All images are loaded, update the adapter
                                    Fragment fragment = findFragment(favourites, myReservations, accommodationImageMap);
                                    openFragment(fragment);
                                }
                            } else {
                                Log.d("GuestMainScreen", "Error code " + response.code());
                            }
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } catch (IOException e) {
                        Log.e("GuestMainScreen", "Error reading response body: " + e.getMessage());
                    }
                });
            }
            accommodationImageMap.put(accommodation.getId(), photosBitmap);
        }
        executorService.shutdown();
    }

    private List<Accommodation> findChoosenAccommodations(List<Accommodation> favourites, List<Reservation> myReservations){
        List<Accommodation> choosenAccommodations = new ArrayList<>();
        if(reservationsIsClicked){;
            for(Reservation reservation : myReservations){
                choosenAccommodations.add(reservation.getAccommodation());
            }
        } else {
            choosenAccommodations.addAll(favourites);
        }

        return  choosenAccommodations;
    }

    private Fragment findFragment(List<Accommodation> favourites, List<Reservation> myReservations, Map<Long, List<Bitmap>> accommodationImageMap){
        if (reservationsIsClicked){
            ReservationListFragment reservationListFragment = new ReservationListFragment(myReservations, R.id.frame_layout, accommodationImageMap);
            return reservationListFragment;
        } else {
            FavouriteAccommodationFragment favouriteAccommodationFragment = new FavouriteAccommodationFragment(favourites, accommodationImageMap);
            return favouriteAccommodationFragment;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_account){
            updateUser();
        } else if (itemId == R.id.nav_language){
            openFragment(new LanguageFragment());
        }
        else if(itemId == R.id.nav_settings) {
            openFragment(new SettingsFragment());
        }
        else if(itemId == R.id.nav_aboutus){
            openFragment(new AboutUsFragment());
        }
        else if(itemId == R.id.nav_notifications){
            openFragment(new NotificationsFragment(notifications, R.id.frame_layout));
        }
        else if(itemId == R.id.nav_logout){
            Toast.makeText(GuestMainScreen.this,"Log out",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }

    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment); //fragment_container
        transaction.commit();
    }



}