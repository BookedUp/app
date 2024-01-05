package com.example.bookedup.fragments.home;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookedup.R;
import com.example.bookedup.adapters.PopularDestinationAdapter;
import com.example.bookedup.adapters.PopularAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.accommodations.SearchFilterFragment;
import com.example.bookedup.fragments.calendar.CalendarFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Destination;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerViewPopular, recyclerViewDestinations;
    private RecyclerView.Adapter adapterPopular;

    private ArrayList<Accommodation> mostPopularAccommodations;

    private ArrayList<Accommodation> results;
    private boolean isStartDateButtonClicked;
    private boolean isEndDateButtonClicked;
    private static int targetLayout;

    private EditText whereToGoTxt, guestsNumberTxt;

    private TextView checkInTxt, checkOutTxt;

    private Integer guestsNumber;


    public HomeFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Intent intent = getActivity().getIntent();
        ComponentName componentName = intent.getComponent();
        if (componentName.getClassName().equals("com.example.bookedup.activities.GuestMainScreen")) {
            targetLayout = R.id.frame_layout;
        } else if (componentName.getClassName().equals("com.example.bookedup.activities.AdministratorMainScreen")){
            targetLayout = R.id.frame_layoutAdmin;
        } else if (componentName.getClassName().equals("com.example.bookedup.activities.HostMainScreen")){
            targetLayout = R.id.frame_layoutHost;
        }
        FloatingActionButton searchButton = view.findViewById(R.id.searchButtonHome);

        recyclerViewPopular = view.findViewById(R.id.view_pop);
        recyclerViewDestinations = view.findViewById(R.id.view_destinations);

        whereToGoTxt = view.findViewById(R.id.location);
        checkInTxt = view.findViewById(R.id.checkInText);
        checkOutTxt = view.findViewById(R.id.checkOutText);
        guestsNumberTxt = view.findViewById(R.id.guestsNumber);
        initRecycleView();

        ImageView startDateBtn =  view.findViewById(R.id.startDate);
        ImageView endDateBtn =  view.findViewById(R.id.endDate);
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
                openSearchFilterFragment(whereToGoTxt, guestsNumberTxt, checkInTxt, checkOutTxt);
            }
        });

        return view;
    }

    private void openSearchFilterFragment(EditText whereToGoTxt, EditText guestsNumberTxt, TextView checkInTxt, TextView checkOutTxt) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date startDate = new Date();
        Date endDate = new Date();


        try {
            startDate = dateFormat.parse(checkInTxt.getText().toString());
            startDate.setHours(13);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            endDate = dateFormat.parse(checkOutTxt.getText().toString());
            endDate.setHours(13);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        List<Object> amenities = new ArrayList<>();
        if (!guestsNumberTxt.getText().toString().isEmpty()){
            guestsNumber = Integer.parseInt(guestsNumberTxt.getText().toString());
        } else {
            guestsNumber = 0;
        }
        Log.d("HomeFragment", "StartDate " + startDate);
        Log.d("HomeFragment", "EndDate " + endDate);
        Log.d("HomeFragment", "Location " + whereToGoTxt.getText().toString());
        Log.d("HomeFragment", "GuestsNumber " + guestsNumber);

        Call<ArrayList<Accommodation>> searchedResults = ClientUtils.accommodationService.searchAccommodations(
                whereToGoTxt.getText().toString(),
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

        Log.d("HomeFragment", "Prosaoooooo" + searchedResults);


        searchedResults.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("HomeFragment", "Successful response: " + response.body());
                        results = response.body();
                        for (Accommodation accommodation : results) {
                            Log.d("HomeFragment", "Accommodation: " + accommodation);
                        }

                        SearchFilterFragment searchFilterFragment = new SearchFilterFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("whereToGo", whereToGoTxt.getText().toString());
                        bundle.putInt("guestsNumber", guestsNumber);
                        bundle.putString("checkIn", checkInTxt.getText().toString());
                        bundle.putString("checkOut", checkOutTxt.getText().toString());
                        String resultsJson = new Gson().toJson(results);
                        bundle.putString("resultsJson", resultsJson);

                        searchFilterFragment.setArguments(bundle);

                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(targetLayout, searchFilterFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
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
                        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                        adapterPopular = new PopularAdapter(HomeFragment.this, mostPopularAccommodations, targetLayout);
                        recyclerViewPopular.setAdapter(adapterPopular);
                        adapterPopular.notifyDataSetChanged();
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
