package com.example.bookedup.model.enums;

public enum PriceType {
    PER_NIGHT("per night"),
    PER_GUEST("per guest");

    private final String priceType;

    PriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getPriceType() {
        return priceType;
    }
}