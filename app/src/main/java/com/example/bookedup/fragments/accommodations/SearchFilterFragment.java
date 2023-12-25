package com.example.bookedup.fragments.accommodations;

import android.app.DatePickerDialog;
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
//import com.example.bookedup.adapters.PopularAdapter;
import com.example.bookedup.adapters.CategoryAdapter;
import com.example.bookedup.adapters.SearchAccommodationAdapter;
import com.example.bookedup.fragments.accommodations.FilterFragment;
import com.example.bookedup.fragments.calendar.CalendarFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Address;
import com.example.bookedup.model.Category;
import com.example.bookedup.model.Destination;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.enums.AccommodationStatus;
import com.example.bookedup.model.enums.AccommodationType;
import com.example.bookedup.model.enums.PriceType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SearchFilterFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerViewCategory, recyclerViewResults;
    private RecyclerView.Adapter adapterResults, adapterCategory;

    private ImageView filter;
    private boolean isStartDateButtonClicked;
    private boolean isEndDateButtonClicked;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFilterFragment() {
        // Required empty public constructor
    }

    public static SearchFilterFragment newInstance(String param1, String param2) {
        SearchFilterFragment fragment = new SearchFilterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_filter, container, false);

        recyclerViewResults = view.findViewById(R.id.view_acc_results);
        recyclerViewCategory = view.findViewById(R.id.view_category);
        filter = view.findViewById(R.id.filter);

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

        return view;
    }

    private void setFilterClickListener() {
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterFragment();
            }
        });
    }

    private void openFilterFragment() {
        // Create a new instance of FilterFragment
        FilterFragment filterFragment = FilterFragment.newInstance(null, null);

        // Begin the transaction
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

        // Replace the current fragment with the new fragment
        transaction.replace(R.id.frame_layout, filterFragment);

        // Add the transaction to the back stack so the user can navigate back
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private void initRecycleView() {
        ArrayList<Accommodation> items = new ArrayList<>();

        Address address = new Address(1L, "Italy", "Paris", "1523", "John Smith 77", true, 45.125, 82.225);
        Photo photo = new Photo();
        List<Photo> photos = new ArrayList<Photo>();
        photos.add(photo);


        items.add(new Accommodation("Lakeside Motel", photos, address, 5.0, AccommodationType.Apartment, 500, PriceType.PER_GUEST));
        items.add(new Accommodation("Lakeside Motel", photos, address, 5.0, AccommodationType.Apartment, 500, PriceType.PER_GUEST));
        items.add(new Accommodation("Lakeside Motel", photos, address, 5.0, AccommodationType.Apartment, 500, PriceType.PER_GUEST));




        recyclerViewResults.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adapterResults = new SearchAccommodationAdapter(items);
        recyclerViewResults.setAdapter(adapterResults);


        ArrayList<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Hotel", "img_hotel"));
        categoryList.add(new Category("Hostel", "img_hostel"));
        categoryList.add(new Category("Apartment", "img_apartment"));
        categoryList.add(new Category("Resort", "img_resort"));
        categoryList.add(new Category("Villa", "img_villa"));

        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterCategory = new CategoryAdapter(categoryList);
        recyclerViewCategory.setAdapter(adapterCategory);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        try {

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
