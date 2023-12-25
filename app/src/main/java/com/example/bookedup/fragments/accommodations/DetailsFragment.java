package com.example.bookedup.fragments.accommodations;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.bookedup.R;
import com.example.bookedup.activities.GuestMainScreen;
import com.example.bookedup.model.Accommodation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailsFragment extends Fragment {

    private TextView titleTxt, locationTxt, bedTxt, wifiTxt, parkingTxt, descriptionTxt, scoreTxt, priceTxt;
    private Accommodation item;
    private ImageView picImg, fav;

    private boolean isFavorite = false;

    private FloatingActionButton commentPopup;

    private Dialog commentDialog;

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

        initView(view);
//        setVariable();
//        setAccommodation();
        setupFavoriteIcon();

//        if (accommodation != null && accommodation.getPhotos() != null && !accommodation.getPhotos().isEmpty()) {
//            String photoUrl = accommodation.getPhotos().get(0).getUrl();
//
//            drawableResId = String.valueOf(getResources().getIdentifier(photoUrl, "drawable", requireActivity().getPackageName()));
//
//            if (isAdded()) {
//                Glide.with(requireActivity()).load(drawableResId).into(picImg);
//            }
//        }
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
//            titleTxt.setText(accommodation.getName());
//
//            scoreTxt.setText(String.valueOf(accommodation.getAverageRating()));
//
//            locationTxt.setText(accommodation.getAddress().getCountry() + " " + accommodation.getAddress().getCity());
//
//            scoreTxt.setText(String.valueOf(accommodation.getAverageRating()));



        }


        // ... other TextViews



    private void initView(View view) {
        titleTxt = view.findViewById(R.id.titleTxt);
        locationTxt = view.findViewById(R.id.locationTxt);
        bedTxt = view.findViewById(R.id.bedTxt);
        wifiTxt = view.findViewById(R.id.wifiTxt);
        parkingTxt = view.findViewById(R.id.parkingTxt);
        descriptionTxt = view.findViewById(R.id.descriptionTxt);
        scoreTxt = view.findViewById(R.id.scoreTxt);
        picImg = view.findViewById(R.id.picImg);
        priceTxt = view.findViewById(R.id.priceTxt);
        fav = view.findViewById(R.id.fav);
        commentPopup = view.findViewById(R.id.addCommentBtn);

        titleTxt.setText(accommodation.getName());
        locationTxt.setText(accommodation.getAddress().getCountry() + " " + accommodation.getAddress().getCity());
        scoreTxt.setText(String.valueOf(accommodation.getAverageRating()));
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
    }


    private void showCommentDialog() {
        commentDialog = new Dialog(requireContext());
        commentDialog.setContentView(R.layout.comment_popup);
        commentDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        commentDialog.show();
    }


}
