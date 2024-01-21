package com.example.bookedup.services;

import com.example.bookedup.model.Accommodation;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AccommodationService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("accommodations")
    Call<ArrayList<Accommodation>> getAccommodations();

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

    @GET("accommodations/created")
    Call<ArrayList<Accommodation>> getAllCreated();

    @GET("accommodations/changed")
    Call<ArrayList<Accommodation>> getAllChanged();

    @GET("accommodations/host/{hostId}")
    Call<ArrayList<Accommodation>> getAllByHostId(@Path("hostId") Long id);

    @GET("accommodations/host/{hostId}/active")
    Call<ArrayList<Accommodation>> getAllActiveByHostId(@Path("hostId") Long id);

    @PUT("accommodations/{id}/confirmation")
    Call<Accommodation> approveAccommodation(@Path("id") Long id);

    @PUT("accommodations/{id}/rejection")
    Call<Accommodation> rejectAccommodation(@Path("id") Long id);

    @POST("accommodations")
    Call<Accommodation> createAccommodation(@Body Accommodation accommodation);

    @PUT("accommodations/{id}")
    Call<Accommodation> updateAccommodation(@Body Accommodation accommodation, @Path("id") Long id);
}
