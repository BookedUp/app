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
import com.example.bookedup.model.Review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportReasonsAdapter extends RecyclerView.Adapter<ReportReasonsAdapter.ViewHolder>{

    private List<String> reasons;

    public ReportReasonsAdapter(List<String> reasons) {
        this.reasons = reasons;
    }

    @NonNull
    @Override
    public ReportReasonsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_reports_reasons, parent, false);
        return new ReportReasonsAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportReasonsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String currentReason = reasons.get(position);
        holder.reason.setText(currentReason);

    }

    @Override
    public int getItemCount() {
        return reasons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView reason;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reason = itemView.findViewById(R.id.reasonTxt);
        }
    }

}
