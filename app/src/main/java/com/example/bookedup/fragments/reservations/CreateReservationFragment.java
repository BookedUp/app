package com.example.bookedup.fragments.reservations;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.example.bookedup.model.Notification;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.Review;
import com.example.bookedup.model.User;
import com.example.bookedup.model.UserReport;
import com.example.bookedup.model.enums.NotificationType;
import com.example.bookedup.model.enums.ReservationStatus;
import com.example.bookedup.model.enums.ReviewType;
import com.example.bookedup.model.enums.Role;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateReservationFragment extends Fragment {

    private Accommodation accommodation;
    private String checkIn, checkOut;
    private Integer guestsNumber, currentImageIndex = 0;
    private ImageView picImg;
    private Dialog reportDialog, commentDialog;
    private FloatingActionButton reportUserBtn;
    private Button btnCreate, reportBtn, postComment;
    private TextView title, location, score, pricePer, startDateTxt, endDateTxt, totalPrice, staysTime, status, user;
    private EditText reportReasonsTxt, accommodationCommentTxt, hostCommentTxt;
    private Date startDate = new Date(), endDate = new Date();
    private Reservation existingReservation = null;
    private RatingBar accommodationRating, hostRating;
    private List<Bitmap> accommodationImages;

    public CreateReservationFragment(Reservation existingReservation, List<Bitmap> accommodationImages) {
        this.existingReservation = existingReservation;
        this.accommodationImages = accommodationImages;
    }

    public CreateReservationFragment(Accommodation accommodation, String checkIn, String checkOut, Integer guestsNumber, List<Bitmap> accommodationImages) {
        this.accommodation = accommodation;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guestsNumber = guestsNumber;
        this.accommodationImages = accommodationImages;
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
            user.setVisibility(View.VISIBLE);
            if (LoginScreen.loggedUser.getRole().equals(Role.GUEST)) {

                if (getDaysPastOfCompletedReservation(existingReservation) <= 7 && existingReservation.getStatus().equals(ReservationStatus.COMPLETED)) {
                    btnCreate.setText("Add review");
                    btnCreate.setVisibility(View.VISIBLE);
                } else {
                    btnCreate.setVisibility(View.INVISIBLE);
                }
            } else {
                btnCreate.setVisibility(View.INVISIBLE);
            }
            initExistingReservationView(view);
        } else {
            user.setVisibility(View.INVISIBLE);
            status.setVisibility(View.INVISIBLE);
            reportUserBtn.setVisibility(View.INVISIBLE);
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

        picImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextImage();
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
        if (LoginScreen.loggedUser.getRole().equals(Role.GUEST)){
            reportedUser = existingReservation.getAccommodation().getHost();
        } else if (LoginScreen.loggedUser.getRole().equals(Role.HOST)){
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
        reportUserBtn = view.findViewById(R.id.reportUserBtn);
        user = view.findViewById(R.id.user);
        status = view.findViewById(R.id.status);
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
                        createNotification(newReservation);
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

    private void createNotification(Reservation newReservation) {
        Log.d("CreateReservationFragment", "USAAAAAAAAAAO ");
        Notification notification = new Notification(null, newReservation.getAccommodation().getHost(), "New Reservation", "Check out for this! Created new reservation for " + newReservation.getAccommodation().getName() + " accommodation", new Date(), NotificationType.RESERVATION_CREATED, true);
        Call<Notification> createdNotification = ClientUtils.notificationService.createNotification(notification);
        createdNotification.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CreateReservationFragment", "Successful response: " + response.body());
                    Notification newNotification = response.body();
                    Log.d("CreateReservationFragment", "Notification : " + newNotification.toString());
                } else {
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
            public void onFailure(Call<Notification> call, Throwable t) {
                Log.d("CreateReservationFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void initExistingReservationView(View view){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        picImg = view.findViewById(R.id.picImg);
        picImg.setImageBitmap(accommodationImages.get(0));

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


        if (LoginScreen.loggedGuest != null){
            user.setText("Host: " + existingReservation.getAccommodation().getHost().getFirstName() + " " + existingReservation.getAccommodation().getHost().getLastName());
        } else if (LoginScreen.loggedHost != null){
            user.setText("Guest: " + existingReservation.getGuest().getFirstName() + " " + existingReservation.getGuest().getLastName());
        }

        reportUserBtn = view.findViewById(R.id.reportUserBtn);
        if (!existingReservation.getStatus().equals(ReservationStatus.COMPLETED)){
            reportUserBtn.setVisibility(View.INVISIBLE);
        } else {
            reportUserBtn.setVisibility(View.VISIBLE);
        }
    }

    private void initCreateReservationView(View view){
        picImg = view.findViewById(R.id.picImg);
        picImg.setImageBitmap(accommodationImages.get(0));

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
                        createNotification(newReview);
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

    private void createNotification(Review newReview) {
        Notification notification = null;
        if (newReview.getType().equals(ReviewType.HOST)){
            notification = new Notification(null, newReview.getAccommodation().getHost(), "Rating", "You have received a new rating! Keep it up XD", new Date(), NotificationType.HOST_RATED, true);
        } else if (newReview.getType().equals(ReviewType.ACCOMMODATION)){
            notification = new Notification(null, newReview.getAccommodation().getHost(), "Accommodation rating", "Someone has rated your accommodation " + newReview.getAccommodation().getName() + "! It is known who has the best accommodations :D", new Date(), NotificationType.RESERVATION_CANCELED, true);
        }

        Call<Notification> createdNotification = ClientUtils.notificationService.createNotification(notification);
        createdNotification.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CreateReservationFragment", "Successful response: " + response.body());
                    Notification newNotification = response.body();
                    Log.d("CreateReservationFragment", "Notification : " + newNotification.toString());
                } else {
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
            public void onFailure(Call<Notification> call, Throwable t) {
                Log.d("CreateReservationFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }




}