package com.example.bookedup.fragments.accommodations;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.bookedup.R;
import com.example.bookedup.model.DateRange;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddNewFragment extends Fragment {

    private EditText nameEditText, addressStreetEditText, addressCityEditText, addressPostalCodeEditText,
            addressCountryEditText, priceEditText, overviewEditText, minimumGuestsEditText, maximumGuestsEditText,
            cancellationEditText;

    private CheckBox perNightCheckBox, perGuestCheckBox;
    private SwitchMaterial toggleSwitch;
    private Button addImageBtn, addAvailabilityBtn, removeAvailabilityBtn, addNewPriceBtn;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TextView availabilityTextView;
    private List<DateRange> addedDates = new ArrayList<>(); // Assuming you have a DateRange class

    private CalendarView calendarView;

    DateRange selectedRange = new DateRange(null, null);

    public AddNewFragment() {
        // Required empty public constructor
    }

    public static AddNewFragment newInstance(String param1, String param2) {
        AddNewFragment fragment = new AddNewFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_create_accommodation, container, false);

        // Initialize UI components and set up listeners
        initView(view);
        setupListeners(); // Make sure this method sets up the OnClickListener for addAvailabilityBtn
        setInitialDate(); // Set today's date in the CalendarView

        // Add any additional setup or listeners here

        Log.d("AddNewFragment", "onCreateView");
        return view;
    }


    private void initView(View view) {
        nameEditText = view.findViewById(R.id.nameEditText);
        addressStreetEditText = view.findViewById(R.id.addressStreetEditText);
        addressCityEditText = view.findViewById(R.id.addressCityEditText);
        addressPostalCodeEditText = view.findViewById(R.id.addressPostalCodeEditText);
        addressCountryEditText = view.findViewById(R.id.addressCountryEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        overviewEditText = view.findViewById(R.id.overviewEditText);
        minimumGuestsEditText = view.findViewById(R.id.minimumGuestsEditText);
        maximumGuestsEditText = view.findViewById(R.id.maximumGuestsEditText);
        cancellationEditText = view.findViewById(R.id.cancellationEditText);

        perNightCheckBox = view.findViewById(R.id.perNightCheckBox);
        perGuestCheckBox = view.findViewById(R.id.perGuestCheckBox);
        toggleSwitch = view.findViewById(R.id.toggleSwitch);

        addImageBtn = view.findViewById(R.id.addImageBtn);
        calendarView = view.findViewById(R.id.calendarView);

        addAvailabilityBtn = view.findViewById(R.id.addAvailabilityBtn);
        removeAvailabilityBtn = view.findViewById(R.id.removeAvailabilityBtn);
        addNewPriceBtn = view.findViewById(R.id.addNewPriceBtn);

        availabilityTextView = view.findViewById(R.id.availabilityTextView);

        // Add any additional setup or listeners here
    }

    private void setInitialDate() {
        // Get today's date
        Calendar today = Calendar.getInstance();

        // Set today's date to the CalendarView
        calendarView.setDate(today.getTimeInMillis());
    }

    private void setupListeners() {
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click for adding a new accommodation
            }
        });

        addAvailabilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AddNewFragment", "addAvailabilityBtn clicked");
                addDateRange();
            }
        });


        removeAvailabilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDateRange();
            }
        });

        addNewPriceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click for adding a new price
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                System.out.println("Selected day change: " + year + "-" + (month + 1) + "-" + dayOfMonth);
                handleDateSelection(year, month, dayOfMonth);
            }
        });
    }

    private void handleDateSelection(int year, int month, int dayOfMonth) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, dayOfMonth);

        if (this.selectedRange.getStartDate()== null){
            this.selectedRange.setStartDate(selectedDate.getTime());
        } else if(this.selectedRange.getEndDate() == null){
            // if start date is already selected, update end date
            this.selectedRange.setEndDate(selectedDate.getTime());
            addedDates.add(this.selectedRange);
            addedDates = mergeOverlappingDateRanges(addedDates);
            updateAvailabilityTextView();
        } else{
            // if both dates are currently selected, go from the beginning
            this.selectedRange.setStartDate(selectedDate.getTime());
            this.selectedRange.setEndDate(null);
        }
        System.out.println("Tu sam " + this.selectedRange.toString());
        updateCalendarView();
    }


    private void addDateRange() {

        if (this.selectedRange.getEndDate() != null) {
            addedDates.add(this.selectedRange);
            addedDates = mergeOverlappingDateRanges(addedDates);

            this.selectedRange.setStartDate(null);
            this.selectedRange.setEndDate(null);

        } else {
            Toast.makeText(requireContext(), "Please select a valid date range!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCalendarView() {
        // Clear any previous selections
        calendarView.setDate(0);

        // Iterate through the added date ranges and handle selection manually
        for (DateRange range : addedDates) {
            Calendar startDate = Calendar.getInstance();
            startDate.setTime(range.getStartDate());

            Calendar endDate = Calendar.getInstance();
            endDate.setTime(range.getEndDate());

            // Highlight the selected range in the calendar manually
            while (!startDate.after(endDate)) {
                calendarView.setDate(Calendar.DAY_OF_MONTH);
                startDate.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        // Optionally, trigger a redraw or update method in your CalendarView
        calendarView.invalidate();
    }

    private List<DateRange> mergeOverlappingDateRanges(List<DateRange> dateRanges) {
        List<DateRange> mergedRanges = new ArrayList<>();

        if (dateRanges.isEmpty()) {
            return mergedRanges;
        }

        dateRanges.sort((a, b) -> a.getStartDate().compareTo(b.getStartDate()));

        DateRange currentRange = dateRanges.get(0);

        for (int i = 1; i < dateRanges.size(); i++) {
            DateRange nextRange = dateRanges.get(i);

            if (currentRange.getEndDate().compareTo(nextRange.getStartDate()) >= 0) {
                // Overlapping ranges, merge them
                currentRange.setEndDate(max(currentRange.getEndDate(), nextRange.getEndDate()));
            } else {
                // Non-overlapping ranges, add the current range to the result
                mergedRanges.add(currentRange);
                currentRange = nextRange;
            }
        }

        // Add the last range
        mergedRanges.add(currentRange);

        return mergedRanges;
    }

    private Date max(Date date1, Date date2) {
        return date1.compareTo(date2) >= 0 ? date1 : date2;
    }
    private void deleteDateRange() {

        if (this.selectedRange != null) {
            Calendar startSelectedDate = Calendar.getInstance();
            startSelectedDate.setTime(this.selectedRange.getStartDate());

            Calendar endSelectedDate = Calendar.getInstance();
            endSelectedDate.setTime(this.selectedRange.getEndDate());

            List<DateRange> updatedDateRanges = new ArrayList<>();

            for (DateRange dateRange : addedDates) {
                Calendar startDate = Calendar.getInstance();
                startDate.setTime(dateRange.getStartDate());

                Calendar endDate = Calendar.getInstance();
                endDate.setTime(dateRange.getEndDate());

                if (endSelectedDate.before(startDate) || startSelectedDate.after(endDate)) {
                    // No overlap, do nothing
                    updatedDateRanges.add(dateRange);
                } else if (startSelectedDate.compareTo(startDate) <= 0 && endSelectedDate.compareTo(endDate) >= 0) {
                    // Selected range completely covers the current range, remove it
                    // Do nothing (remove it from the list)
                } else if (startSelectedDate.compareTo(startDate) <= 0 && endSelectedDate.before(endDate)) {
                    // Overlapping on the left side, adjust start date
                    dateRange.setStartDate(endSelectedDate.getTime());
                    updatedDateRanges.add(dateRange);
                } else if (startSelectedDate.after(startDate) && endSelectedDate.compareTo(endDate) >= 0) {
                    // Overlapping on the right side, adjust end date
                    dateRange.setEndDate(startSelectedDate.getTime());
                    updatedDateRanges.add(dateRange);
                } else if (startSelectedDate.after(startDate) && endSelectedDate.before(endDate)) {
                    // Selected range is in the middle, split the current range
                    dateRange.setEndDate(startSelectedDate.getTime());
                    updatedDateRanges.add(dateRange);

                    // Insert a new range for the right side
                    updatedDateRanges.add(new DateRange(endSelectedDate.getTime(), endDate.getTime()));
                }
            }

            addedDates = mergeOverlappingDateRanges(updatedDateRanges);
            updateAvailabilityTextView();
        }
    }

    private void updateAvailabilityTextView() {
        availabilityTextView.setText(getString(R.string.availability_label));

        if (addedDates.size() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            StringBuilder dateRangeText = new StringBuilder();

            for (DateRange dateRange : addedDates) {
                String startDateStr = dateFormat.format(dateRange.getStartDate());
                String endDateStr = dateFormat.format(dateRange.getEndDate());

                dateRangeText.append(startDateStr).append(" to ").append(endDateStr).append("\n");
            }

            availabilityTextView.append(dateRangeText.toString());
        }
    }


}
