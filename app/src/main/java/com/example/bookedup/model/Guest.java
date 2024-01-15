package com.example.bookedup.model;

import com.example.bookedup.model.enums.Role;

import java.util.List;

public class Guest extends User {
    private List<Accommodation> favourites;
    private boolean notificationEnable;

    // Konstruktori


    public Guest(Long id, String firstName, String lastName, Address address, Integer phone, String email, String password, boolean isBlocked, boolean verified, boolean active, Photo profilePicture, Role role, List<Accommodation> favourites, boolean notificationEnable) {
        super(id, firstName, lastName, address, phone, email, password, isBlocked, verified, active, profilePicture, role);
        this.favourites = favourites;
        this.notificationEnable = notificationEnable;
    }

    public Guest(String firstName, String lastName, Address address, Integer phone, String email, String password, boolean isBlocked, boolean verified, boolean active, Photo profilePicture, Role role, List<Accommodation> favourites, boolean notificationEnable) {
        super(firstName, lastName, address, phone, email, password, isBlocked, verified, active, profilePicture, role);
        this.favourites = favourites;
        this.notificationEnable = notificationEnable;
    }

    public List<Accommodation> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<Accommodation> favourites) {
        this.favourites = favourites;
    }

    public boolean isNotificationEnable() {
        return notificationEnable;
    }

    public void setNotificationEnable(boolean notificationEnable) {
        this.notificationEnable = notificationEnable;
    }


}
