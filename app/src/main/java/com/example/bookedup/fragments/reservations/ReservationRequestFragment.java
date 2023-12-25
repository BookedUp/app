package com.example.bookedup.fragments.reservations;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookedup.R;
import com.example.bookedup.adapters.ReservationAdapter;
import com.example.bookedup.adapters.ReservationRequestAdapter;
import com.example.bookedup.adapters.TypeAdapter;
import com.example.bookedup.model.Reservation;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservationRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservationRequestFragment extends Fragment implements TypeAdapter.TypeSelectionListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView typeRecyclerView;
    private RecyclerView reservationRecyclerView;

    private TypeAdapter typeAdapter;
    private ReservationRequestAdapter reservationAdapter;

    public ReservationRequestFragment() {
        // Required empty public constructor
    }

    public static ReservationRequestFragment newInstance(String param1, String param2) {
        ReservationRequestFragment fragment = new ReservationRequestFragment();
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
        View view = inflater.inflate(R.layout.fragment_reservation_request, container, false);

        // Inicijalizacija i postavljanje TypeAdapter-a
        typeRecyclerView = view.findViewById(R.id.frr_recyclerType);
        typeAdapter = new TypeAdapter(getTypeList(), this);
        typeRecyclerView.setAdapter(typeAdapter);
        typeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Inicijalizacija i postavljanje RequestAdapter-a
        reservationRecyclerView = view.findViewById(R.id.cards_reservationRequests);
        if (reservationRecyclerView != null) {
            reservationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
            List<Reservation> reservationList = getReservationList();
            if (reservationList != null) {
                // Obezbedite da getContext() ne vraća null
                Context context = getContext();
                if (context != null) {
                    // Kreirajte RequestAdapter samo ako requestList i context nisu null
                    reservationAdapter = new ReservationRequestAdapter(reservationList, context);
                    reservationRecyclerView.setAdapter(reservationAdapter);
                } else {
                    Log.d("AccommodationRequestFrag", "getContext() returned null");
                }
            } else {
                Log.d("AccommodationRequestFrag", "getRequestList() returned null");
            }
        } else {
            Log.d("AccommodationRequestFrag", "RecyclerView is null");
        }

        return view;
    }

    private List<String> getTypeList() {
        // Vraća listu tipova koje želite prikazati
        List<String> types = new ArrayList<>();
        types.add("All Reservations");
        types.add("Waiting For Approval");
        types.add("Accepted");
        types.add("Rejected");
        types.add("Cancelled");
        types.add("Completed");
        return types;
    }

    private List<Reservation> getReservationList() {
        List<Reservation> reservations = new ArrayList<>();


//        Accommodation accommodation = new Accommodation();
//        accommodation.setName("pls");
//        accommodation.setAverageRating(3.5);
//        accommodation.setStatus(AccommodationStatus.ACTIVE);
//
//        Address address = new Address(1L,"Drzva", "city", "21424", "sajdas", true, 23.9,48.0);
//        accommodation.setAddress(address);
//
//        accommodation.setPrice(100.0);
//        accommodation.setPriceType(PriceType.PER_GUEST);
//
//        Photo photo = new Photo(1L,"url", "", true);
//
//        List<Photo>photos = new ArrayList<>();
//        photos.add(photo);
//
//        accommodation.setPhotos(photos);
//        accommodations.add(accommodation);

        return reservations;
    }

    @Override
    public void onTypeSelected(String selectedType) {
        // Ovdje možete promijeniti prikaz na temelju odabranog tipa
        // Primjerice, ažurirajte RequestAdapter prema odabranom tipu
        updateReservationAdapter(selectedType);
    }

    private void updateReservationAdapter(String selectedType) {
        // Ovdje možete ažurirati RequestAdapter prema odabranom tipu
        // Na primjer, dohvatite nove podatke prema odabranom tipu i postavite ih na RequestAdapter
        List<Reservation> updatedList = getUpdatedReservationList(selectedType);
        reservationAdapter.updateData(updatedList);
    }

    private List<Reservation> getUpdatedReservationList(String selectedType) {
        // Ovdje možete dohvatiti nove podatke prema odabranom tipu
        // Vratite ažuriranu listu smještaja
        // Primjerice, dohvatite nove podatke iz baze podataka ili web servisa
        return new ArrayList<>(); // Vratite ažuriranu listu
    }
}