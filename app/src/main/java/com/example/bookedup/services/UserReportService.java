package com.example.bookedup.services;

import com.example.bookedup.model.UserReport;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserReportService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("user-reports")
    Call<ArrayList<UserReport>> getUserReports();

    @POST("user-reports")
    Call<UserReport> createUserReport(@Body UserReport userReport);

    @PUT("user-reports/{id}")
    Call<UserReport> updateUserReport(@Body UserReport userReport, @Path("id") Long id);

    @DELETE("user-reports/{id}")
    Call<UserReport> deleteUserReport(@Path("id") Long id);
}
