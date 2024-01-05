package com.example.bookedup.model;

import java.util.Date;

public class DateRange {
    private Long id;
    private Date startDate;
    private Date endDate;

    // Konstruktori, getteri i setteri

    public DateRange() {
        // Prazan konstruktor
    }

    public DateRange(Long id, Date startDate, Date endDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public DateRange( Date startDate, Date endDate) {

        this.startDate = startDate;
        this.endDate = endDate;
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
}