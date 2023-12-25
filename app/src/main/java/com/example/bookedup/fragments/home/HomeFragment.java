package com.example.bookedup.fragments.home;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookedup.R;
import com.example.bookedup.adapters.ImageAdapter;
import com.example.bookedup.adapters.PopularAdapter;
import com.example.bookedup.adapters.CategoryAdapter;
import com.example.bookedup.fragments.accommodations.FilterFragment;
import com.example.bookedup.fragments.accommodations.SearchFilterFragment;
import com.example.bookedup.fragments.calendar.CalendarFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Address;
import com.example.bookedup.model.Category;
import com.example.bookedup.model.Destination;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.enums.AccommodationStatus;
import com.example.bookedup.model.enums.AccommodationType;
import com.example.bookedup.model.enums.PriceType;
//import com.example.bookedup.services.AccommodationService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerViewPopular, recyclerViewDestinations;
    private RecyclerView.Adapter adapterPopular;

//    private AccommodationService accommodationService;

    private ImageView filter;
    private boolean isStartDateButtonClicked;
    private boolean isEndDateButtonClicked;

    private static final String ARG_TARGET = "arg_target";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Integer mParam1;
    private String mParam2;

    private static int targetLayout;

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Intent intent = getActivity().getIntent();
//        Log.d("HomeFragment", "onCreateView called with targetLayout: " + intent);
        ComponentName componentName = intent.getComponent();
//        Log.d("HomeFragment", "onCreateView called with targetLayout: " + componentName.getClassName());
        if (componentName.getClassName().equals("com.example.bookedup.activities.GuestMainScreen")) {
//            Log.d("HomeFragment", "Trenutna aktivnost je GuestMainScreen");
            targetLayout = R.id.frame_layout;
        } else if (componentName.getClassName().equals("com.example.bookedup.activities.AdministratorMainScreen")){
            targetLayout = R.id.frame_layoutAdmin;
        } else if (componentName.getClassName().equals("com.example.bookedup.activities.HostMainScreen")){
            targetLayout = R.id.frame_layoutHost;
        }

        recyclerViewPopular = view.findViewById(R.id.view_pop);
        recyclerViewDestinations = view.findViewById(R.id.view_destinations);
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

        FloatingActionButton searchButton = view.findViewById(R.id.searchButtonHome);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HomeFragment", "Kliknuo");
                openSearchFilterFragment();
            }
        });




        return view;
    }

    public void setTargetLayout(int target){
        this.targetLayout = target;
    }

    private void openSearchFilterFragment() {
        SearchFilterFragment searchFilterFragment = new SearchFilterFragment();

        // Begin the transaction
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

        // Replace the current fragment with the new fragment
        transaction.replace(targetLayout, searchFilterFragment);

        // Add the transaction to the back stack so the user can navigate back if needed
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private void initRecycleView() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://adresa_vaseg_backenda.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

//        accommodationService = retrofit.create(AccommodationService.class);



        ArrayList<Accommodation> items = new ArrayList<>();
        Photo photo = new Photo();
        List<Photo> photos = new ArrayList<Photo>();
        photos.add(photo);
        Address address = new Address(1L, "Italy", "Paris", "1523", "John Smith 77", true, 45.125, 82.225);

        items.add(new Accommodation("Lakeside Motel", photos, address, 5));
        items.add(new Accommodation("Lakeside Motel", photos, address, 5));
        items.add(new Accommodation("Lakeside Hotel", photos, address, 5));

        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        Log.d("HomeFragment", "ISPREDDDDDDDDDDDDDDDD ");
        adapterPopular = new PopularAdapter(items, targetLayout);
        recyclerViewPopular.setAdapter(adapterPopular);

        ArrayList<Destination> destinationList = new ArrayList<>();
        destinationList.add(new Destination(R.drawable.australia, "Australia"));
        destinationList.add(new Destination(R.drawable.japan, "Japan"));
        destinationList.add(new Destination(R.drawable.new_zeland, "New Zealand"));
        destinationList.add(new Destination(R.drawable.dubai, "Dubai"));

        recyclerViewDestinations.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        ImageAdapter imageAdapter = new ImageAdapter(destinationList);
        recyclerViewDestinations.setAdapter(imageAdapter);

    }
//
//    Call<List<Accommodation>> findPopularCall = accommodationService.;
//    findPopularCall.enqueue(new Callback<List<Accommodation>>() {
//        @Override
//        public void onResponse(Call<List<Accommodation>> call, Response<List<Accommodation>> response) {
//            if (response.isSuccessful()) {
//                // Uspješan odgovor
//                List<Accommodation> popularAccommodations = response.body();
//
//                // Ažuriranje RecyclerView-a s novim podacima
//                updateRecyclerView(popularAccommodations);
//            } else {
//                // Greška u odgovoru
//                // Obrada greške
//            }
//        }
//
//        @Override
//        public void onFailure(Call<List<Accommodation>> call, Throwable t) {
//            // Greška u izvršavanju zahtjeva
//            // Obrada greške
//        }
//    });



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        try {
            Log.d("HomeFragment", "onDateSet called");
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Format the date as "year-month-day"
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(c.getTime());

            // Determine if it's the start date or end date
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
