package com.example.bookedup.model;

public class StatisticData {

    private String name;
    private double totalEarnings;
    private int totalReservations;

    public StatisticData(String name, double totalEarnings, int totalReservations) {
        this.name = name;
        this.totalEarnings = totalEarnings;
        this.totalReservations = totalReservations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public int getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }

    @Override
    public String toString() {
        return "StatisticData{" +
                "name='" + name + '\'' +
                ", totalEarnings=" + totalEarnings +
                ", totalReservations=" + totalReservations +
                '}';
    }
}
