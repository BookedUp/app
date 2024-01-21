package com.example.bookedup.fragments.accommodations;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import com.example.bookedup.model.enums.AccommodationStatus;
import com.example.bookedup.model.enums.AccommodationType;
import com.example.bookedup.model.enums.Amenity;
import com.example.bookedup.model.enums.PriceType;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpdateAccommodationFragment extends Fragment {

    private Accommodation accommodation;
    private EditText nameEditText, addressStreetEditText, addressCityEditText, addressPostalCodeEditText,
            addressCountryEditText, priceEditText, overviewEditText, minimumGuestsEditText, maximumGuestsEditText,
            cancellationEditText, newPriceEditText;
    private ImageView selectedImageView;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private int currentImageIndex = 0;
    private RadioGroup priceTypeRadioGroup, accommodationTypeRadioGroup;
    private RadioButton perNightRadioButton, perGuestRadioButton;
    private CheckBox wifiCheckBox, nonSmokingCheckBox, parkingCheckBox,restaurantCheckBox, swimmingPoolCheckBox, fitnessCheckBox;
    private RadioButton hotelRadioButton, hostelRadioButton, villaRadioButton, apartmentRadioButton, resortRadioButton;
    private SwitchMaterial toggleSwitch;
    private Button updateAccommodationBtn, addAvailabilityBtn, removeAvailabilityBtn, addNewPriceBtn, removePriceChange, selectImageBtn, removeImageBtn;
    private DatePickerDialog.OnDateSetListener datePickerSetListener;
    private TextView availabilityTextView, datePicker, priceChangesTextView;
    private List<DateRange> addedDates = new ArrayList<>();
    private List<String> availabilityStringList = new ArrayList<>();
    private StringBuilder stringBuilderAvailibility = new StringBuilder();
    private List<PriceChange> priceChanges = new ArrayList<>();
    private List<String> priceChangesStringList = new ArrayList<>();
    private StringBuilder stringBuilderPriceChange = new StringBuilder();
    private List<Photo> photos = new ArrayList<Photo>();
    private List<DateRange> availibility = new ArrayList<>();
    private CalendarView calendarView;
    private PriceType priceType;
    private AccommodationType type;
    DateRange selectedRange = new DateRange(null, null);
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<Bitmap> accommodationImages = new ArrayList<>();
    private List<Bitmap> originalAccommodationImages = new ArrayList<>();


    public UpdateAccommodationFragment(Accommodation accommodation, List<Bitmap> accommodationImages ) {
        this.accommodation = accommodation;
        this.accommodationImages = accommodationImages;
        this.originalAccommodationImages.addAll(accommodationImages);
        this.type = accommodation.getType();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_accommodation, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stringBuilderAvailibility = new StringBuilder();
        stringBuilderPriceChange = new StringBuilder();
        initView(view);
        setInitialDate();
        setAccommodationData();
        setupListeners();

        displayInitialImage();

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
                        Bitmap bitmap = getBitmapFromUri(imageUri);
                        accommodationImages.add(bitmap);
                    }
                } else if (data.getData() != null) {
                    // Single image selected
                    Uri imageUri = data.getData();
                    Bitmap bitmap = getBitmapFromUri(imageUri);
                    accommodationImages.add(bitmap);
                }

                // Display the first image in the ImageView
                displayInitialImage();

                updateRemoveImageButtonState();
            }
        }
    }

    // Helper method to convert Uri to Bitmap
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void cycleImages() {
        if (!accommodationImages.isEmpty()) {
            // Get the current index
            int currentIndex = (int) selectedImageView.getTag();

            // Update the index for the next image
            int nextIndex = (currentIndex + 1) % accommodationImages.size();

            // Display the next image
            selectedImageView.setImageBitmap(accommodationImages.get(nextIndex));
            selectedImageView.setTag(nextIndex);
        } else {
            // No more images, clear the ImageView
            selectedImageView.setImageDrawable(null);
            selectedImageView.setTag(null);
        }
    }

    private void displayNextImage() {
        int totalImages = accommodationImages.size() + selectedImageUris.size();

        if (totalImages > 0) {
            // Increment the index
            currentImageIndex = (currentImageIndex + 1) % totalImages;

            if (currentImageIndex < accommodationImages.size()) {
                // Display the next image from accommodationImages
                selectedImageView.setImageBitmap(accommodationImages.get(currentImageIndex));
            } else {
                // Display the next image from selectedImageUris
                Uri currentImageUri = selectedImageUris.get(currentImageIndex - accommodationImages.size());
                selectedImageView.setImageURI(currentImageUri);
            }

            selectedImageView.setTag(currentImageIndex);
        } else {
            // No more images, clear the ImageView
            selectedImageView.setImageDrawable(null);
            selectedImageView.setTag(null);
        }
    }


    private void displayInitialImage() {
        if (!accommodationImages.isEmpty()) {
            selectedImageView.setImageBitmap(accommodationImages.get(0));
            selectedImageView.setTag(0); // Set tag to index 0
        } else {
            selectedImageView.setImageDrawable(null);
            selectedImageView.setTag(null);
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
                int currentIndex = (int) selectedImageView.getTag();

                if (!accommodationImages.isEmpty()) {
                    // Remove the image at the current index from accommodationImages
                    if (currentIndex >= 0 && currentIndex < accommodationImages.size()) {
                        accommodationImages.remove(currentIndex);

                        if (!accommodationImages.isEmpty()) {
                            // If there are still images, display the next one
                            currentImageIndex = currentIndex % accommodationImages.size();
                            displayNextImage();
                        } else {
                            // No more images, clear the ImageView
                            selectedImageView.setImageDrawable(null);
                            selectedImageView.setTag(null);
                        }
                    }

                    // Disable or enable the removeImageBtn based on whether there are images
                    updateRemoveImageButtonState();
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
                Log.d("UpdateAccommodationFragment", "PARTS AVAILIBILITY  " + parts.length);
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

        removePriceChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!priceChangesStringList.isEmpty()) {
                    priceChangesStringList.remove(priceChangesStringList.size() - 1);
                    updatePriceChangesTextView();
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
                String dateString = datePicker.getText().toString();
                String newPriceString = newPriceEditText.getText().toString();
                priceChangesStringList.add(dateString + " - " + Double.parseDouble(newPriceString));

                updatePriceChangesTextView();

                Toast.makeText(getActivity(), "Price changes added!", Toast.LENGTH_SHORT).show();
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

        updateAccommodationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImages();
            }
        });

    }

    private void updateRemoveImageButtonState() {
        if (accommodationImages.isEmpty()) {
            removeImageBtn.setEnabled(false);
        } else {
            removeImageBtn.setEnabled(true);
        }
    }

    private void updatePriceChangesTextView() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String element : priceChangesStringList) {
            stringBuilder.append(element).append("\n");
        }
        priceChangesTextView.setText(stringBuilder.toString());
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
        for (DateRange range : addedDates) {
            Calendar startDate = Calendar.getInstance();
            startDate.setTime(range.getStartDate());  // Change here

            Calendar endDate = Calendar.getInstance();
            if (range.getEndDate() != null) {
                endDate.setTime(range.getEndDate());
            } else {
                continue;
            }

            while (!startDate.after(endDate)) {
                calendarView.setDate(startDate.getTimeInMillis(), true, true);
                startDate.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

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

            for (DateRange dateRange : addedDates) {
                String startDateStr = dateFormat.format(dateRange.getStartDate());
                String endDateStr = dateFormat.format(dateRange.getEndDate());

                dateRangeText.append(startDateStr).append(" to ").append(endDateStr).append("\n");
            }

            availabilityTextView.append(dateRangeText.toString());
        }
    }
    private void uploadImages() {
        Log.d("UpdateAccommodationFragment", "USAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAO" + accommodationImages.size() + " Size  " + accommodationImages.size());

        List<Photo> uploadedPhotos = new ArrayList<>();
        AtomicInteger uploadCount = new AtomicInteger(0);
        int size = accommodationImages.size();

        while(size >= 1) {
            Log.d("UpdateAccommodationFragment", "USAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAO SIZEEEE " + size);

            for (Bitmap bitmap : accommodationImages) {
                if (!isImageAlreadyUploaded(bitmap)) {
                    Log.d("UpdateAccommodationFragment", "USAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAO");

                    // Convert Bitmap to File
                    String fileName = "acc" + System.currentTimeMillis() + ".jpg";
                    File imageFile = bitmapToFile(bitmap, fileName);

                    // Convert File to MultipartBody.Part
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", fileName, requestFile);

                    // Call the method to upload the image
                    Call<ResponseBody> uploadCall = ClientUtils.photoService.uploadPhoto(imagePart);
                    int finalSize = size;
                    uploadCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Photo photo = new Photo("images/" + fileName, "Caption", true);
                                Log.d("UpdateAccommodationFragment", "Photo NEWNEW NEW " + photo);
                                uploadedPhotos.add(photo);

                                // Check if all images are uploaded
                                if (uploadCount.incrementAndGet() == finalSize) {
                                    // All images are uploaded, call another function
                                    Log.d("UpdateAccommodationFragment", "NASAO JE NEKE SLIKE");
                                    onAllImagesUploaded(uploadedPhotos);
                                }
                            } else {
                                // Log error details
                                Log.d("UpdateAccommodationFragment", "Unsuccessful response: " + response.code());
                                try {
                                    Log.d("UpdateAccommodationFragment", "Error Body: " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("UpdateAccommodationFragment", t.getMessage() != null ? t.getMessage() : "error");
                        }
                    });
                } else {
                    size -= 1;
                    Log.d("UpdateAccommodationFragment", "USAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAO SIZEEEEposzle promene " + size);
                }
            }

        }
        Log.d("UpdateAccommodationFragment", "ACC UMG SIZE JE 0");
        onAllImagesUploaded(uploadedPhotos);
    }

    private void onAllImagesUploaded(List<Photo> uploadedPhotos) {
        Log.d("UpdateAccommodationFragment", "USAAAAAAAAAAAAO u onAllImagesUploaded(");
        for(Photo photo : uploadedPhotos){
            Log.d("UpdateAccommodationFragment", "New photo " + photo.toString());
            if (!accommodation.getPhotos().contains(photo)){
                accommodation.getPhotos().add(photo);
            }
        }
        photos = new ArrayList<>(accommodation.getPhotos());
        Log.d("UpdateAccommodationFragment", "Photos size "  + photos.size());
        for(Photo p : photos){
            Log.d("UpdateAccommodationFragment", "Photo " + p.toString());
        }
        getData();


    }


    private boolean isImageAlreadyUploaded(Bitmap bitmap) {
        // Assuming originalAccommodationImages is a list of Bitmaps
        for (Bitmap originalBitmap : originalAccommodationImages) {
            // Compare bitmaps (you may need a more sophisticated comparison based on your requirements)
            if (originalBitmap.sameAs(bitmap)) {
                return true; // Image is already present in originalAccommodationImages
            }
        }
        return false; // Image is not present in originalAccommodationImages
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;

        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        fileName = cursor.getString(index);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (uri.getScheme().equals("file")) {
            fileName = new File(uri.getPath()).getName();
        }

        return fileName;
    }

    private File bitmapToFile(Bitmap bitmap, String filename) {
        try {
            // Create a file with a unique timestamp-based filename

            File file = new File(requireContext().getCacheDir(), filename);
            file.createNewFile();

            // Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos); // Use JPEG format
            byte[] bitmapdata = bos.toByteArray();

            // Write the bytes to the file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    private void getData(){
        Log.d("UpdateAccommodationFragment", "Name " + nameEditText.getText().toString());
        Log.d("UpdateAccommodationFragment", "Address Street and Number " + addressStreetEditText.getText().toString());
        Log.d("UpdateAccommodationFragment", "Address City " + addressCityEditText.getText().toString());
        Log.d("UpdateAccommodationFragment", "Address Country " + addressCountryEditText.getText().toString());
        Log.d("UpdateAccommodationFragment", "Price" + priceEditText.getText().toString());
        Log.d("UpdateAccommodationFragment", "PriceType " + priceType);
        Log.d("UpdateAccommodationFragment", "Description " + overviewEditText.getText().toString());
        Log.d("UpdateAccommodationFragment", "Postal code " + addressPostalCodeEditText.getText().toString());
        Log.d("UpdateAccommodationFragment", "MinGuests " + minimumGuestsEditText.getText().toString());
        Log.d("UpdateAccommodationFragment", "MaxGuests " + maximumGuestsEditText.getText().toString());
        Log.d("UpdateAccommodationFragment", "Cancellation " + cancellationEditText.getText().toString());
        if (nameEditText.getText().toString().isEmpty() || addressStreetEditText.getText().toString().isEmpty() || addressCityEditText.getText().toString().isEmpty() || addressCountryEditText.getText().toString().isEmpty() || addressPostalCodeEditText.getText().toString().isEmpty() || priceEditText.getText().toString().isEmpty() || overviewEditText.getText().toString().isEmpty() || priceType == null || minimumGuestsEditText.getText().toString().isEmpty() || maximumGuestsEditText.getText().toString().isEmpty() || cancellationEditText.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Some fields are empty!", Toast.LENGTH_SHORT).show();
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

            update(accommodationName, address, price, priceType, description, amenities, type, minGuests, maxGuests, cancellationDeadLine, automaticAcceptReservations);
        }
    }

    private void getPriceChangesList(){
        Log.d("UpdateAccommodationFragment", "SIZEEEEEEEEE " + priceChangesStringList.size());
        if(!priceChangesStringList.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            for (String pc : priceChangesStringList) {
//                String[] lines = pc.split("\\n");
//                for (String line : lines) {
                    String[] parts = pc.split(" - ");
                    Date dateChange = null;
                    Double newPrice = Double.parseDouble(parts[1]);

                    try {
                        dateChange = sdf.parse(parts[0]);
                        PriceChange priceChange = new PriceChange(dateChange, newPrice);
                        priceChanges.add(priceChange);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    private void update(String accommodationName, Address address, Double price, PriceType priceType, String description, List<Amenity> amenities, AccommodationType accommodationType, Integer minGuests, Integer maxGuests, Integer cancellationDeadLine, boolean automaticAcceptReservations){

        getPriceChangesList();

        Log.d("AccommodationAdapter", "Name " + accommodationName);
        Log.d("AccommodationAdapter", "Address " + address.toString());
        Log.d("AccommodationAdapter", "Price " + price);
        Log.d("AccommodationAdapter", "PriceType " + priceType);
        Log.d("AccommodationAdapter", "Description " + description);
        Log.d("AccommodationAdapter", "Amenities size " + amenities.size());
        Log.d("AccommodationAdapter", "AccommodationType " + accommodationType);
        Log.d("AccommodationAdapter", "MinGuests " + minGuests);
        Log.d("AccommodationAdapter", "MaxGuests " + maxGuests);
        Log.d("AccommodationAdapter", "Cancellation " + cancellationDeadLine);
        Log.d("AccommodationAdapter", "Automatic " + automaticAcceptReservations);
        Log.d("AccommodationAdapter", "PriceChanges size " + priceChanges.size());
        Log.d("AccommodationAdapter", "PHOTOSZ SIZEEE " + photos.size());
        for(Photo photo : photos){
            Log.d("AccommodationAdapter", "PHOTOOOOOOOOOOOOOOOOOOOO " + photo.toString());
        }

        Accommodation newAccommodation = new Accommodation(accommodationName, description, address, amenities, photos, minGuests, maxGuests, accommodationType, availibility, priceType, priceChanges, automaticAcceptReservations, AccommodationStatus.CHANGED, LoginScreen.loggedHost, price, 0.0, accommodation.getAverageRating(), cancellationDeadLine);

        Call<Accommodation> updatedAccommodation = ClientUtils.accommodationService.updateAccommodation(newAccommodation, accommodation.getId());

        updatedAccommodation.enqueue(new Callback<Accommodation>() {
            @Override
            public void onResponse(Call<Accommodation> call, Response<Accommodation> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("AccommodationAdapter", "Successful response: " + response.body());
                        Accommodation newAccommodation = response.body();
                        Toast.makeText(requireContext(), "Accommodation updated!", Toast.LENGTH_SHORT).show();
                        updateAccommodationBtn.setEnabled(false);
                    } else {
                        Log.d("AccommodationAdapter", "Response body is null");
                    }
                }  else if (response.code() == 403) {
                    Toast.makeText(requireContext(), "The accommodation has active reservations!", Toast.LENGTH_LONG).show();
                    try {
                        Log.d("AccommodationAdapter", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Accommodation> call, Throwable t) {
                Log.d("AccommodationAdapter", t.getMessage() != null?t.getMessage():"error");
            }
        });
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
        newPriceEditText = view.findViewById(R.id.newPriceEditText);

        updateAccommodationBtn = view.findViewById(R.id.updateAccommodationBtn);
        calendarView = view.findViewById(R.id.calendarView);

        addAvailabilityBtn = view.findViewById(R.id.addAvailabilityBtn);
        removeAvailabilityBtn = view.findViewById(R.id.removeAvailabilityBtn);
        addNewPriceBtn = view.findViewById(R.id.addNewPriceBtn);
        removePriceChange = view.findViewById(R.id.deletePriceChange);

        availabilityTextView = view.findViewById(R.id.availabilityTextView);
        priceChangesTextView = view.findViewById(R.id.priceChangesTextView);

        datePicker = view.findViewById(R.id.datePicker);
        selectImageBtn = view.findViewById(R.id.selectImageBtn);
        selectedImageView = view.findViewById(R.id.selectedImageView);
        removeImageBtn = view.findViewById(R.id.removeImageBtn);

    }

    private void setAccommodationData(){
        nameEditText.setText(accommodation.getName());
        addressStreetEditText.setText(accommodation.getAddress().getStreetAndNumber());
        addressCityEditText.setText(accommodation.getAddress().getCity());
        addressPostalCodeEditText.setText(accommodation.getAddress().getPostalCode());
        addressCountryEditText.setText(accommodation.getAddress().getCountry());
        priceEditText.setText(String.valueOf(accommodation.getPrice()));
        overviewEditText.setText(accommodation.getDescription());
        minimumGuestsEditText.setText(String.valueOf(accommodation.getMinGuests()));
        maximumGuestsEditText.setText(String.valueOf(accommodation.getMaxGuests()));
        cancellationEditText.setText(String.valueOf(accommodation.getCancellationDeadline()));

        if (accommodation.getPriceType().equals(PriceType.PER_GUEST)){
            perGuestRadioButton.setChecked(true);
            priceType = PriceType.PER_GUEST;
        } else {
            perNightRadioButton.setChecked(true);
            priceType = PriceType.PER_NIGHT;
        }

        if (accommodation.getAmenities().contains(Amenity.FREE_WIFI)){
            wifiCheckBox.setChecked(true);
        }
        if(accommodation.getAmenities().contains(Amenity.FITNESS_CENTRE)){
            fitnessCheckBox.setChecked(true);
        }
        if(accommodation.getAmenities().contains(Amenity.PARKING)){
            parkingCheckBox.setChecked(true);
        }
        if(accommodation.getAmenities().contains(Amenity.RESTAURANT)){
            restaurantCheckBox.setChecked(true);
        }
        if(accommodation.getAmenities().contains(Amenity.NON_SMOKING_ROOMS)){
            nonSmokingCheckBox.setChecked(true);
        }
        if(accommodation.getAmenities().contains(Amenity.SWIMMING_POOL)){
            swimmingPoolCheckBox.setChecked(true);
        }

        if(accommodation.getType().equals(AccommodationType.APARTMENT)){
            apartmentRadioButton.setChecked(true);
        }
        if(accommodation.getType().equals(AccommodationType.HOTEL)){
            hotelRadioButton.setChecked(true);
        }
        if(accommodation.getType().equals(AccommodationType.HOSTEL)){
            hostelRadioButton.setChecked(true);
        }
        if(accommodation.getType().equals(AccommodationType.RESORT)){
            resortRadioButton.setChecked(true);
        }
        if(accommodation.getType().equals(AccommodationType.VILLA)){
            villaRadioButton.setChecked(true);
        }

        if(accommodation.isAutomaticReservationAcceptance()){
            toggleSwitch.setChecked(true);
        } else {
            toggleSwitch.setChecked(false);
        }

        availabilityStringList = new ArrayList<>();
        Log.d("UpdateAccommodationFragment", "AVAILLIBILITYY " + accommodation.getAvailability().size());
        Log.d("UpdateAccommodationFragment", "PRICE CAHNGES " + accommodation.getPriceChanges().size());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        if(!accommodation.getAvailability().isEmpty()){
            for (DateRange dr : accommodation.getAvailability()){
                String startDate = sdf.format(dr.getStartDate());
                String endDate = sdf.format(dr.getEndDate());
                if(!availabilityStringList.contains(startDate + " to " + endDate)){
                    availabilityStringList.add(startDate + " to " + endDate);
                }
            }
        } else {
            availabilityTextView.setText("");
        }

        for(String date : availabilityStringList){
            stringBuilderAvailibility.append(date).append("\n");
        }

        availabilityTextView.setText(stringBuilderAvailibility.toString());

        priceChangesStringList = new ArrayList<>();

        if(!accommodation.getPriceChanges().isEmpty()){
            for (PriceChange pr : accommodation.getPriceChanges()){
                String startDate = sdf.format(pr.getChangeDate());
                if (!priceChangesStringList.contains(startDate + " - " + pr.getNewPrice())){
                    priceChangesStringList.add(startDate + " - " + pr.getNewPrice());
                }
            }
        } else {
            priceChangesTextView.setText("");
        }
        for(String priceChange : priceChangesStringList){
            stringBuilderPriceChange.append(priceChange).append("\n");
        }
        priceChangesTextView.setText(stringBuilderPriceChange.toString());
    }




}