package com.example.bookedup.fragments.accommodations;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.bookedup.R;
import com.example.bookedup.activities.GuestMainScreen;
import com.example.bookedup.fragments.reservations.CreateReservationFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.enums.Amenity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DetailsFragment extends Fragment {

    private TextView titleTxt, locationTxt, descriptionTxt, scoreTxt, priceTxt, pricePerTxt, staysTimeTxt;
    private ImageView picImg, fav;

    private int daysNum = 0;

    private int guestNum = 0;

    private boolean isFavorite = false;

    private FloatingActionButton commentPopup;

    private Dialog commentDialog;

    private Button book;

    private FragmentManager fragmentManager;

    private int targetLayout;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String drawableResId;

    private Accommodation accommodation;


    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        Intent intent = getActivity().getIntent();
        ComponentName componentName = intent.getComponent();
        if (componentName.getClassName().equals("com.example.bookedup.activities.GuestMainScreen")) {
            targetLayout = R.id.frame_layout;
        } else if (componentName.getClassName().equals("com.example.bookedup.activities.AdministratorMainScreen")){
            targetLayout = R.id.frame_layoutAdmin;
        } else if (componentName.getClassName().equals("com.example.bookedup.activities.HostMainScreen")){
            targetLayout = R.id.frame_layoutHost;
        }

        initView(view);
        setupFavoriteIcon();

        return view;
    }

    private void setupFavoriteIcon() {
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavoriteIcon();
            }
        });
    }

    private void toggleFavoriteIcon() {
        isFavorite = !isFavorite;

        // Change the icon based on the favorite state
        if (isFavorite) {
            fav.setImageResource(R.drawable.ic_favorites);
        } else {
            fav.setImageResource(R.drawable.ic_red_heart);

        }
    }

    public void setAccommodation(Accommodation accommodation) {
        Log.e("DetailsFragment", "TUUU JEEEEE");
        this.accommodation = accommodation;
        }



    private void initView(View view) {

        titleTxt = view.findViewById(R.id.titleTxt);
        locationTxt = view.findViewById(R.id.locationTxt);;
        descriptionTxt = view.findViewById(R.id.descriptionTxt);
        scoreTxt = view.findViewById(R.id.scoreTxt);
        picImg = view.findViewById(R.id.picImg);
        priceTxt = view.findViewById(R.id.priceTxt);
        pricePerTxt = view.findViewById(R.id.pricePerTxt);
        fav = view.findViewById(R.id.fav);
        commentPopup = view.findViewById(R.id.addCommentBtn);
        book = view.findViewById(R.id.bookNow);
        staysTimeTxt = view.findViewById(R.id.staysTimeTxt);

        titleTxt.setText(accommodation.getName());
        locationTxt.setText(accommodation.getAddress().getCountry() + " " + accommodation.getAddress().getCity());
        scoreTxt.setText(String.valueOf(accommodation.getAverageRating()));
        descriptionTxt.setText(accommodation.getDescription());

        if (accommodation.getTotalPrice() == 0) {
            // Sakrij priceTxt ako je cena 0
            priceTxt.setVisibility(View.GONE);
        } else {
            // Postavite tekst ako cena nije 0
            priceTxt.setText(String.valueOf(accommodation.getTotalPrice()) + "$");
        }
        pricePerTxt.setText(String.valueOf(accommodation.getPrice()) + " " + accommodation.getPriceType().getPriceType());

        if (guestNum == 0 || daysNum == 0) {
            // Sakrij priceTxt ako je cena 0
            staysTimeTxt.setVisibility(View.GONE);
        } else {
            // Postavite tekst ako cena nije 0
            staysTimeTxt.setText(guestNum + " adults "  + daysNum + " days");
        }
        displayAmenities(accommodation.getAmenities(), view);

        String imageUrl = accommodation.getPhotos().get(0).getUrl();
        Glide.with(requireContext()).load(imageUrl)
                .transform(new CenterCrop(), new GranularRoundedCorners(40, 40, 40, 40))
                .into(picImg);

        commentPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new CreateReservationFragment());
            }
        });
    }


    private void showCommentDialog() {
        commentDialog = new Dialog(requireContext());
        commentDialog.setContentView(R.layout.comment_popup);
        commentDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        commentDialog.show();
    }

    private void openFragment(Fragment fragment){
        fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(targetLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void displayAmenities(List<Amenity> amenities, View view) {
        LinearLayout amenitiesContainer = view.findViewById(R.id.amenitiesContainer);

        for (Amenity amenity : amenities) {
            // Inflate the amenity layout dynamically
            View amenityView = LayoutInflater.from(requireContext()).inflate(R.layout.amenity_item, amenitiesContainer, false);

            // Find views in the inflated layout
            ImageView amenityImage = amenityView.findViewById(R.id.amenityImage);
            TextView amenityText = amenityView.findViewById(R.id.amenityText);

            // Set values based on the Amenity enum
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
            amenityText.setText(amenity.getAmenity()); // You can customize this based on your enum structure

            // Add the inflated layout to the container
            amenitiesContainer.addView(amenityView);
        }
    }



}
