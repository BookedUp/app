package com.example.bookedup.fragments.accommodations;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.adapters.RequestAdapter;
import com.example.bookedup.adapters.TypeAdapter;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Address;
import com.example.bookedup.model.DateRange;
import com.example.bookedup.model.Host;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.enums.AccommodationStatus;
import com.example.bookedup.model.enums.AccommodationType;
import com.example.bookedup.model.enums.PriceType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AccommodationRequestFragment extends Fragment implements TypeAdapter.TypeSelectionListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView typeRecyclerView;
    private RecyclerView requestRecyclerView;

    private TypeAdapter typeAdapter;
    private RequestAdapter requestAdapter;

    public AccommodationRequestFragment() {
        // Required empty public constructor
    }

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

        // Inicijalizacija i postavljanje TypeAdapter-a
        typeRecyclerView = view.findViewById(R.id.far_recyclerType);
        typeAdapter = new TypeAdapter(getTypeList(), this);
        typeRecyclerView.setAdapter(typeAdapter);
        typeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Inicijalizacija i postavljanje RequestAdapter-a
        requestRecyclerView = view.findViewById(R.id.cards_accommodationRequests);
        if (requestRecyclerView != null) {
            // Postavljanje layout manager-a (npr. LinearLayoutManager)
            requestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            // Obezbedite da getRequestList() ne vraća null
            List<Accommodation> requestList = getRequestList();
            if (requestList != null) {
                // Obezbedite da getContext() ne vraća null
                Context context = getContext();
                if (context != null) {
                    // Kreirajte RequestAdapter samo ako requestList i context nisu null
                    requestAdapter = new RequestAdapter(requestList, context);
                    requestRecyclerView.setAdapter(requestAdapter);
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
        types.add("All Accommodations");
        types.add("New");
        types.add("Changed");
        return types;
    }

    private List<Accommodation> getRequestList() {
        List<Accommodation> accommodations = new ArrayList<>();


        Accommodation accommodation = new Accommodation();
        accommodation.setName("pls");
        accommodation.setAverageRating(3.5);
        accommodation.setStatus(AccommodationStatus.ACTIVE);

        Address address = new Address(1L,"Drzva", "city", "21424", "sajdas", true, 23.9,48.0);
        accommodation.setAddress(address);

        accommodation.setPrice(100.0);
        accommodation.setPriceType(PriceType.PER_GUEST);

        Photo photo = new Photo(1L,"url", "", true);

        List<Photo>photos = new ArrayList<>();
        photos.add(photo);

        accommodation.setPhotos(photos);
        accommodations.add(accommodation);

        return accommodations;
    }

    @Override
    public void onTypeSelected(String selectedType) {
        // Ovdje možete promijeniti prikaz na temelju odabranog tipa
        // Primjerice, ažurirajte RequestAdapter prema odabranom tipu
        updateRequestAdapter(selectedType);
    }

    private void updateRequestAdapter(String selectedType) {
        // Ovdje možete ažurirati RequestAdapter prema odabranom tipu
        // Na primjer, dohvatite nove podatke prema odabranom tipu i postavite ih na RequestAdapter
        List<Accommodation> updatedList = getUpdatedRequestList(selectedType);
        requestAdapter.updateData(updatedList);
    }

    private List<Accommodation> getUpdatedRequestList(String selectedType) {
        // Ovdje možete dohvatiti nove podatke prema odabranom tipu
        // Vratite ažuriranu listu smještaja
        // Primjerice, dohvatite nove podatke iz baze podataka ili web servisa
        return new ArrayList<>(); // Vratite ažuriranu listu
    }
}