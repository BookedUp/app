package com.example.bookedup.fragments.accommodations;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bookedup.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment {

    private ListView filterList;

    private ListView filtersPopular;
    private ArrayAdapter<String> budget_adapter;

    private ArrayAdapter<String> popular_adapter;

    private View lastClickedView;
    private String[] budgetFilter = {"$ 0 - $ 50", "$ 50 - $ 100", "$ 100 - $ 1,000", "$ 1,000 - $ 2,000", "$ 2,000 and more"};

    private String[] popularFilter = {"Free cancellation", "Spa", "Beach front", "Hot tub/jacuzzi", "Book without credit card", "No prepayment"};
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<Integer> selectedPopularFilters = new ArrayList<>();



    private String mParam1;
    private String mParam2;

    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance(String param1, String param2) {
        FilterFragment fragment = new FilterFragment();
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
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        filterList = view.findViewById(R.id.filterBudget);
        filtersPopular = view.findViewById(R.id.filtersPopular);

        // Creating an ArrayAdapter and setting it to the ListView
        budget_adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, budgetFilter);
        filterList.setAdapter(budget_adapter);

        popular_adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, popularFilter);
        filtersPopular.setAdapter(popular_adapter);

        filterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Reset the background color of the last clicked item
                if (lastClickedView != null) {
                    lastClickedView.setBackgroundColor(getResources().getColor(android.R.color.white));
                }
                view.setBackgroundColor(getResources().getColor(R.color.neutral));

                // Update the last clicked view
                lastClickedView = view;

            }
        });

        filtersPopular.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Check if the item is already selected
                if (selectedPopularFilters.contains(position)) {
                    // Item is already selected, unselect it
                    view.setBackgroundColor(getResources().getColor(android.R.color.white));
                    selectedPopularFilters.remove(Integer.valueOf(position));
                } else {
                    // Item is not selected, select it
                    view.setBackgroundColor(getResources().getColor(R.color.neutral));
                    selectedPopularFilters.add(position);
                }

                // Perform other actions if needed
                // For example, you might want to handle the selection logic or trigger some other event
            }
        });

    }



}
