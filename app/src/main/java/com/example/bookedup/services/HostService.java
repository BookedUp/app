package com.example.bookedup.services;

import com.example.bookedup.model.Guest;
import com.example.bookedup.model.Host;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface HostService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("hosts/{id}")
    Call<Host> getHost(@Path("id") Long id);
}
