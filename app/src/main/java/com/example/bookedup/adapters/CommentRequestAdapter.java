package com.example.bookedup.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.reservations.CreateReservationFragment;
import com.example.bookedup.fragments.reservations.ReservationListFragment;
import com.example.bookedup.fragments.reservations.ReservationRequestFragment;
import com.example.bookedup.fragments.reviews.ReviewRequestsFragment;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.Review;
import com.example.bookedup.model.enums.ReservationStatus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentRequestAdapter extends RecyclerView.Adapter<CommentRequestAdapter.ViewHolder>{
    private List<Review> reviews;
    private Context context;
    private int layout;
    private Fragment fragment;
    private Map<Long, Bitmap> userImages = new HashMap<>();

    public CommentRequestAdapter(List<Review> reviews, Context context, int before_layout, Fragment fragment, Map<Long,Bitmap> userImages) {
        this.reviews = reviews;
        this.context = context;
        this.layout = before_layout;
        this.fragment = fragment;
        this.userImages = userImages;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView guestImg;
        TextView guestInfo, comment, commentStatus, who, host, accommodation;
        RatingBar guestRating;
        Button acceptBtn, rejectBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            guestImg = itemView.findViewById(R.id.guestImg);
            guestInfo = itemView.findViewById(R.id.guestInfo);
            guestRating = itemView.findViewById(R.id.guestRating);
            comment = itemView.findViewById(R.id.comment);
            commentStatus = itemView.findViewById(R.id.commentStatus);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            who = itemView.findViewById(R.id.whoIsCommented);
            host = itemView.findViewById(R.id.host);
            accommodation = itemView.findViewById(R.id.accommodation);
        }
    }

    @NonNull
    @Override
    public CommentRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_review_request, parent, false);
        return new CommentRequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRequestAdapter.ViewHolder holder, int position) {
        Review currentReview = reviews.get(position);

        holder.guestInfo.setText(currentReview.getGuest().getFirstName() + " " + currentReview.getGuest().getLastName());
        holder.guestRating.setRating(currentReview.getReview());
        holder.comment.setText(String.valueOf(currentReview.getComment()));
        holder.who.setText(currentReview.getType().toString());
        holder.host.setText("Host: " + currentReview.getHost().getFirstName() + " " + currentReview.getHost().getLastName());
        holder.accommodation.setText("Accommodation: " + currentReview.getAccommodation());

        Bitmap bitmap = userImages.get(currentReview.getGuest().getId());
        if (bitmap != null)
            holder.guestImg.setImageBitmap(bitmap);
        else {
            holder.guestImg.setImageResource(R.drawable.default_hotel_img);
        }
        if (currentReview.getApproved()){
            holder.commentStatus.setText("APPROVED");
            holder.commentStatus.setTextColor(Color.GREEN);
            holder.acceptBtn.setEnabled(false);
        } else {
            holder.commentStatus.setText("WAITING FOR APPROVAL");
            holder.commentStatus.setTextColor(Color.RED);
            holder.acceptBtn.setEnabled(true);
        }


        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptReview(currentReview.getId());
            }
        });

//        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rejectReview(currentReview);
//            }
//        });
    }

    private void acceptReview(Long id){
        Call<Review> acceptedReview = ClientUtils.reviewService.approveReview(id);
        acceptedReview.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("CommentRequestAdapter", "Successful response: " + response.body());
                        Review review = response.body();
                        Log.d("CommentRequestAdapter", "Accepted Reservation " + review.toString());

                        updateList(review, "accept");
                    } else {
                        Log.d("CommentRequestAdapter", "Response body is null");
                    }
                }  else {
                    // Log error details
                    Log.d("CommentRequestAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("CommentRequestAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Log.d("CommentRequestAdapter", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void rejectReview(Review review){
        Call<Review> rejectedReview = ClientUtils.reviewService.deleteReview(review.getId());
        rejectedReview.enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful()) {
                    updateList(review, "delete");
                }  else {
                    // Log error details
                    Log.d("CommentRequestAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("CommentRequestAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Log.d("CommentRequestAdapter", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void updateList(Review review, String action) {
        int index = findIndexOfAccommodation(review.getId());

        if(action.equals("delete")){
            if (index != -1) {
                reviews.remove(index);
                notifyDataSetChanged();
            } else {
                Log.e("CommentRequestAdapter", "Attempted to remove a non-existing reservation.");
            }
        } else {
            if (index != -1) {
                reviews.set(index, review);
                notifyDataSetChanged();
            } else {
                Log.e("CommentRequestAdapter", "Attempted to update a non-existing accommodation.");
            }
        }

        if (fragment instanceof ReviewRequestsFragment) {
            ((ReviewRequestsFragment) fragment).updateReviewList(new ArrayList<>(reviews));
        } else {
            Log.e("CommentRequestAdapter", "Fragment is not an instance of ReservationRequestFragment");
        }
    }


    private int findIndexOfAccommodation(Long reservationId) {
        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getId().equals(reservationId)) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void updateData(List<Review> updatedList) {
        reviews.clear();
        reviews.addAll(updatedList);
        notifyDataSetChanged();
    }
}
