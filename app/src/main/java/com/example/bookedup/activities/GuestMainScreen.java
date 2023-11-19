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
import com.example.bookedup.adapters.CategoryAdapter;
import com.example.bookedup.adapters.PopularAdapter;
import com.example.bookedup.fragments.about.AboutUsFragment;
import com.example.bookedup.fragments.account.AccountFragment;
import com.example.bookedup.fragments.home.HomeFragment;
import com.example.bookedup.fragments.language.LanguageFragment;
import com.example.bookedup.fragments.notifications.NotificationsFragment;
import com.example.bookedup.fragments.settings.SettingsFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Category;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;


public class GuestMainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FloatingActionButton fab;

    private BottomNavigationView bottomNavigationView;

    private FragmentManager fragmentManager;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_main_screen);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar = findViewById(R.id.toolbar); //Ignore red line errors


        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new LanguageFragment()).commit();
//            navigationView.setCheckedItem(R.id.nav_language);
//        }

//        replaceFragment(new SettingsFragment());



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
                   Toast.makeText(GuestMainScreen.this,"Reservations clicked",Toast.LENGTH_SHORT).show();
                   //openFragment(new AccountFragment());
                   return true;
               }
               else if(itemId == R.id.nav_favorites){
                   Toast.makeText(GuestMainScreen.this,"Favorites clicked",Toast.LENGTH_SHORT).show();
                   //openFragment(new AccountFragment());
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
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
        if (itemId == R.id.nav_language){
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
        else if(itemId == R.id.nav_notifications){
            //Toast.makeText(GuestMainScreen.this,"NOTIFICATIONS clicked",Toast.LENGTH_SHORT).show();
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


//    private void initRecycleView(){
//        ArrayList<Accommodation> items = new ArrayList<>();
//        items.add(new Accommodation("Lorem ipsum", "Miami", "description", 2, 4.8, "pic1", true, 1000));
//        items.add(new Accommodation("Lorem ipsum", "Miami", "description", 2, 4.8, "pic1", true, 1000));
//        items.add(new Accommodation("Lorem ipsum", "Miami", "description", 2, 4.8, "pic1", true, 1000));
//
//        //recyclerViewPopular.findViewById(R.id.view_pop);
//        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        adapterPopular = new PopularAdapter(items);
//        recyclerViewPopular.setAdapter(adapterPopular);
//
//        ArrayList<Category> categoryList = new ArrayList<>();
//        categoryList.add(new Category("Hotels", "guest"));
//        categoryList.add(new Category("Motels", "guest"));
//        categoryList.add(new Category("Vilas", "guest"));
//        categoryList.add(new Category("Apartments", "guest"));
//        categoryList.add(new Category("Studios", "guest"));
//
//        //recyclerViewCategory.findViewById(R.id.view_category);
//        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        adapterCategory = new CategoryAdapter(categoryList);
//        recyclerViewCategory.setAdapter(adapterCategory);
//    }

}