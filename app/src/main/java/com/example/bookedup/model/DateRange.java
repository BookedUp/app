package com.example.bookedup.model;

import java.util.Date;

public class DateRange {
    private Long id;

    private Date startDate;

    private Date endDate;

    public DateRange(Date start, Date end){
        this.startDate = start;
        this.endDate = end;
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