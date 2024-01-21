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
import com.example.bookedup.adapters.ReviewReportAdapter;
import com.example.bookedup.adapters.TypeAdapter;
import com.example.bookedup.adapters.UserReportAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.Review;
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

public class ReviewReportFragment extends Fragment {

    private List<Review> reportedReviews = new ArrayList<>();
    private int targetLayout;
    private RecyclerView reviewReportRecyclerView;
    private ReviewReportAdapter reviewReportAdapter;
    private Map<Long, Bitmap> userImages = new HashMap<>();

//    private ProgressBar progressBar;
//    private int count = 0;
//    private Timer timer;
//
//    private View darkBackground;

    public ReviewReportFragment(List<Review> reportedReviews, int targetLayout, Map<Long, Bitmap> userImages) {
        this.reportedReviews = reportedReviews;
        this.targetLayout = targetLayout;
        this.userImages = userImages;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review_report, container, false);

    }

//    private void initiateProgressBar() {
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(() -> {
//            progressBar.setVisibility(View.VISIBLE);
//            darkBackground.setVisibility(View.VISIBLE);
//            progressBar.setProgress(0);
//
//        });
//
//        // Postavi brojač na 0
//        count = 0;
//
//        // Pokreni Timer za okretanje progres bara
//        timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                count++;
//                handler.post(() -> {
//                    progressBar.setProgress(count);
//
//                });
//
//                if (count == 100) {
//                    timer.cancel();
//                }
//            }
//        };
//        timer.schedule(timerTask, 0, 100);
//    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
//        initiateProgressBar();
//        if (progressBar.getVisibility() == View.VISIBLE) {
//            darkBackground.setVisibility(View.VISIBLE);
//        }
        initUI(view);

    }

    private void initView(View view) {
        reviewReportRecyclerView = view.findViewById(R.id.reportedReviewsRecyclerView);
    }

    private void initUI(View view){
        reviewReportAdapter = new ReviewReportAdapter(this, reportedReviews, targetLayout, userImages);
        reviewReportRecyclerView.setAdapter(reviewReportAdapter);
        reviewReportRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

    }



}