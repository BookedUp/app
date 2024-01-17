package com.example.bookedup.model;

import com.example.bookedup.model.enums.ReviewType;
import com.example.bookedup.utils.LocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;

public class Review {
    private Long id;
    private Guest guest;
    private int review;
    private String comment;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private String date;
    private Host host;
    private Accommodation accommodation;
    private ReviewType type;
    private Boolean approved;

    public Review(Guest guest, int review, String comment, String date, Host host, Accommodation accommodation, ReviewType type, Boolean approved) {
        this.guest = guest;
        this.review = review;
        this.comment = comment;
        this.date = date;
        this.host = host;
        this.accommodation = accommodation;
        this.type = type;
        this.approved = approved;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    public ReviewType getType() {
        return type;
    }

    public void setType(ReviewType type) {
        this.type = type;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", guest=" + guest +
                ", review=" + review +
                ", comment='" + comment + '\'' +
                ", date=" + date +
                ", host=" + host +
                ", accommodation=" + accommodation +
                ", type=" + type +
                ", approved=" + approved +
                '}';
    }
}
