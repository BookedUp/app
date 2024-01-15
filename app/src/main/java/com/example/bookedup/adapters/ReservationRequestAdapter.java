package com.example.bookedup.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.accommodations.AccommodationRequestFragment;
import com.example.bookedup.fragments.reservations.CreateReservationFragment;
import com.example.bookedup.fragments.reservations.ReservationRequestFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.enums.AccommodationStatus;
import com.example.bookedup.model.enums.ReservationStatus;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationRequestAdapter extends RecyclerView.Adapter<ReservationRequestAdapter.ViewHolder> {

    private List<Reservation> reservations;
    private Context context;

    private int layout;

    private Fragment fragment;

    public ReservationRequestAdapter(List<Reservation> reservations, Context context, int before_layout, Fragment fragment) {
        this.reservations = reservations;
        this.context = context;
        this.layout = before_layout;
        this.fragment = fragment;


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView accommodationImage;
        TextView title, averageRating, status, price, checkInDate, checkOutDate;
        Button btnViewDetails, btnReject, btnAccept;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            accommodationImage = itemView.findViewById(R.id.resReq_imageHotel);
            title = itemView.findViewById(R.id.resReq_Title);
            averageRating = itemView.findViewById(R.id.resReq_averageRating);
            status = itemView.findViewById(R.id.resReq_status);
            price = itemView.findViewById(R.id.resReq_price);
            checkInDate = itemView.findViewById(R.id.resReq_checkInDate);
            checkOutDate = itemView.findViewById(R.id.resReq_checkOutDate);

            btnViewDetails = itemView.findViewById(R.id.resReq_btnViewDetails);
            btnAccept = itemView.findViewById(R.id.resReq_btnAccept);
            btnReject = itemView.findViewById(R.id.resReq_btnReject);
        }
    }

    @NonNull
    @Override
    public ReservationRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_reservation_request, parent, false);
        return new ReservationRequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationRequestAdapter.ViewHolder holder, int position) {
        Reservation currentReservation = reservations.get(position);

        Log.d("ReservationRequestAdapter", "Res " + currentReservation.toString());

        holder.title.setText(currentReservation.getAccommodation().getName());
        holder.averageRating.setText(String.valueOf(currentReservation.getAccommodation().getAverageRating()));

        ReservationStatus status = currentReservation.getStatus();
        if (status != null) {
            holder.status.setText(status.getStatus());
        } else {
            holder.status.setText("N/A");
        }

        holder.price.setText(String.valueOf(currentReservation.getTotalPrice()) + "$");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentReservation.getStartDate());
        holder.checkInDate.setText(formattedDate);

        String formattedDateEnd = dateFormat.format(currentReservation.getEndDate());
        holder.checkOutDate.setText(formattedDateEnd);


        int drawableResourceId = context.getResources().getIdentifier(currentReservation.getAccommodation().getPhotos().get(0).getUrl(), "drawable", context.getPackageName());
        holder.accommodationImage.setImageResource(drawableResourceId);

        if (!currentReservation.getStatus().equals(ReservationStatus.CREATED)){
            holder.btnAccept.setVisibility(View.INVISIBLE);
            holder.btnReject.setVisibility(View.INVISIBLE);
        } else {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        }

        holder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ReservationRequestAdapter", "layout  " + layout);
                CreateReservationFragment reservationFragment = new CreateReservationFragment(currentReservation);
                AppCompatActivity activity = (AppCompatActivity) context;
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(layout, reservationFragment); // Replace 'R.id.fragment_container' with the actual ID of your fragment container
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptReservation(currentReservation.getId());
            }
        });

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectReservation(currentReservation.getId());
            }
        });
    }

    private void acceptReservation(Long id){
        Call<Reservation> acceptedReservation = ClientUtils.reservationService.approveReservation(id);
        acceptedReservation.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("ReservationRequestAdapter", "Successful response: " + response.body());
                        Reservation reservation = response.body();
                        Log.d("ReservationRequestAdapter", "Accepted Reservation " + reservation.toString());

                        updateList(reservation);
                    } else {
                        Log.d("ReservationRequestAdapter", "Response body is null");
                    }
                }  else {
                    // Log error details
                    Log.d("ReservationRequestAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("ReservationRequestAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                Log.d("ReservationRequestAdapter", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void rejectReservation(Long id){
        Call<Reservation> rejectedReservation = ClientUtils.reservationService.rejectReservation(id);
        rejectedReservation.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("ReservationRequestAdapter", "Successful response: " + response.body());
                        Reservation reservation = response.body();
                        Log.d("ReservationRequestAdapter", "Rejected Reservation " + reservation.toString());

                        updateList(reservation);
                    } else {
                        Log.d("ReservationRequestAdapter", "Response body is null");
                    }
                }  else {
                    // Log error details
                    Log.d("ReservationRequestAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("ReservationRequestAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                Log.d("ReservationRequestAdapter", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void updateList(Reservation reservation) {
        if (fragment != null && fragment.isAdded()) {
            int index = findIndexOfAccommodation(reservation.getId());

            if (index != -1) {
                reservations.set(index, reservation);
                notifyDataSetChanged();
                if (fragment instanceof ReservationRequestFragment) {
                    ((ReservationRequestFragment) fragment).updateReservationList(new ArrayList<>(reservations));
                } else {
                    Log.e("RequestAdapter", "Fragment is not an instance of ReservationRequestFragment");
                }

            } else {
                Log.e("RequestAdapter", "Attempted to update a non-existing accommodation.");
            }
        }
    }




    private int findIndexOfAccommodation(Long reservationId) {
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getId().equals(reservationId)) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public void updateData(List<Reservation> updatedList) {
        reservations.clear();
        reservations.addAll(updatedList);
        notifyDataSetChanged();
    }
}