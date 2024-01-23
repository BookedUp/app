package com.example.bookedup.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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

    private ArrayList<User> reports;
    private ArrayList<String> reasonsList;
    private Fragment fragment;
    private int layout;
    private Context context;
    private Map<Long, Bitmap> usersImages = new HashMap<>();
    private Dialog reasonsDialog;

    public UserReportAdapter(Fragment fragment, ArrayList<User> reports, int beforeLayout, Map<Long, Bitmap> usersImages) {
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
    public void onBindViewHolder(@NonNull UserReportAdapter.ViewHolder holder, int position) {
        User currentUser = reports.get(position);
        Bitmap image = usersImages.get(currentUser.getId());
        if (image != null) {
            holder.guestImg.setImageBitmap(image);
        } else {
            holder.guestImg.setImageResource(R.drawable.default_hotel_img);
        }

        holder.guestInfo.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        if (currentUser.blocked()){
            holder.status.setText("BLOCKED");
            holder.status.setTextColor(Color.RED);
            holder.reasonsBtn.setVisibility(View.INVISIBLE);
        } else {
            holder.status.setTextColor(Color.GREEN);
            holder.status.setText("ACTIVE");
            holder.reasonsBtn.setVisibility(View.VISIBLE);
        }
        holder.address.setText("Address: " + currentUser.getAddress().getCountry() + " " + currentUser.getAddress().getCity());
        holder.email.setText("Email: " + currentUser.getEmail());
        holder.phone.setText("Phone: " + currentUser.getPhone());

        holder.reasonsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReportReasons(currentUser);
            }
        });
    }



    private void getReportReasons(User user){
        Call<ArrayList<String>> reasons = ClientUtils.userReportService.getReportReasons(user.getId());
        reasons.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if (response.isSuccessful()) {
                    reasonsList = response.body();
                    showReasonsDialog(user, reasonsList);
                    Log.d("UserReportAdapter", "Reasons size " + reasonsList.size());
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
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                Log.d("UserReportAdapter", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }


    private void showReasonsDialog(User currentUser, ArrayList<String> reasons) {
        reasonsDialog = new Dialog(context);
        reasonsDialog.setContentView(R.layout.reported_review_reasons_popup);
        Button deleteBtn = reasonsDialog.findViewById(R.id.deleteButton);
        deleteBtn.setText("Block");
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
                showConfirmationDialog("block", currentUser);

            }
        });

    }

    private void blockUser(User user) {
        Call<User> blockedUser = ClientUtils.userService.blockUser(user.getId());
        blockedUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User blocked = response.body();
                    reasonsDialog.dismiss();
                    updateListWithUser(blocked);
                    Toast.makeText(context, "User blocked!", Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("UserReportAdapter", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void updateListWithUser(User user) {
        int index = findIndexOfUser(user.getId());

            if (index != -1) {
                reports.set(index, user);
                notifyDataSetChanged();
                ((UserReportFragment)fragment).updateReportsList(new ArrayList<>(reports));
            } else {
                Log.e("UserReportAdapter", "Attempted to update a non-existing accommodation.");
            }

    }

    private int findIndexOfUser(Long userReportId) {
        for (int i = 0; i < reports.size(); i++) {
            if (reports.get(i).getId().equals(userReportId)) {
                return i;
            }
        }
        return -1;
    }

    private void showConfirmationDialog(String action, User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(action + " confirmation")
                .setMessage("Are you sure you want to " + action + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        blockUser(user);
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
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView guestInfo, status, address, phone, email;
        ImageView guestImg;
        Button reasonsBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            guestImg = itemView.findViewById(R.id.guestImg);
            guestInfo = itemView.findViewById(R.id.guestInfo);
            address = itemView.findViewById(R.id.address);
            status = itemView.findViewById(R.id.status);
            phone = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
            reasonsBtn = itemView.findViewById(R.id.reasonsButton);
        }
    }

    public void updateUserReportData(List<User> updatedList) {
        reports.clear();
        reports.addAll(updatedList);
        notifyDataSetChanged();
    }

}
