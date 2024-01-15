package com.example.bookedup.services;

import com.example.bookedup.model.Admin;
import com.example.bookedup.model.Guest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface AdminService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("admins/{id}")
    Call<Admin> getById(@Path("id") Long id);
}
