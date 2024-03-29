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

import com.example.bookedup.R;
import com.example.bookedup.adapters.CommentAdapter;
import com.example.bookedup.adapters.ReportReasonsAdapter;
import com.example.bookedup.clients.ClientUtils;
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


public class ReportsReasonListFragment extends Fragment {

    private List<String> reasons = new ArrayList<>();
    private RecyclerView recyclerView;

    public ReportsReasonListFragment(List<String> reasons) {
        this.reasons = reasons;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports_reason_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initRecyclerView();

    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        ReportReasonsAdapter reasonsAdapter = new ReportReasonsAdapter(reasons);
        recyclerView.setAdapter(reasonsAdapter);
    }


    private void initView(View view) {
        recyclerView = view.findViewById(R.id.reasonsRecyclerView);
    }
}