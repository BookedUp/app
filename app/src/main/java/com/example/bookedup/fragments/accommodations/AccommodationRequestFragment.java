package com.example.bookedup.fragments.accommodations;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.adapters.PopularAdapter;
import com.example.bookedup.adapters.RequestAdapter;
import com.example.bookedup.adapters.TypeAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.home.HomeFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.enums.AccommodationStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import com.example.bookedup.utils.DataGenerator;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccommodationRequestFragment extends Fragment implements TypeAdapter.TypeSelectionListener {

    private RecyclerView typeRecyclerView;
    private RecyclerView requestRecyclerView;

    private TypeAdapter typeAdapter;
    private RequestAdapter requestAdapter;

    private List<Accommodation> allAccommodations = new ArrayList<>();

    private List<Accommodation> copyAccommodations = new ArrayList<>();

    private List<Accommodation> filteredList = new ArrayList<>();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public AccommodationRequestFragment() {}

    public static AccommodationRequestFragment newInstance(String param1, String param2) {
        AccommodationRequestFragment fragment = new AccommodationRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accommodation_request, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCallerData();
        initUI(view);
    }

    private void getCallerData(){
        Bundle arguments = getArguments();
        if (arguments != null) {
            String resultsJson = arguments.getString("resultsJson");
            Type type = new TypeToken<ArrayList<Accommodation>>() {}.getType();
            allAccommodations = new Gson().fromJson(resultsJson, type);
            Log.d("AccommodationRequestFragment", "ACC SIZE " + allAccommodations.size());
        }
    }

    private void initUI(View view){
        requestRecyclerView = view.findViewById(R.id.cards_accommodationRequests);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestAdapter = new RequestAdapter(new ArrayList<>(allAccommodations), getContext(), this);
        requestRecyclerView.setAdapter(requestAdapter);


        typeRecyclerView = view.findViewById(R.id.far_recyclerType);
        typeAdapter = new TypeAdapter(getTypeList(), this);
        typeRecyclerView.setAdapter(typeAdapter);
        typeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }


    private List<String> getTypeList() {
        List<String> types = new ArrayList<>();
        types.add("All Accommodations");
        types.add("New");
        types.add("Changed");
        return types;
    }

    @Override
    public void onTypeSelected(String selectedType) {
        updateRequestAdapter(selectedType);
    }

    private void updateRequestAdapter(String selectedType) {
        List<Accommodation> updatedList = getUpdatedRequestList(selectedType);
        requestAdapter.updateData(updatedList);
    }

    private List<Accommodation> getUpdatedRequestList(String selectedType) {

        switch (selectedType) {
            case "All Accommodations":
                Log.d("AccommodationRequestFragment", "size " + allAccommodations.size());
                filteredList.clear();
                filteredList.addAll(allAccommodations);
                break;
            case "New":
                Log.d("AccommodationRequestFragment", "size " + allAccommodations.size());
                filteredList.clear();
                for (Accommodation accommodation : allAccommodations) {
                    if (accommodation.getStatus() == AccommodationStatus.CREATED) {
                        filteredList.add(accommodation);
                    }
                }
                break;
            case "Changed":
                Log.d("AccommodationRequestFragment", "size " + allAccommodations.size());
                filteredList.clear();
                for (Accommodation accommodation : allAccommodations) {
                    if (accommodation.getStatus() == AccommodationStatus.CHANGED) {
                        filteredList.add(accommodation);
                    }
                }
                break;
        }

        return filteredList;
    }

    public void updateAccommodationList(List<Accommodation> updatedList) {
        if (requestRecyclerView != null) {
            allAccommodations.clear();
            allAccommodations.addAll(updatedList);
            requestAdapter.updateData(updatedList);
        }
    }

}
