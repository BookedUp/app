package com.example.bookedup.model;

import java.util.List;

public class Guest extends User {
    private List<Accommodation> favourites;
    private boolean notificationEnable;

    // Konstruktori

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

    // Dodatni getteri i setteri po potrebi
}
