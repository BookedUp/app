package com.example.bookedup.model;

public class SingleStatisticData {
    private String month;
    private Double totalEarnings;
    private Integer totalReservations;

    public SingleStatisticData(String month, Double totalEarnings, Integer totalReservations) {
        this.month = month;
        this.totalEarnings = totalEarnings;
        this.totalReservations = totalReservations;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(Double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public Integer getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(Integer totalReservations) {
        this.totalReservations = totalReservations;
    }

    @Override
    public String toString() {
        return "SingleStatisticData{" +
                "month='" + month + '\'' +
                ", totalEarnings=" + totalEarnings +
                ", totalReservations=" + totalReservations +
                '}';
    }
}
