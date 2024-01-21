package com.example.bookedup.fragments.accommodations;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
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
import com.example.bookedup.fragments.calendar.CalendarFragment;
import com.example.bookedup.fragments.home.HomeFragment;
import com.example.bookedup.fragments.reservations.CreateReservationFragment;
import com.example.bookedup.fragments.reservations.ReservationRequestFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Guest;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.StatisticData;
import com.example.bookedup.model.User;
import com.example.bookedup.model.enums.Amenity;
import com.example.bookedup.model.enums.ReservationStatus;
import com.example.bookedup.services.StatisticService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticFragment extends Fragment implements DatePickerDialog.OnDateSetListener{
    private PieChart totalProfitChart;
    private Button downloadPdf;
    private PieChart numberOfReservationsChart;
    private FloatingActionButton statisticBtn;
    private boolean isStartDateButtonClicked, isEndDateButtonClicked;
    private ImageView startDateBtn, endDateBtn;
    private TextView checkInTxt, checkOutTxt;
    private List<Accommodation> accommodations = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    public StatisticFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        getData();

        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new CalendarFragment();
                datePicker.setTargetFragment(StatisticFragment.this, 0);
                datePicker.show(requireActivity().getSupportFragmentManager(), "date picker");
                isStartDateButtonClicked = true;
                isEndDateButtonClicked = false;
            }
        });

        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new CalendarFragment();
                datePicker.setTargetFragment(StatisticFragment.this, 0);
                datePicker.show(requireActivity().getSupportFragmentManager(), "date picker");
                isEndDateButtonClicked = true;
                isStartDateButtonClicked = false;
            }
        });

        statisticBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkInTxt.getText().toString().isEmpty() || checkOutTxt.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Enter start and end date!", Toast.LENGTH_SHORT).show();
                } else {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date startDate = null;
                    Date endDate = null;
                    try {
                        startDate = sdf.parse(checkInTxt.getText().toString());
                        endDate = sdf.parse(checkOutTxt.getText().toString());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    startDate.setHours(13);
                    endDate.setHours(13);

                    Log.d("StatisticFragment", "Start date" + startDate);
                    Log.d("StatisticFragment", "End date" + endDate);


                    StatisticService statisticService = new StatisticService();
                    List<StatisticData> statisticDataResults = statisticService.getAllStatistic(accommodations, reservations, startDate, endDate);
                    Log.d("StatisticFragment", "SIZE " + statisticDataResults.size());
                    for (StatisticData statisticData : statisticDataResults) {
                        Log.d("StatisticFragment", "STD " + statisticData.toString());
                    }

                    setTotalProfitChartData(statisticDataResults);
                    setNumberOfReservationsChart(statisticDataResults);
                }
            }
        });

        downloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap totalProfitBitmap = getChartBitmap(totalProfitChart);
                Bitmap numberOfReservationsBitmap = getChartBitmap(numberOfReservationsChart);

                // Kreiraj PDF
                try {
                    createAndSavePdf(totalProfitBitmap, numberOfReservationsBitmap);
                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private void initView(View view) {
        checkInTxt = view.findViewById(R.id.checkInText);
        checkOutTxt = view.findViewById(R.id.checkOutText);
        startDateBtn =  view.findViewById(R.id.startDate);
        endDateBtn =  view.findViewById(R.id.endDate);
        statisticBtn = view.findViewById(R.id.statisticButton);
        totalProfitChart = view.findViewById(R.id.pieChartTotalProfit);
        numberOfReservationsChart = view.findViewById(R.id.pieChartNumberOfReservations);
        downloadPdf = view.findViewById(R.id.downloadPDF);
    }

    private void getData(){
        getAllActiveHostsAccommodations();
        for (Accommodation acc : accommodations) {
            Log.d("StatisticFragment", "Acc " + acc.toString());
        }
        getHostsCompletedReservations();
        for (Reservation res : reservations) {
            Log.d("StatisticFragment", "Res " + res.toString());
        }
    }

    private void setTotalProfitChartData(List<StatisticData> results){
        ArrayList<PieEntry> totalProfitEntries = new ArrayList<>();
        for(StatisticData statisticData : results){
            totalProfitEntries.add(new PieEntry((float) statisticData.getTotalEarnings(), statisticData.getName().toString()));
        }

        PieDataSet pieDataSet = new PieDataSet(totalProfitEntries, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(14f);


        PieData pieData = new PieData(pieDataSet);
        totalProfitChart.setData(pieData);

        totalProfitChart.getDescription().setEnabled(false);
        totalProfitChart.setEntryLabelColor(Color.BLACK);

        totalProfitChart.animateY(1000);
        totalProfitChart.invalidate();

    }

    private void setNumberOfReservationsChart(List<StatisticData> results){
        ArrayList<PieEntry> numberOfReservationsEntries = new ArrayList<>();
        for(StatisticData statisticData : results){
//            int roundedNumberOfReservations = (int) Math.round(statisticData.getTotalReservations());
            numberOfReservationsEntries.add(new PieEntry((float) statisticData.getTotalReservations(), statisticData.getName().toString()));
        }

        PieDataSet pieDataSet = new PieDataSet(numberOfReservationsEntries, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(14f);

        PieData pieData = new PieData(pieDataSet);
        numberOfReservationsChart.setData(pieData);

        numberOfReservationsChart.getDescription().setEnabled(false);
        numberOfReservationsChart.setEntryLabelColor(Color.BLACK);

        numberOfReservationsChart.animateY(1000);
        numberOfReservationsChart.invalidate();

    }

    private void getAllActiveHostsAccommodations(){
        Call<ArrayList<Accommodation>> hostsAccommodations = ClientUtils.accommodationService.getAllActiveByHostId(LoginScreen.loggedHost.getId());
        hostsAccommodations.enqueue(new Callback<ArrayList<Accommodation>>() {
            @Override
            public void onResponse(Call<ArrayList<Accommodation>> call, Response<ArrayList<Accommodation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("StatisticFragment", "Successful response: " + response.body());
                    accommodations = response.body();

                } else {
                    // Log error details
                    Log.d("StatisticFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("StatisticFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Accommodation>> call, Throwable t) {
                Log.d("StatisticFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }


    private void getHostsCompletedReservations(){
        Call<ArrayList<Reservation>> hostsReservations = ClientUtils.reservationService.getReservationsByStatusAndHostId(LoginScreen.loggedHost.getId(), ReservationStatus.COMPLETED);
        hostsReservations.enqueue(new Callback<ArrayList<Reservation>>() {
            @Override
            public void onResponse(Call<ArrayList<Reservation>> call, Response<ArrayList<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("StatisticFragment", "Successful response: " + response.body());
                    reservations = response.body();

                } else {
                    // Log error details
                    Log.d("StatisticFragment", "Unsuccessful response: " + response.code());
                    try {
                        Log.d("StatisticFragment", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Reservation>> call, Throwable t) {
                Log.d("StatisticFragment", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        try {
            Log.d("StatisticFragment", "onDateSet called");
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(c.getTime());
            if (isStartDateButtonClicked) {
                checkInTxt.setText(formattedDate);
            } else {
                checkOutTxt.setText(formattedDate);
            }
        } catch (Exception e) {
            Log.e("StatisticFragment", "Error in onDateSet", e);
        }
    }

    private Bitmap getChartBitmap(PieChart chart) {
        chart.setDrawingCacheEnabled(true);
        Bitmap currentChartBitmap = Bitmap.createBitmap(chart.getMeasuredWidth(), chart.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(currentChartBitmap);
        chart.draw(canvas);

        chart.setDrawingCacheEnabled(false);
        chart.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        chart.layout(0, 0, chart.getMeasuredWidth(), chart.getMeasuredHeight());
        return currentChartBitmap;
    }

    private void createAndSavePdf(Bitmap totalProfitBitmap, Bitmap numberOfReservationsBitmap) throws IOException, DocumentException {
        Document document = new Document();
        String pdfFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/statistics.pdf";
        File pdfFile = new File(pdfFilePath);

        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

        document.open();
        document.addTitle("Statistics");
        addImageToPdf(document, totalProfitBitmap);
        addImageToPdf(document, numberOfReservationsBitmap);

        document.close();
        Toast.makeText(getContext(), "PDF saved in downloads!", Toast.LENGTH_SHORT).show();
    }

    private void addImageToPdf(Document document, Bitmap bitmap) throws IOException, DocumentException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image chartImage = Image.getInstance(stream.toByteArray());
        chartImage.scaleToFit(PageSize.A4.getWidth() - 50, PageSize.A4.getHeight() - 50);
        document.add(chartImage);
    }



}
