package com.example.bookedup.adapters;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.accommodations.DetailsFragment;
import com.example.bookedup.fragments.accommodations.UpdateAccommodationFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.Review;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAccommodationAdapter extends RecyclerView.Adapter<SearchAccommodationAdapter.ViewHolder>{
    private ArrayList<Accommodation> items;
    private ArrayList<Review> accommodationReviews = new ArrayList<Review>();
    private String checkIn, checkOut;
    private Integer guestsNumber, targetLayout;
    private Map<Long, List<Bitmap>> accommodationImages = new HashMap<>();

    public SearchAccommodationAdapter(ArrayList<Accommodation> items, int targetLayout, String checkIn, String checkOut, Integer guestsNumber, Map<Long, List<Bitmap>> accommodationImages) {
        this.items = items;
        this.targetLayout = targetLayout;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guestsNumber = guestsNumber;
        this.accommodationImages = accommodationImages;
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
            Accommodation currentAccommodation = items.get(position);
            holder.titleTxt.setText(currentAccommodation.getName());
            holder.locationTxt.setText(currentAccommodation.getAddress().getCountry() + " " + items.get(position).getAddress().getCity());
            holder.averageRatingTxt.setText(String.valueOf(currentAccommodation.getAverageRating()));
            holder.typeTxt.setText(currentAccommodation.getType().name());
            holder.totalPriceTxt.setText("Total: " + String.valueOf(currentAccommodation.getTotalPrice()));
            if (String.valueOf(currentAccommodation.getPrice()) != null) {
                holder.priceTxt.setText(String.valueOf(currentAccommodation.getPrice()) + "$");
            } else {
                holder.priceTxt.setText("");
            }
            holder.priceTypeTxt.setText("/" + String.valueOf(currentAccommodation.getPriceType().getPriceType()));

            List<Bitmap> bitmaps = accommodationImages.get(currentAccommodation.getId());
            Log.d("SearchAccommodationAdapter", "Accommodation id " + currentAccommodation.getId());
            Log.d("SearchAccommodationAdapter", "Size " + bitmaps.size());
            if (!bitmaps.isEmpty())
                if (bitmaps.get(0) != null) {
                    holder.picImg.setImageBitmap(bitmaps.get(0));
                }
                else {
                    holder.picImg.setImageResource(R.drawable.default_hotel_img);
                }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getAccommodationComments(currentAccommodation, v);
                }
            });
        }
    }
    private void getAccommodationComments(Accommodation accommodation, View v) {
        Call<ArrayList<Review>> accommodationReviewsCall = ClientUtils.reviewService.getAccommodationReviews(accommodation.getId());
        accommodationReviewsCall.enqueue(new Callback<ArrayList<Review>>() {
            @Override
            public void onResponse(Call<ArrayList<Review>> call, Response<ArrayList<Review>> response) {
                if (response.isSuccessful()) {
                    accommodationReviews = response.body();  // Update the global variable
                    DetailsFragment detailsFragment = new DetailsFragment(accommodationImages.get(accommodation.getId()));
                    detailsFragment.setAccommodation(accommodation);
                    detailsFragment.setAccommodationReviews(accommodationReviews);

                    getHostComments(detailsFragment, accommodation, v);
                } else {
                    Log.d("SearchAccommodationAdapter", "Failed to fetch updated guest data. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Review>> call, Throwable t) {
                Log.d("SearchAccommodationAdapter", "Failed to fetch updated guest data: " + t.getMessage());
            }
        });
    }

    private void getHostComments(DetailsFragment detailsFragment, Accommodation accommodation, View v){
        Call<ArrayList<Review>> hostReviews = ClientUtils.reviewService.getHostReviewsByHostId(accommodation.getHost().getId());
        hostReviews.enqueue(new Callback<ArrayList<Review>>() {
            @Override
            public void onResponse(Call<ArrayList<Review>> call, Response<ArrayList<Review>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Review> hostReviews = response.body();
                    Context context = v.getContext();
                    detailsFragment.setHostReviews(hostReviews);

                    openDetailsFragment(detailsFragment,context);
                } else {
                    Log.d("SearchAccommodationAdapter", "Failed to fetch updated guest data. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Review>> call, Throwable t) {
                Log.d("SearchAccommodationAdapter","Failed to fetch updated guest data: "  + t.getMessage());
            }
        });
    }

    private void openDetailsFragment(DetailsFragment detailsFragment, Context context){
        Bundle bundle = new Bundle();
        bundle.putString("checkIn", checkIn);
        bundle.putString("checkOut", checkOut);
        bundle.putInt("guestsNumber", guestsNumber);
        detailsFragment.setArguments(bundle);
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(targetLayout, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
