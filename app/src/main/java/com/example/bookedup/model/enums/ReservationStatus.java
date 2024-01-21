package com.example.bookedup.model.enums;

public enum ReservationStatus {
    CREATED("CREATED"),
    REJECTED("REJECTED"),
    ACCEPTED("ACCEPTED"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED");

    private final String status;

    ReservationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    // Dodatne metode po potrebi
}
