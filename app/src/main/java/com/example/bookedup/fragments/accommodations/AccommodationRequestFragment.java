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
import com.example.bookedup.model.enums.AccommodationStatus;
import com.example.bookedup.utils.DataGenerator;

import java.util.ArrayList;
import java.util.List;

public class AccommodationRequestFragment extends Fragment implements TypeAdapter.TypeSelectionListener {

    private RecyclerView typeRecyclerView;
    private RecyclerView requestRecyclerView;

    private TypeAdapter typeAdapter;
    private RequestAdapter requestAdapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private List<Accommodation> allAccommodations;  // Dodajte varijablu za čuvanje svih smještaja

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

        // Inicijalizujte sve smještaje kada se fragment kreira
        allAccommodations = DataGenerator.generateAllAccommodationsRequests();
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
                    Log.d("AccommodationRequestFragment", "USAAAAAAAAAAAAAO");
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
        Log.d("AccommodationRequestFragment", "usao");
        return allAccommodations;  // Vraća sve smještaje
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
        List<Accommodation> filteredList = new ArrayList<>();

        switch (selectedType) {
            case "All Accommodations":
                filteredList.addAll(allAccommodations);  // Prikazi sve smještaje
                break;
            case "New":
                for (Accommodation accommodation : allAccommodations) {
                    if (accommodation.getStatus() == AccommodationStatus.CREATED) {
                        filteredList.add(accommodation);
                    }
                }
                break;
            case "Changed":
                for (Accommodation accommodation : allAccommodations) {
                    if (accommodation.getStatus() == AccommodationStatus.CHANGED) {
                        filteredList.add(accommodation);
                    }
                }
                break;
        }

        return filteredList;
    }
}
