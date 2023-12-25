//package com.example.bookedup.services;
//
//import com.example.bookedup.model.Accommodation;
//
//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.GET;
//import retrofit2.http.Headers;
//import retrofit2.http.POST;
//
//public interface AccommodationService {
//
//    @Headers({
//            "User-Agent: Mobile-Android",
//            "Content-Type:application/json"
//    })
//    @POST("accommodations")
//    Call<Accommodation> save(@Body Accommodation accommodation);
//
//    @Headers({
//            "User-Agent: Mobile-Android",
//            "Content-Type:application/json"
//    })
//    @GET("guest")
//    Call<Accommodation> getForGuest(@Body Accommodation accommodation);
//}
