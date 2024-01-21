package com.example.bookedup.fragments.reports;

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
import com.example.bookedup.adapters.TypeAdapter;
//import com.example.bookedup.adapters.UserAdapter;
import com.example.bookedup.adapters.UserReportAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.UserReport;

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
import retrofit2.Callback;
import retrofit2.Response;
public class UserReportFragment extends Fragment implements TypeAdapter.TypeSelectionListener {

    private List<UserReport> reports = new ArrayList<>();
    private List<UserReport> originalReports = new ArrayList<>();
    private int targetLayout;
    private RecyclerView userReportRecyclerView, typeRecyclerView;
    private TypeAdapter typeAdapter;
    private UserReportAdapter userReportAdapter;

    public UserReportFragment(List<UserReport> originalReports, int targetLayout) {
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reports.clear();
        reports.addAll(originalReports);
        initView(view);
        initUI(view);
        initReportsRecyclerView(reports);

    }

    private void initView(View view) {
        userReportRecyclerView = view.findViewById(R.id.userReportsRecyclerView);
    }

    private void initUI(View view){
        typeRecyclerView = view.findViewById(R.id.typesRecyclerView);
        typeAdapter = new TypeAdapter(getTypeList(), this);
        typeRecyclerView.setAdapter(typeAdapter);
        typeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        loadProfilePictures(reports);
    }

    private List<String> getTypeList() {
        List<String> types = new ArrayList<>();
        types.add("All");
        types.add("Waiting For Approval");
        types.add("Accepted");
        return types;
    }


    private void updateUserReportAdapter(String selectedType) {
        List<UserReport> updatedList = getUpdatedUserReportsList(selectedType);
        userReportAdapter.updateUserReportData(updatedList);
    }

    private List<UserReport> getUpdatedUserReportsList(String selectedType) {
        List<UserReport> filteredList = new ArrayList<UserReport>();

        switch (selectedType) {
            case "All":
                filteredList.addAll(reports);
                break;
            case "Waiting For Approval":
                for (UserReport userReport : reports) {
                    if (!userReport.isStatus()) {
                        filteredList.add(userReport);
                    }
                }
                break;
            case "Accepted":
                for (UserReport userReport : reports) {
                    if (userReport.isStatus()) {
                        filteredList.add(userReport);
                    }
                }
                break;

        }
        if(filteredList.isEmpty()){
            Toast.makeText(getActivity(),"No results!",Toast.LENGTH_SHORT).show();
        }

        return filteredList;
    }

    private void initReportsRecyclerView(List<UserReport> reports) {
        if (!reports.isEmpty()) {
            loadProfilePictures(reports);
        } else {
            Toast.makeText(requireContext(), "No results!", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadProfilePictures(List<UserReport> reports){
        Map<Long, Bitmap> usersImageMap = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        AtomicInteger loadedImagesCount = new AtomicInteger(0);

        for (UserReport userReport : reports) {
            executorService.execute(() -> {
                try {
                    Call<ResponseBody> photoCall = ClientUtils.photoService.loadPhoto(userReport.getReportedUser().getProfilePicture().getId());
                    Response<ResponseBody> response = photoCall.execute();

                    if (response.isSuccessful()) {
                        byte[] photoData = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
                        usersImageMap.put(userReport.getReportedUser().getId(), bitmap);

                        if (loadedImagesCount.incrementAndGet() == reports.size()) {
                            requireActivity().runOnUiThread(() -> {
                                userReportRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                                userReportAdapter = new UserReportAdapter(this, new ArrayList<>(reports), targetLayout, usersImageMap);
                                userReportRecyclerView.setAdapter(userReportAdapter);
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



    public void updateReportsList(ArrayList<UserReport> updatedList) {
        if (userReportRecyclerView != null) {
            reports.clear();
            reports.addAll(updatedList);
            userReportAdapter.updateUserReportData(reports);
        }
    }


    @Override
    public void onTypeSelected(String selectedType) {
        getAllUserReports(selectedType);
    }

    private void getAllUserReports(String selectedType){
        Call<ArrayList<UserReport>> allReports = ClientUtils.userReportService.getUserReports();
        allReports.enqueue(new Callback<ArrayList<UserReport>>() {
            @Override
            public void onResponse(Call<ArrayList<UserReport>> call, Response<ArrayList<UserReport>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("UserReportFragment", "Successful response: " + response.body());
                    reports = response.body();
                    updateUserReportAdapter(selectedType);
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
            public void onFailure(Call<ArrayList<UserReport>> call, Throwable t) {
                Log.d("UserReportFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }
}