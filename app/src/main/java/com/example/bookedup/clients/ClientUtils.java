package com.example.bookedup.clients;

import com.example.bookedup.BuildConfig;
import com.example.bookedup.adapters.DateTypeAdapter;
import com.example.bookedup.services.AccommodationService;
import com.example.bookedup.services.AdminService;
import com.example.bookedup.services.HostService;
import com.example.bookedup.services.ReservationService;
import com.example.bookedup.services.UserReportService;
import com.example.bookedup.services.UserService;
import com.example.bookedup.services.GuestService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {
    public static final String SERVICE_API_PATH = "http://"+ BuildConfig.IP_ADDR +":8080/api/";

    private static String authToken = ""; // Initially empty, set dynamically during login

    private static final Interceptor jwtInterceptor = chain -> {
        Request originalRequest = chain.request();

        // Check if the token is not empty, then add it to the header
        if (!authToken.isEmpty()) {
            originalRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + authToken)
                    .build();
        }

        return chain.proceed(originalRequest);
    };


    public static void setAuthToken(String token) {
        authToken = token;
    }

    public static String getAuthToken() {
        return authToken;
    }
    public static OkHttpClient test(){
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(jwtInterceptor).build();

        return client;
    }

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .create();



    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(test())
            .build();


    public static UserService userService = retrofit.create(UserService.class);
    public static GuestService guestService = retrofit.create(GuestService.class);
    public static HostService hostService = retrofit.create(HostService.class);
    public static AdminService adminService = retrofit.create(AdminService.class);
    public static AccommodationService accommodationService = retrofit.create(AccommodationService.class);
    public static ReservationService reservationService = retrofit.create(ReservationService.class);
    public static UserReportService userReportService = retrofit.create(UserReportService.class);

}
