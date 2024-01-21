package com.example.bookedup.model.enums;

public enum AccommodationType {

    HOTEL("Hotel"),
    HOSTEL("Hostel"),
    VILLA("Villa"),
    RESORT("Resort"),
    APARTMENT("Apartment");

    private final String status;

    AccommodationType(String status) {
        this.status = status;
    }

    public String getAccommodationType() {
        return status;
    }
}