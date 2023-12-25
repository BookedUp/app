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

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private List<Accommodation> accommodationRequests;
    private Context context;

    public RequestAdapter(List<Accommodation> accommodationRequests, Context context) {
        this.accommodationRequests = accommodationRequests;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView accommodationImage;
        TextView title, averageRating, status, address, price, priceType;
        Button btnViewDetails, btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            accommodationImage = itemView.findViewById(R.id.request_imageHotel);
            title = itemView.findViewById(R.id.request_Title);
            averageRating = itemView.findViewById(R.id.request_averageRating);
            status = itemView.findViewById(R.id.request_status);
            address = itemView.findViewById(R.id.request_address);
            price = itemView.findViewById(R.id.request_price);
            priceType = itemView.findViewById(R.id.request_priceType);

            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_accommodation_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Accommodation currentRequest = accommodationRequests.get(position);

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
        holder.priceType.setText("/" +currentRequest.getPriceType().getPriceType());

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

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementacija za Accept
            }
        });

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementacija za Reject
            }
        });
    }


    @Override
    public int getItemCount() {
        return accommodationRequests.size();
    }

    public void updateData(List<Accommodation> updatedList) {
        accommodationRequests.clear();
        accommodationRequests.addAll(updatedList);
        notifyDataSetChanged();
    }
}