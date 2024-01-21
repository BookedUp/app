package com.example.bookedup.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.reservations.ReservationRequestFragment;
import com.example.bookedup.model.Notification;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.Review;
import com.example.bookedup.model.ReviewReport;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewReportAdapter extends RecyclerView.Adapter<ReviewReportAdapter.ViewHolder>{

    private List<Review> reviews;
    private Fragment fragment;
    private int layout;
    private Map<Long, Bitmap> usersImages = new HashMap<>();

    private Dialog reasonsDialog;
    private Context context;

    private ArrayList<String> reasonsList = new ArrayList<>();

    public ReviewReportAdapter(Fragment fragment, List<Review> reviews, int beforeLayout, Map<Long, Bitmap> usersImages) {
        this.fragment = fragment;
        this.reviews = reviews;
        this.layout = beforeLayout;
        this.usersImages = usersImages;
    }

    @NonNull
    @Override
    public ReviewReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_review_report, parent, false);
        return new ReviewReportAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewReportAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Review currentReview = reviews.get(position);
        Bitmap image = usersImages.get(currentReview.getGuest().getId());
        if (image != null){
            holder.guestImg.setImageBitmap(image);
        } else {
            holder.guestImg.setImageResource(R.drawable.default_hotel_img);
        }

        holder.guestInfo.setText(currentReview.getGuest().getFirstName() + " " + currentReview.getGuest().getLastName());
        holder.guestRating.setRating(currentReview.getReview());
        holder.comment.setText(currentReview.getComment());
        holder.who.setText(currentReview.getType().toString());
        holder.host.setText("Host: " + currentReview.getHost().getFirstName() + " " + currentReview.getHost().getLastName());
        holder.accommodation.setText("Accommodation: " + currentReview.getAccommodation());

        
        holder.reasonsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReportReasons(currentReview);
            }
        });

    }

    private void showReasonsDialog(Review currentReview, ArrayList<String> reasons) {
        reasonsDialog = new Dialog(context);
        reasonsDialog.setContentView(R.layout.reported_review_reasons_popup);
        Button deleteBtn = reasonsDialog.findViewById(R.id.deleteButton);
        TextView reportReasonsTxt = reasonsDialog.findViewById(R.id.reasonsInput);
        reasonsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        reasonsDialog.show();

        StringBuilder reasonsText = new StringBuilder();

        for (String reason : reasons) {
            reasonsText.append(reason).append("\n");
        }

        reportReasonsTxt.setText(reasonsText.toString());

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Call<Review> deletedReview = ClientUtils.reviewService.deleteReview(currentReview.getId());
                deletedReview.enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        if (response.isSuccessful()) {
                            Review deleted = response.body();
                            reasonsDialog.dismiss();
                            updateReviewsList(currentReview);
                            Toast.makeText(context, "Comment deleted!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("ReviewReportAdapter", "Unsuccessful response: " + response.code());
                            try {
                                Log.d("ReviewReportAdapter", "Error Body: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {
                        Log.d("ReviewReportAdapter", t.getMessage() != null ? t.getMessage() : "error");
                    }
                });
            }
        });

    }

    private void updateReviewsList(Review review) {
        if (fragment != null && fragment.isAdded()) {
            int index = findIndexOfReviews(review.getId());

            if (index != -1) {
                reviews.remove(index);
                notifyDataSetChanged();

            } else {
                Log.e("ReviewReportAdapter", "Attempted to update a non-existing accommodation.");
            }
        }
    }

    private int findIndexOfReviews(Long reviewId) {
        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getId().equals(reviewId)) {
                return i;
            }
        }
        return -1;
    }

    private void getReportReasons(Review review){
        Call<ArrayList<String>> reasons = ClientUtils.reviewReportService.getReportReasons(review.getId());
        reasons.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if (response.isSuccessful()) {
                    reasonsList = response.body();
                    showReasonsDialog(review, reasonsList);
                    Log.d("ReviewReportAdapter", "Reasons size " + reasonsList.size());
                } else {
                    Log.d("ReviewReportAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("ReviewReportAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                Log.d("ReviewReportAdapter", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }



    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView guestInfo, comment, who, host, accommodation;
        RatingBar guestRating;

        ImageView guestImg;

        FloatingActionButton commentReportBtn;
        Button reasonsBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            guestImg = itemView.findViewById(R.id.guestImg);
            guestInfo = itemView.findViewById(R.id.guestInfo);
            guestRating = itemView.findViewById(R.id.guestRating);
            comment = itemView.findViewById(R.id.comment);
            commentReportBtn = itemView.findViewById(R.id.commentReportBtn);
            reasonsBtn = itemView.findViewById(R.id.reasonsBtn);
            who = itemView.findViewById(R.id.whoIsCommented);
            host = itemView.findViewById(R.id.host);
            accommodation = itemView.findViewById(R.id.accommodation);
        }
    }

    public void updateReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }
}
