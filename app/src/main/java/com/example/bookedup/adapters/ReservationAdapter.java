package com.example.bookedup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.enums.AccommodationStatus;
import com.example.bookedup.model.enums.ReservationStatus;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private List<Reservation> reservations;
    private Context context;

    public ReservationAdapter(List<Reservation> reservations, Context context) {
        this.reservations = reservations;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView accommodationImage;
        TextView title, averageRating, status, price, checkInDate, checkOutDate;
        Button btnViewDetails;

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

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentReservation.getStartDate());
        holder.checkInDate.setText(formattedDate);

        String formattedDateEnd = dateFormat.format(currentReservation.getEndDate());
        holder.checkOutDate.setText(formattedDateEnd);


        int drawableResourceId = context.getResources().getIdentifier(currentReservation.getAccommodation().getPhotos().get(0).getUrl(), "drawable", context.getPackageName());
        holder.accommodationImage.setImageResource(drawableResourceId);

        holder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementacija za View Details
            }
        });


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