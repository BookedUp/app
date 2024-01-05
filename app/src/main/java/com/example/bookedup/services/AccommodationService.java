package com.example.bookedup.services;

import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.enums.AccommodationType;
import com.example.bookedup.model.enums.Amenity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AccommodationService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("accommodations")
    Call<ArrayList<Accommodation>> getAccommodations(@Body Accommodation accommodation);

    @GET("accommodations/mostPopular")
    Call<ArrayList<Accommodation>> getMostPopular();

    @GET("accommodations/search-filter")
    Call<ArrayList<Accommodation>> searchAccommodations(
            @Query("location") String location,
            @Query("guestsNumber") Integer guestsNumber,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("amenities") List<Object> amenities,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("customMaxBudget") Double customMaxBudget,
            @Query("selectedType") Object selectedType,
            @Query("name") String name
    );

}
