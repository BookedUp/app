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
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.bookedup.R;
import com.example.bookedup.fragments.about.AboutUsFragment;
import com.example.bookedup.fragments.account.AccountFragment;
//import com.example.bookedup.fragments.admin.user.AdminManageUsersFragment;
import com.example.bookedup.fragments.home.HomeFragment;
import com.example.bookedup.fragments.language.LanguageFragment;
import com.example.bookedup.fragments.notifications.NotificationsFragment;
import com.example.bookedup.fragments.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class AdministratorMainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;

    private BottomNavigationView bottomNavigationView;

    private FragmentManager fragmentManager;

    private Toolbar toolbar;


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
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                int itemId=item.getItemId();
                if(itemId==R.id.nav_homeAdmin){
                    openFragment(new HomeFragment());
                    return true;
                }
                else if(itemId==R.id.nav_accommodation){
                    Toast.makeText(AdministratorMainScreen.this,"Reservations clicked",Toast.LENGTH_SHORT).show();
                    //openFragment(new AccountFragment());
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

    private  void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layoutAdmin,fragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){

        return true;
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

