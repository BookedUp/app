package com.example.bookedup.adapters;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.bookedup.R;
import com.example.bookedup.fragments.accommodations.DetailsFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Photo;

import java.io.Serializable;
import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder>{
    ArrayList<Accommodation> items;

    //DecimalFormat formatter;

    public PopularAdapter(ArrayList<Accommodation> items) {
        this.items = items;
        //formatter = new DecimalFormat("###,###,###,###");
    }

    @NonNull
    @Override
    public PopularAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_popular, parent, false);
        return new PopularAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.ViewHolder holder, int position) {
        holder.titleTxt.setText(items.get(position).getName());
        holder.locationTxt.setText(items.get(position).getAddress().getCountry() + " " + items.get(position).getAddress().getCity());
        holder.scoreTxt.setText(""+items.get(position).getAverageRating());
        String imageUrl = items.get(position).getPhotos().get(0).getUrl();
        Glide.with(holder.itemView.getContext()).load(imageUrl).transform(new CenterCrop(), new GranularRoundedCorners(40, 40, 40, 40)).into(holder.picImg);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the Context associated with the View
                Context context = v.getContext();

                // Ensure the context is an instance of AppCompatActivity and is not null
                if (context instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) context;

                    // Create a new instance of DetailsFragment
                    DetailsFragment detailsFragment = new DetailsFragment();

                    // Pass the selected item to the fragment
                    detailsFragment.setAccommodation(items.get(position));

                    // Replace the existing fragment (HomeFragment) with the new fragment (DetailsFragment)
                    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, detailsFragment);
                    transaction.addToBackStack(null);  // Optional: Adds the transaction to the back stack
                    transaction.commit();
                } else {
                    Log.e("PopularAdapter", "Context is not an instance of AppCompatActivity");
                }
            }
        });





    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTxt, locationTxt, scoreTxt;

        ImageView picImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            picImg = itemView.findViewById(R.id.picImg);
            locationTxt = itemView.findViewById(R.id.locationTxt);
            scoreTxt = itemView.findViewById(R.id.scoreTxt);
        }
    }
}
