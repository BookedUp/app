package com.example.bookedup.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.activities.SplashScreen;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.accommodations.AccommodationRequestFragment;
import com.example.bookedup.fragments.accommodations.DetailsFragment;
import com.example.bookedup.fragments.reservations.CreateReservationFragment;
import com.example.bookedup.fragments.reservations.ReservationListFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Notification;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.User;
import com.example.bookedup.model.enums.AccommodationStatus;
import com.example.bookedup.model.enums.NotificationType;
import com.example.bookedup.model.enums.ReservationStatus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntBinaryOperator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private List<Reservation> reservations;
    private Context context;
    private Fragment fragment;
    private int layout;
    private Map<Long, List<Bitmap>> accommodationImages = new HashMap<>();


    public ReservationAdapter(List<Reservation> reservations, Context context, int before_layout, Fragment fragment, Map<Long, List<Bitmap>> accommodationImages) {
        this.reservations = reservations;
        this.context = context;
        this.fragment = fragment;
        this.layout = before_layout;
        this.accommodationImages = accommodationImages;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView accommodationImage;
        TextView title, averageRating, status, price, checkInDate, checkOutDate;
        Button btnViewDetails, btnManageRequest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            accommodationImage = itemView.findViewById(R.id.res_imageHotel);
            title = itemView.findViewById(R.id.res_Title);
            averageRating = itemView.findViewById(R.id.res_averageRating);
            status = itemView.findViewById(R.id.res_status);
            price = itemView.findViewById(R.id.res_price);
            checkInDate = itemView.findViewById(R.id.res_checkInDate);
            checkOutDate = itemView.findViewById(R.id.res_checkOutDate);
            btnViewDetails = itemView.findViewById(R.id.res_btnViewDetails);
            btnManageRequest = itemView.findViewById(R.id.manageRequest);
        }
    }

    @NonNull
    @Override
    public ReservationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_reservation, parent, false);
        return new ReservationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationAdapter.ViewHolder holder, int position) {
        Reservation currentReservation = reservations.get(position);

        holder.title.setText(currentReservation.getAccommodation().getName());
        holder.averageRating.setText(String.valueOf(currentReservation.getAccommodation().getAverageRating()));

        ReservationStatus status = currentReservation.getStatus();
        if (status != null) {
            holder.status.setText(status.getStatus());
        } else {
            holder.status.setText("N/A");
        }

        holder.price.setText(String.valueOf(currentReservation.getTotalPrice()) + "$");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(currentReservation.getStartDate());
        holder.checkInDate.setText(formattedDate);

        String formattedDateEnd = sdf.format(currentReservation.getEndDate());
        holder.checkOutDate.setText(formattedDateEnd);

        List<Bitmap> bitmaps = accommodationImages.get(currentReservation.getAccommodation().getId());
        if (!bitmaps.isEmpty()) {
            if (bitmaps.get(0) != null) {
                holder.accommodationImage.setImageBitmap(bitmaps.get(0));
            } else {
                holder.accommodationImage.setImageResource(R.drawable.default_hotel_img);
            }
        }
        if (currentReservation.getStatus().equals(ReservationStatus.CREATED)){
            holder.btnManageRequest.setVisibility(View.VISIBLE);
            holder.btnManageRequest.setText("Delete");
        } else if (currentReservation.getStatus().equals(ReservationStatus.ACCEPTED)){
            holder.btnManageRequest.setVisibility(View.VISIBLE);
            holder.btnManageRequest.setText("Cancel");
        } else {
            holder.btnManageRequest.setVisibility(View.INVISIBLE);
        }


        holder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateReservationFragment reservationFragment = new CreateReservationFragment(currentReservation, accommodationImages.get(currentReservation.getAccommodation().getId()));
                AppCompatActivity activity = (AppCompatActivity) context;
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(layout, reservationFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });



        holder.btnManageRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btnManageRequest.getText().toString().equals("Delete")){
                    showConfirmationDialog("delete", currentReservation);
                } else if (holder.btnManageRequest.getText().toString().equals("Cancel")){
                    showConfirmationDialog("cancel", currentReservation);
                }
            }
        });


    }

    private void showConfirmationDialog(String action, Reservation reservation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(action + " confirmation")
                .setMessage("Are you sure you want to " + action + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (action.equals("delete")){
                            deleteReservation(reservation, action);

                        } else if (action.equals("cancel")){
                            cancelReservation(reservation, action);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "No," do nothing or handle accordingly
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
        }


    //FIZICKO BRISANJE
    private void deleteReservation(Reservation reservation, String action){
        Call<Reservation> deletedReservation = ClientUtils.reservationService.deleteReservation(reservation.getId());
        deletedReservation.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Deletion successful", Toast.LENGTH_SHORT).show();

                    updateListWithReservation(reservation, action);
                } else {
                    Log.d("ReservationAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("ReservationAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                Log.d("ReservationAdapter", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void cancelReservation(Reservation reservation, String action){
        Call<Reservation> cancelledReservation = ClientUtils.reservationService.cancelReservation(reservation.getId());
        cancelledReservation.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful()) {
                    Reservation changedReservation = response.body();
                    Toast.makeText(context, "Cancellation successful!", Toast.LENGTH_SHORT).show();
                    createNotification(changedReservation);
                    updateListWithReservation(changedReservation, action);
                } else {
                    Log.d("ReservationAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("ReservationAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                Log.d("ReservationAdapter", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void createNotification(Reservation newReservation) {
        Notification notification = new Notification(null, newReservation.getAccommodation().getHost(), "Cancelled Reservation", "Bad news.. Your reservation for accommodation " + newReservation.getAccommodation().getName() + " has been cancelled. At least you shine today :)", new Date(), NotificationType.RESERVATION_CANCELED, true);
        Call<Notification> createdNotification = ClientUtils.notificationService.createNotification(notification);
        createdNotification.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ReservationAdapter", "Successful response: " + response.body());
                    Notification newNotification = response.body();
                    Log.d("ReservationAdapter", "Notification : " + newNotification.toString());
                } else {
                    // Log error details
                    Log.d("ReservationAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("ReservationAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Log.d("ReservationAdapter", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void updateListWithReservation(Reservation reservation, String action) {
        int index = findIndexOfAccommodation(reservation.getId());

        if(action.equals("delete")){
            if (index != -1) {
                reservations.remove(index);
                notifyDataSetChanged();
            } else {
                Log.e("ReservationAdapter", "Attempted to remove a non-existing reservation.");
            }
        } else {
            if (index != -1) {
                reservations.set(index, reservation);
                notifyDataSetChanged();
            } else {
                Log.e("ReservationAdapter", "Attempted to update a non-existing accommodation.");
            }
        }

        if (fragment instanceof ReservationListFragment) {
            ((ReservationListFragment) fragment).updateReservationList(new ArrayList<>(reservations));
        } else {
            Log.e("ReservationAdapter", "Fragment is not an instance of ReservationRequestFragment");
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