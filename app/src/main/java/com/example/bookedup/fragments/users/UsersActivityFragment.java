package com.example.bookedup.fragments.users;

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
import com.example.bookedup.adapters.UserAdapter;
import com.example.bookedup.adapters.UserReportAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.User;
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


public class UsersActivityFragment extends Fragment  implements TypeAdapter.TypeSelectionListener{

    private List<User> users = new ArrayList<>();
    private int targetLayout;
    private RecyclerView usersRecyclerView, typeRecyclerView;
    private TypeAdapter typeAdapter;
    private UserAdapter userAdapter;
    private Map<Long, Integer> numberOfReports = new HashMap<>();
    private Map<Long, List<String>> usersReportsReasons = new HashMap<>();
    private List<User> originalUsers = new ArrayList<>();

    public UsersActivityFragment(List<User> originalUsers, int targetLayout, Map<Long, Integer> numberOfReports, Map<Long, List<String>> usersReportsReasons) {
        this.originalUsers = originalUsers;
        this.targetLayout = targetLayout;
        this.numberOfReports = numberOfReports;
        this.usersReportsReasons = usersReportsReasons;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        users.clear();
        users.addAll(originalUsers);
        initView(view);
        initUI(view);
        initRecyclerView();


    }

    private void initUI(View view){
        typeRecyclerView = view.findViewById(R.id.typesRecyclerView);
        typeAdapter = new TypeAdapter(getTypeList(), this);
        typeRecyclerView.setAdapter(typeAdapter);
        typeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        loadProfilePictures(users);
    }

    private List<String> getTypeList() {
        List<String> types = new ArrayList<>();
        types.add("All Users");
        types.add("Blocked");
        types.add("Unblocked");
        return types;
    }


    private void updateUserAdapter(String selectedType) {
        List<User> updatedList = getUpdatedUserList(selectedType);
        userAdapter.updateUserData(updatedList);
    }

    public List<User> getUpdatedUserList(String selectedType) {
        List<User> filteredList = new ArrayList<User>();

        switch (selectedType) {
            case "All Users":
                filteredList.addAll(users);
                break;
            case "Blocked":
                for (User user : users) {
                    if (user.blocked()) {
                        filteredList.add(user);
                    }
                }
                break;
            case "Unblocked":
                for (User user : users) {
                    if (!user.blocked()) {
                        filteredList.add(user);
                    }
                }
                break;

        }
        if(filteredList.isEmpty()){
            Toast.makeText(getActivity(),"No results!",Toast.LENGTH_SHORT).show();
        }

        return filteredList;
    }

    private void initRecyclerView() {
        if (!originalUsers.isEmpty()) {
            loadProfilePictures(users);
        } else {
            Toast.makeText(requireContext(), "No results!", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadProfilePictures(List<User> users){
        Map<Long, Bitmap> usersImageMap = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        AtomicInteger loadedImagesCount = new AtomicInteger(0);

        for (User user : users) {
            executorService.execute(() -> {
                try {
                    Call<ResponseBody> photoCall = ClientUtils.photoService.loadPhoto(user.getProfilePicture().getId());
                    Response<ResponseBody> response = photoCall.execute();

                    if (response.isSuccessful()) {
                        byte[] photoData = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
                        usersImageMap.put(user.getId(), bitmap);

                        if (loadedImagesCount.incrementAndGet() == users.size()) {
                            requireActivity().runOnUiThread(() -> {
                                usersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                                userAdapter = new UserAdapter(this, new ArrayList<>(originalUsers), targetLayout, numberOfReports, usersReportsReasons);
                                userAdapter.setImages(usersImageMap);
                                usersRecyclerView.setAdapter(userAdapter);
                            });
                        }
                    } else {
                        Log.d("UsersActivityFragment", "Error code " + response.code());
                    }
                } catch (IOException e) {
                    Log.e("UsersActivityFragment", "Error reading response body: " + e.getMessage());
                }
            });
        }
        executorService.shutdown();
    }


    private void initView(View view) {
        usersRecyclerView = view.findViewById(R.id.usersRecyclerView);
    }

    public void updateUsersList(List<User> updatedList) {
        if (usersRecyclerView != null) {
            users.clear();
            users.addAll(updatedList);
            userAdapter.updateUserData(users);
        }
    }


    @Override
    public void onTypeSelected(String selectedType) {
        getAllUsers(selectedType);
    }

    private void getAllUsers(String selectedType){
        Call<ArrayList<User>> allUsers = ClientUtils.userService.getUsers();
        allUsers.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("UsersActivityFragment", "Successful response: " + response.body());
                    users = response.body();
                    updateUserAdapter(selectedType);
                } else {
                    // Log error details
                    Log.d("UsersActivityFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("UsersActivityFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                Log.d("UsersActivityFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }
}