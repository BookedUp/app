package com.example.bookedup.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.accommodations.DetailsFragment;
import com.example.bookedup.fragments.accommodations.SearchFilterFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Destination;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularDestinationAdapter extends RecyclerView.Adapter<PopularDestinationAdapter.ViewHolder> {

    private List<Destination> destinationList;

    private List<Accommodation> results = new ArrayList<Accommodation>();
    private String whereToGo;

    private int layout;

    public PopularDestinationAdapter(List<Destination> destinationList, int targetLayout) {
        this.destinationList = destinationList;
        this.layout = targetLayout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_destinations, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Destination destination = destinationList.get(position);
        holder.bindData(destination);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whereToGo = destination.getDestinationName();
                openSearchFilterFragment(v, whereToGo);

            }
        });


    }

    private void openSearchFilterFragment(View v, String whereToGo) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date startDate = new Date();
        Date endDate = new Date();
        startDate.setHours(13);
        endDate.setHours(13);


        List<Object> amenities = new ArrayList<>();
        Integer guestsNumber = 0;
        Log.d("PopularDestinationAdapter", "StartDate " + startDate);
        Log.d("PopularDestinationAdapter", "EndDate " + endDate);
        Log.d("PopularDestinationAdapter", "Location " + whereToGo);
        Log.d("PopularDestinationAdapter", "GuestsNumber " + guestsNumber);

        Call<ArrayList<Accommodation>> searchedResults = ClientUtils.accommodationService.searchAccommodations(
                whereToGo,
                guestsNumber,
                dateFormat.format(startDate),
                dateFormat.format(endDate),
                amenities,
                0.0,
                0.0,
                0.0,
                "null",
                ""
        );

        Log.d("PopularDestinationAdapter", "Prosaoooooo" + searchedResults);


        searchedResults.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("PopularDestinationAdapter", "Successful response: " + response.body());
                        results = response.body();
                        for (Accommodation accommodation : results) {
                            Log.d("PopularDestinationAdapter", "Accommodation: " + accommodation);
                        }

                        Context context = v.getContext();
                        if (context instanceof AppCompatActivity) {
                            AppCompatActivity activity = (AppCompatActivity) context;
                            SearchFilterFragment searchFilterFragment = new SearchFilterFragment();

                            Bundle bundle = new Bundle();
                            bundle.putString("whereToGo", whereToGo);
                            bundle.putInt("guestsNumber", guestsNumber);
                            bundle.putString("checkIn", dateFormat.format(startDate));
                            bundle.putString("checkOut", dateFormat.format(endDate));
                            String resultsJson = new Gson().toJson(results);
                            bundle.putString("resultsJson", resultsJson);

                            searchFilterFragment.setArguments(bundle);

                            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                            transaction.replace(layout, searchFilterFragment);
                            transaction.addToBackStack(null);  // Optional: Adds the transaction to the back stack
                            transaction.commit();
                        }
                    } else {
                        Log.d("PopularDestinationAdapter", "Response body is null");
                    }
                }  else {
                    // Log error details
                    Log.d("PopularDestinationAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("PopularDestinationAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.d("PopularDestinationAdapter", "GRESKA response: " + response.body());
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
                Log.d("PopularDestinationAdapter", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    @Override
    public int getItemCount() {
        return destinationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView destinationName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.picImg);
            destinationName = itemView.findViewById(R.id.destinationName);
        }

        public void bindData(Destination destination) {
            imageView.setImageResource(destination.getImageResource());
            destinationName.setText(destination.getDestinationName());
        }
    }
}

