package com.example.bookedup.model.enums;

public enum AccommodationStatus {
    CREATED("CREATED"),
    ACTIVE("ACTIVE"),
    REJECTED("REJECTED"),
    CHANGED("CHANGED");

    private final String status;

    AccommodationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


}