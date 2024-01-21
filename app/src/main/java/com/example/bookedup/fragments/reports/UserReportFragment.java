package com.example.bookedup.fragments.reports;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bookedup.R;
import com.example.bookedup.adapters.TypeAdapter;
//import com.example.bookedup.adapters.UserAdapter;
import com.example.bookedup.adapters.UserReportAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.User;
import com.example.bookedup.model.UserReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class UserReportFragment extends Fragment  {

    private ArrayList<User> reportedUsers = new ArrayList<>();
    private ArrayList<User> originalReports = new ArrayList<>();

    private ArrayList<User> blockedUsers = new ArrayList<>();
    private int targetLayout;
    private RecyclerView userReportRecyclerView, typeRecyclerView;
    private TypeAdapter typeAdapter;
    private UserReportAdapter userReportAdapter;

    private ProgressBar progressBar;
    private int count = 0;
    private Timer timer;

    private View darkBackground;

    public UserReportFragment(ArrayList<User> originalReports, int targetLayout) {
        this.originalReports = originalReports;
        this.targetLayout = targetLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_report, container, false);

    }

    private void initiateProgressBar() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            progressBar.setVisibility(View.VISIBLE);
            darkBackground.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);

        });

        // Postavi brojač na 0
        count = 0;

        // Pokreni Timer za okretanje progres bara
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                count++;
                handler.post(() -> {
                    progressBar.setProgress(count);

                });

                if (count == 100) {
                    timer.cancel();
                }
            }
        };
        timer.schedule(timerTask, 0, 100);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reportedUsers.clear();
        reportedUsers.addAll(originalReports);
        initView(view);
        if(!reportedUsers.isEmpty()) {
            initiateProgressBar();
            if (progressBar.getVisibility() == View.VISIBLE) {
                darkBackground.setVisibility(View.VISIBLE);
            }
        }
        initUI(view);
        initReportsRecyclerView();

    }

    private void initView(View view) {
        userReportRecyclerView = view.findViewById(R.id.userReportsRecyclerView);
        progressBar = view.findViewById(R.id.loadingProgressBar);
        darkBackground = view.findViewById(R.id.darkBackground);
    }

    private void initUI(View view){

        loadProfilePictures(reportedUsers);
    }






    private void initReportsRecyclerView() {
        if (!reportedUsers.isEmpty()) {
            loadProfilePictures(reportedUsers);
        } else {
            Toast.makeText(requireContext(), "No results!", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadProfilePictures(List<User> reportedUsers){
        Map<Long, Bitmap> usersImageMap = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        AtomicInteger loadedImagesCount = new AtomicInteger(0);
        Handler handler = new Handler(Looper.getMainLooper());

        for (User user : reportedUsers) {
            executorService.execute(() -> {
                try {
                    Call<ResponseBody> photoCall = ClientUtils.photoService.loadPhoto(user.getProfilePicture().getId());
                    Response<ResponseBody> response = photoCall.execute();

                    if (response.isSuccessful()) {
                        byte[] photoData = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
                        usersImageMap.put(user.getId(), bitmap);

                        if (loadedImagesCount.incrementAndGet() == reportedUsers.size()) {
                            requireActivity().runOnUiThread(() -> {
                                handler.post(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    darkBackground.setVisibility(View.GONE);
                                    userReportRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                                    userReportAdapter = new UserReportAdapter(this, new ArrayList<>(reportedUsers), targetLayout, usersImageMap);
                                    userReportRecyclerView.setAdapter(userReportAdapter);
                                });
                            });
                        }
                    } else {
                        Log.d("UserReportFragment", "Error code " + response.code());
                    }
                } catch (IOException e) {
                    Log.e("UserReportFragment", "Error reading response body: " + e.getMessage());
                }
            });
        }
        executorService.shutdown();
    }



    public void updateReportsList(ArrayList<User> updatedList) {
        if (userReportRecyclerView != null) {
            reportedUsers.clear();
            reportedUsers.addAll(updatedList);
            userReportAdapter.updateUserReportData(reportedUsers);
        }
    }


    private void getAllBlockedUsers(){
        Call<ArrayList<User>> blocked = ClientUtils.userService.getBlockedUsers();
        blocked.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("UserReportFragment", "Successful response: " + response.body());
                    blockedUsers = response.body();
                } else {
                    // Log error details
                    Log.d("UserReportFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("UserReportFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                Log.d("UserReportFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }
}