package com.example.bookedup.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookedup.R;
import com.example.bookedup.adapters.PopularAdapter;
import com.example.bookedup.adapters.CategoryAdapter;
import com.example.bookedup.fragments.accommodations.FilterFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Category;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPopular, recyclerViewCategory;
    private RecyclerView.Adapter adapterPopular, adapterCategory;

    private ImageView filter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewPopular = view.findViewById(R.id.view_pop);
        recyclerViewCategory = view.findViewById(R.id.view_category);
        filter = view.findViewById(R.id.filter);

        initRecycleView();
        setFilterClickListener();

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
        items.add(new Accommodation("Lakeside Motel", "Paris", "Featuring free WiFi throughout the property, Lakeside Motel Waterfront offers accommodations in Lakes Entrance, 19 mi from Bairnsdale. Free private parking is available on site.\n" +
                "\n" +
                "Each room at this motel is air conditioned and comes with a flat-screen TV. You will find a kettle, toaster and a microwave in the room. Each room is fitted with a private bathroom. Guests have access to barbecue facilities and a lovely large lawn area. Metung is 6.8 mi from Lakeside Motel Waterfront, while Paynesville is 14 mi from the property.\n" +
                "\n" +
                "Couples in particular like the location – they rated it 9.2 for a two-person trip.", 2, 4.8, "hotel1", true, true, 180));
        items.add(new Accommodation("Julia Dens Resort", "Greece", "Featuring free WiFi throughout the property, Lakeside Motel Waterfront offers accommodations in Lakes Entrance, 19 mi from Bairnsdale. Free private parking is available on site.\n" +
                "\n" +
                "Each room at this motel is air conditioned and comes with a flat-screen TV. You will find a kettle, toaster and a microwave in the room. Each room is fitted with a private bathroom. Guests have access to barbecue facilities and a lovely large lawn area. Metung is 6.8 mi from Lakeside Motel Waterfront, while Paynesville is 14 mi from the property.\n" +
                "\n" +
                "Couples in particular like the location – they rated it 9.2 for a two-person trip.", 2, 4.5, "hotel2", true, true, 220));
        items.add(new Accommodation("Marineford Hotel", "Turkey", "Featuring free WiFi throughout the property, Lakeside Motel Waterfront offers accommodations in Lakes Entrance, 19 mi from Bairnsdale. Free private parking is available on site.\n" +
                "\n" +
                "Each room at this motel is air conditioned and comes with a flat-screen TV. You will find a kettle, toaster and a microwave in the room. Each room is fitted with a private bathroom. Guests have access to barbecue facilities and a lovely large lawn area. Metung is 6.8 mi from Lakeside Motel Waterfront, while Paynesville is 14 mi from the property.\n" +
                "\n" +
                "Couples in particular like the location – they rated it 9.2 for a two-person trip.", 2, 4.7, "hotel3", false, true, 250));

        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterPopular = new PopularAdapter(items);
        recyclerViewPopular.setAdapter(adapterPopular);

        ArrayList<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Hotels", "hotel"));
        categoryList.add(new Category("Hostel", "hostel"));
        categoryList.add(new Category("Apartment", "apartmentpic"));
        categoryList.add(new Category("Resort", "resortpic"));
        categoryList.add(new Category("Villa", "villapic"));

        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterCategory = new CategoryAdapter(categoryList);
        recyclerViewCategory.setAdapter(adapterCategory);
    }
}
