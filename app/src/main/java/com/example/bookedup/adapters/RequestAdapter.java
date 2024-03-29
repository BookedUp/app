package com.example.bookedup.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.accommodations.AccommodationRequestFragment;
import com.example.bookedup.fragments.accommodations.DetailsFragment;
import com.example.bookedup.fragments.home.HomeFragment;
import com.example.bookedup.fragments.reservations.ReservationRequestFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Review;
import com.example.bookedup.model.enums.AccommodationStatus;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private List<Accommodation> accommodationRequests;
    private List<Accommodation> updatedAllAccommodations = new ArrayList<Accommodation>();
    private ArrayList<Review> accommodationReviews = new ArrayList<Review>();
    private Context context;
    private Fragment fragment;
    private Map<Long, List<Bitmap>> accommodationImages = new HashMap<>();
    private Bitmap detailsPicture;

    public RequestAdapter(List<Accommodation> accommodationRequests, Context context, Fragment fragment, Map<Long, List<Bitmap>> accommodationImages) {
        this.accommodationRequests = accommodationRequests;
        this.context = context;
        this.fragment = fragment;
        this.accommodationImages = accommodationImages;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Accommodation currentRequest = accommodationRequests.get(position);

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
        holder.priceType.setText("/" +currentRequest.getPriceType().getPriceType());


        if (currentRequest.getStatus().equals(AccommodationStatus.ACTIVE) || accommodationRequests.get(position).getStatus().equals(AccommodationStatus.REJECTED)){
            holder.btnAccept.setVisibility(View.INVISIBLE);
            holder.btnReject.setVisibility(View.INVISIBLE);
        }else {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        }

        List<Bitmap> bitmaps = accommodationImages.get(currentRequest.getId());
        if (!bitmaps.isEmpty())
            if (bitmaps.get(0) != null) {
                holder.accommodationImage.setImageBitmap(bitmaps.get(0));
            }
        else {
            holder.accommodationImage.setImageResource(R.drawable.default_hotel_img);
        }


        holder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccommodationComments(currentRequest, v);
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveAccommodation(currentRequest.getId());
            }
        });

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectAccommodation(currentRequest.getId());
            }
        });
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
                    Log.d("RequestAdapter", "Failed to fetch updated guest data. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Review>> call, Throwable t) {
                Log.d("RequestAdapter", "Failed to fetch updated guest data: " + t.getMessage());
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
                    detailsFragment.setHostReviews(hostReviews);

                    openDetailsFragment(detailsFragment);
                } else {
                    Log.d("RequestAdapter", "Failed to fetch updated guest data. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Review>> call, Throwable t) {
                Log.d("RequestAdapter","Failed to fetch updated guest data: "  + t.getMessage());
            }
        });
    }

    private void openDetailsFragment(DetailsFragment detailsFragment){
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layoutAdmin, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void approveAccommodation(Long id){
        Call<Accommodation> acceptedAccommodation = ClientUtils.accommodationService.approveAccommodation(id);
        acceptedAccommodation.enqueue(new Callback<Accommodation>() {
            @Override
            public void onResponse(Call<Accommodation> call, Response<Accommodation> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("RequestAdapter", "Successful response: " + response.body());
                        Accommodation accommodation = response.body();
                        Log.d("RequestAdapter", "Accepted Accommodation " + accommodation.toString());

                        updateListWithAccommodation(accommodation);
                    } else {
                        Log.d("RequestAdapter", "Response body is null");
                    }
                }  else {
                    // Log error details
                    Log.d("RequestAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("RequestAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Accommodation> call, Throwable t) {
                Log.d("RequestAdapter", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    public void rejectAccommodation(Long id){
        Call<Accommodation> rejectedAccommodation = ClientUtils.accommodationService.rejectAccommodation(id);
        rejectedAccommodation.enqueue(new Callback<Accommodation>() {
            @Override
            public void onResponse(Call<Accommodation> call, Response<Accommodation> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("RequestAdapter", "Successful response: " + response.body());
                        Accommodation accommodation = response.body();
                        Log.d("RequestAdapter", "Rejected Accommodation " + accommodation.toString());

                        updateListWithAccommodation(accommodation);
                    } else {
                        Log.d("RequestAdapter", "Response body is null");
                    }
                }  else {
                    // Log error details
                    Log.d("RequestAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("RequestAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Accommodation> call, Throwable t) {
                Log.d("RequestAdapter", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void updateListWithAccommodation(Accommodation accommodation) {
        int index = findIndexOfAccommodation(accommodation.getId());

        if (index != -1) {
            accommodationRequests.set(index, accommodation);
            notifyDataSetChanged();
            if (fragment instanceof AccommodationRequestFragment) {
                ((AccommodationRequestFragment) fragment).updateAccommodationList(new ArrayList<>(accommodationRequests));
            } else {
                Log.e("RequestAdapter", "Fragment is not an instance of ReservationRequestFragment");
            }
        } else {
            Log.e("RequestAdapter", "Attempted to update a non-existing accommodation.");
        }
    }


    private int findIndexOfAccommodation(Long accommodationId) {
        for (int i = 0; i < accommodationRequests.size(); i++) {
            if (accommodationRequests.get(i).getId().equals(accommodationId)) {
                return i;
            }
        }
        return -1;
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