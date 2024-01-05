package com.example.bookedup.fragments.accommodations;

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
import android.widget.Toast;

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
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFilterFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerViewResults;
    private RecyclerView.Adapter adapterResults;

    private ImageView filter;
    private boolean isStartDateButtonClicked;
    private boolean isEndDateButtonClicked;

    private FloatingActionButton search;

    private ArrayList<Accommodation> results = new ArrayList<Accommodation>();

    private static int targetLayout;

    private String whereToGo, checkIn, checkOut;

    private Integer guestsNumber;

    private EditText whereToGoTxt, guestsNumberTxt;

    private TextView checkInTxt, checkOutTxt;

    private ArrayList<Accommodation> searchResults = new ArrayList<Accommodation>();

    private ArrayList<Category> categoryList = new ArrayList<>();

    public SearchFilterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_filter, container, false);

        recyclerViewResults = view.findViewById(R.id.view_acc_results);

        filter = view.findViewById(R.id.filter);
        search = view.findViewById(R.id.searchButton);

        whereToGoTxt = view.findViewById(R.id.locationTxt);
        guestsNumberTxt = view.findViewById(R.id.guestsNumber);
        checkInTxt = view.findViewById(R.id.checkInText);
        checkOutTxt = view.findViewById(R.id.checkOutText);


        Intent intent = getActivity().getIntent();
        ComponentName componentName = intent.getComponent();
        if (componentName.getClassName().equals("com.example.bookedup.activities.GuestMainScreen")) {
            targetLayout = R.id.frame_layout;
        } else if (componentName.getClassName().equals("com.example.bookedup.activities.AdministratorMainScreen")){
            targetLayout = R.id.frame_layoutAdmin;
        } else if (componentName.getClassName().equals("com.example.bookedup.activities.HostMainScreen")){
            targetLayout = R.id.frame_layoutHost;
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            whereToGo = arguments.getString("whereToGo");
            guestsNumber = arguments.getInt("guestsNumber");
            checkIn = arguments.getString("checkIn");
            checkOut = arguments.getString("checkOut");
            String resultsJson = arguments.getString("resultsJson");
            Type type = new TypeToken<ArrayList<Accommodation>>(){}.getType();
            searchResults = new Gson().fromJson(resultsJson, type);

            if (searchResults.isEmpty()){
                Toast.makeText(getContext(), "No results!", Toast.LENGTH_SHORT).show();
            }

            Log.d("SearchFilterFragment", "Location: " + whereToGo);
            Log.d("SearchFilterFragment", "GuestsNumber: " + guestsNumber);
            Log.d("SearchFilterFragment", "CheckIn: " + checkIn);
            Log.d("SearchFilterFragment", "CheckOut: " + checkOut);
            Log.d("SearchFilterFragment", "SearchResults size: " + searchResults.size());

            whereToGoTxt.setText(whereToGo);
            guestsNumberTxt.setText(String.valueOf(guestsNumber));
            if (!checkIn.isEmpty()){
                checkInTxt.setHint(checkIn);
            } else {
                checkInTxt.setHint("Check-In");
            }
            if (!checkOut.isEmpty()){
                checkOutTxt.setHint(checkOut);
            } else {
                checkOutTxt.setHint("Check-Out");
            }
        }
        initRecycleView();
        setFilterClickListener();

        ImageView startDateBtn =  view.findViewById(R.id.startDate);
        ImageView endDateBtn =  view.findViewById(R.id.endDate);

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
                openSearchFilterFragment(whereToGoTxt, guestsNumberTxt, checkInTxt, checkOutTxt);
            }
        });



        return view;
    }

    private void openSearchFilterFragment(EditText whereToGo, EditText guestsNumberTxt, TextView checkIn, TextView checkOut) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date startDate = new Date();
        Date endDate = new Date();


        try {
            startDate = dateFormat.parse(checkIn.getText().toString());
            startDate.setHours(13);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            endDate = dateFormat.parse(checkOut.getText().toString());
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
        Log.d("HomeFragment", "Location " + whereToGo.getText().toString());
        Log.d("HomeFragment", "GuestsNumber " + guestsNumber);

        Call<ArrayList<Accommodation>> searchedResults = ClientUtils.accommodationService.searchAccommodations(
                whereToGo.getText().toString(),
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
                            Log.d("PopularAdapter", "Accommodation: " + accommodation);
                        }

                        SearchFilterFragment searchFilterFragment = new SearchFilterFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("whereToGo", whereToGo.getText().toString());
                        bundle.putInt("guestsNumber", guestsNumber);
                        bundle.putString("checkIn", checkIn.getText().toString());
                        bundle.putString("checkOut", checkOut.getText().toString());
                        String resultsJson = new Gson().toJson(results);
                        bundle.putString("resultsJson", resultsJson);

                        searchFilterFragment.setArguments(bundle);

                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(targetLayout, searchFilterFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        Toast.makeText(getContext(), "No accommodations found", Toast.LENGTH_SHORT).show();
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

    private void setFilterClickListener() {
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterFragment(whereToGoTxt, guestsNumberTxt, checkInTxt, checkOutTxt);
            }
        });
    }

    private void openFilterFragment(EditText whereToGo, EditText guestsNumber, TextView checkIn, TextView checkOut) {
        FilterFragment filterFragment = new FilterFragment();

        Bundle bundle = new Bundle();
        bundle.putString("whereToGo", whereToGo.getText().toString());
        bundle.putInt("guestsNumber", Integer.valueOf(guestsNumber.getText().toString()));
        bundle.putString("checkIn", checkIn.getText().toString());
        bundle.putString("checkOut", checkOut.getText().toString());
        filterFragment.setArguments(bundle);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(targetLayout, filterFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void initRecycleView() {

        recyclerViewResults.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adapterResults = new SearchAccommodationAdapter(searchResults, targetLayout);
        recyclerViewResults.setAdapter(adapterResults);
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
            Log.e("HomeFragment", "Error in onDateSet", e);
        }
    }
}
