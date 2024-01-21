package com.example.bookedup.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.reports.UserReportFragment;
import com.example.bookedup.fragments.reservations.ReservationListFragment;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.Review;
import com.example.bookedup.model.User;
import com.example.bookedup.model.UserReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserReportAdapter extends RecyclerView.Adapter<UserReportAdapter.ViewHolder>{

    private List<UserReport> reports;
    private Fragment fragment;
    private int layout;
    private Context context;
    private Map<Long, Bitmap> usersImages = new HashMap<>();

    public UserReportAdapter(Fragment fragment, List<UserReport> reports, int beforeLayout, Map<Long, Bitmap> usersImages) {
        this.fragment = fragment;
        this.reports = reports;
        this.layout = beforeLayout;
        this.usersImages = usersImages;
    }

    @NonNull
    @Override
    public UserReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_user_report, parent, false);
        context = parent.getContext();
        return new UserReportAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReportAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        UserReport currentReport = reports.get(position);
        Bitmap image = usersImages.get(currentReport.getReportedUser().getId());
        if (image != null) {
            holder.guestImg.setImageBitmap(image);
        } else {
            holder.guestImg.setImageResource(R.drawable.default_hotel_img);
        }

        holder.guestInfo.setText(currentReport.getReportedUser().getFirstName() + " " + currentReport.getReportedUser().getLastName());
        if (currentReport.isStatus()) {
            holder.accept.setVisibility(View.INVISIBLE);
            holder.status.setText("APPROVED");
            holder.status.setTextColor(Color.GREEN);
            holder.reject.setText("Delete");
        } else {
            holder.status.setText("NOT APPROVED");
            holder.status.setTextColor(Color.RED);
            holder.accept.setVisibility(View.VISIBLE);
            holder.reject.setText("Delete");
        }

        holder.reason.setText(currentReport.getReason());

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserReport newUserReport = new UserReport(currentReport.getId(), currentReport.getReason(), currentReport.getReportedUser(), true);
                updateUserReport(newUserReport);
            }
        });

        if(holder.reject.getText().toString().equals("Delete")) {

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmationDialog("delete", currentReport);
                }
            });
        }


    }

    private void updateUserReport(UserReport userReport){
        Call<UserReport> acceptedUserReport = ClientUtils.userReportService.updateUserReport(userReport, userReport.getId());
        acceptedUserReport.enqueue(new Callback<UserReport>() {
            @Override
            public void onResponse(Call<UserReport> call, Response<UserReport> response) {
                if (response.isSuccessful()) {
                    UserReport changedUserReport = response.body();
                    Toast.makeText(fragment.requireContext(), "Acception successful", Toast.LENGTH_SHORT).show();

                    updateListWithUserReport(changedUserReport, "accept");
                } else {
                    Log.d("UserReportAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("UserReportAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserReport> call, Throwable t) {
                Log.d("UserReportAdapter", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }


    private void deleteUserReport(UserReport userReport){
        Call<UserReport> deletedUserReport = ClientUtils.userReportService.deleteUserReport(userReport.getId());
        deletedUserReport.enqueue(new Callback<UserReport>() {
            @Override
            public void onResponse(Call<UserReport> call, Response<UserReport> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(fragment.requireContext(), "Deletion successful", Toast.LENGTH_SHORT).show();

                    updateListWithUserReport(userReport, "delete");
                } else {
                    Log.d("UserReportAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("UserReportAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserReport> call, Throwable t) {
                Log.d("UserReportAdapter", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void updateListWithUserReport(UserReport userReport, String action) {
        int index = findIndexOfUserReport(userReport.getId());

        if(action.equals("delete")){
            if (index != -1) {
                reports.remove(index);
                notifyDataSetChanged();
            } else {
                Log.e("UserReportAdapter", "Attempted to remove a non-existing reservation.");
            }
        } else {
            if (index != -1) {
                reports.set(index, userReport);
                notifyDataSetChanged();
            } else {
                Log.e("UserReportAdapter", "Attempted to update a non-existing accommodation.");
            }
        }

        if (fragment instanceof UserReportFragment) {
            ((UserReportFragment) fragment).updateReportsList(new ArrayList<>(reports));
        } else {
            Log.e("UserReportAdapter", "Fragment is not an instance of ReservationRequestFragment");
        }
    }

    private int findIndexOfUserReport(Long userReportId) {
        for (int i = 0; i < reports.size(); i++) {
            if (reports.get(i).getId().equals(userReportId)) {
                return i;
            }
        }
        return -1;
    }

    private void showConfirmationDialog(String action, UserReport userReport) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(action + " confirmation")
                .setMessage("Are you sure you want to " + action + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (action.equals("delete")){
                            deleteUserReport(userReport);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }


    @Override
    public int getItemCount() {
        return reports.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView guestInfo, reason, status;
        ImageView guestImg;
        Button accept, reject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            guestImg = itemView.findViewById(R.id.guestImg);
            guestInfo = itemView.findViewById(R.id.guestInfo);
            reason = itemView.findViewById(R.id.reportReason);
            status = itemView.findViewById(R.id.reportStatus);
            accept = itemView.findViewById(R.id.acceptUserReport);
            reject = itemView.findViewById(R.id.rejectUserReport);
        }
    }

    public void updateUserReportData(List<UserReport> updatedList) {
        reports.clear();
        reports.addAll(updatedList);
        notifyDataSetChanged();
    }

}
