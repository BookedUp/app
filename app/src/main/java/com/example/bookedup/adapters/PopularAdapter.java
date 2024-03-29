package com.example.bookedup.adapters;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.bookedup.R;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.accommodations.DetailsFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.Review;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder>{
    private List<Accommodation> items;
    private SparseArray<Bitmap> bitmapArray = new SparseArray<>();
    private Map<Long, List<Bitmap>> accommodationImageMap = new HashMap<>();
    private Fragment fragment;
    int layout;
    private ArrayList<Review> reviews = new ArrayList<>();

    public PopularAdapter(Fragment fragment, List<Accommodation> items, int beforeLayout, Map<Long, List<Bitmap>> accommodationImageMap) {
        this.fragment = fragment;
        this.items = items;
        this.layout = beforeLayout;
        this.accommodationImageMap = accommodationImageMap;
    }

    @NonNull
    @Override
    public PopularAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_popular, parent, false);
        return new PopularAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Accommodation accommodation = items.get(position);
        holder.titleTxt.setText(items.get(position).getName());
        holder.locationTxt.setText(accommodation.getAddress().getCountry() + " " + accommodation.getAddress().getCity());
        holder.scoreTxt.setText(""+accommodation.getAverageRating());

        List<Bitmap> bitmaps = accommodationImageMap.get(accommodation.getId());
        Log.d("PopularAdapter", "Accommodation id " + accommodation.getId());
        Log.d("PopularAdapter", "Size " + bitmaps.size());
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
                getAccommodationComments(items.get(position), v);
            }
        });
    }


    private void getAccommodationComments(Accommodation accommodation, View v){
        Call<ArrayList<Review>> accommodationReviews = ClientUtils.reviewService.getAccommodationReviews(accommodation.getId());
        accommodationReviews.enqueue(new Callback<ArrayList<Review>>() {
            @Override
            public void onResponse(Call<ArrayList<Review>> call, Response<ArrayList<Review>> response) {
                if (response.isSuccessful()) {
                    reviews = response.body();
                    DetailsFragment detailsFragment = new DetailsFragment(accommodationImageMap.get(accommodation.getId()));
                    detailsFragment.setAccommodation(accommodation);
                    detailsFragment.setAccommodationReviews(reviews);

                    getHostComments(detailsFragment, accommodation, v);
                } else {
                    Log.d("PopularFragment", "Failed to fetch updated guest data. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Review>> call, Throwable t) {
                Log.d("PopularFragment","Failed to fetch updated guest data: "  + t.getMessage());
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
                    Log.d("PopularFragment", "Failed to fetch updated guest data. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Review>> call, Throwable t) {
                Log.d("PopularFragment","Failed to fetch updated guest data: "  + t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void openDetailsFragment(DetailsFragment detailsFragment, Context context){
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(layout, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

//    public void setBitmapForAccommodation(Long id, Bitmap bitmap) {
//        bitmapArray.put(id.intValue(), bitmap);
//        notifyDataSetChanged();
//    }

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
