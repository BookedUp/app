package com.example.bookedup.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.bookedup.R;
import com.example.bookedup.model.Review;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    List<Review> reviews;
    private Fragment fragment;
    int layout;

    public CommentAdapter(Fragment fragment, List<Review> reviews, int beforeLayout) {
        this.fragment = fragment;
        this.reviews = reviews;
        this.layout = beforeLayout;
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
        if (currentReview.getGuest().getProfilePicture() != null) {
            String imageUrl = currentReview.getGuest().getProfilePicture().getUrl();
            // Load the image using Glide
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .transform(new CenterCrop(), new GranularRoundedCorners(40, 40, 40, 40))
                    .into(holder.guestImg);
        } else {
            holder.guestImg.setImageResource(R.drawable.default_hotel_img);
        }

        holder.guestInfo.setText(currentReview.getGuest().getFirstName() + " " + currentReview.getGuest().getLastName());
        holder.guestRating.setRating(currentReview.getReview());
        holder.comment.setText(currentReview.getComment());

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView guestInfo, comment;
        RatingBar guestRating;

        ImageView guestImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            guestImg = itemView.findViewById(R.id.guestImg);
            guestInfo = itemView.findViewById(R.id.guestInfo);
            guestRating = itemView.findViewById(R.id.guestRating);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    public void updateReviews(List<Review> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }
}
