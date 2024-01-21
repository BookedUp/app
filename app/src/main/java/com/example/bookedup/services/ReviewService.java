package com.example.bookedup.services;

import com.example.bookedup.model.Review;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ReviewService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("reviews")
    Call<ArrayList<Review>> getReviews();

    @POST("reviews")
    Call<Review> createReview(@Body Review review);

    @GET("reviews/accommodation/{id}")
    Call<ArrayList<Review>> getAccommodationReviews(@Path("id") Long id);

    @GET("reviews/host/{hostId}/host")
    Call<ArrayList<Review>> getHostReviewsByHostId(@Path("hostId") Long hostId);

    @PUT("reviews/{id}/confirmation")
    Call<Review> approveReview(@Path("id") Long id);

    @DELETE("reviews/{id}")
    Call<Review> deleteReview(@Path("id") Long id);
}
