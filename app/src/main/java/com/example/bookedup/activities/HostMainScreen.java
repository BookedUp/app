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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookedup.R;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.about.AboutUsFragment;
import com.example.bookedup.fragments.accommodations.AccommodationListFragment;
import com.example.bookedup.fragments.accommodations.CreateAccommodationFragment;
import com.example.bookedup.fragments.accommodations.StatisticFragment;
import com.example.bookedup.fragments.account.AccountFragment;
import com.example.bookedup.fragments.home.HomeFragment;
import com.example.bookedup.fragments.language.LanguageFragment;
import com.example.bookedup.fragments.notifications.NotificationsFragment;
import com.example.bookedup.fragments.reservations.ReservationRequestFragment;
import com.example.bookedup.fragments.settings.SettingsFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Notification;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

public class HostMainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private List<Reservation> myReservations = new ArrayList<>();
    private List<Accommodation> myAccommodations = new ArrayList<>();
    private ArrayList<Notification> notifications = new ArrayList<>();
    private boolean reservationsIsClicked = false;

    private ProgressBar progressBar;
    private int count = 0;
    private Timer timer;

    private View darkBackground;
    private View darkBackgroundBottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_main_screen);
        checkForNewNotifications();

        progressBar = findViewById(R.id.loadingProgressBar);
        darkBackground = findViewById(R.id.darkBackground);
        darkBackgroundBottomAppBar = findViewById(R.id.darkBackgroundBottomAppBar);
        progressBar.setVisibility(View.INVISIBLE);

        bottomNavigationView=findViewById(R.id.bottomNavigationViewHost);
        drawerLayout=findViewById(R.id.drawer_layoutHost);
        NavigationView navigationView=findViewById(R.id.nav_viewHost);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar=findViewById(R.id.toolbarHost); //Ignore red line errors
        fab = findViewById(R.id.fabHostScreen);


        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                int itemId=item.getItemId();
                if(itemId==R.id.nav_homeHost){
                    openFragment(new HomeFragment());
                    return true;
                }
                else if(itemId==R.id.nav_accommodationHost){
                    reservationsIsClicked = false;
                    setMyAccommodations();
                    return true;
                }
                else if(itemId==R.id.nav_reservationsHost){
                    reservationsIsClicked = true;
                    setMyReservations();
                    return true;
                }
                else if(itemId==R.id.nav_reportHost){
                   openFragment(new StatisticFragment());
                    return true;
                }
                return false;
            }
        });


        fragmentManager=getSupportFragmentManager();
        openFragment(new HomeFragment());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
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

    private void checkForNewNotifications() {
        if (LoginScreen.loggedHost != null) {
            Call<ArrayList<Notification>> allNotifications = ClientUtils.notificationService.getEnabledNotificationsByUserId(LoginScreen.loggedHost.getId());
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

    private void setMyReservations() {
        Call<ArrayList<Reservation>> reservations = ClientUtils.reservationService.getReservationsByHostId(LoginScreen.loggedHost.getId());
        reservations.enqueue(new Callback<ArrayList<Reservation>>() {
            @Override
            public void onResponse(Call<ArrayList<Reservation>> call, Response<ArrayList<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HostMainScreen", "Successful response: " + response.body());
                    myReservations = response.body();
                    if(!myReservations.isEmpty()) {
                        getLoadPictures(myAccommodations, myReservations);
                    }else {
                        Toast.makeText(HostMainScreen.this,"Reservations list is empty!",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Log error details
                    Log.d("HostMainScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("HostMainScreen", "Error Body: " + response.errorBody().string());
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

    private void setMyAccommodations() {
        Call<ArrayList<Accommodation>> accommodations = ClientUtils.accommodationService.getAllByHostId(LoginScreen.loggedHost.getId());
        accommodations.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HostMainScreen", "Successful response: " + response.body());
                    myAccommodations = response.body();
                    if (!myAccommodations.isEmpty()) {
                        getLoadPictures(myAccommodations, myReservations);
                    } else {
                        Toast.makeText(HostMainScreen.this,"Accommodations list is empty!",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("HostMainScreen", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("HostMainScreen", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
                Log.d("GuestMainScreen", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void getLoadPictures(List<Accommodation> myAccommodations, List<Reservation> myReservations) {
        initiateProgressBar();
        if (progressBar.getVisibility() == View.VISIBLE) {
            darkBackground.setVisibility(View.VISIBLE);
            darkBackgroundBottomAppBar.setVisibility(View.VISIBLE);
        }
        Map<Long, List<Bitmap>> accommodationImageMap = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        AtomicInteger totalImagesToLoad = new AtomicInteger(0);

        List<Accommodation> choosenAccommodations = findChoosenAccommodations(myAccommodations, myReservations);

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
                                    runOnUiThread(() -> {
                                        progressBar.setVisibility(View.GONE);
                                        darkBackground.setVisibility(View.GONE);
                                        darkBackgroundBottomAppBar.setVisibility(View.GONE);
                                        Fragment fragment = findFragment(myAccommodations, myReservations, accommodationImageMap);
                                        openFragment(fragment);
                                    });
                                }
                            } else {
                                Log.d("HostMainScreeen", "Error code " + response.code());
                            }
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } finally {
                            response.body().close(); // Close the response body after using it
                        }
                    } catch (IOException e) {
                        Log.e("HostMainScreeen", "Error reading response body: " + e.getMessage());
                    }
                });
            }
            accommodationImageMap.put(accommodation.getId(), photosBitmap);
        }
        executorService.shutdown();
    }

    private List<Accommodation> findChoosenAccommodations(List<Accommodation> myAccommodations, List<Reservation> myReservations){
        List<Accommodation> choosenAccommodations = new ArrayList<>();
        if(reservationsIsClicked){
            for(Reservation reservation : myReservations){
                choosenAccommodations.add(reservation.getAccommodation());
            }
        } else {
            choosenAccommodations.addAll(myAccommodations);
        }

        return  choosenAccommodations;
    }

    private Fragment findFragment(List<Accommodation> myAccommodations, List<Reservation> myReservations, Map<Long, List<Bitmap>> accommodationImageMap){
        if (reservationsIsClicked){
            ReservationRequestFragment reservationRequestFragment = new ReservationRequestFragment(myReservations, accommodationImageMap, R.id.frame_layoutHost);
            return reservationRequestFragment;
        } else {
            AccommodationListFragment accommodationListFragment = new AccommodationListFragment(accommodationImageMap, R.id.frame_layoutHost, myAccommodations);
            return accommodationListFragment;
        }
    }

    private void updateUser(){
        Call<User> updatedUser = ClientUtils.userService.getUser(LoginScreen.loggedHost.getId());
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

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheets_host);

        LinearLayout videoLayout = dialog.findViewById(R.id.layout_accommodation);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButtonHost);

        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                openFragment(new CreateAccommodationFragment());

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private  void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layoutHost,fragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
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
            openFragment(new NotificationsFragment(notifications, R.id.frame_layoutHost));
        }
        else if(itemId == R.id.nav_logout){
            Toast.makeText(HostMainScreen.this,"Log out",Toast.LENGTH_SHORT).show();
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
                    Log.d("HostMainScreen", "Error code " + response.code());
                }
            } catch (IOException e) {
                Log.e("HostMainScreen", "Error reading response body: " + e.getMessage());
            }
        });
        executorService.shutdown();
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
        transaction.replace(R.id.frame_layoutHost, fragment); //fragment_container
        transaction.commit();
    }}