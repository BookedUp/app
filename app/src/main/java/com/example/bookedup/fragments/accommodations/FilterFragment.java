package com.example.bookedup.fragments.accommodations;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bookedup.R;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.enums.AccommodationType;
import com.example.bookedup.model.enums.Amenity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterFragment extends Fragment {

    private ListView filtersBudget, filtersPopular, filtersCategory;
    private ArrayAdapter<String> budget_adapter, popular_adapter, category_adapter;
    private Button filter;
    private View lastClickedBudgetView, lastClickedTypeView;
    private String[] budgetFilter = {"0 - 500", "500 - 1000", "1000 - 1500", "1500 - 2000", "2000 and more"};
    private List<String> popularFilter = new ArrayList<String>();
    private List<String> categoryFilter = new ArrayList<String>();
    private List<Integer> selectedPopularFilters = new ArrayList<>();
    private List<Object> selectedAmenities = new ArrayList<Object>();
    private List<Accommodation> results = new ArrayList<Accommodation>();
    private String whereToGo, checkIn, checkOut;
    private Integer guestsNumber;
    private double minPrice = 0.0, maxPrice = 0.0;
    private AccommodationType type = null;
    private static int targetLayout;
    private Map<Long, List<Bitmap>> accommodationImages = new HashMap<>();
    public FilterFragment(Map<Long, List<Bitmap>> accommodationImages) {
        this.accommodationImages = accommodationImages;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCallerData();
        findTargetLayout();
        initView(view);
    }

    private void getCallerData(){
        Bundle arguments = getArguments();
        if (arguments != null) {
            whereToGo = arguments.getString("whereToGo");
            guestsNumber = arguments.getInt("guestsNumber");
            checkIn = arguments.getString("checkIn");
            checkOut = arguments.getString("checkOut");

            Log.d("SearchFilterFragment", "Location: " + whereToGo);
            Log.d("SearchFilterFragment", "GuestsNumber: " + guestsNumber);
            Log.d("SearchFilterFragment", "CheckIn: " + checkIn);
            Log.d("SearchFilterFragment", "CheckOut: " + checkOut);

        }
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

    private void initView(View view) {
        filtersBudget = view.findViewById(R.id.filterBudget);
        filtersPopular = view.findViewById(R.id.filtersPopular);
        filtersCategory = view.findViewById(R.id.filtersCategory);

        for (Amenity amenity : Amenity.values()){
            popularFilter.add(amenity.getAmenity());
        }

        for (AccommodationType type : AccommodationType.values()){
            categoryFilter.add(type.getAccommodationType());
        }

        filter = view.findViewById(R.id.filterBtn);

        budget_adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, budgetFilter);
        filtersBudget.setAdapter(budget_adapter);

        popular_adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, popularFilter);
        filtersPopular.setAdapter(popular_adapter);

        category_adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, categoryFilter);
        filtersCategory.setAdapter(category_adapter);

        filtersBudget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lastClickedBudgetView != null) {
                    lastClickedBudgetView.setBackgroundColor(getResources().getColor(android.R.color.white));
                }
                view.setBackgroundColor(getResources().getColor(R.color.neutral));
                lastClickedBudgetView = view;

                String  selectedBudget = (String) parent.getItemAtPosition(position);
                String[] prices = selectedBudget.split(" - ");
                minPrice = Double.parseDouble(prices[0]);
                maxPrice = Double.parseDouble(prices[1]);

                Log.d("FilterFragment", "MInPrice: " + minPrice);
                Log.d("FilterFragment", "MaxPrice: " + maxPrice);
            }
        });

        filtersPopular.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedPopularFilters.contains(position)) {
                    view.setBackgroundColor(getResources().getColor(android.R.color.white));
                    selectedPopularFilters.remove(Integer.valueOf(position));

                    String amenityStr = (String) parent.getItemAtPosition(position);
                    for (Amenity amenity : Amenity.values()){
                        if (amenity.getAmenity().equals(amenityStr)){
                            selectedAmenities.remove(amenity);
                        }
                    }

                    for (Object am : selectedAmenities){
                        Log.d("FilterFragment", "Selected Amenityyy:" + am);
                    }


                } else {
                    view.setBackgroundColor(getResources().getColor(R.color.neutral));
                    selectedPopularFilters.add(position);
                    String amenityStr = (String) parent.getItemAtPosition(position);
                    Log.d("FilterFragment", "Selected " + amenityStr);
                    for (Amenity amenity : Amenity.values()){
                        if (amenity.getAmenity().equals(amenityStr)){
                            selectedAmenities.add(amenity);
                        }
                    }

                    for (Object am : selectedAmenities){
                        Log.d("FilterFragment", "Selected Amenityyy:" + am);
                    }
                }

            }
        });

        filtersCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lastClickedTypeView != null) {
                    lastClickedTypeView.setBackgroundColor(getResources().getColor(android.R.color.white));
                }
                view.setBackgroundColor(getResources().getColor(R.color.neutral));
                lastClickedTypeView = view;

                String  selectedType = (String) parent.getItemAtPosition(position);
                for (AccommodationType accType : AccommodationType.values()){
                    if (accType.getAccommodationType().equalsIgnoreCase(selectedType)){
                        type = accType;
                    }
                }
            }
        });


        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFilter(whereToGo, guestsNumber, checkIn, checkOut);
            }
        });
    }

    private void searchFilter(String whereToGo, Integer guestsNumber, String checkIn, String checkOut) {

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
//        Log.d("FilterFragment", "StartDate " + checkIn);
//        Log.d("FilterFragment", "EndDate " + checkOut);
//        Log.d("FilterFragment", "Location " + whereToGo);
//        Log.d("FilterFragment", "GuestsNumber " + guestsNumber);
//        Log.d("FilterFragment", "Type " + type);

        Call<ArrayList<Accommodation>> searchedResults = ClientUtils.accommodationService.searchAccommodations(
                whereToGo,
                guestsNumber,
                dateFormat.format(startDate),
                dateFormat.format(endDate),
                selectedAmenities,
                minPrice,
                maxPrice,
                0.0,
                type,
                ""
        );

        searchedResults.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("FilterFragment", "Successful response: " + response.body());
                        results = response.body();
//                        for (Accommodation accommodation : results) {
//                            Log.d("FilterFragment", "Accommodation: " + accommodation);
//                        }
                        Log.d("FilterFragment", "Results size " + results.size());
                        openSearchFilterFragment(whereToGo, results, guestsNumber, checkIn, checkOut, accommodationImages);

                    } else {
                        Log.d("FilterFragment", "Response body is null");
                    }
                }  else {
                    // Log error details
                    Log.d("FilterFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("FilterFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.d("FilterFragment", "GRESKA response: " + response.body());
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
                Log.d("FilterFragment", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void openSearchFilterFragment(String whereToGo, List<Accommodation> results, Integer guestsNumber, String finalCheckIn, String finalCheckOut, Map<Long, List<Bitmap>> accommodationImages){
        Log.d("FilterFragment", "Results size " + results.size());
        SearchFilterFragment searchFilterFragment = new SearchFilterFragment(accommodationImages);

        Bundle bundle = new Bundle();
        bundle.putString("whereToGo", whereToGo);
        bundle.putInt("guestsNumber", guestsNumber);
        bundle.putString("checkIn", finalCheckIn);
        bundle.putString("checkOut", finalCheckOut);

        String resultsJson = new Gson().toJson(results);
        bundle.putString("resultsJson", resultsJson);

        searchFilterFragment.setArguments(bundle);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(targetLayout, searchFilterFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }





}
