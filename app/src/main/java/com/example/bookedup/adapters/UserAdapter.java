package com.example.bookedup.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.reports.ReportsReasonListFragment;
import com.example.bookedup.fragments.reports.UserReportFragment;
import com.example.bookedup.fragments.users.UsersActivityFragment;
import com.example.bookedup.model.User;
import com.example.bookedup.model.UserReport;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private List<User> users;
    private Fragment fragment;
    private int layout;
    private Map<Long, Bitmap> usersImages = new HashMap<>();
    private Map<Long, Integer> numberOfReportsMap = new HashMap<>();
    private Map<Long, List<String>> usersReportsReasons = new HashMap<>();

    public UserAdapter(Fragment fragment, List<User> users, int beforeLayout, Map<Long, Integer> numberOfReports, Map<Long, List<String>> usersReportsReasons) {
        this.fragment = fragment;
        this.layout = beforeLayout;
//        this.usersImages = usersImages;
        this.users = users;
        this.numberOfReportsMap = numberOfReports;
        this.usersReportsReasons = usersReportsReasons;
    }

    public void setImages(Map<Long, Bitmap> usersImages){
        this.usersImages = usersImages;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_user_activity, parent, false);
        return new UserAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = users.get(position);
        Bitmap image = usersImages.get(user.getId());
        if (image != null) {
            holder.guestImg.setImageBitmap(image);
        } else {
            holder.guestImg.setImageResource(R.drawable.default_hotel_img);
        }
        holder.guestInfo.setText(user.getFirstName() + " " + user.getLastName());
        holder.address.setText("Address: " + user.getAddress().getCountry() + " " + user.getAddress().getCity());
        holder.phone.setText("Phone: " + String.valueOf(user.getPhone()));

       if (user.blocked()){
           holder.status.setText("BLOCKED");
           holder.status.setTextColor(Color.RED);
           holder.block.setEnabled(false);
           holder.unblock.setEnabled(true);
       } else {
           holder.status.setText("ACTIVE");
           holder.status.setTextColor(Color.GREEN);
           holder.unblock.setEnabled(false);
           holder.block.setEnabled(true);
       }
       Integer reportsNumber = numberOfReportsMap.get(user.getId());
       holder.numberOfReports.setText("Reports number: " + String.valueOf(reportsNumber));

       List<String> reasons = usersReportsReasons.get(user.getId());

       if(reportsNumber == 0){
           holder.userReportsBtn.setVisibility(View.INVISIBLE);
       }

       holder.block.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               blockUser(user);
           }
       });

       holder.unblock.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               unblockUser(user);
           }
       });

       holder.userReportsBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Context context = fragment.requireContext();
               ReportsReasonListFragment reportsReasonListFragment = new ReportsReasonListFragment(reasons);
               AppCompatActivity activity = (AppCompatActivity) context;
               FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
               transaction.replace(layout, reportsReasonListFragment);
               transaction.addToBackStack(null);
               transaction.commit();
           }
       });


    }

    private void blockUser(User user){
        Call<User> blockedUser = ClientUtils.userService.blockUser(user.getId());
        blockedUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User blocked = response.body();
//                    blocked.setBlocked(true);
                    Log.d("UserAdapter", "Body " + blocked.toString());
                    updateListWithUser(blocked, "block");
                } else {
                    Log.d("UserAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("UserAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("UserAdapter", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }


    private void unblockUser(User user){
        Call<User> unblockUser = ClientUtils.userService.unblockUser(user.getId());
        unblockUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User unblocked = response.body();
//                    unblocked.setBlocked(false);
                    updateListWithUser(unblocked, "unblock");
                } else {
                    Log.d("UserAdapter", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("UserAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("UserAdapter", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void updateListWithUser(User user, String action) {
        int index = findIndexOfUser(user.getId());

        if (index != -1) {
            users.set(index, user);
            notifyDataSetChanged();

            if (fragment instanceof UsersActivityFragment) {
                ((UsersActivityFragment) fragment).updateUsersList(new ArrayList<>(users));
                Toast.makeText(fragment.requireContext(), "Successfully " + (user.blocked() ? "blocked" : "unblocked"), Toast.LENGTH_SHORT).show();
            } else {
                Log.e("UserAdapter", "Fragment is not an instance of UsersActivityFragment");
            }
        } else {
            Log.e("UserAdapter", "Attempted to update a non-existing user.");
        }
    }


    private int findIndexOfUser(Long userId) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(userId)) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView guestInfo, status, numberOfReports, address, phone;
        ImageView guestImg;
        Button block, unblock;

        FloatingActionButton userReportsBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            guestImg = itemView.findViewById(R.id.guestImg);
            guestInfo = itemView.findViewById(R.id.guestInfo);
            status = itemView.findViewById(R.id.reportStatus);
            numberOfReports = itemView.findViewById(R.id.numberOfReports);
            block = itemView.findViewById(R.id.blockUserBtn);
            unblock = itemView.findViewById(R.id.unblockUserBtn);
            userReportsBtn = itemView.findViewById(R.id.userReportsBtn);
            address = itemView.findViewById(R.id.address);
            phone = itemView.findViewById(R.id.phone);
        }
    }

    public void updateUserData(List<User> updatedList) {
        users.clear();
        users.addAll(updatedList);
        notifyDataSetChanged();
    }




}
