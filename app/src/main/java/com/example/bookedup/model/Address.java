package com.example.bookedup.model;

import java.io.Serializable;

public class Address implements Serializable {
    private Long id;
    private String country;
    private String city;
    private String postalCode;
    private String streetAndNumber;
    private boolean active;
    private double latitude;
    private double longitude;

    // Konstruktori, getteri i setteri

    public Address() {
        // Prazan konstruktor
    }

    public Address(Long id, String country, String city, String postalCode, String streetAndNumber, boolean active, double latitude, double longitude) {
        this.id = id;
        this.country = country;
        this.city = city;
        this.postalCode = postalCode;
        this.streetAndNumber = streetAndNumber;
        this.active = active;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Address(String country, String city, String postalCode, String streetAndNumber, boolean active, double latitude, double longitude) {
        this.country = country;
        this.city = city;
        this.postalCode = postalCode;
        this.streetAndNumber = streetAndNumber;
        this.active = active;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreetAndNumber() {
        return streetAndNumber;
    }

    public void setStreetAndNumber(String streetAndNumber) {
        this.streetAndNumber = streetAndNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", streetAndNumber='" + streetAndNumber + '\'' +
                ", active=" + active +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}