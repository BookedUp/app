package com.example.bookedup.services;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PhotoService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("photo/{id}/load")
    Call<ResponseBody> loadPhoto(@Path("id") Long id);

    @Multipart
    @POST("photo/upload")
    Call<ResponseBody> uploadPhoto(@Part MultipartBody.Part image);



}
