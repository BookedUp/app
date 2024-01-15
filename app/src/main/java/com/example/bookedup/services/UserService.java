package com.example.bookedup.services;

import com.example.bookedup.model.LoginRequest;
import com.example.bookedup.model.Token;
import com.example.bookedup.model.User;

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

    @GET("users/{id}")
    Call<User> getUser(@Path("id") Long id);

    @PUT("users/{id}")
    Call<User> updateUser(@Body User user, @Path("id") Long id);

    @DELETE("users/{id}")
    Call<User> deleteUser(@Path("id") Long id);


}
