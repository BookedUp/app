package com.example.bookedup.services;

import com.example.bookedup.model.LoginRequest;
import com.example.bookedup.model.Token;
import com.example.bookedup.model.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("login")
    Call<Token> login(@Body LoginRequest loginRequest);

    @POST("registration/android")
    Call<Token> register(@Body User user);

    @GET("users")
    Call<ArrayList<User>> getUsers();

    @GET("users/{id}")
    Call<User> getUser(@Path("id") Long id);

    @PUT("users/{id}")
    Call<User> updateUser(@Body User user, @Path("id") Long id);

    @DELETE("users/{id}")
    Call<User> deleteUser(@Path("id") Long id);

    @PUT("users/{id}/block")
    Call<User> blockUser(@Path("id") Long id);

    @PUT("users/{id}/unblock")
    Call<User> unblockUser(@Path("id") Long id);

    @GET("users/blocked-users")
    Call<ArrayList<User>> getBlockedUsers();

    @GET("users/unblocked-users")
    Call<ArrayList<User>> getUnblockedUsers();


}
