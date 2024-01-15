package com.example.bookedup.model;

import com.example.bookedup.model.enums.ReservationStatus;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Reservation implements Serializable {
    private Long id;

    @SerializedName("startDate")
    private Date startDate;
    @SerializedName("endDate")
    private Date endDate;
    private double totalPrice;
    private int guestsNumber;
    private Accommodation accommodation;
    private Guest guest;
    private ReservationStatus status;

    // Konstruktori, getteri i setteri

    public Reservation() {
        // Prazan konstruktor
    }

    public Reservation(Long id, Date startDate, Date endDate, double totalPrice, int guestsNumber, Accommodation accommodation, Guest guest, ReservationStatus status) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.guestsNumber = guestsNumber;
        this.accommodation = accommodation;
        this.guest = guest;
        this.status = status;
    }

    public Reservation(Date startDate, Date endDate, double totalPrice, int guestsNumber, Accommodation accommodation, Guest guest, ReservationStatus status) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.guestsNumber = guestsNumber;
        this.accommodation = accommodation;
        this.guest = guest;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getGuestsNumber() {
        return guestsNumber;
    }

    public void setGuestsNumber(int guestsNumber) {
        this.guestsNumber = guestsNumber;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalPrice=" + totalPrice +
                ", guestsNumber=" + guestsNumber +
                ", accommodation=" + accommodation +
                ", guest=" + guest +
                ", status=" + status +
                '}';
    }
}