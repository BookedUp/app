package com.example.bookedup.fragments.reservations;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.adapters.ReservationRequestAdapter;
import com.example.bookedup.adapters.TypeAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.calendar.CalendarFragment;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.enums.ReservationStatus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationRequestFragment extends Fragment implements TypeAdapter.TypeSelectionListener, DatePickerDialog.OnDateSetListener{


    private RecyclerView typeRecyclerView, reservationRecyclerView;
    private TypeAdapter typeAdapter;
    private ReservationRequestAdapter reservationAdapter;
    private int layout_caller;
    private List<Reservation> reservations = new ArrayList<>();
    private List<Reservation> originalReservations = new ArrayList<>();
    private EditText accommodationName;
    private TextView checkInTxt, checkOutTxt;
    private ImageView startDateCalendar, endDateCalendar;
    private boolean isStartDateButtonClicked, isEndDateButtonClicked;
    private FloatingActionButton search, refresh;
    private Map<Long, List<Bitmap>> accommodationImages = new HashMap<>();

    public ReservationRequestFragment(List<Reservation> originalReservations,  Map<Long, List<Bitmap>> accommodationImages, int layout_caller) {
        this.originalReservations = originalReservations;
        this.accommodationImages = accommodationImages;
        this.layout_caller = layout_caller;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation_request, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reservations.clear();
        reservations.addAll(originalReservations);
        initView(view);
        initUI(view);

        startDateCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new CalendarFragment();
                datePicker.setTargetFragment(ReservationRequestFragment.this, 0);
                datePicker.show(requireActivity().getSupportFragmentManager(), "date picker");
                isStartDateButtonClicked = true;
                isEndDateButtonClicked = false;
            }
        });

        endDateCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new CalendarFragment();
                datePicker.setTargetFragment(ReservationRequestFragment.this, 0);
                datePicker.show(requireActivity().getSupportFragmentManager(), "date picker");
                isEndDateButtonClicked = true;
                isStartDateButtonClicked = false;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(accommodationName.getText().toString(), checkInTxt.getText().toString(), checkOutTxt.getText().toString());
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadReservationsData();
            }
        });
    }

    private void initView(View view){
        accommodationName = view.findViewById(R.id.accommodationName);
        startDateCalendar = view.findViewById(R.id.startDate);
        endDateCalendar = view.findViewById(R.id.endDate);
        checkInTxt = view.findViewById(R.id.checkInText);
        checkOutTxt = view.findViewById(R.id.checkOutText);
        search = view.findViewById(R.id.searchReservationsBtn);
        refresh = view.findViewById(R.id.refreshReservationsBtn);
    }

    private void initUI(View view) {
        typeRecyclerView = view.findViewById(R.id.frr_recyclerType);
        typeAdapter = new TypeAdapter(getTypeList(), this);
        typeRecyclerView.setAdapter(typeAdapter);
        typeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        reservationRecyclerView = view.findViewById(R.id.cards_reservationRequests);
        reservationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reservationAdapter = new ReservationRequestAdapter(new ArrayList<>(reservations), getContext(), layout_caller, this, accommodationImages);
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
                filteredList.clear();
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

    public void updateReservationList(List<Reservation> updatedList) {
        if (reservationRecyclerView != null) {
            reservations.clear();
            reservations.addAll(updatedList);
            reservationAdapter.updateData(reservations);
        }
    }


    private void search(String accommodationName, String checkIn, String checkOut){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (checkIn.isEmpty() && checkOut.isEmpty()){
            checkIn = null;
            checkOut = null;
        }


        Call<ArrayList<Reservation>> searchedResults = ClientUtils.reservationService.searchReservations(LoginScreen.loggedHost.getId(), checkIn, checkOut, accommodationName);
        searchedResults.enqueue(new Callback<ArrayList<Reservation>>() {
            @Override
            public void onResponse(Call<ArrayList<Reservation>> call, Response<ArrayList<Reservation>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("ReservationRequestFragment", "Successful response: " + response.body());
                        List<Reservation> results = response.body();
                        reservationAdapter.updateData(results);
                        reservations.clear();
                        reservations.addAll(results);
                    } else {
                        Log.d("ReservationRequestFragment", "Response body is null");
                    }
                }  else {
                    // Log error details
                    Log.d("ReservationRequestFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("ReservationRequestFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Reservation>> call, Throwable t) {
                Log.d("ReservationRequestFragment", t.getMessage() != null?t.getMessage():"error");
            }
        });


    }

    private void reloadReservationsData() {
        reservations.clear();
        reservations.addAll(originalReservations);
        reservationAdapter.updateData(originalReservations);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        try {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(c.getTime());
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
            Log.e("ReservationRequestFragment", "Error in onDateSet", e);
        }
    }

    private void getAllReservations(String selectedType){
        Call<ArrayList<Reservation>> myReservations = ClientUtils.reservationService.getReservationsByHostId(LoginScreen.loggedHost.getId());
        myReservations.enqueue(new Callback<ArrayList<Reservation>>() {
            @Override
            public void onResponse(Call<ArrayList<Reservation>> call, Response<ArrayList<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ReservationRequestFragment", "Successful response: " + response.body());
                    reservations = response.body();
                    updateReservationAdapter(selectedType);
                } else {
                    // Log error details
                    Log.d("ReservationRequestFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("ReservationRequestFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Reservation>> call, Throwable t) {
                Log.d("ReservationRequestFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }
}