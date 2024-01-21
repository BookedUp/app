package com.example.bookedup.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.model.Notification;
import com.example.bookedup.model.Review;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private List<Notification> notifications;
    private Fragment fragment;
    private int layout;

    public NotificationAdapter(Fragment fragment, List<Notification> notifications, int beforeLayout) {
        this.fragment = fragment;
        this.notifications = notifications;
        this.layout = beforeLayout;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_notification, parent, false);
        return new NotificationAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Notification currentNotification = notifications.get(position);

        if (!currentNotification.getTitle().isEmpty()){
            holder.timeStatus.setText(currentNotification.getTitle());
        } else {
            holder.timeStatus.setText("NEW");
        }
        holder.timestamp.setText("Timestamp: " + currentNotification.getTimestamp());
        holder.message.setText(currentNotification.getMessage());

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView timeStatus, timestamp, message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeStatus = itemView.findViewById(R.id.timeStatus);
            timestamp = itemView.findViewById(R.id.timestamp);
            message = itemView.findViewById(R.id.message);
        }
    }

    public void updateNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }
}
