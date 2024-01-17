package com.example.bookedup.fragments.reservations;

import android.content.Context;
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
import android.widget.Button;
import android.widget.Toast;

import com.example.bookedup.R;
import com.example.bookedup.activities.AdministratorMainScreen;
import com.example.bookedup.adapters.AccommodationAdapter;
import com.example.bookedup.adapters.RequestAdapter;
import com.example.bookedup.adapters.ReservationAdapter;
import com.example.bookedup.adapters.TypeAdapter;
import com.example.bookedup.fragments.accommodations.AccommodationListFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Address;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.enums.AccommodationStatus;
import com.example.bookedup.model.enums.PriceType;
import com.example.bookedup.model.enums.ReservationStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReservationListFragment extends Fragment implements TypeAdapter.TypeSelectionListener {

    private RecyclerView typeRecyclerView;
    private RecyclerView reservationRecyclerView;
    private TypeAdapter typeAdapter;
    private ReservationAdapter reservationAdapter;
    private int layout_caller;
    private List<Reservation> reservations = new ArrayList<Reservation>();
    public ReservationListFragment() {}

    public static ReservationListFragment newInstance(String param1, String param2) {
        ReservationListFragment fragment = new ReservationListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation_list, container, false);
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
            layout_caller = arguments.getInt("layout_caller");
            Type type = new TypeToken<ArrayList<Reservation>>() {}.getType();
            reservations = new Gson().fromJson(resultsJson, type);
            Log.d("ReservationListFragment", "ACC SIZE " + reservations.size());
        }
    }

    private void initUI(View view){
        typeRecyclerView = view.findViewById(R.id.frl_recyclerType);
        typeAdapter = new TypeAdapter(getTypeList(), this);
        typeRecyclerView.setAdapter(typeAdapter);
        typeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        reservationRecyclerView = view.findViewById(R.id.cards_myReservations);
        reservationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Context context = getContext();
        reservationAdapter = new ReservationAdapter(reservations, context, layout_caller, this);
        reservationRecyclerView.setAdapter(reservationAdapter);
    }

    private List<String> getTypeList() {
        List<String> types = new ArrayList<>();
        types.add("All Reservations");
        types.add("Waiting For Approval");
        types.add("Accepted");
        types.add("Rejected");
        types.add("Cancelled");
        types.add("Completed");
        return types;
    }


    @Override
    public void onTypeSelected(String selectedType) {
        updateReservationAdapter(selectedType);
    }

    private void updateReservationAdapter(String selectedType) {
        List<Reservation> updatedList = getUpdatedReservationList(selectedType);
        reservationAdapter = new ReservationAdapter(updatedList, getContext(), layout_caller, this);
        reservationRecyclerView.setAdapter(reservationAdapter);
    }

    private List<Reservation> getUpdatedReservationList(String selectedType) {
        List<Reservation> filteredList = new ArrayList<Reservation>();

        switch (selectedType) {
            case "All Reservations":
                filteredList.addAll(reservations);
                break;
            case "Waiting For Approval":
                for (Reservation reservation : reservations) {
                    if (reservation.getStatus() == ReservationStatus.CREATED) {
                        filteredList.add(reservation);
                    }
                }
                break;
            case "Accepted":
                for (Reservation reservation : reservations) {
                    if (reservation.getStatus() == ReservationStatus.ACCEPTED) {
                        filteredList.add(reservation);
                    }
                }
                break;
            case "Rejected":
                for (Reservation reservation : reservations) {
                    if (reservation.getStatus() == ReservationStatus.REJECTED) {
                        filteredList.add(reservation);
                    }
                }
                break;
            case "Cancelled":
                for (Reservation reservation : reservations) {
                    if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                        filteredList.add(reservation);
                    }
                }
                break;
            case "Completed":
                for (Reservation reservation : reservations) {
                    if (reservation.getStatus() == ReservationStatus.COMPLETED) {
                        filteredList.add(reservation);
                    }
                }
                break;
        }
        if(filteredList.isEmpty()){
            Toast.makeText(getActivity(),"No results!",Toast.LENGTH_SHORT).show();
        }

        return filteredList;
    }

    public void updateReservationList(ArrayList<Reservation> updatedList) {
        if (reservationRecyclerView != null) {
            reservations.clear();
            reservations.addAll(updatedList);
            reservationAdapter.updateData(updatedList);
        }
    }
}