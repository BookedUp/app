package com.example.bookedup.fragments.accommodations;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookedup.R;
import com.example.bookedup.model.DateRange;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateAccommodationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateAccommodationFragment extends Fragment {

    private EditText nameEditText, addressStreetEditText, addressCityEditText, addressPostalCodeEditText,
            addressCountryEditText, priceEditText, overviewEditText, minimumGuestsEditText, maximumGuestsEditText,
            cancellationEditText;

    private CheckBox perNightCheckBox, perGuestCheckBox;
    private CheckBox hotelCheckBox, hostelCheckBox, villaCheckBox, apartmentCheckBox, resortCheckBox;

    private SwitchMaterial toggleSwitch;


    private ImageView accommodationImageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button addImageBtn, addAvailabilityBtn, removeAvailabilityBtn, addNewPriceBtn, addAccommodationBtn;
    private EditText specialPriceEditText; // Add this field for the associated EditText


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TextView availabilityTextView;
    private TextView startRangeDateView;
    private TextView endRangeDateView;
    private List<DateRange> addedDates = new ArrayList<>(); // Assuming you have a DateRange class

    private CalendarView calendarView;

    DateRange selectedRange = new DateRange(null, null);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public CreateAccommodationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateAccommodationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateAccommodationFragment newInstance(String param1, String param2) {
        CreateAccommodationFragment fragment = new CreateAccommodationFragment();
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
        View view = inflater.inflate(R.layout.fragment_create_accommodation, container, false);

        // Initialize UI components and set up listeners
        initView(view);
        setupListeners(); // Make sure this method sets up the OnClickListener for addAvailabilityBtn
        setInitialDate(); // Set today's date in the CalendarView

        // Add any additional setup or listeners here


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

        // Set the initial state
        perNightCheckBox.setChecked(true);
        perGuestCheckBox.setChecked(false);
        perNightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // If perNightCheckBox is checked, uncheck perGuestCheckBox
                    perGuestCheckBox.setChecked(false);
                }
            }
        });

        perGuestCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // If perGuestCheckBox is checked, uncheck perNightCheckBox
                    perNightCheckBox.setChecked(false);
                }
            }
        });

        hotelCheckBox = view.findViewById(R.id.hotelCheckBox);
        hostelCheckBox = view.findViewById(R.id.hostelCheckBox);
        villaCheckBox = view.findViewById(R.id.villaCheckBox);
        apartmentCheckBox = view.findViewById(R.id.apartmentCheckBox);
        resortCheckBox = view.findViewById(R.id.resortCheckBox);

        // Set the initial state
        hotelCheckBox.setChecked(false);
        hostelCheckBox.setChecked(false);
        villaCheckBox.setChecked(false);
        apartmentCheckBox.setChecked(false);
        resortCheckBox.setChecked(false);

        // Add listeners to checkboxes
        hotelCheckBox.setOnCheckedChangeListener(createCheckedChangeListener(hostelCheckBox, villaCheckBox, apartmentCheckBox, resortCheckBox));
        hostelCheckBox.setOnCheckedChangeListener(createCheckedChangeListener(hotelCheckBox, villaCheckBox, apartmentCheckBox, resortCheckBox));
        villaCheckBox.setOnCheckedChangeListener(createCheckedChangeListener(hotelCheckBox, hostelCheckBox, apartmentCheckBox, resortCheckBox));
        apartmentCheckBox.setOnCheckedChangeListener(createCheckedChangeListener(hotelCheckBox, hostelCheckBox, villaCheckBox, resortCheckBox));
        resortCheckBox.setOnCheckedChangeListener(createCheckedChangeListener(hotelCheckBox, hostelCheckBox, villaCheckBox, apartmentCheckBox));

        toggleSwitch = view.findViewById(R.id.toggleSwitch);

        startRangeDateView = view.findViewById(R.id.startRangeDateView);
        endRangeDateView = view.findViewById(R.id.endRangeDateView);
        addImageBtn = view.findViewById(R.id.selectImageBtn);
        accommodationImageView = view.findViewById(R.id.selectedImageView);

        calendarView = view.findViewById(R.id.calendarView);

        addAvailabilityBtn = view.findViewById(R.id.addAvailabilityBtn);
        removeAvailabilityBtn = view.findViewById(R.id.removeAvailabilityBtn);

        specialPriceEditText = view.findViewById(R.id.newPriceEditText); // Replace with the actual ID of your EditText

        addNewPriceBtn = view.findViewById(R.id.addNewPriceBtn);

        availabilityTextView = view.findViewById(R.id.availabilityTextView);

        addAccommodationBtn = view.findViewById(R.id.addAccommodationBtn);
        // Add any additional setup or listeners here
    }

    // Helper method to create a common listener for each group
    private CompoundButton.OnCheckedChangeListener createCheckedChangeListener(CheckBox... checkboxes) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If the current checkbox is checked, uncheck the others in the group
                if (isChecked) {
                    for (CheckBox checkbox : checkboxes) {
                        checkbox.setChecked(false);
                    }
                }
            }
        };
    }


    private void setupListeners() {
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addAvailabilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                checkAndShowPopup();
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Log.d("CreateAccommodationFragment", "Selected day change: " + year + "-" + (month + 1) + "-" + dayOfMonth);
                handleDateSelection(year, month, dayOfMonth);
            }
        });

        addAccommodationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement logic here
                showSuccessPopupBigger("Accommodation successfully created!");
            }
        });
    }

    private void setInitialDate() {
        // Get today's date
        Calendar today = Calendar.getInstance();

        // Set today's date to the CalendarView
        calendarView.setDate(today.getTimeInMillis());
    }

    private void handleDateSelection(int year, int month, int dayOfMonth) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, dayOfMonth);

        if (this.selectedRange.getStartDate() == null) {
            this.selectedRange.setStartDate(selectedDate.getTime());
            updateStartDateView();
        } else if (this.selectedRange.getEndDate() == null) {
            this.selectedRange.setEndDate(selectedDate.getTime());
            updateEndDateView();
        } else {
            this.selectedRange.setStartDate(selectedDate.getTime());
            this.selectedRange.setEndDate(null);
            setInitialDate();
            updateStartDateView();
            updateEndDateView();
        }
    }

    private void updateStartDateView() {
        if (this.selectedRange.getStartDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String startDateStr = dateFormat.format(this.selectedRange.getStartDate());
            startRangeDateView.setText("Start Range Date: " + startDateStr);
        }else{
            startRangeDateView.setText("Start Range Date: none" );
        }
    }

    private void updateEndDateView() {
        if (this.selectedRange.getEndDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String endDateStr = dateFormat.format(this.selectedRange.getEndDate());
            endRangeDateView.setText("End Range Date: " + endDateStr);
        }else{
            endRangeDateView.setText("End Range Date: none");
        }
    }


    private void addDateRange() {
        if (this.selectedRange.getEndDate() != null) {
            addedDates.add(new DateRange(this.selectedRange.getStartDate(), this.selectedRange.getEndDate()));

            mergeOverlappingDateRanges(addedDates);

            updateAvailabilityTextView();
            this.selectedRange.setStartDate(null);
            this.selectedRange.setEndDate(null);

        } else {
            Toast.makeText(requireContext(), "Please select a valid date range!", Toast.LENGTH_SHORT).show();
        }
    }

    private void mergeOverlappingDateRanges(List<DateRange> dateRanges) {
        List<DateRange> mergedRanges = new ArrayList<>();

        if (dateRanges.isEmpty()) {
            return;
        }

        dateRanges.sort((a, b) -> a.getStartDate().compareTo(b.getStartDate()));

        DateRange currentRange = dateRanges.get(0);

        for (int i = 1; i < dateRanges.size(); i++) {
            DateRange nextRange = dateRanges.get(i);

            if (currentRange.getEndDate().compareTo(nextRange.getStartDate()) >= 0) {
                // Overlapping ranges, merge them
                currentRange.setEndDate(max(currentRange.getEndDate(), nextRange.getEndDate()));
            } else if (currentRange.getEndDate().equals(nextRange.getStartDate())) {
                // End date of current range is the same as the start date of the next range
                // Connect these ranges
                currentRange.setEndDate(nextRange.getEndDate());
            } else {
                // Non-overlapping ranges, add the current range to the result
                mergedRanges.add(currentRange);
                currentRange = nextRange;
            }
        }

        // Add the last range
        mergedRanges.add(currentRange);

        this.addedDates = mergedRanges;
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

            mergeOverlappingDateRanges(updatedDateRanges);
            updateAvailabilityTextView();
        }
    }

    private void updateAvailabilityTextView() {
        availabilityTextView.setText("\n");

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
    private void checkAndShowPopup() {
        String specialPrice = specialPriceEditText.getText().toString().trim();

        if (!specialPrice.isEmpty()) {
            // Successfully added special price
            showSuccessPopup("Successfully added special price!");
        } else {
            // Error: You didn't enter a value for special price
            showErrorPopup("You didn't enter a value for special price");
        }
    }

    private void showSuccessPopup(String message) {
        // You can implement your own logic to show a success popup here
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccessPopupBigger(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showErrorPopup(String errorMessage) {
        // You can implement your own logic to show an error popup here
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            loadImage(selectedImageUri);
        }
    }

    private void loadImage(Uri imageUri) {
        // Set the selected image to the ImageView
        accommodationImageView.setImageURI(imageUri);
    }

}