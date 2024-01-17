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

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.example.bookedup.R;
//import com.example.bookedup.adapters.CategoryAdapter;
//import com.example.bookedup.adapters.PopularAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.about.AboutUsFragment;
import com.example.bookedup.fragments.accommodations.AccommodationRequestFragment;
import com.example.bookedup.fragments.accommodations.FavouriteAccommodationFragment;
import com.example.bookedup.fragments.account.AccountFragment;
import com.example.bookedup.fragments.home.HomeFragment;
import com.example.bookedup.fragments.language.LanguageFragment;
import com.example.bookedup.fragments.notifications.NotificationsFragment;
import com.example.bookedup.fragments.reservations.ReservationListFragment;
import com.example.bookedup.fragments.settings.SettingsFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Category;
import com.example.bookedup.model.Reservation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GuestMainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    private List<Reservation> myReservations = new ArrayList<Reservation>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_main_screen);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        fab = findViewById(R.id.fab);
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
       bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               int itemId = item.getItemId();
               if (itemId == R.id.nav_home){
                   openFragment(new HomeFragment());
                   return true;
               }
               else if(itemId == R.id.nav_reservations) {
                   setMyReservations();
                   return true;
               }
               else if(itemId == R.id.nav_favorites){
                   openFragment(new FavouriteAccommodationFragment());
                   return true;
               }
               else if(itemId == R.id.nav_account){
                   openFragment(new AccountFragment());
                   return true;
               }
               return false;
           }
       });
       fragmentManager = getSupportFragmentManager();
       openFragment(new HomeFragment());

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showBottomDialog();
//            }
//        });
    }


    private void setMyReservations() {
        Call<ArrayList<Reservation>> reservations = ClientUtils.reservationService.getReservationsByGuestId(LoginScreen.loggedGuest.getId());
        reservations.enqueue(new Callback<ArrayList<Reservation>>() {
            @Override
            public void onResponse(Call<ArrayList<Reservation>> call, Response<ArrayList<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("GuestMainScreen", "Successful response: " + response.body());
                    myReservations = response.body();
                    ReservationListFragment reservationListFragment = new ReservationListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("layout_caller", R.id.frame_layout);
                    String resultsJson = new Gson().toJson(myReservations);
                    bundle.putString("resultsJson", resultsJson);

                    reservationListFragment.setArguments(bundle);
                    openFragment(reservationListFragment);
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

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheets);

        LinearLayout videoLayout = dialog.findViewById(R.id.layout_reservation);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(GuestMainScreen.this,"Added new reservation",Toast.LENGTH_SHORT).show();

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_account){
            openFragment(new AccountFragment());
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
            openFragment(new NotificationsFragment());
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