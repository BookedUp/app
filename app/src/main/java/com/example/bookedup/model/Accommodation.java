package com.example.bookedup.model;

import java.io.Serializable;

public class Accommodation implements Serializable {

    private String title;

    private String location;

    private String description;

    private int bed;

    private double score;

    private String pic;

    private boolean parking;

    private boolean wifi;

    private int price;

    public Accommodation(String title, String location, String description, int bed, double score, String pic, boolean parking, boolean wifi, int price) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.bed = bed;
        this.score = score;
        this.pic = pic;
        this.parking = parking;
        this.wifi = wifi;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBed() {
        return bed;
    }

    public void setBed(int bed) {
        this.bed = bed;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isParking() {
        return parking;
    }

    public void setParking(boolean parking) {
        this.parking = parking;
    }
}