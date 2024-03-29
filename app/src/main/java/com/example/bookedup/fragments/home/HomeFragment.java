package com.example.bookedup.fragments.home;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookedup.R;
import com.example.bookedup.adapters.PopularDestinationAdapter;
import com.example.bookedup.adapters.PopularAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.accommodations.SearchFilterFragment;
import com.example.bookedup.fragments.calendar.CalendarFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Destination;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.Review;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import kotlinx.coroutines.CoroutineScope;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerViewPopular, recyclerViewDestinations;
    private RecyclerView.Adapter adapterPopular;
    private ArrayList<Accommodation> results;
    private boolean isStartDateButtonClicked, isEndDateButtonClicked, isSearch;
    private static int targetLayout;
    private EditText whereToGoTxt, guestsNumberTxt;
    private TextView checkInTxt, checkOutTxt;
    private Integer guestsNumber;
    private FloatingActionButton searchButton;
    private ImageView startDateBtn, endDateBtn;
    private String whereToGo, checkIn, checkOut;
    private ArrayList<Accommodation> mostPopularAccommodations = new ArrayList<>();
    private ProgressBar progressBar;
    private int count = 0;
    private Timer timer;

    private View darkBackground;


    public HomeFragment() {

    }    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findTargetLayout();
        initView(view);
        initView(view);
        initiateProgressBar();
        if (progressBar.getVisibility() == View.VISIBLE) {
            darkBackground.setVisibility(View.VISIBLE);
        }
        initRecycleView();
        isSearch = false;

        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new CalendarFragment();
                datePicker.setTargetFragment(HomeFragment.this, 0);
                datePicker.show(requireActivity().getSupportFragmentManager(), "date picker");
                isStartDateButtonClicked = true;
                isEndDateButtonClicked = false;
            }
        });

        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new CalendarFragment();
                datePicker.setTargetFragment(HomeFragment.this, 0);
                datePicker.show(requireActivity().getSupportFragmentManager(), "date picker");
                isEndDateButtonClicked = true;
                isStartDateButtonClicked = false;
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearch = true;
                if (!guestsNumberTxt.getText().toString().isEmpty()){
                    guestsNumber = Integer.parseInt(guestsNumberTxt.getText().toString());
                } else {
                    guestsNumber = 0;
                }
                whereToGo = whereToGoTxt.getText().toString();
                checkIn = checkInTxt.getText().toString();
                checkOut = checkOutTxt.getText().toString();
                searchFilter();
            }
        });
    }

    private void initiateProgressBar() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            setDarkBackgroundVisibility(true);
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

    private void stopProgressBar() {
        timer.cancel();
        setDarkBackgroundVisibility(false); // Revert the background visibility
        progressBar.setVisibility(View.GONE);
    }

    private void setDarkBackgroundVisibility(boolean visible) {
        darkBackground.setVisibility(visible ? View.VISIBLE : View.GONE);
        float alpha = visible ? 0.5f : 0.0f; // Set the alpha value based on your preference
        darkBackground.setAlpha(alpha);
    }


    private void findTargetLayout(){
        Intent intent = getActivity().getIntent();
        ComponentName componentName = intent.getComponent();
        if (componentName.getClassName().equals("com.example.bookedup.activities.GuestMainScreen")) {
            targetLayout = R.id.frame_layout;
        } else if (componentName.getClassName().equals("com.example.bookedup.activities.AdministratorMainScreen")){
            targetLayout = R.id.frame_layoutAdmin;
        } else if (componentName.getClassName().equals("com.example.bookedup.activities.HostMainScreen")){
            targetLayout = R.id.frame_layoutHost;
        }
    }

    private void initView(View view){
        searchButton = view.findViewById(R.id.searchButtonHome);

        recyclerViewPopular = view.findViewById(R.id.view_pop);
        recyclerViewDestinations = view.findViewById(R.id.view_destinations);

        whereToGoTxt = view.findViewById(R.id.location);
        checkInTxt = view.findViewById(R.id.checkInText);
        checkOutTxt = view.findViewById(R.id.checkOutText);
        guestsNumberTxt = view.findViewById(R.id.guestsNumber);
        startDateBtn =  view.findViewById(R.id.startDate);
        endDateBtn =  view.findViewById(R.id.endDate);

        progressBar = view.findViewById(R.id.loadingProgressBar);
        darkBackground = view.findViewById(R.id.darkBackground);
    }

    private void searchFilter() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date startDate = new Date();
        Date endDate = new Date();


        try {
            startDate = dateFormat.parse(checkIn);
            endDate = dateFormat.parse(checkOut);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        startDate.setHours(13);
        endDate.setHours(13);
        List<Object> amenities = new ArrayList<>();


        Call<ArrayList<Accommodation>> searchedResults = ClientUtils.accommodationService.searchAccommodations(
                whereToGo,
                guestsNumber,
                dateFormat.format(startDate),
                dateFormat.format(endDate),
                amenities,
                0.0,
                0.0,
                0.0,
                "null",
                ""
        );

//        Log.d("HomeFragment", "Prosaoooooo" + searchedResults);
        searchedResults.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("HomeFragment", "Successful response: " + response.body());
                        results = response.body();
                        getLoadPictures(results);
//                        openSearchFilterFragment(whereToGo, results, guestsNumber, checkIn, checkOut);

                    } else {
                        Log.d("HomeFragment", "Response body is null");
                    }
                }  else {
                    // Log error details
                    Log.d("HomeFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("HomeFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.d("HomeFragment", "GRESKA response: " + response.body());
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
                Log.d("HomeFragment", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void openSearchFilterFragment(String whereToGo, List<Accommodation> results, Integer guestsNumber, String checkIn, String checkOut, Map<Long, List<Bitmap>> accommodationImages){
        SearchFilterFragment searchFilterFragment = new SearchFilterFragment(accommodationImages);

        Bundle bundle = new Bundle();
        bundle.putString("whereToGo", whereToGo);
        bundle.putInt("guestsNumber", guestsNumber);
        bundle.putString("checkIn", checkIn);
        bundle.putString("checkOut", checkOut);
        String resultsJson = new Gson().toJson(results);
        bundle.putString("resultsJson", resultsJson);

        searchFilterFragment.setArguments(bundle);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(targetLayout, searchFilterFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void initRecycleView() {

        ArrayList<Destination> destinationList = new ArrayList<>();
        destinationList.add(new Destination(R.drawable.australia, "Australia"));
        destinationList.add(new Destination(R.drawable.japan, "Japan"));
        destinationList.add(new Destination(R.drawable.new_zeland, "New Zealand"));
        destinationList.add(new Destination(R.drawable.dubai, "Dubai"));

        recyclerViewDestinations.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        PopularDestinationAdapter imageAdapter = new PopularDestinationAdapter(destinationList, targetLayout);
        recyclerViewDestinations.setAdapter(imageAdapter);

        Call<ArrayList<Accommodation>> mostPopular = ClientUtils.accommodationService.getMostPopular();
        mostPopular.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("HomeFragment", "Successful response: " + response.body());
                        mostPopularAccommodations = response.body();
                        getLoadPictures(mostPopularAccommodations);
                    } else {
                        Log.d("HomeFragment", "Response body is null");
                    }
                }  else {
                // Log error details
                    Log.d("HomeFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("HomeFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
                Log.d("HomeFragment", t.getMessage() != null?t.getMessage():"error");
            }
        });

    }

    private void getLoadPictures(ArrayList<Accommodation> mostPopularAccommodations) {
        Map<Long, List<Bitmap>> accommodationImageMap = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        AtomicInteger totalImagesToLoad = new AtomicInteger(0);
        Handler handler = new Handler(Looper.getMainLooper());

        for (Accommodation accommodation : mostPopularAccommodations) {
            List<Bitmap> photosBitmap = new ArrayList<>();
            for(Photo photo : accommodation.getPhotos()) {
                totalImagesToLoad.incrementAndGet();
                executorService.execute(() -> {
                    try {
                        Call<ResponseBody> photoCall = ClientUtils.photoService.loadPhoto(photo.getId());
                        Response<ResponseBody> response = photoCall.execute();
                        try {

                            if (response.isSuccessful()) {
                                byte[] photoData = response.body().bytes();

                                Bitmap bitmap = Glide.with(this)
                                        .asBitmap()
                                        .load(photoData)
                                        .override(300, 300)
                                        .submit()
                                        .get();
                                photosBitmap.add(bitmap);

                                int remainingImages = totalImagesToLoad.decrementAndGet();

                                if (remainingImages == 0) {
                                    handler.post(() -> {
                                        stopProgressBar();
                                        progressBar.setVisibility(View.GONE);
                                        darkBackground.setVisibility(View.GONE);
                                        if (isSearch) {
                                            openSearchFilterFragment(whereToGo, results, guestsNumber, checkIn, checkOut, accommodationImageMap);
                                        } else {
                                            startPopularFragment(accommodationImageMap);
                                        }
                                    });
                                }
                            } else {
                                Log.d("HomeFragment", "Error code " + response.code());
                            }
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } finally {
                            response.body().close(); // Close the response body after using it
                        }
                    } catch (IOException e) {
                        Log.e("HomeFragment", "Error reading response body: " + e.getMessage());
                    }
                });
            }
            accommodationImageMap.put(accommodation.getId(), photosBitmap);
        }
        executorService.shutdown();
    }


    private void startPopularFragment(Map<Long, List<Bitmap>> accommodationImages) {
        adapterPopular = new PopularAdapter(this, mostPopularAccommodations, targetLayout, accommodationImages);

        // Run the UI-related code on the main thread
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerViewPopular.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                recyclerViewPopular.setAdapter(adapterPopular);
                adapterPopular.notifyDataSetChanged();
            }
        });
    }




    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        try {
            Log.d("HomeFragment", "onDateSet called");
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(c.getTime());
            if (isStartDateButtonClicked) {
                checkInTxt.setText(formattedDate);
            } else {
                checkOutTxt.setText(formattedDate);
            }
        } catch (Exception e) {
            Log.e("HomeFragment", "Error in onDateSet", e);
        }
    }

}
