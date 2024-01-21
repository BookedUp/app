package com.example.bookedup.services;

import com.example.bookedup.model.Notification;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("notifications/user/enabled/{id}")
    Call<ArrayList<Notification>> getEnabledNotificationsByUserId(@Path("id") Long id);

    @POST("notifications")
    Call<Notification> createNotification(@Body Notification notification);
}
