package com.example.bookedup.fragments.reservations;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.adapters.AccommodationAdapter;
import com.example.bookedup.adapters.TypeAdapter;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Guest;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.Review;
import com.example.bookedup.model.User;
import com.example.bookedup.model.UserReport;
import com.example.bookedup.model.enums.ReservationStatus;
import com.example.bookedup.model.enums.ReviewType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateReservationFragment extends Fragment {

    private Accommodation accommodation;
    private String checkIn, checkOut;
    private Integer guestsNumber;
    private ImageView picImg;
    private Dialog reportDialog, commentDialog;
    private FloatingActionButton reportUserBtn;
    private Button btnCreate, reportBtn, postComment;
    private TextView title, location, score, pricePer, startDateTxt, endDateTxt, totalPrice, staysTime, status, user;
    private EditText reportReasonsTxt, accommodationCommentTxt, hostCommentTxt;
    private Date startDate = new Date(), endDate = new Date();
    private Reservation existingReservation = null;
    private RatingBar accommodationRating, hostRating;

    public CreateReservationFragment(Reservation existingReservation) {
        this.existingReservation = existingReservation;
    }

    public CreateReservationFragment(Accommodation accommodation, String checkIn, String checkOut, Integer guestsNumber) {
        this.accommodation = accommodation;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guestsNumber = guestsNumber;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_reservation, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        if (existingReservation!=null){
            Log.d("CreateReservationFragment", "end date " + existingReservation.getEndDate());
            if (getDaysPastOfCompletedReservation(existingReservation) >= 7){
                btnCreate.setText("Add review");
                btnCreate.setVisibility(View.VISIBLE);
            } else {
                btnCreate.setVisibility(View.INVISIBLE);
            }
            initExistingReservationView(view);
        } else {
            initCreateReservationView(view);
        }

        if(btnCreate.getText().toString().equals("Add review")){

            btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCommentDialog();
                }
            });

        } else {

            btnCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Reservation reservation = new Reservation();
                    Log.d("CreateReservationFragment", "id " + LoginScreen.loggedUser.getRole());
                    Log.d("CreateReservationFragment", "Guest " + LoginScreen.loggedGuest);

                    reservation.setGuest(LoginScreen.loggedGuest);

                    reservation.setAccommodation(accommodation);
                    reservation.setStartDate(startDate);
                    reservation.setEndDate(endDate);
                    reservation.setGuestsNumber(guestsNumber);
                    reservation.setTotalPrice(accommodation.getTotalPrice());
                    if (reservation.getAccommodation().isAutomaticReservationAcceptance()) {
                        reservation.setStatus(ReservationStatus.ACCEPTED);
                    } else {
                        reservation.setStatus(ReservationStatus.CREATED);
                    }
                    createReservation(reservation);
                }
            });
        }

        reportUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportDialog();
            }
        });

    }

    private int getDaysPastOfCompletedReservation(Reservation existingReservation) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();
        long timeDifference = currentDate.getTime() - existingReservation.getEndDate().getTime();
        int daysPassed = (int)TimeUnit.DAYS.convert(timeDifference, TimeUnit.MILLISECONDS);

        Log.d("CreateReservationFragment", "Days passed since the end date: " + daysPassed);

        return daysPassed;
    }


    private void showReportDialog() {
        reportDialog = new Dialog(requireContext());
        reportDialog.setContentView(R.layout.user_report_popup);
        reportBtn = reportDialog.findViewById(R.id.reportButton);
        reportReasonsTxt = reportDialog.findViewById(R.id.reasonsInput);
        reportDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        reportDialog.show();

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportUser();
            }
        });
    }

    private void reportUser(){
        String reasons = reportReasonsTxt.getText().toString();
        User reportedUser = null;
        if (LoginScreen.loggedGuest != null){
            reportedUser = existingReservation.getAccommodation().getHost();
        } else if (LoginScreen.loggedHost != null){
            reportedUser = existingReservation.getGuest();
        }

        UserReport userReport = new UserReport(reasons, reportedUser, true);

        Call<UserReport> report = ClientUtils.userReportService.createUserReport(userReport);
        report.enqueue(new Callback<UserReport>() {
            @Override
            public void onResponse(Call<UserReport> call, Response<UserReport> response) {
                if (response.isSuccessful()) {
                    UserReport newReport = response.body();
                    Log.d("CreateReservationFragment", "User report " + newReport.toString());
                    reportDialog.dismiss();
                    Toast.makeText(requireContext(), "User report created!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("CreateReservationFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("CreateReservationFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserReport> call, Throwable t) {
                Log.d("CreateReservationFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void initView(View view){
        btnCreate = view.findViewById(R.id.buttonCreateRes);
    }

    private void createReservation(Reservation reservation){
        Call<Reservation> createdReservation = ClientUtils.reservationService.createReservation(reservation);

        createdReservation.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("CreateReservationFragment", "Successful response: " + response.body());
                        Reservation newReservation = response.body();
                        Toast.makeText(requireContext(), "Reservation created!", Toast.LENGTH_SHORT).show();
                        btnCreate.setEnabled(false);
                    } else {
                        Log.d("CreateReservationFragment", "Response body is null");
                    }
                }  else {
                    // Log error details
                    Log.d("CreateReservationFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("CreateReservationFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                Log.d("CreateReservationFragment", t.getMessage() != null?t.getMessage():"error");
            }
        });
    }

    private void initExistingReservationView(View view){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        picImg = view.findViewById(R.id.picImg);
        String imageUrl = existingReservation.getAccommodation().getPhotos().get(0).getUrl();
        Glide.with(requireContext()).load(imageUrl)
                .transform(new CenterCrop(), new GranularRoundedCorners(40, 40, 40, 40))
                .into(picImg);

        title = view.findViewById(R.id.titleTxt);
        title.setText(existingReservation.getAccommodation().getName());

        location = view.findViewById(R.id.locationTxt);
        location.setText(existingReservation.getAccommodation().getAddress().getCountry() + " " + existingReservation.getAccommodation().getAddress().getCity());

        score = view.findViewById(R.id.scoreTxt);
        score.setText(existingReservation.getAccommodation().getAverageRating().toString());

        pricePer = view.findViewById(R.id.priceTxt);
        pricePer.setText(existingReservation.getAccommodation().getPrice() + "$/" + existingReservation.getAccommodation().getPriceType().getPriceType().toString());

        startDateTxt = view.findViewById(R.id.checkInTxt);
        startDateTxt.setText("Check-In: " + sdf.format(existingReservation.getStartDate()));

        endDateTxt = view.findViewById(R.id.checkOutTxt);
        endDateTxt.setText("Check-Out: " + sdf.format(existingReservation.getEndDate()));

        totalPrice = view.findViewById(R.id.totalPriceTxt);
        totalPrice.setText("Total price: " + existingReservation.getTotalPrice() + "$");

        int days = calculateDays(sdf.format(existingReservation.getStartDate()), sdf.format(existingReservation.getEndDate()));

        staysTime = view.findViewById(R.id.staysTimeTxt);
        staysTime.setText(existingReservation.getGuestsNumber() + " adults "  + days + " days ");

        status  = view.findViewById(R.id.status);
        status.setText("Status: " + existingReservation.getStatus().toString());

        user = view.findViewById(R.id.user);
        if (LoginScreen.loggedGuest != null){
            user.setText("Host: " + existingReservation.getAccommodation().getHost().getFirstName() + " " + existingReservation.getAccommodation().getHost().getLastName());
        } else if (LoginScreen.loggedHost != null){
            user.setText("Guest: " + existingReservation.getGuest().getFirstName() + " " + existingReservation.getGuest().getLastName());
        }

        reportUserBtn = view.findViewById(R.id.reportUserBtn);
        if (!existingReservation.getStatus().equals(ReservationStatus.COMPLETED)){
            reportUserBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void initCreateReservationView(View view){
        picImg = view.findViewById(R.id.picImg);
        String imageUrl = accommodation.getPhotos().get(0).getUrl();
        Glide.with(requireContext()).load(imageUrl)
                .transform(new CenterCrop(), new GranularRoundedCorners(40, 40, 40, 40))
                .into(picImg);

        title = view.findViewById(R.id.titleTxt);
        title.setText(accommodation.getName());

        location = view.findViewById(R.id.locationTxt);
        location.setText(accommodation.getAddress().getCountry() + " " + accommodation.getAddress().getCity());

        score = view.findViewById(R.id.scoreTxt);
        score.setText(accommodation.getAverageRating().toString());

        pricePer = view.findViewById(R.id.priceTxt);
        pricePer.setText(accommodation.getPrice() + "$/" + accommodation.getPriceType().getPriceType().toString());

        startDateTxt = view.findViewById(R.id.checkInTxt);
        startDateTxt.setText("Check-In: " + checkIn.toString());

        endDateTxt = view.findViewById(R.id.checkOutTxt);
        endDateTxt.setText("Check-Out: " + checkOut.toString());

        totalPrice = view.findViewById(R.id.totalPriceTxt);
        totalPrice.setText("Total price: " + accommodation.getTotalPrice() + "$");

        int days = calculateDays(checkIn, checkOut);

        staysTime = view.findViewById(R.id.staysTimeTxt);
        staysTime.setText(guestsNumber + " adults "  + days + " days ");
    }

    private int calculateDays(String checkIn, String checkOut) {
        Log.d("CreateReservationFragment", "CheckIn " + checkIn);
        Log.d("CreateReservationFragment", "CheckOut " + checkOut);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            startDate = sdf.parse(checkIn);
            endDate = sdf.parse(checkOut);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long startTime = startDate.getTime();
        long endTime = endDate.getTime();

        long diffInMilliseconds = endTime - startTime;
        int days = (int) TimeUnit.MILLISECONDS.toDays(diffInMilliseconds);
        return days;
    }

        private void showCommentDialog() {
            commentDialog = new Dialog(requireContext());
            commentDialog.setContentView(R.layout.comment_popup);
            commentDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            postComment = commentDialog.findViewById(R.id.postButton);
            accommodationRating = commentDialog.findViewById(R.id.ratingBarAccommodation);
            hostRating = commentDialog.findViewById(R.id.hostRatingBar);
            accommodationCommentTxt = commentDialog.findViewById(R.id.commentAccommodationInput);
            hostCommentTxt = commentDialog.findViewById(R.id.commentHostInput);

            postComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rateAndComment();
                    commentDialog.dismiss();
                }
            });

            commentDialog.show();


        }

        private void rateAndComment() {
            Integer accommodationReview = (int) accommodationRating.getRating();
            Integer hostReview = (int) hostRating.getRating();
            String accommodationComment = accommodationCommentTxt.getText().toString();
            String hostComment = hostCommentTxt.getText().toString();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            if (accommodationReview != 0 || !accommodationComment.isEmpty()){
                Review review = new Review(LoginScreen.loggedGuest, accommodationReview, accommodationComment, sdf.format(new Date()), existingReservation.getAccommodation().getHost(), existingReservation.getAccommodation(), ReviewType.ACCOMMODATION, false);
                createReview(review);
            }

            if (hostReview != 0 || !hostComment.isEmpty()){
                Review review = new Review(LoginScreen.loggedGuest, hostReview, hostComment, sdf.format(new Date()), existingReservation.getAccommodation().getHost(), existingReservation.getAccommodation(), ReviewType.HOST, false);
                createReview(review);
            }

        }

        private void createReview(Review review){
            Call<Review> createdReview = ClientUtils.reviewService.createReview(review);
            createdReview.enqueue(new Callback<Review>() {
                @Override
                public void onResponse(Call<Review> call, Response<Review> response) {
                    if (response.isSuccessful()) {
                        Review newReview = response.body();
                        Toast.makeText(getContext(), "Comment successfully created!", Toast.LENGTH_SHORT).show();
                        Log.d("CreateReservationFragment", "New Review " + newReview.toString());
                    } else {
                        Log.d("CreateReservationFragment", "Error " + response.code());
                    }
                }
                @Override
                public void onFailure(Call<Review> call, Throwable t) {
                    Log.d("CreateReservationFragment","Error  "  + t.getMessage());
                }
            });
    }




}