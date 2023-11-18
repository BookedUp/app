package com.example.bookedup.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookedup.R;
import com.example.bookedup.adapters.PopularAdapter;
import com.example.bookedup.adapters.CategoryAdapter;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Category;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPopular, recyclerViewCategory;
    private RecyclerView.Adapter adapterPopular, adapterCategory;

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

        initRecycleView();

        return view;
    }

    private void initRecycleView() {
        ArrayList<Accommodation> items = new ArrayList<>();
        items.add(new Accommodation("Lakeside Motel", "Paris", "description", 2, 4.8, "hotel1", true, 1000));
        items.add(new Accommodation("Julia Dens Resort", "Greece", "description", 2, 4.5, "hotel2", true, 1000));
        items.add(new Accommodation("Marineford Hotel", "Turkey", "description", 2, 4.7, "hotel3", true, 1000));

        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterPopular = new PopularAdapter(items);
        recyclerViewPopular.setAdapter(adapterPopular);

        ArrayList<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Hotels", "hotelbuild"));
        categoryList.add(new Category("Motels", "motel"));
        categoryList.add(new Category("Villas", "villa"));
        categoryList.add(new Category("Apartments", "apartment"));
        categoryList.add(new Category("Resorts", "resort"));

        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterCategory = new CategoryAdapter(categoryList);
        recyclerViewCategory.setAdapter(adapterCategory);
    }
}
