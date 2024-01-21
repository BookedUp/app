package com.example.bookedup.fragments.notifications;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bookedup.R;
import com.example.bookedup.adapters.CommentAdapter;
import com.example.bookedup.adapters.NotificationAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.Notification;
import com.example.bookedup.model.Review;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private ArrayList<Notification> notifications = new ArrayList<>();
    private int targetLayout;
    private RecyclerView recyclerView;

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

    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        NotificationAdapter notificationAdapter = new NotificationAdapter(this, notifications, targetLayout);
        recyclerView.setAdapter(notificationAdapter);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.notificationsRecyclerView);
    }
}