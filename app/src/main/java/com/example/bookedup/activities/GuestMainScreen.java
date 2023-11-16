package com.example.bookedup.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.bookedup.R;
import com.example.bookedup.databinding.ActivityGuestMainScreenBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

public class GuestMainScreen extends AppCompatActivity {

    private ActivityGuestMainScreenBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Set<Integer> topLevelDestinations = new HashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("BookedUp", "GuestMainScreen onCreate()");

        binding = ActivityGuestMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        drawer = binding.drawerLayout;
//        navigationView = binding.navView;
//        toolbar = binding.activityHomeBase.toolbar;

    }
}