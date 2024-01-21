package com.example.bookedup.fragments.notifications;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.adapters.CommentAdapter;
import com.example.bookedup.adapters.NotificationAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.Guest;
import com.example.bookedup.model.Host;
import com.example.bookedup.model.Notification;
import com.example.bookedup.model.Review;
import com.example.bookedup.model.User;
import com.example.bookedup.model.enums.Role;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private ArrayList<Notification> notifications = new ArrayList<>();
    private int targetLayout;
    private NotificationAdapter notificationAdapter;
    private Dialog dialog;
    private RecyclerView recyclerView;
    private FloatingActionButton configureNotifications;
    private boolean resAnswerNotification,  createResNotification, cancelResNotification, hostRateNotification, accRateNotification;

    public NotificationsFragment(ArrayList<Notification> notifications, int targetLayout) {
        this.notifications = notifications;
        this.targetLayout = targetLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (notifications.isEmpty()){
            Toast.makeText(getActivity(),"No results!",Toast.LENGTH_SHORT).show();
        }
        initView(view);
        initRecyclerView();

        configureNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginScreen.loggedUser.getRole().equals(Role.HOST)){
                    showHostNotificationConfigPopup();
                } else if (LoginScreen.loggedUser.getRole().equals(Role.GUEST)){
                    showGuestNotificationConfigPopup();
                }
            }
        });

    }

    private void showGuestNotificationConfigPopup() {
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.guest_notification_conf_popup);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        SwitchMaterial  switchBtn = dialog.findViewById(R.id.toggleSwitchResAnswer);

        switchBtn.setChecked(LoginScreen.loggedGuest.isNotificationEnable());

        Button btnSave = dialog.findViewById(R.id.saveBtn);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                resAnswerNotification = LoginScreen.loggedGuest.isNotificationEnable();


                if (switchBtn.isChecked()){

                    resAnswerNotification = true;
                } else {
                    resAnswerNotification = false;
                }

                Guest newGuest = new Guest(LoginScreen.loggedGuest.getFirstName(), LoginScreen.loggedGuest.getLastName(), LoginScreen.loggedGuest.getAddress(), LoginScreen.loggedGuest.getPhone(), LoginScreen.loggedGuest.getEmail(), LoginScreen.loggedGuest.getPassword(), LoginScreen.loggedGuest.blocked(), LoginScreen.loggedGuest.isVerified(), LoginScreen.loggedGuest.isActive(), LoginScreen.loggedGuest.getProfilePicture(), LoginScreen.loggedGuest.getRole(), LoginScreen.loggedGuest.getFavourites(), resAnswerNotification);
                Long id = LoginScreen.loggedGuest.getId();
                LoginScreen.loggedGuest = newGuest;
                LoginScreen.loggedGuest.setId(id);
                updateGuest(newGuest);
            }
        });

        dialog.show();
    }

    private void showHostNotificationConfigPopup() {
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.notifications_conf_popup);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        SwitchMaterial createResBtn = dialog.findViewById(R.id.toggleSwitchCreateRes);
        createResBtn.setChecked(LoginScreen.loggedHost.getReservationCreatedNotificationEnabled());


        SwitchMaterial  cancelResBtn = dialog.findViewById(R.id.toggleSwitchCancelRes);
        cancelResBtn.setChecked(LoginScreen.loggedHost.getCancellationNotificationEnabled());

        SwitchMaterial  hostRateBtn = dialog.findViewById(R.id.toggleSwitchHostRate);
        hostRateBtn.setChecked(LoginScreen.loggedHost.getHostRatingNotificationEnabled());


        SwitchMaterial  accRateBtn = dialog.findViewById(R.id.toggleSwitchAccommodationRate);
        accRateBtn.setChecked(LoginScreen.loggedHost.getAccommodationRatingNotificationEnabled());

        Button btnSave = dialog.findViewById(R.id.saveBtn);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                createResNotification = LoginScreen.loggedHost.getReservationCreatedNotificationEnabled();
                cancelResNotification = LoginScreen.loggedHost.getCancellationNotificationEnabled();
                hostRateNotification = LoginScreen.loggedHost.getHostRatingNotificationEnabled();
                accRateNotification = LoginScreen.loggedHost.getAccommodationRatingNotificationEnabled();

                if (createResBtn.isChecked()){
                    createResNotification = true;
                } else {
                    createResNotification = false;
                }
                if (cancelResBtn.isChecked()){
                    cancelResNotification = true;
                } else {
                    cancelResNotification = false;
                }
                if (hostRateBtn.isChecked()){
                    hostRateNotification = true;
                } else {
                    hostRateNotification = false;
                }
                if (accRateBtn.isChecked()){
                    accRateNotification = true;
                } else {
                    accRateNotification = false;
                }
                Host newHost = new Host(LoginScreen.loggedHost.getFirstName(), LoginScreen.loggedHost.getLastName(), LoginScreen.loggedHost.getAddress(), LoginScreen.loggedHost.getPhone(), LoginScreen.loggedHost.getEmail(), LoginScreen.loggedHost.getPassword(), LoginScreen.loggedHost.blocked(), LoginScreen.loggedHost.isVerified(), LoginScreen.loggedHost.isActive(), LoginScreen.loggedHost.getProfilePicture(), LoginScreen.loggedHost.getRole(), LoginScreen.loggedHost.getAverageRating(), createResNotification, cancelResNotification, hostRateNotification, accRateNotification);
                Long id = LoginScreen.loggedHost.getId();
                LoginScreen.loggedHost = newHost;
                LoginScreen.loggedHost.setId(id);
                updateHost(newHost);
            }
        });

        dialog.show();

    }

    private void updateHost(Host host) {
        Call<Host> updatedHost = ClientUtils.hostService.updateHost(host, LoginScreen.loggedHost.getId());
        updatedHost.enqueue(new Callback<Host>() {
            @Override
            public void onResponse(Call<Host> call, Response<Host> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("NotificationsFragment", "Successful response: " + response.body());
                        Host newHost = response.body();
                        Toast.makeText(getActivity(),"Successful changed!",Toast.LENGTH_SHORT).show();
                        getUpdatedNotifications(newHost.getId());
                        Log.d("NotificationsFragment", "New User " + newHost.toString());
                    } else {
                        Log.d("NotificationsFragment", "Response body is null");
                    }
                }  else {
                    // Log error details
                    Log.d("NotificationsFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("NotificationsFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<Host> call, Throwable t) {
                Log.d("NotificationsFragment", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void updateGuest(Guest guest) {
        Log.d("NotificationsFragment", "Login guest " + LoginScreen.loggedGuest.isNotificationEnable());
        Log.d("NotificationsFragment", "Guest " + guest.isNotificationEnable());

        Call<Guest> updatedGuest = ClientUtils.guestService.updateGuest(LoginScreen.loggedGuest.getId(), guest);
        updatedGuest.enqueue(new Callback<Guest>() {
            @Override
            public void onResponse(Call<Guest> call, Response<Guest> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("NotificationsFragment", "Successful response: " + response.body());
                        Guest newGuest = response.body();
                        Toast.makeText(getActivity(),"Successful changed!",Toast.LENGTH_SHORT).show();
                        getUpdatedNotifications(newGuest.getId());
                        Log.d("NotificationsFragment", "New User " + newGuest.toString());
                    } else {
                        Log.d("NotificationsFragment", "Response body is null");
                    }
                }  else {
                    // Log error details
                    Log.d("NotificationsFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("NotificationsFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<Guest> call, Throwable t) {
                Log.d("NotificationsFragment", "BLAAA " + t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void getUpdatedNotifications(Long id){
        Call<ArrayList<Notification>> allNotifications = ClientUtils.notificationService.getEnabledNotificationsByUserId(id);
        allNotifications.enqueue(new Callback<ArrayList<Notification>>() {
            @Override
            public void onResponse(Call<ArrayList<Notification>> call, Response<ArrayList<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("NotificationsFragment", "Successful response: " + response.body());
                    notifications = response.body();
                    Collections.sort(notifications, (n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()));

                    notificationAdapter.updateNotifications(notifications);

//                    NotificationsFragment notificationsFragment = new NotificationsFragment(notifications, targetLayout);
//                    FragmentManager fragmentManager = getParentFragmentManager();
//                    FragmentTransaction transaction = fragmentManager.beginTransaction();
//                    transaction.replace(R.id.frame_notifications_container, notificationsFragment); //fragment_container
//                    transaction.commit();
                } else {
                    Log.d("NotificationsFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("NotificationsFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Notification>> call, Throwable t) {
                Log.d("NotificationsFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        notificationAdapter = new NotificationAdapter(this, notifications, targetLayout);
        recyclerView.setAdapter(notificationAdapter);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.notificationsRecyclerView);
        configureNotifications = view.findViewById(R.id.configureNotificationsButton);
    }
}