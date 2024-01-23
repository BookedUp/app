package com.example.bookedup.fragments.accommodations;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.adapters.CommentAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.reservations.CreateReservationFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Guest;
import com.example.bookedup.model.Review;
import com.example.bookedup.model.ReviewReport;
import com.example.bookedup.model.UserReport;
import com.example.bookedup.model.enums.Amenity;
import com.example.bookedup.fragments.reviews.ReviewsListFragment;
import com.example.bookedup.model.enums.Role;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsFragment extends Fragment{

    private TextView titleTxt, locationTxt, descriptionTxt, scoreTxt, priceTxt, pricePerTxt, staysTimeTxt, seeMoreAccTxt, seeMoreHostTxt;
    private ImageView picImg;
    private int daysNum = 0, guestNum = 0, targetLayout, currentImageIndex;
    private boolean isFavourite = false;
    private FloatingActionButton favouriteButton, commentReportBtn;
    private Button book;
    private String checkIn, checkOut;
    private FragmentManager fragmentManager;
    private Accommodation accommodation;
    private RecyclerView recyclerViewAccommodation, recyclerViewHost;
    private ArrayList<Review> accommodationReviews = new ArrayList<>();
    private ArrayList<Review> hostReviews = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private List<Bitmap> accommodationImages;



    public DetailsFragment(List<Bitmap> accommodationImages) {
        this.accommodationImages = accommodationImages;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findTargetLayout();
        getCallerData();
        if (LoginScreen.loggedUser.getRole().equals(Role.GUEST)) {
            isAccommodationFavourite();
        }

        initView(view);
        initAccommodationCommentsRecyclerView(accommodationReviews);
        initHostCommentsRecyclerView(hostReviews);

        picImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextImage();
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new CreateReservationFragment(accommodation, checkIn, checkOut, guestNum, accommodationImages));
            }
        });

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavouriteState();
            }
        });

        seeMoreAccTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DetailsFragment", "Accommodation reviews " + accommodationReviews.size());
                if (accommodationReviews.size() <= 3){
                    Toast.makeText(getContext(), "No more comments", Toast.LENGTH_SHORT).show();
                } else {
                    openFragment(new ReviewsListFragment(accommodationReviews, targetLayout));
                }
            }
        });

        seeMoreHostTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hostReviews.size() <= 3){
                    Toast.makeText(getContext(), "No more comments", Toast.LENGTH_SHORT).show();
                } else {
                    openFragment(new ReviewsListFragment(hostReviews, targetLayout));
                }
            }
        });


    }


    private void showNextImage() {
        if (!accommodationImages.isEmpty()) {
            currentImageIndex = (currentImageIndex + 1) % accommodationImages.size();
            picImg.setImageBitmap(accommodationImages.get(currentImageIndex));
        }
        if(accommodationImages.size() == 1){
            Toast.makeText(requireContext(), "No more images to show!", Toast.LENGTH_SHORT).show();
        }
    }


    private void isAccommodationFavourite() {
        Call<Boolean> checked = ClientUtils.guestService.isFavouriteAccommodation(LoginScreen.loggedUser.getId(), accommodation.getId());
        checked.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    isFavourite = response.body();
                    if (isFavourite){
                        setHeartColor(R.color.red);
                    } else {
                        setHeartColor(R.color.grey);
                    }
                } else {
                    Log.d("DetailsFragment", "ABDBSDBBJ Unsuccessful response: " + response.code());
                    try {
                        Log.d("DetailsFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.d("DetailsFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void setHeartColor(Integer color){
        favouriteButton.setColorFilter(getResources().getColor(color));
    }


    private void toggleFavouriteState() {
        isFavourite = !isFavourite;

        if (isFavourite) {
            setHeartColor(R.color.red);
            addToFavourite();
        } else {
            setHeartColor(R.color.grey);
            removeFromFavourite();
        }
    }

    private void addToFavourite(){
        Call<Void> addedFavourite = ClientUtils.guestService.addFavouriteAccommodation(LoginScreen.loggedUser.getId(), accommodation.getId());
        addedFavourite.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_SHORT).show();
                    updateLoggedGuest();
                    for(Accommodation acc : LoginScreen.loggedGuest.getFavourites()){
                        Log.d("DetailsFragment", "Acc " + acc.toString());
                    }
                } else {
                    Log.d("DetailsFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("DetailsFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("DetailsFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void removeFromFavourite(){
        Call<Void> removedFavourite = ClientUtils.guestService.removeFavouriteAccommodation(LoginScreen.loggedUser.getId(), accommodation.getId());
        removedFavourite.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                    updateLoggedGuest();
                } else {
                    Log.d("DetailsFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("DetailsFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("DetailsFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void updateLoggedGuest() {
        Call<Guest> getGuestCall = ClientUtils.guestService.getGuest(LoginScreen.loggedUser.getId());
        getGuestCall.enqueue(new Callback<Guest>() {
            @Override
            public void onResponse(Call<Guest> call, Response<Guest> response) {
                if (response.isSuccessful()) {
                    LoginScreen.loggedGuest = response.body();
                } else {
                    Log.d("DetailsFragment", "Failed to fetch updated guest data");
                }
            }

            @Override
            public void onFailure(Call<Guest> call, Throwable t) {
                Log.d("DetailsFragment", "Failed to fetch updated guest data: " + t.getMessage());
            }
        });
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

    private void getCallerData(){
        Bundle arguments = getArguments();
        if (arguments != null) {
            guestNum = arguments.getInt("guestsNumber");
            checkIn = arguments.getString("checkIn");
            checkOut = arguments.getString("checkOut");
        }
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    public void setAccommodationReviews(ArrayList<Review> reviews) {
        this.accommodationReviews = reviews;
    }

    public void setHostReviews(ArrayList<Review> reviews) {
        this.hostReviews = reviews;
    }

    private void initView(View view) {
        fragmentManager = getParentFragmentManager();
        favouriteButton = view.findViewById(R.id.favouriteButton);
        if (LoginScreen.loggedUser.getRole().equals(Role.GUEST)){
            favouriteButton.setVisibility(View.VISIBLE);
        } else {
            favouriteButton.setVisibility(View.INVISIBLE);
        }

        titleTxt = view.findViewById(R.id.titleTxt);
        locationTxt = view.findViewById(R.id.locationTxt);;
        descriptionTxt = view.findViewById(R.id.descriptionTxt);
        scoreTxt = view.findViewById(R.id.scoreTxt);
        picImg = view.findViewById(R.id.picImg);
        priceTxt = view.findViewById(R.id.priceTxt);
        pricePerTxt = view.findViewById(R.id.pricePerTxt);

        book = view.findViewById(R.id.bookNow);
        staysTimeTxt = view.findViewById(R.id.staysTimeTxt);

        titleTxt.setText(accommodation.getName());
        locationTxt.setText(accommodation.getAddress().getCountry() + " " + accommodation.getAddress().getCity());
        scoreTxt.setText(String.valueOf(accommodation.getAverageRating()));
        descriptionTxt.setText(accommodation.getDescription());

        if (accommodation.getTotalPrice() == 0) {
            priceTxt.setVisibility(View.INVISIBLE);
            book.setVisibility(View.INVISIBLE);
        } else {
            priceTxt.setText(String.valueOf(accommodation.getTotalPrice()) + "$");
        }
        pricePerTxt.setText(String.valueOf(accommodation.getPrice()) + " " + accommodation.getPriceType().getPriceType());

        if (guestNum == 0 || daysNum == 0) {
            staysTimeTxt.setVisibility(View.GONE);
        } else {
            staysTimeTxt.setText(guestNum + " adults "  + daysNum + " days");
        }
        displayAmenities(accommodation.getAmenities(), view);
        picImg.setImageBitmap(accommodationImages.get(0));


        recyclerViewAccommodation = view.findViewById(R.id.commentRecyclerView);
        recyclerViewHost = view.findViewById(R.id.hostRecyclerView);
        seeMoreAccTxt = view.findViewById(R.id.seeMoreAccTxt);
        seeMoreHostTxt = view.findViewById(R.id.seeMoreHostTxt);
    }



    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(targetLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void initAccommodationCommentsRecyclerView(ArrayList<Review> reviews){
        List<Review> limitedList = reviews.subList(0, Math.min(reviews.size(), 3));
        loadProfilePictures(limitedList, recyclerViewAccommodation);
    }

    private void initHostCommentsRecyclerView(ArrayList<Review> reviews){
        List<Review> limitedList = reviews.subList(0, Math.min(reviews.size(), 3));
        loadProfilePictures(limitedList, recyclerViewHost);
    }


    private void loadProfilePictures(List<Review> reviews, RecyclerView recyclerView){
        Map<Long, Bitmap> usersImageMap = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        AtomicInteger loadedImagesCount = new AtomicInteger(0);
        Handler handler = new Handler(Looper.getMainLooper());

        for (Review review : reviews) {
            executorService.execute(() -> {
                try {
                    Call<ResponseBody> photoCall = ClientUtils.photoService.loadPhoto(review.getGuest().getProfilePicture().getId());
                    Response<ResponseBody> response = photoCall.execute();

                    if (response.isSuccessful()) {
                        byte[] photoData = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
                        usersImageMap.put(review.getGuest().getId(), bitmap);

                        if (loadedImagesCount.incrementAndGet() == reviews.size()) {
                            handler.post(() -> {
                                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                                commentAdapter = new CommentAdapter(this, reviews, targetLayout, usersImageMap);
                                recyclerView.setAdapter(commentAdapter);
                            });
                        }
                    } else {
                        Log.d("DetailsFragment", "Error code " + response.code());
                    }
                } catch (IOException e) {
                    Log.e("DetailsFragment", "Error reading response body: " + e.getMessage());
                }
            });
        }
        executorService.shutdown();
    }

    private void displayAmenities(List<Amenity> amenities, View view) {
        LinearLayout amenitiesContainer = view.findViewById(R.id.amenitiesContainer);

        for (Amenity amenity : amenities) {
            View amenityView = LayoutInflater.from(requireContext()).inflate(R.layout.amenity_item, amenitiesContainer, false);

            ImageView amenityImage = amenityView.findViewById(R.id.amenityImage);
            TextView amenityText = amenityView.findViewById(R.id.amenityText);

            if (amenity == Amenity.FREE_WIFI) {
                amenityImage.setImageResource(R.drawable.ic_wifi);
            } else if (amenity == Amenity.PARKING) {
                amenityImage.setImageResource(R.drawable.ic_parking);
            } else if (amenity == Amenity.FITNESS_CENTRE){
                amenityImage.setImageResource(R.drawable.ic_fitness_centre);
            } else if (amenity == Amenity.NON_SMOKING_ROOMS){
                amenityImage.setImageResource(R.drawable.ic_no_smoking);
            } else if (amenity == Amenity.SWIMMING_POOL){
                amenityImage.setImageResource(R.drawable.ic_swimming_pool);
            } else if (amenity == Amenity.RESTAURANT){
                amenityImage.setImageResource(R.drawable.ic_restaurant);
            }
            amenityText.setText(amenity.getAmenity());
            amenitiesContainer.addView(amenityView);
        }
    }


}
