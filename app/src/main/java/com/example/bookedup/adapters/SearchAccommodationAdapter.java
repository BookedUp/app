package com.example.bookedup.adapters;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.bookedup.R;
import com.example.bookedup.fragments.accommodations.DetailsFragment;
import com.example.bookedup.fragments.accommodations.UpdateAccommodationFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Photo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchAccommodationAdapter extends RecyclerView.Adapter<SearchAccommodationAdapter.ViewHolder>{
    ArrayList<Accommodation> items;

    int targetLayout;

    //DecimalFormat formatter;

    public SearchAccommodationAdapter(ArrayList<Accommodation> items, int targetLayout) {
        this.items = items;
        this.targetLayout = targetLayout;
    }

    @NonNull
    @Override
    public SearchAccommodationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_acc_card, parent, false);
        return new SearchAccommodationAdapter.ViewHolder(inflate);
    }



    @Override
    public void onBindViewHolder(@NonNull SearchAccommodationAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (items != null && !items.isEmpty() && position < items.size()) {
            holder.titleTxt.setText(items.get(position).getName());
            holder.locationTxt.setText(items.get(position).getAddress().getCountry() + " " + items.get(position).getAddress().getCity());
            holder.averageRatingTxt.setText(String.valueOf(items.get(position).getAverageRating()));
            holder.typeTxt.setText(items.get(position).getType().name());
            holder.totalPriceTxt.setText("Total: " + String.valueOf(items.get(position).getTotalPrice()));
            // Check if the price is available before setting it
            if (String.valueOf(items.get(position).getPrice()) != null) {
                holder.priceTxt.setText(String.valueOf(items.get(position).getPrice()));
            } else {
                holder.priceTxt.setText(""); // or set a default value
            }

            holder.priceTypeTxt.setText(String.valueOf(items.get(position).getPriceType().getPriceType()));

            List<Photo> photos = items.get(position).getPhotos();

            String imageUrl = items.get(position).getPhotos().get(0).getUrl();
            Glide.with(holder.itemView.getContext()).load(imageUrl).transform(new CenterCrop(), new GranularRoundedCorners(40, 40, 40, 40)).into(holder.picImg);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DetailsFragment detailsFragment = new DetailsFragment();
                    detailsFragment.setAccommodation(items.get(position));
                    FragmentTransaction transaction = ((AppCompatActivity) v.getContext())
                            .getSupportFragmentManager().beginTransaction();
                    transaction.replace(targetLayout, detailsFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTxt, locationTxt, averageRatingTxt, typeTxt, priceTxt, priceTypeTxt, totalPriceTxt;

        ImageView picImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.accTitle);
            picImg = itemView.findViewById(R.id.accImage);
            locationTxt = itemView.findViewById(R.id.accAddress);
            averageRatingTxt = itemView.findViewById(R.id.accAverageRating);
            typeTxt = itemView.findViewById(R.id.accType);
            priceTxt = itemView.findViewById(R.id.accPrice);
            priceTypeTxt = itemView.findViewById(R.id.accPriceType);
            totalPriceTxt = itemView.findViewById(R.id.totalPriceTxt);
        }
    }
}
