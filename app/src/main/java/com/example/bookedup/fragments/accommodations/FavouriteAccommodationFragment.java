package com.example.bookedup.fragments.accommodations;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.adapters.FavouriteAccommodationAdapter;
import com.example.bookedup.adapters.SearchAccommodationAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.calendar.CalendarFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Category;;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteAccommodationFragment extends Fragment {

    private RecyclerView recyclerViewResults;
    private RecyclerView.Adapter adapterResults;
    private int targetLayout;
    private List<Accommodation> favourites = new ArrayList<Accommodation>();
    private Map<Long, List<Bitmap>> accommodationImages = new HashMap<>();


    public FavouriteAccommodationFragment(List<Accommodation> favourites, Map<Long, List<Bitmap>> accommodationImages) {
        this.favourites = favourites;
        this.accommodationImages = accommodationImages;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_accommodation, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateFavoriteList();
        initView(view);
        findTargetLayout();
//        getCallerData();
        initRecycleView();
    }

    private void initView(View view){
        recyclerViewResults = view.findViewById(R.id.view_acc_results);
    }

    private void findTargetLayout(){
        Intent intent = getActivity().getIntent();
        ComponentName componentName = intent.getComponent();
        if (componentName.getClassName().equals("com.example.bookedup.activities.GuestMainScreen")) {
            targetLayout = R.id.frame_layout;
        } else if (componentName.getClassName().equals("com.example.bookedup.activities.AdministratorMainScreen")){
            targetLayout = R.id.frame_layoutAdmin;
        } else if (componentName.getClassName().equals("com.example.bookedup.activities.HostMainScreen")){
            targetLayout = R.id.frame_layoutHost;
        }
    }

    public void updateFavoriteList() {
        favourites = LoginScreen.loggedGuest.getFavourites();
        if (adapterResults != null) {
            adapterResults.notifyDataSetChanged();
        }
    }


    private void initRecycleView() {

        recyclerViewResults.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adapterResults = new FavouriteAccommodationAdapter(favourites, targetLayout, accommodationImages);
        recyclerViewResults.setAdapter(adapterResults);
    }


}
