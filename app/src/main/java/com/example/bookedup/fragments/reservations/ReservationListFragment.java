package com.example.bookedup.fragments.reservations;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.adapters.ReservationAdapter;
import com.example.bookedup.adapters.TypeAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.enums.ReservationStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationListFragment extends Fragment implements TypeAdapter.TypeSelectionListener {

    private RecyclerView typeRecyclerView;
    private RecyclerView reservationRecyclerView;
    private TypeAdapter typeAdapter;
    private ReservationAdapter reservationAdapter;
    private int layout_caller;
    private List<Reservation> originalReservations = new ArrayList<Reservation>();
    private List<Reservation> reservations = new ArrayList<Reservation>();
    private Map<Long, List<Bitmap>> accommodationImages = new HashMap<>();

    public ReservationListFragment(List<Reservation> originalReservations, int layout_caller, Map<Long, List<Bitmap>> accommodationImages) {
        this.originalReservations = originalReservations;
        this.layout_caller = layout_caller;
        this.accommodationImages = accommodationImages;
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
        reservations.clear();
        reservations.addAll(originalReservations);
        initUI(view);
    }


    private void initUI(View view){
        typeRecyclerView = view.findViewById(R.id.frl_recyclerType);
        typeAdapter = new TypeAdapter(getTypeList(), this);
        typeRecyclerView.setAdapter(typeAdapter);
        typeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        reservationRecyclerView = view.findViewById(R.id.cards_myReservations);
        reservationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Context context = getContext();
        reservationAdapter = new ReservationAdapter(new ArrayList<>(reservations), context, layout_caller, this, accommodationImages);
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
        getAllReservations(selectedType);
    }

    private void updateReservationAdapter(String selectedType) {
        List<Reservation> updatedList = getUpdatedReservationList(selectedType);
        reservationAdapter.updateData(updatedList);
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
            reservationAdapter.updateData(reservations);
        }
    }

    private void getAllReservations(String selectedType){
        Call<ArrayList<Reservation>> myReservations = ClientUtils.reservationService.getReservationsByGuestId(LoginScreen.loggedGuest.getId());
        myReservations.enqueue(new Callback<ArrayList<Reservation>>() {
            @Override
            public void onResponse(Call<ArrayList<Reservation>> call, Response<ArrayList<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ReservationListFragment", "Successful response: " + response.body());
                    reservations = response.body();
                    updateReservationAdapter(selectedType);
                } else {
                    // Log error details
                    Log.d("ReservationListFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("ReservationListFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Reservation>> call, Throwable t) {
                Log.d("GuestMainScreen", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

}