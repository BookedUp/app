package com.example.bookedup.model.enums;

public enum Amenity {

    FREE_WIFI("Free WiFi"),
    NON_SMOKING_ROOMS("Non smoking rooms"),
    PARKING("Parking"),
    RESTAURANT("Rastaurant"),
    FITNESS_CENTRE("Fitness centre"),
    SWIMMING_POOL("Swimming pool");

    private final String status;

    Amenity(String status) {
        this.status = status;
    }

    public String getAmenity() {
        return status;
    }

    // Dodatne metode po potrebi
}