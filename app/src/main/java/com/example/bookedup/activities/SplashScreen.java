package com.example.bookedup.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.bookedup.R;
//import com.example.bookedup.services.AccommodationService;
import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 5000; // 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_splash_screen);


        // Provera internet veze
         if(isConnectedToInternet()) {
            int SPLASH_TIME_OUT = 5000;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, LoginScreen.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIME_OUT);


        } else {
            // Ako korisnik nije povezan na internet, prikazi Toast poruku
            showInternetConnectionMessage();
        }
    }

    // Metoda za proveru internet veze
    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    // Metoda za prikazivanje Toast poruke o nepovezanosti na internet
    private void showInternetConnectionMessage() {
        Toast toast = Toast.makeText(this, "Niste povezani na internet. Povežite se i pokušajte ponovo.", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();


//        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
//                "Niste povezani na internet. Povežite se i pokušajte ponovo.", Snackbar.LENGTH_INDEFINITE)
//                .setAction("Poveži se", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        // Dodajte kod za povezivanje na internet ako je korisnik pritisnuo "Poveži se"
//                    }
//                });
//        snackbar.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Dodajte kod koji će se izvršiti kada aktivnost postane vidljiva korisniku
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Dodajte kod koji će se izvršiti kada se aktivnost restartuje
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Dodajte kod koji će se izvršiti kada aktivnost ponovno postane interaktivna
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Dodajte kod koji će se izvršiti kada aktivnost izgubi fokus
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Dodajte kod koji će se izvršiti kada aktivnost više nije vidljiva korisniku
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dodajte kod koji će se izvršiti kada aktivnost bude uništena
    }
}