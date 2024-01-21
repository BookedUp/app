package com.example.bookedup.model;

import java.io.Serializable;
import java.util.Date;

public class PriceChange implements Serializable {
    private Long id;
    private Date changeDate;
    private double newPrice;

    // Konstruktori, getteri i setteri

    public PriceChange() {
        // Prazan konstruktor
    }

    public PriceChange(Long id, Date changeDate, double newPrice) {
        this.id = id;
        this.changeDate = changeDate;
        this.newPrice = newPrice;
    }

    public PriceChange( Date changeDate, double newPrice) {
        this.changeDate = changeDate;
        this.newPrice = newPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }
}