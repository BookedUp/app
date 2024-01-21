package com.example.bookedup.services;

import com.example.bookedup.model.Review;
import com.example.bookedup.model.ReviewReport;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReviewReportService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @POST("review-reports")
    Call<ReviewReport> createReviewReport(@Body ReviewReport reviewReport);

    @GET("review-reports/reported-reviews")
    Call<ArrayList<Review>> getAllReportedReviews();

    @GET("review-reports/reasons/{reportReviewId}")
    Call<ArrayList<String>> getReportReasons(@Path("reportReviewId") Long reportReviewId);

    @DELETE("review-reports/{id}")
    Call<ReviewReport> deleteReviewReport(@Path("id") Long id);
}
