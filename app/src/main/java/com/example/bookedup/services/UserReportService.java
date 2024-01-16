package com.example.bookedup.services;

import com.example.bookedup.model.UserReport;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserReportService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @POST("user-reports")
    Call<UserReport> createUserReport(@Body UserReport userReport);
}
