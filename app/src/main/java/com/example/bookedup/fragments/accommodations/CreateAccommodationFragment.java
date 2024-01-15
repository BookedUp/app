package com.example.bookedup.fragments.accommodations;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Address;
import com.example.bookedup.model.DateRange;
import com.example.bookedup.model.Photo;
import com.example.bookedup.model.PriceChange;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.enums.AccommodationStatus;
import com.example.bookedup.model.enums.AccommodationType;
import com.example.bookedup.model.enums.Amenity;
import com.example.bookedup.model.enums.PriceType;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccommodationFragment extends Fragment {

    private EditText nameEditText, addressStreetEditText, addressCityEditText, addressPostalCodeEditText,
            addressCountryEditText, priceEditText, overviewEditText, minimumGuestsEditText, maximumGuestsEditText,
            cancellationEditText;

    private ImageView selectedImageView;

    private List<Uri> selectedImageUris = new ArrayList<>();

    private static final int PICK_IMAGE_REQUEST = 1;

    private int currentImageIndex = 0;
    private RadioGroup priceTypeRadioGroup, accommodationTypeRadioGroup;

    private RadioButton perNightRadioButton, perGuestRadioButton;

    private CheckBox wifiCheckBox, nonSmokingCheckBox, parkingCheckBox,restaurantCheckBox, swimmingPoolCheckBox, fitnessCheckBox;

    private RadioButton hotelRadioButton, hostelRadioButton, villaRadioButton, apartmentRadioButton, resortRadioButton;
    private SwitchMaterial toggleSwitch;

    private Button createAccommodationBtn, addAvailabilityBtn, removeAvailabilityBtn, addNewPriceBtn, selectImageBtn, removeImageBtn;

    private DatePickerDialog.OnDateSetListener datePickerSetListener;
    private TextView availabilityTextView, datePicker;
    private List<DateRange> addedDates = new ArrayList<>();

    private List<PriceChange> priceChanges = new ArrayList<>();

    private List<Photo> photos = new ArrayList<Photo>();

    private List<DateRange> availibility = new ArrayList<>();

    private CalendarView calendarView;

    private PriceType priceType;

    private AccommodationType type;

    DateRange selectedRange = new DateRange(null, null);

    public CreateAccommodationFragment() {}
    public static CreateAccommodationFragment newInstance(String param1, String param2) {
        CreateAccommodationFragment fragment = new CreateAccommodationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_accommodation, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setInitialDate();
        setupListeners();

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

        priceTypeRadioGroup = view.findViewById(R.id.priceTypeRadioGroup);
        perNightRadioButton = view.findViewById(R.id.perNightRadioButton);
        perGuestRadioButton = view.findViewById(R.id.perGuestRadioButton);

        wifiCheckBox = view.findViewById(R.id.wifiCheckBox);
        nonSmokingCheckBox = view.findViewById(R.id.nonSmokingCheckBox);
        parkingCheckBox = view.findViewById(R.id.parkingCheckBox);
        restaurantCheckBox = view.findViewById(R.id.restaurantCheckBox);
        swimmingPoolCheckBox = view.findViewById(R.id.swimmingPoolCheckBox);
        fitnessCheckBox = view.findViewById(R.id.fitnessCheckBox);


        accommodationTypeRadioGroup = view.findViewById(R.id.accommodationTypeRadioGroup);
        hotelRadioButton = view.findViewById(R.id.hotelRadioButton);
        hostelRadioButton = view.findViewById(R.id.hostelRadioButton);
        villaRadioButton = view.findViewById(R.id.villaRadioButton);
        apartmentRadioButton = view.findViewById(R.id.apartmentRadioButton);
        resortRadioButton = view.findViewById(R.id.resortRadioButton);

        toggleSwitch = view.findViewById(R.id.toggleSwitch);

        createAccommodationBtn = view.findViewById(R.id.createAccommodationBtn);
        calendarView = view.findViewById(R.id.calendarView);

        addAvailabilityBtn = view.findViewById(R.id.addAvailabilityBtn);
        removeAvailabilityBtn = view.findViewById(R.id.removeAvailabilityBtn);
        addNewPriceBtn = view.findViewById(R.id.addNewPriceBtn);

        availabilityTextView = view.findViewById(R.id.availabilityTextView);

        datePicker = view.findViewById(R.id.datePicker);
        selectImageBtn = view.findViewById(R.id.selectImageBtn);
        selectedImageView = view.findViewById(R.id.selectedImageView);
        removeImageBtn = view.findViewById(R.id.removeImageBtn);

    }

    private void setInitialDate() {
        Calendar today = Calendar.getInstance();
        calendarView.setDate(today.getTimeInMillis());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    // Multiple images selected
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        selectedImageUris.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    // Single image selected
                    Uri imageUri = data.getData();
                    selectedImageUris.add(imageUri);
                }

                // Display the first image in the ImageView
                if (!selectedImageUris.isEmpty()) {
                    selectedImageView.setImageURI(selectedImageUris.get(0));
                }
            }
        }
    }

    private void cycleImages() {
        if (!selectedImageUris.isEmpty()) {
            // Get the current image index
            int currentIndex = selectedImageUris.indexOf(selectedImageView.getTag());

            // Update the index for the next image
            int nextIndex = (currentIndex + 1) % selectedImageUris.size();

            // Display the next image
            selectedImageView.setImageURI(selectedImageUris.get(nextIndex));
            selectedImageView.setTag(selectedImageUris.get(nextIndex));
        }
    }

    private void displayNextImage() {
        if (!selectedImageUris.isEmpty()) {
            // Increment the index
            currentImageIndex = (currentImageIndex + 1) % selectedImageUris.size();
            // Display the next image
            displaySelectedImage();
        } else {
            // No more images, clear the ImageView
            selectedImageView.setImageDrawable(null);
            selectedImageView.setTag(null);
        }
    }


    private void displaySelectedImage() {
        if (currentImageIndex < selectedImageUris.size()) {
            Uri currentImageUri = selectedImageUris.get(currentImageIndex);
            selectedImageView.setImageURI(currentImageUri);
        }
    }

    private void setupListeners() {

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        selectedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cycleImages();
            }
        });

        removeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedImageUris.isEmpty()) {
                    Uri currentImageUri = (Uri) selectedImageView.getTag();
                    selectedImageUris.remove(currentImageUri);
                    displayNextImage();
                }
            }
        });


        addAvailabilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date startDate = null;
                Date endDate = null;


                String availibilityString = availabilityTextView.getText().toString();
                String[] parts = availibilityString.split("\\n");
                for (String p : parts){
                    String[] dates = p.split(" to ");
                    try {
                        startDate = sdf.parse(dates[0]);
                        endDate = sdf.parse(dates[1]);


                        DateRange dateRange = new DateRange(startDate, endDate);
                        availibility.add(dateRange);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (!availibility.isEmpty()){
                    Toast.makeText(getActivity(),"Availability added!",Toast.LENGTH_SHORT).show();
                }


            }
        });


        removeAvailabilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String availibilityString = availabilityTextView.getText().toString();
                String[] parts = availibilityString.split("\\n");
                if (parts.length > 0) {
                    String lastElement = parts[parts.length - 1];
                    String[] newArray = new String[parts.length - 1];
                    System.arraycopy(parts, 0, newArray, 0, parts.length - 1);
                    if (newArray.length > 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String element : newArray) {
                            stringBuilder.append(element).append("\n");
                        }
                        availabilityTextView.setText(stringBuilder.toString());
                    } else {
                        availabilityTextView.setText("");
                    }
                }
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                handleDateSelection(year, month, dayOfMonth);
            }


        });

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, datePickerSetListener,year, month, day );
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        datePickerSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                Log.d("CreateAccommodationFragment", "date: " + year + "/" + month + "/" + dayOfMonth);
                String date = year  + "-" + month + "-" + dayOfMonth;
                datePicker.setText(date);
            }
        };

        addNewPriceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPriceChangeDate = datePicker.getText().toString();
                String newPriceChange = priceEditText.getText().toString();

                if (!newPriceChangeDate.isEmpty() && !newPriceChange.isEmpty()){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Double newPrice = Double.parseDouble(newPriceChange);
                    Date priceChangeDate = null;

                    try {
                        priceChangeDate = sdf.parse(datePicker.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (priceChangeDate != null){
                        priceChanges.add(new PriceChange(priceChangeDate, newPrice));
                    }
                }

                Toast.makeText(getActivity(),"Price changes added!",Toast.LENGTH_SHORT).show();
            }
        });

        priceTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.perNightRadioButton) {
                    priceType = PriceType.PER_NIGHT;
                } else if (checkedId == R.id.perGuestRadioButton) {
                    priceType = PriceType.PER_GUEST;
                }
            }
        });

        accommodationTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.hotelRadioButton) {
                    type = AccommodationType.HOTEL;
                } else if (checkedId == R.id.hostelRadioButton) {
                    type = AccommodationType.HOSTEL;
                } else if (checkedId == R.id.villaRadioButton){
                    type = AccommodationType.VILLA;
                } else if (checkedId == R.id.apartmentRadioButton){
                    type = AccommodationType.APARTMENT;
                } else if (checkedId == R.id.resortRadioButton){
                    type = AccommodationType.RESORT;
                }
            }
        });

        createAccommodationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

    }


    private void handleDateSelection(int year, int month, int dayOfMonth) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.YEAR, year);
        selectedDate.set(Calendar.MONTH, month);
        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        if (selectedRange.getStartDate() == null) {
            selectedRange.setStartDate(selectedDate.getTime());
        } else if (selectedRange.getEndDate() == null) {
            // if start date is already selected, update end date
            selectedRange.setEndDate(selectedDate.getTime());
            addedDates.add(selectedRange);
            addedDates = mergeOverlappingDateRanges(addedDates);
            updateAvailabilityTextView();
        } else {
            // if both dates are currently selected, go from the beginning
            this.selectedRange.setStartDate(selectedDate.getTime());
            this.selectedRange.setEndDate(null);
        }
        Log.d("CreateAccommodationFragment", "Selected Range: " + selectedRange.toString());
        updateCalendarView();
    }




    private void updateCalendarView() {
//        calendarView.setDate(0);
        for (DateRange range : addedDates) {
            Calendar startDate = Calendar.getInstance();
            startDate.setTime(range.getStartDate());  // Change here

            Calendar endDate = Calendar.getInstance();
            if (range.getEndDate() != null) {
                endDate.setTime(range.getEndDate());
            } else {
                continue;
            }

            // Highlight the selected range in the calendar manually
            while (!startDate.after(endDate)) {
                calendarView.setDate(startDate.getTimeInMillis(), true, true);
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
                currentRange.setEndDate(max(currentRange.getEndDate(), nextRange.getEndDate()));
            } else {
                mergedRanges.add(currentRange);
                currentRange = nextRange;
            }
        }

        mergedRanges.add(currentRange);

        return mergedRanges;
    }

    private Date max(Date date1, Date date2) {
        return date1.compareTo(date2) >= 0 ? date1 : date2;
    }


    private void updateAvailabilityTextView() {

        if (addedDates.size() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            StringBuilder dateRangeText = new StringBuilder();
            Log.d("CreateAccommodationFragment", "addedDates SIZEEEEEEEE " + addedDates.size());

            for (DateRange dateRange : addedDates) {
                String startDateStr = dateFormat.format(dateRange.getStartDate());
                String endDateStr = dateFormat.format(dateRange.getEndDate());

                dateRangeText.append(startDateStr).append(" to ").append(endDateStr).append("\n");
            }

            availabilityTextView.append(dateRangeText.toString());
        }
    }

    private void getData(){
        if (!selectedImageUris.isEmpty()) {
            for (Uri imageUri : selectedImageUris) {
                String imageUrl = imageUri.toString();
                Log.d("CreateAccommodationFragment", "Image url " + imageUrl);
//                    String caption = "Caption";
//                    boolean active = true;
//
//                    Photo photo = new Photo(imageUrl, caption, active);
//                    photos.add(photo);
            }
        }
        if (nameEditText.getText().toString().isEmpty() || addressStreetEditText.getText().toString().isEmpty() || addressCityEditText.getText().toString().isEmpty() || addressCountryEditText.getText().toString().isEmpty() || addressPostalCodeEditText.getText().toString().isEmpty() || priceEditText.getText().toString().isEmpty() || overviewEditText.getText().toString().isEmpty() || priceType == null || minimumGuestsEditText.getText().toString().isEmpty() || maximumGuestsEditText.getText().toString().isEmpty() || cancellationEditText.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Fill the fields!", Toast.LENGTH_SHORT).show();
        } else {
            String accommodationName = nameEditText.getText().toString();
            String streetAndNumber = addressStreetEditText.getText().toString();
            String city = addressCityEditText.getText().toString();
            String country = addressCountryEditText.getText().toString();
            String postalCode = addressPostalCodeEditText.getText().toString();
            Address address = new Address(country, city, postalCode, streetAndNumber, true, 0, 0);

            Double price = Double.parseDouble(priceEditText.getText().toString());

            String description = overviewEditText.getText().toString();

            List<String> checkedCheckBoxTexts = new ArrayList<>();
            List<Amenity> amenities = new ArrayList<>();

            if (wifiCheckBox.isChecked()) {
                checkedCheckBoxTexts.add(wifiCheckBox.getText().toString());
            }

            if (nonSmokingCheckBox.isChecked()) {
                checkedCheckBoxTexts.add(nonSmokingCheckBox.getText().toString());
            }

            if (parkingCheckBox.isChecked()) {
                checkedCheckBoxTexts.add(parkingCheckBox.getText().toString());
            }

            if (restaurantCheckBox.isChecked()) {
                checkedCheckBoxTexts.add(restaurantCheckBox.getText().toString());
            }

            if (swimmingPoolCheckBox.isChecked()) {
                checkedCheckBoxTexts.add(swimmingPoolCheckBox.getText().toString());
            }

            if (fitnessCheckBox.isChecked()) {
                checkedCheckBoxTexts.add(fitnessCheckBox.getText().toString());
            }

            for (String text : checkedCheckBoxTexts) {
                for (Amenity amenity : Amenity.values()) {
                    if (text.equalsIgnoreCase(amenity.getAmenity())) {
                        amenities.add(amenity);
                    }
                }
            }

            Integer minGuests = Integer.parseInt(minimumGuestsEditText.getText().toString());
            Integer maxGuests = Integer.parseInt(maximumGuestsEditText.getText().toString());
            Integer cancellationDeadLine = Integer.parseInt(cancellationEditText.getText().toString());
            boolean automaticAcceptReservations = false;

            if (toggleSwitch.isChecked()) {
                automaticAcceptReservations = true;
            } else {
                automaticAcceptReservations = false;
            }


//            create(accommodationName, address, price, priceType, description, amenities, type, minGuests, maxGuests, cancellationDeadLine, automaticAcceptReservations, priceChanges);
        }
    }


    private void create(String accommodationName, Address address, Double price, PriceType priceType, String description, List<Amenity> amenities, AccommodationType accommodationType, Integer minGuests, Integer maxGuests, Integer cancellationDeadLine, boolean automaticAcceptReservations, List<PriceChange> priceChanges){
        Log.d("CreateAccommodationFragment", "Name " + accommodationName);
        Log.d("CreateAccommodationFragment", "Address " + address.toString());
        Log.d("CreateAccommodationFragment", "Price " + price);
        Log.d("CreateAccommodationFragment", "PriceType " + priceType);
        Log.d("CreateAccommodationFragment", "Description " + description);
        Log.d("CreateAccommodationFragment", "Amenities size " + amenities.size());
        Log.d("CreateAccommodationFragment", "AccommodationType " + accommodationType);
        Log.d("CreateAccommodationFragment", "MinGuests " + minGuests);
        Log.d("CreateAccommodationFragment", "MaxGuests " + maxGuests);
        Log.d("CreateAccommodationFragment", "Cancellation " + cancellationDeadLine);
        Log.d("CreateAccommodationFragment", "Automatic " + automaticAcceptReservations);
        Log.d("CreateAccommodationFragment", "PriceChanges size " + priceChanges.size());



        Accommodation accommodation = new Accommodation(accommodationName, description, address, amenities, photos, minGuests, maxGuests, accommodationType, availibility, priceType, priceChanges, automaticAcceptReservations, AccommodationStatus.CREATED, LoginScreen.loggedHost, price, 0.0, 0.0, cancellationDeadLine);

        Call<Accommodation> createdAccommodation = ClientUtils.accommodationService.createAccommodation(accommodation);

        createdAccommodation.enqueue(new Callback<Accommodation>() {
            @Override
            public void onResponse(Call<Accommodation> call, Response<Accommodation> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("CreateAccommodationFragment", "Successful response: " + response.body());
                        Accommodation newAccommodation = response.body();
                        Toast.makeText(requireContext(), "Accommodation created!", Toast.LENGTH_SHORT).show();
                        createAccommodationBtn.setEnabled(false);
                    } else {
                        Log.d("CreateAccommodationFragment", "Response body is null");
                    }
                }  else {
                    // Log error details
                    Log.d("CreateAccommodationFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("CreateAccommodationFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Accommodation> call, Throwable t) {
                Log.d("CreateAccommodationFragment", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }
}