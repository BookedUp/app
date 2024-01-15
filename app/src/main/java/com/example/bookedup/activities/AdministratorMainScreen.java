package com.example.bookedup.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.bookedup.R;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.about.AboutUsFragment;
import com.example.bookedup.fragments.accommodations.AccommodationRequestFragment;
import com.example.bookedup.fragments.account.AccountFragment;
//import com.example.bookedup.fragments.admin.user.AdminManageUsersFragment;
import com.example.bookedup.fragments.home.HomeFragment;
import com.example.bookedup.fragments.language.LanguageFragment;
import com.example.bookedup.fragments.notifications.NotificationsFragment;
import com.example.bookedup.fragments.settings.SettingsFragment;
import com.example.bookedup.model.Accommodation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdministratorMainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;

    private BottomNavigationView bottomNavigationView;

    private FragmentManager fragmentManager;

    private Toolbar toolbar;

    private List<Accommodation> allAccommodations = new ArrayList<Accommodation>();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_main_screen);

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
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                int itemId=item.getItemId();
                if(itemId==R.id.nav_homeAdmin){
                    openFragment(new HomeFragment());
                    return true;
                }
                else if(itemId==R.id.nav_accommodation){
                    AccommodationRequestFragment accommodationRequestFragment = new AccommodationRequestFragment();
                    Bundle bundle = new Bundle();
                    String resultsJson = new Gson().toJson(allAccommodations);
                    bundle.putString("resultsJson", resultsJson);

                    accommodationRequestFragment.setArguments(bundle);
                    openFragment(accommodationRequestFragment);
                    return true;
                }
                else if(itemId==R.id.nav_users){
                    Toast.makeText(AdministratorMainScreen.this,"Favorites clicked",Toast.LENGTH_SHORT).show();
//                    openFragment(new AdminManageUsersFragment());
                    return true;
                }
                else if(itemId==R.id.nav_comments){
                    Toast.makeText(AdministratorMainScreen.this,"Favorites clicked",Toast.LENGTH_SHORT).show();
                    //openFragment(new AccountFragment());
                    return true;
                }
                return false;
            }
        });


        fragmentManager=getSupportFragmentManager();
        openFragment(new HomeFragment());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_account){
            openFragment(new AccountFragment());
        } else if(itemId == R.id.nav_notifications){
            //Toast.makeText(GuestMainScreen.this,"NOTIFICATIONS clicked",Toast.LENGTH_SHORT).show();
            openFragment(new NotificationsFragment());
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

    private void setAllAccommodations() {
        Call<ArrayList<Accommodation>> all = ClientUtils.accommodationService.getAccommodations();
        all.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AccommodationRequestFragment", "Successful response: " + response.body());
                    allAccommodations = response.body();
                } else {
                    // Log error details
                    Log.d("AccommodationRequestFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("AccommodationRequestFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
                Log.d("AccommodationRequestFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private  void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layoutAdmin,fragment);
        fragmentTransaction.commit();
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
    }}

