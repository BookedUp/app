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
import com.example.bookedup.model.enums.AccommodationStatus;

import java.util.List;

public class AccommodationAdapter extends RecyclerView.Adapter<AccommodationAdapter.ViewHolder> {

    private List<Accommodation> accommodations;
    private Context context;

    public AccommodationAdapter(List<Accommodation> accommodations, Context context) {
        this.accommodations = accommodations;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView accommodationImage;
        TextView title, averageRating, status, address, price, priceType;
        Button btnViewDetails, btnEditDetails;

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
        }
    }

    @NonNull
    @Override
    public AccommodationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_my_accommodation, parent, false);
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
            // Postavite neku podrazumevanu vrednost ili obradite ovu situaciju kako vam odgovara
            holder.status.setText("N/A");
        }

        holder.address.setText(currentRequest.getAddress().getStreetAndNumber() + ", " +
                currentRequest.getAddress().getCity() + ", " +
                currentRequest.getAddress().getCountry());
        holder.price.setText(String.valueOf(currentRequest.getPrice()) + "$");
        holder.priceType.setText("/"+currentRequest.getPriceType().getPriceType());

        // Postavljanje slike
        int drawableResourceId = context.getResources().getIdentifier(currentRequest.getPhotos().get(0).getUrl(), "drawable", context.getPackageName());
        holder.accommodationImage.setImageResource(drawableResourceId);

        // Postavljanje listener-a za dugmad
        holder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementacija za View Details
            }
        });

        holder.btnEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementacija za View Details
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