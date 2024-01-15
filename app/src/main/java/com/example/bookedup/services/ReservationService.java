package com.example.bookedup.services;

import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReservationService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })

    @GET("reservations")
    Call<ArrayList<Reservation>> getReservations();

    @POST("reservations")
    Call<Reservation> createReservation(@Body Reservation reservation);

    @DELETE("reservations/{id}")
    Call<Reservation> deleteReservation(@Path("id") Long id);

    @GET("reservations/guest/{guestId}")
    Call<ArrayList<Reservation>> getReservationsByGuestId(@Path("guestId") Long id);

    @GET("reservations/host/{hostId}")
    Call<ArrayList<Reservation>> getReservationsByHostId(@Path("hostId") Long id);

    @PUT("reservations/{id}/confirmation")
    Call<Reservation> approveReservation(@Path("id") Long id);

    @PUT("reservations/{id}/rejection")
    Call<Reservation> rejectReservation(@Path("id") Long id);

    @PUT("reservations/{id}/cancellation")
    Call<Reservation> cancelReservation(@Path("id") Long id);

    @GET("reservations/search/{hostId}")
    Call<ArrayList<Reservation>> searchReservations(
            @Path("hostId") Long hostId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("accommodationName") String accommodationName
    );

}
