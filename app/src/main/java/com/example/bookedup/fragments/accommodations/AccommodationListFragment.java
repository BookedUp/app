package com.example.bookedup.fragments.accommodations;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bookedup.R;
import com.example.bookedup.adapters.AccommodationAdapter;
import com.example.bookedup.adapters.TypeAdapter;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.enums.AccommodationStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccommodationListFragment extends Fragment implements TypeAdapter.TypeSelectionListener {

    private RecyclerView typeRecyclerView, accommodationRecyclerView;
    private TypeAdapter typeAdapter;
    private AccommodationAdapter accommodationAdapter;
    private int layout_caller;
    private List<Accommodation> accommodations = new ArrayList<>();
    private Map<Long, List<Bitmap>> accommodationImages = new HashMap<>();

    public AccommodationListFragment(Map<Long, List<Bitmap>> accommodationImages, int layout_caller, List<Accommodation> accommodations) {
        this.accommodationImages = accommodationImages;
        this.layout_caller = layout_caller;
        this.accommodations = accommodations;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accommodation_list, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getCallerData();
        initUI(view);

    }

//    private void getCallerData(){
//        Bundle arguments = getArguments();
//        if (arguments != null) {
//            String resultsJson = arguments.getString("resultsJson");
//            layout_caller = arguments.getInt("layout_caller");
//            Type type = new TypeToken<ArrayList<Accommodation>>() {}.getType();
//            accommodations = new Gson().fromJson(resultsJson, type);
//            Log.d("AccommodationListFragment", "ACC SIZE " + accommodations.size());
//        }
//    }

    private void initUI(View view){
        typeRecyclerView = view.findViewById(R.id.fal_recyclerType);
        typeAdapter = new TypeAdapter(getTypeList(), this);
        typeRecyclerView.setAdapter(typeAdapter);
        typeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        accommodationRecyclerView = view.findViewById(R.id.cards_myAccommodations);
        accommodationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Context context = getContext();
        accommodationAdapter = new AccommodationAdapter(accommodations, context, accommodationImages);
        accommodationRecyclerView.setAdapter(accommodationAdapter);
    }

    private List<String> getTypeList() {
        List<String> types = new ArrayList<>();
        types.add("All Accommodations");
        types.add("Waiting For Approval");
        types.add("Accepted");
        types.add("Rejected");
        return types;
    }

    @Override
    public void onTypeSelected(String selectedType) {
        updateAccommodationAdapter(selectedType);
    }

    private void updateAccommodationAdapter(String selectedType) {
        List<Accommodation> updatedList = getUpdatedAccommodationList(selectedType);
        accommodationAdapter = new AccommodationAdapter(updatedList, getContext(), accommodationImages);
        accommodationRecyclerView.setAdapter(accommodationAdapter);
    }

    private List<Accommodation> getUpdatedAccommodationList(String selectedType) {
        List<Accommodation> filteredList = new ArrayList<Accommodation>();

        switch (selectedType) {
            case "All Accommodations":
                filteredList.addAll(accommodations);
                break;
            case "Waiting For Approval":
                for (Accommodation accommodation : accommodations) {
                    if (accommodation.getStatus() == AccommodationStatus.CREATED || accommodation.getStatus() == AccommodationStatus.CHANGED) {
                        filteredList.add(accommodation);
                    }
                }
                break;
            case "Accepted":
                for (Accommodation accommodation : accommodations) {
                    if (accommodation.getStatus() == AccommodationStatus.ACTIVE) {
                        filteredList.add(accommodation);
                    }
                }
                break;
            case "Rejected":
                for (Accommodation accommodation : accommodations) {
                    if (accommodation.getStatus() == AccommodationStatus.REJECTED) {
                        filteredList.add(accommodation);
                    }
                }
                break;
        }
        if(filteredList.isEmpty()){
            Toast.makeText(getActivity(),"No results!",Toast.LENGTH_SHORT).show();
        }

        return filteredList;
    }

    private void openFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(layout_caller, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}