package com.example.bookedup.services;

import com.example.bookedup.model.Guest;
import com.example.bookedup.model.LoginRequest;
import com.example.bookedup.model.Token;
import com.example.bookedup.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GuestService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("guests/{id}")
    Call<Guest> getGuest(@Path("id") Long id);
}
