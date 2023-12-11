package com.example.bookedup.fragments.accommodations;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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
        setVariable();
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

    private void setVariable() {
        // Replace this with the appropriate way of getting your item
        item = (Accommodation) getArguments().getSerializable("object");

        titleTxt.setText(item.getTitle());
        scoreTxt.setText(String.valueOf(item.getScore()));
        locationTxt.setText(item.getLocation());
        bedTxt.setText(item.getBed() + " bed");
        descriptionTxt.setText(item.getDescription());
        priceTxt.setText("$" + item.getPrice() + " per night");

        if (item.isWifi()) {
            wifiTxt.setText("wifi");
        } else {
            wifiTxt.setText("no-wifi");
        }

        if (item.isParking()) {
            parkingTxt.setText("parking");
        } else {
            parkingTxt.setText("no-parking");
        }

        int drawableResId = getResources().getIdentifier(item.getPic(), "drawable", requireActivity().getPackageName());

        Glide.with(requireActivity()).load(drawableResId).into(picImg);
    }

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
