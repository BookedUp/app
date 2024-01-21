package com.example.bookedup.fragments.reviews;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.adapters.CommentRequestAdapter;
import com.example.bookedup.adapters.ReservationRequestAdapter;
import com.example.bookedup.adapters.TypeAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.calendar.CalendarFragment;
import com.example.bookedup.fragments.reservations.ReservationRequestFragment;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.Review;
import com.example.bookedup.model.enums.ReservationStatus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRequestsFragment extends Fragment implements TypeAdapter.TypeSelectionListener{

    private RecyclerView typeRecyclerView, commentsRequestsRecyclerView;
    private TypeAdapter typeAdapter;
    private CommentRequestAdapter commentRequestAdapter;
    private int layout_caller;
    private List<Review> reviews = new ArrayList<>();
    private List<Review> originalReviews = new ArrayList<>();
    private Map<Long, Bitmap> usersImages = new HashMap<>();

    public ReviewRequestsFragment(List<Review> originalReviews,  Map<Long, Bitmap> userImages, int layout_caller) {
        this.originalReviews = originalReviews;
        this.usersImages = userImages;
        this.layout_caller = layout_caller;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_requests, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reviews.clear();
        reviews.addAll(originalReviews);
        initUI(view);

    }

    private void initUI(View view) {
        typeRecyclerView = view.findViewById(R.id.typesRecyclerView);
        typeAdapter = new TypeAdapter(getTypeList(), this);
        typeRecyclerView.setAdapter(typeAdapter);
        typeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        commentsRequestsRecyclerView = view.findViewById(R.id.commentsRequestsRecyclerView);
        commentsRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentRequestAdapter = new CommentRequestAdapter(new ArrayList<>(reviews), getContext(), layout_caller, this, usersImages);
        commentsRequestsRecyclerView.setAdapter(commentRequestAdapter);
    }

    private List<String> getTypeList() {
        List<String> types = new ArrayList<>();
        types.add("Unapproved");
        return types;
    }

    @Override
    public void onTypeSelected(String selectedType) {
        updateReviewAdapter(selectedType);
    }

    private void updateReviewAdapter(String selectedType) {
        List<Review> updatedList = getUpdatedReviewsList(selectedType);
        commentRequestAdapter.updateData(updatedList);
    }

    private List<Review> getUpdatedReviewsList(String selectedType) {
        List<Review> filteredList = new ArrayList<Review>();

        switch (selectedType) {
            case "All Comments":
                filteredList.addAll(reviews);
                break;
        }
        if(filteredList.isEmpty()){
            Toast.makeText(getActivity(),"No results!",Toast.LENGTH_SHORT).show();
        }

        return filteredList;
    }

    public void updateReviewList(List<Review> updatedList) {
        if (commentsRequestsRecyclerView != null) {
            reviews.clear();
            reviews.addAll(updatedList);
            commentRequestAdapter.updateData(reviews);
        }
    }

    private void getAllReviews(String selectedType){
        Call<ArrayList<Review>> allReviews = ClientUtils.reviewService.getReviews();
        allReviews.enqueue(new Callback<ArrayList<Review>>() {
            @Override
            public void onResponse(Call<ArrayList<Review>> call, Response<ArrayList<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ReviewRequestFragment", "Successful response: " + response.body());
                    reviews= response.body();
                    updateReviewAdapter(selectedType);
                } else {
                    // Log error details
                    Log.d("ReviewRequestFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("ReviewRequestFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Review>> call, Throwable t) {
                Log.d("ReviewRequestFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

}