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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.Review;
import com.example.bookedup.model.ReviewReport;
import com.example.bookedup.model.UserReport;
import com.example.bookedup.model.enums.Role;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private List<Review> reviews;
    private Fragment fragment;
    private int layout;
    private Map<Long, Bitmap> usersImages = new HashMap<>();
    private Dialog commentReportDialog;
    private FloatingActionButton commentReportBtn;
    private Context context;


    public CommentAdapter(Fragment fragment, List<Review> reviews, int beforeLayout, Map<Long, Bitmap> usersImages) {
        this.fragment = fragment;
        this.reviews = reviews;
        this.layout = beforeLayout;
        this.usersImages = usersImages;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_comments, parent, false);
        return new CommentAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Review currentReview = reviews.get(position);
        Bitmap image = usersImages.get(currentReview.getGuest().getId());
        if (image != null){
            holder.guestImg.setImageBitmap(image);
        } else {
            holder.guestImg.setImageResource(R.drawable.default_hotel_img);
        }
        if(!LoginScreen.loggedUser.getRole().equals(Role.HOST)){
            commentReportBtn.setVisibility(View.INVISIBLE);
        }

        holder.guestInfo.setText(currentReview.getGuest().getFirstName() + " " + currentReview.getGuest().getLastName());
        holder.guestRating.setRating(currentReview.getReview());
        holder.comment.setText(currentReview.getComment());

        holder.commentReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentReportPopup(currentReview);
            }
        });

    }

    private void showCommentReportPopup(Review review){
        commentReportDialog = new Dialog(context);
        commentReportDialog.setContentView(R.layout.user_report_popup);
        Button reportBtn = commentReportDialog.findViewById(R.id.reportButton);
        EditText reportReasonsTxt = commentReportDialog.findViewById(R.id.reasonsInput);
        commentReportDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        commentReportDialog.show();

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewReport reviewReport = new ReviewReport(reportReasonsTxt.getText().toString(), review, false);

                Call<ReviewReport> report = ClientUtils.reviewReportService.createReviewReport(reviewReport);
                report.enqueue(new Callback<ReviewReport>() {
                    @Override
                    public void onResponse(Call<ReviewReport> call, Response<ReviewReport> response) {
                        if (response.isSuccessful()) {
                            ReviewReport newReport = response.body();
                            Log.d("CommentAdapter", "User report " + newReport.toString());
                            commentReportDialog.dismiss();
                            Toast.makeText(context, "Comment report created!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("CommentAdapter", "Unsuccessful response: " + response.code());
                            try {
                                Log.d("CommentAdapter", "Error Body: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewReport> call, Throwable t) {
                        Log.d("CommentAdapter", t.getMessage() != null ? t.getMessage() : "error");
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView guestInfo, comment;
        RatingBar guestRating;

        ImageView guestImg;

        FloatingActionButton commentReportBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            guestImg = itemView.findViewById(R.id.guestImg);
            guestInfo = itemView.findViewById(R.id.guestInfo);
            guestRating = itemView.findViewById(R.id.guestRating);
            comment = itemView.findViewById(R.id.comment);
            commentReportBtn = itemView.findViewById(R.id.commentReportBtn);
        }
    }

    public void updateReviews(List<Review> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }
}
