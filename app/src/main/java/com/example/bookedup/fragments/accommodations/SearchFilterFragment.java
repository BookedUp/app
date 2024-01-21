package com.example.bookedup.fragments.accommodations;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.adapters.SearchAccommodationAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.calendar.CalendarFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Category;;
import com.example.bookedup.model.Photo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFilterFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerViewResults;
    private RecyclerView.Adapter adapterResults;
    private ImageView filter, startDateBtn, endDateBtn;
    private boolean isStartDateButtonClicked, isEndDateButtonClicked;
    private FloatingActionButton search;
    private ArrayList<Accommodation> results = new ArrayList<Accommodation>();
    private static int targetLayout;
    private String whereToGo, checkIn, checkOut;
    private Integer guestsNumber;
    private EditText whereToGoTxt, guestsNumberTxt;
    private TextView checkInTxt, checkOutTxt;
//    private ArrayList<Accommodation> searchResults = new ArrayList<Accommodation>();
    private ArrayList<Category> categoryList = new ArrayList<>();
    private Map<Long, List<Bitmap>> accommodationImages = new HashMap<>();

    public SearchFilterFragment(Map<Long, List<Bitmap>> accommodationImages) {
        this.accommodationImages = accommodationImages;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_filter, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        findTargetLayout();
        getCallerData();
        initRecycleView();

        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new CalendarFragment();
                datePicker.setTargetFragment(SearchFilterFragment.this, 0);
                datePicker.show(requireActivity().getSupportFragmentManager(), "date picker");
                isStartDateButtonClicked = true;
                isEndDateButtonClicked = false;
            }
        });

        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new CalendarFragment();
                datePicker.setTargetFragment(SearchFilterFragment.this, 0);
                datePicker.show(requireActivity().getSupportFragmentManager(), "date picker");
                isEndDateButtonClicked = true;
                isStartDateButtonClicked = false;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!guestsNumberTxt.getText().toString().isEmpty()){
                    guestsNumber = Integer.parseInt(guestsNumberTxt.getText().toString());
                } else {
                    guestsNumber = 0;
                }
                openFilterFragment(whereToGoTxt.getText().toString(), guestsNumber, checkInTxt.getHint().toString(), checkOutTxt.getHint().toString());
            }
        });

    }

    private void initView(View view){
        recyclerViewResults = view.findViewById(R.id.view_acc_results);

        filter = view.findViewById(R.id.filter);
        search = view.findViewById(R.id.searchButton);

        whereToGoTxt = view.findViewById(R.id.locationTxt);
        guestsNumberTxt = view.findViewById(R.id.guestsNumber);
        checkInTxt = view.findViewById(R.id.checkInText);
        checkOutTxt = view.findViewById(R.id.checkOutText);

        startDateBtn =  view.findViewById(R.id.startDate);
        endDateBtn =  view.findViewById(R.id.endDate);
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

    private void getCallerData(){
        Bundle arguments = getArguments();
        if (arguments != null) {
            whereToGo = arguments.getString("whereToGo");
            guestsNumber = arguments.getInt("guestsNumber");
            checkIn = arguments.getString("checkIn");
            checkOut = arguments.getString("checkOut");
            String resultsJson = arguments.getString("resultsJson");
            Type type = new TypeToken<ArrayList<Accommodation>>(){}.getType();
            results = new Gson().fromJson(resultsJson, type);

            if (results.isEmpty()){
                Toast.makeText(getContext(), "No results!", Toast.LENGTH_SHORT).show();
            }

            whereToGoTxt.setText(whereToGo);
            guestsNumberTxt.setText(String.valueOf(guestsNumber));
            checkInTxt.setHint(checkIn);
            checkOutTxt.setHint(checkOut);
        }
    }

    private void searchFilter() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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

//        Log.d("HomeFragment", "StartDate " + startDate);
//        Log.d("HomeFragment", "EndDate " + endDate);
//        Log.d("HomeFragment", "Location " + whereToGo.getText().toString());
//        Log.d("HomeFragment", "GuestsNumber " + guestsNumber);

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

        searchedResults.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("SearchFilterFragment", "Successful response: " + response.body());
                        results = response.body();
                        for (Accommodation accommodation : results) {
                            Log.d("SearchFilterFragment", "Accommodation: " + accommodation);
                        }
//                        openSearchFilterFragment(whereToGo, results, guestsNumber, checkIn, checkOut);
                        getLoadPictures(results);
                    } else {
                        Toast.makeText(getContext(), "No accommodations found", Toast.LENGTH_SHORT).show();
                    }
                }  else {
                    // Log error details
                    Log.d("SearchFilterFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("SearchFilterFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.d("SearchFilterFragment", "GRESKA response: " + response.body());
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
                Log.d("SearchFilterFragment", t.getMessage() != null?t.getMessage():"error");
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


    private void openFilterFragment(String whereToGo, Integer guestsNumber, String checkIn, String checkOut) {
        FilterFragment filterFragment = new FilterFragment(accommodationImages);

        Bundle bundle = new Bundle();
        bundle.putString("whereToGo", whereToGo);
        bundle.putInt("guestsNumber", guestsNumber);
        bundle.putString("checkIn", checkIn);
        bundle.putString("checkOut", checkOut);
        filterFragment.setArguments(bundle);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(targetLayout, filterFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void initRecycleView() {

        recyclerViewResults.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adapterResults = new SearchAccommodationAdapter(results, targetLayout, checkIn, checkOut, guestsNumber, accommodationImages);
        recyclerViewResults.setAdapter(adapterResults);
    }

    private void getLoadPictures(ArrayList<Accommodation> results) {
        Map<Long, List<Bitmap>> accommodationImageMap = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        AtomicInteger totalImagesToLoad = new AtomicInteger(0);

        for (Accommodation accommodation : results) {
            List<Bitmap> photosBitmap = new ArrayList<>();
            for(Photo photo : accommodation.getPhotos()) {
                totalImagesToLoad.incrementAndGet();
                executorService.execute(() -> {
                    try {
                        Call<ResponseBody> photoCall = ClientUtils.photoService.loadPhoto(photo.getId());
                        Response<ResponseBody> response = photoCall.execute();

                        if (response.isSuccessful()) {
                            byte[] photoData = response.body().bytes();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
                            photosBitmap.add(bitmap);

                            // Smanjite broj preostalih slika koje treba učitati
                            int remainingImages = totalImagesToLoad.decrementAndGet();

                            if (remainingImages == 0) {
                                // All images are loaded, update the adapter
                                openSearchFilterFragment(whereToGo, results, guestsNumber, checkIn, checkOut, accommodationImageMap);
                            }
                        } else {
                            Log.d("SearchFilterFragment", "Error code " + response.code());
                        }
                    } catch (IOException e) {
                        Log.e("SearchFilterFragment", "Error reading response body: " + e.getMessage());
                    }
                });
            }
            accommodationImageMap.put(accommodation.getId(), photosBitmap);
        }
        executorService.shutdown();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        try {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(c.getTime());
            TextView textView;
            if (isStartDateButtonClicked) {
                textView = getView().findViewById(R.id.checkInText);
            } else {
                textView = getView().findViewById(R.id.checkOutText);
            }

            if (textView != null) {
                textView.setText(formattedDate);
            }
        } catch (Exception e) {
            Log.e("SearchFilterFragment", "Error in onDateSet", e);
        }
    }
}
