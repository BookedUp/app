package com.example.bookedup.fragments.reviews;

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

import com.example.bookedup.R;
import com.example.bookedup.adapters.CommentAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.reservations.CreateReservationFragment;
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

public class ReviewsListFragment extends Fragment {

    private ArrayList<Review> reviews = new ArrayList<>();
    private int targetLayout;
    private RecyclerView recyclerView;

    public ReviewsListFragment(ArrayList<Review> reviews, int targetLayout) {
        this.reviews = reviews;
        this.targetLayout = targetLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviews_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initRecyclerView(reviews);

    }

    private void initRecyclerView(ArrayList<Review> reviews) {
        loadProfilePictures(reviews);
    }

    private void loadProfilePictures(List<Review> reviews){
        Map<Long, Bitmap> usersImageMap = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        AtomicInteger loadedImagesCount = new AtomicInteger(0);

        for (Review review : reviews) {
            executorService.execute(() -> {
                try {
                    Call<ResponseBody> photoCall = ClientUtils.photoService.loadPhoto(review.getGuest().getProfilePicture().getId());
                    Response<ResponseBody> response = photoCall.execute();

                    if (response.isSuccessful()) {
                        byte[] photoData = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
                        usersImageMap.put(review.getGuest().getId(), bitmap);

                        if (loadedImagesCount.incrementAndGet() == reviews.size()) {
                            requireActivity().runOnUiThread(() -> {
                                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                                CommentAdapter commentAdapter = new CommentAdapter(this, reviews, targetLayout, usersImageMap);
                                recyclerView.setAdapter(commentAdapter);
                            });
                        }
                    } else {
                        Log.d("DetailsFragment", "Error code " + response.code());
                    }
                } catch (IOException e) {
                    Log.e("DetailsFragment", "Error reading response body: " + e.getMessage());
                }
            });
        }
        executorService.shutdown();
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.commentRecyclerView);
    }


}