package com.example.bookedup.services;

import android.database.Observable;
import android.os.Bundle;
import android.util.Log;

import com.example.bookedup.R;
import com.example.bookedup.activities.LoginScreen;
import com.example.bookedup.clients.ClientUtils;
import com.example.bookedup.fragments.accommodations.SingleStatisticFragment;
import com.example.bookedup.fragments.reservations.ReservationRequestFragment;
import com.example.bookedup.model.Accommodation;
import com.example.bookedup.model.Reservation;
import com.example.bookedup.model.SingleStatisticData;
import com.example.bookedup.model.StatisticData;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class StatisticService {
    private int numberOfReservations;
    private double profit;
    private List<StatisticData> statisticDataResults = new ArrayList<>();
    private List<SingleStatisticData> singleStatisticDataResults = new ArrayList<>();

    public StatisticService() {}

    public List<StatisticData> getAllStatistic(List<Accommodation> accommodations, List<Reservation> reservations, Date startDate, Date endDate){
        for (Accommodation accommodation : accommodations){
            Log.d("StatisticService", "Acc " + accommodation.toString());
            numberOfReservations = 0;
            profit = 0;
            for(Reservation reservation : reservations){
                if (startDate.compareTo(reservation.getStartDate()) <= 0 && startDate.compareTo(reservation.getEndDate()) < 0 && endDate.compareTo(reservation.getEndDate()) >= 0 && endDate.compareTo(reservation.getStartDate()) > 0) {
                    Log.d("StatisticService", "USAAAAAAAAAAAAO");
                    if (accommodation.getId().equals(reservation.getAccommodation().getId())) {
                        Log.d("StatisticService", "USAAAAAAAAAAAAO2");
                        numberOfReservations += 1;
                        profit += reservation.getTotalPrice();
                    }
                }
            }
            StatisticData statisticData = new StatisticData(accommodation.getName(), profit, numberOfReservations);
            Log.d("StatisticService", "USAAAAAAAAAAAAO3" + statisticData.toString());
            statisticDataResults.add(statisticData);
        }
        Log.d("StatisticFragment", "SIZE " + statisticDataResults.size());
        for(StatisticData statisticData : statisticDataResults){
            Log.d("StatisticFragment", "STD " + statisticData.toString());
        }
        return statisticDataResults;
    }

    public List<SingleStatisticData> getSingleStatistic(Accommodation accommodation, List<Reservation> reservations){
        Log.d("StatisticService", "Acc " + accommodation.toString());
        for (int targetMonth = 1; targetMonth <= 12; targetMonth++) {
            numberOfReservations = 0;
            profit = 0;
            String month = "";
            for (Reservation reservation : reservations) {
                Log.d("StatisticService", "MESEC  " + targetMonth);
                if (reservation.getStartDate().getMonth() + 1 == targetMonth && reservation.getEndDate().getMonth() + 1 == targetMonth) {
                    Log.d("StatisticService", "USAAAAAAAAAAAAO2");
                    if (accommodation.getId().equals(reservation.getAccommodation().getId())) {
                        numberOfReservations += 1;
                        profit += reservation.getTotalPrice();
                    }
                }
            }
            month = getMonthName(targetMonth);
            SingleStatisticData singleStatisticData = new SingleStatisticData(month, profit, numberOfReservations);
            Log.d("StatisticService", "USAAAAAAAAAAAAO3" + singleStatisticData.toString());
            singleStatisticDataResults.add(singleStatisticData);
        }
        Log.d("StatisticFragment", "SIZE " + singleStatisticDataResults.size());
        for(SingleStatisticData singleStatistic : singleStatisticDataResults){
            Log.d("StatisticFragment", "STD " + singleStatistic.toString());
        }
        return singleStatisticDataResults;
    }

    private String getMonthName(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month should be between 1 and 12");
        }

        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();

        // months array is 0-based, so we need to subtract 1 from the month
        return months[month - 1];
    }





}
