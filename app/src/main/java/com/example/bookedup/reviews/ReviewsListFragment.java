package com.example.bookedup.reviews;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookedup.R;
import com.example.bookedup.adapters.CommentAdapter;
import com.example.bookedup.fragments.reservations.CreateReservationFragment;
import com.example.bookedup.model.Review;

import java.util.ArrayList;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        CommentAdapter commentAdapter = new CommentAdapter(this, reviews, targetLayout);
        recyclerView.setAdapter(commentAdapter);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.commentRecyclerView);
    }


}