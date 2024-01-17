package com.example.bookedup.fragments.accommodations;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.bookedup.R;
import com.example.bookedup.activities.GuestMainScreen;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.activities.SplashScreen;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.reservations.CreateReservationFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Guest;
import com.example.bookedup.model.User;
import com.example.bookedup.model.enums.Amenity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsFragment extends Fragment {

    private TextView titleTxt, locationTxt, descriptionTxt, scoreTxt, priceTxt, pricePerTxt, staysTimeTxt;
    private ImageView picImg;
    private int daysNum = 0, guestNum = 0, targetLayout;
    private boolean isFavourite = false;
    private FloatingActionButton commentPopup, favouriteButton;
    private Dialog commentDialog;
    private Button book;
    private String checkIn, checkOut;
    private FragmentManager fragmentManager;
    private Accommodation accommodation;
    public DetailsFragment() {
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
        isAccommodationFavourite();
        initView(view);

        commentPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new CreateReservationFragment(accommodation, checkIn, checkOut, guestNum));
            }
        });

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavouriteState();
            }
        });
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
                    Log.d("DetailsFragment", "Unsuccessful response: " + response.code());
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
        // Fetch the latest guest data from the server based on the user ID
        Call<Guest> getGuestCall = ClientUtils.guestService.getGuest(LoginScreen.loggedUser.getId());
        getGuestCall.enqueue(new Callback<Guest>() {
            @Override
            public void onResponse(Call<Guest> call, Response<Guest> response) {
                if (response.isSuccessful()) {
                    // Update the loggedGuest object with the latest data
                    LoginScreen.loggedGuest = response.body();
                } else {
                    // Handle unsuccessful response
                    Log.d("DetailsFragment", "Failed to fetch updated guest data");
                }
            }

            @Override
            public void onFailure(Call<Guest> call, Throwable t) {
                // Handle failure
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

    private void initView(View view) {
        fragmentManager = getParentFragmentManager();
        favouriteButton = view.findViewById(R.id.favouriteButton);
        titleTxt = view.findViewById(R.id.titleTxt);
        locationTxt = view.findViewById(R.id.locationTxt);;
        descriptionTxt = view.findViewById(R.id.descriptionTxt);
        scoreTxt = view.findViewById(R.id.scoreTxt);
        picImg = view.findViewById(R.id.picImg);
        priceTxt = view.findViewById(R.id.priceTxt);
        pricePerTxt = view.findViewById(R.id.pricePerTxt);
        commentPopup = view.findViewById(R.id.addCommentBtn);
        book = view.findViewById(R.id.bookNow);
        staysTimeTxt = view.findViewById(R.id.staysTimeTxt);

        titleTxt.setText(accommodation.getName());
        locationTxt.setText(accommodation.getAddress().getCountry() + " " + accommodation.getAddress().getCity());
        scoreTxt.setText(String.valueOf(accommodation.getAverageRating()));
        descriptionTxt.setText(accommodation.getDescription());

        if (accommodation.getTotalPrice() == 0) {
            priceTxt.setVisibility(View.GONE);
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


        //MENJACE SE

        String imageUrl = "";

        if (!accommodation.getPhotos().isEmpty()) {
            imageUrl = accommodation.getPhotos().get(0).getUrl();
        } else {
            imageUrl = "android.resource://" + requireContext().getPackageName() + "/" + R.drawable.default_hotel_img;
        }

        Glide.with(requireContext()).load(imageUrl)
                .transform(new CenterCrop(), new GranularRoundedCorners(40, 40, 40, 40))
                .into(picImg);
    }


    private void showCommentDialog() {
        commentDialog = new Dialog(requireContext());
        commentDialog.setContentView(R.layout.comment_popup);
        commentDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        commentDialog.show();
    }

    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(targetLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
