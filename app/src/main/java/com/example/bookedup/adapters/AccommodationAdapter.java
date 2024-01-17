package com.example.bookedup.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.accommodations.DetailsFragment;
import com.example.bookedup.fragments.accommodations.SingleStatisticFragment;
import com.example.bookedup.fragments.accommodations.UpdateAccommodationFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.enums.AccommodationStatus;
import com.example.bookedup.model.enums.ReservationStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccommodationAdapter extends RecyclerView.Adapter<AccommodationAdapter.ViewHolder> {

    private List<Accommodation> accommodations;
    private Context context;
    private List<Reservation> reservations = new ArrayList<>();

    public AccommodationAdapter(List<Accommodation> accommodations, Context context) {
        this.accommodations = accommodations;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView accommodationImage;
        TextView title, averageRating, status, address, price, priceType;
        Button btnViewDetails, btnEditDetails, btnGetStatistic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            accommodationImage = itemView.findViewById(R.id.myAcc_imageHotel);
            title = itemView.findViewById(R.id.myAcc_Title);
            averageRating = itemView.findViewById(R.id.myAcc_averageRating);
            status = itemView.findViewById(R.id.myAcc_status);
            address = itemView.findViewById(R.id.myAcc_address);
            price = itemView.findViewById(R.id.myAcc_price);
            priceType = itemView.findViewById(R.id.myAcc_priceType);

            btnViewDetails = itemView.findViewById(R.id.myAcc_btnViewDetails);
            btnEditDetails = itemView.findViewById(R.id.myAcc_btnEditDetails);
            btnGetStatistic = itemView.findViewById(R.id.getStatisticBtn);

        }
    }

    @NonNull
    @Override
    public AccommodationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_my_accommodation, parent, false);
        getHostsCompletedReservations();
        return new AccommodationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccommodationAdapter.ViewHolder holder, int position) {
        Accommodation currentRequest = accommodations.get(position);

        holder.title.setText(currentRequest.getName());
        holder.averageRating.setText(String.valueOf(currentRequest.getAverageRating()));

        AccommodationStatus status = currentRequest.getStatus();
        if (status != null) {
            holder.status.setText(status.getStatus());
        } else {
            holder.status.setText("N/A");
        }

        holder.address.setText(currentRequest.getAddress().getStreetAndNumber() + ", " +
                currentRequest.getAddress().getCity() + ", " +
                currentRequest.getAddress().getCountry());
        holder.price.setText(String.valueOf(currentRequest.getPrice()) + "$");
        holder.priceType.setText("/" + currentRequest.getPriceType().getPriceType());

        // Postavljanje slike
        //MENJACE SE
        if (!currentRequest.getPhotos().isEmpty()){
            int drawableResourceId = context.getResources().getIdentifier(currentRequest.getPhotos().get(0).getUrl(), "drawable", context.getPackageName());
            holder.accommodationImage.setImageResource(drawableResourceId);
        } else {
            holder.accommodationImage.setImageResource(R.drawable.default_hotel_img);
        }


        holder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsFragment detailsFragment = new DetailsFragment();
                detailsFragment.setAccommodation(currentRequest);
                AppCompatActivity activity = (AppCompatActivity) context;
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame_layoutHost, detailsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        holder.btnEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAccommodationFragment updateAccommodationFragment = new UpdateAccommodationFragment(currentRequest);
                AppCompatActivity activity = (AppCompatActivity) context;
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame_layoutHost, updateAccommodationFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        holder.btnGetStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AccommodationAdapter", "Res SIZE " + reservations.size());
                for (Reservation res : reservations) {
                    Log.d("AccommodationAdapter", "Res " + res.toString());
                }
                SingleStatisticFragment singleStatisticFragment = new SingleStatisticFragment(currentRequest, reservations);
                AppCompatActivity activity = (AppCompatActivity) context;
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame_layoutHost, singleStatisticFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    private void getHostsCompletedReservations(){
        Call<ArrayList<Reservation>> hostsReservations = ClientUtils.reservationService.getReservationsByStatusAndHostId(LoginScreen.loggedHost.getId(), ReservationStatus.COMPLETED);
        hostsReservations.enqueue(new Callback<ArrayList<Reservation>>() {
            @Override
            public void onResponse(Call<ArrayList<Reservation>> call, Response<ArrayList<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AccommodationAdapter", "Successful response: " + response.body());
                    reservations = response.body();
                } else {
                    // Log error details
                    Log.d("AccommodationAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("AccommodationAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Reservation>> call, Throwable t) {
                Log.d("AccommodationAdapter", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }


    @Override
    public int getItemCount() {
        return accommodations.size();
    }

    public void updateData(List<Accommodation> updatedList) {
        accommodations.clear();
        accommodations.addAll(updatedList);
        notifyDataSetChanged();
    }
}