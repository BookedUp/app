package com.example.bookedup.model;

import com.example.bookedup.model.enums.Role;

public class Host extends User {
    private Double averageRating;
    private Boolean reservationCreatedNotificationEnabled;
    private Boolean cancellationNotificationEnabled;
    private Boolean hostRatingNotificationEnabled;
    private Boolean accommodationRatingNotificationEnabled;

    public Host(Long id, String firstName, String lastName, Address address, Integer phone, String email, String password, boolean isBlocked, boolean verified, boolean active, Photo profilePicture, Role role, Double averageRating, Boolean reservationCreatedNotificationEnabled, Boolean cancellationNotificationEnabled, Boolean hostRatingNotificationEnabled, Boolean accommodationRatingNotificationEnabled) {
        super(id, firstName, lastName, address, phone, email, password, isBlocked, verified, active, profilePicture, role);
        this.averageRating = averageRating;
        this.reservationCreatedNotificationEnabled = reservationCreatedNotificationEnabled;
        this.cancellationNotificationEnabled = cancellationNotificationEnabled;
        this.hostRatingNotificationEnabled = hostRatingNotificationEnabled;
        this.accommodationRatingNotificationEnabled = accommodationRatingNotificationEnabled;
    }

    public Host(String firstName, String lastName, Address address, Integer phone, String email, String password, boolean isBlocked, boolean verified, boolean active, Photo profilePicture, Role role, Double averageRating, Boolean reservationCreatedNotificationEnabled, Boolean cancellationNotificationEnabled, Boolean hostRatingNotificationEnabled, Boolean accommodationRatingNotificationEnabled) {
        super(firstName, lastName, address, phone, email, password, isBlocked, verified, active, profilePicture, role);
        this.averageRating = averageRating;
        this.reservationCreatedNotificationEnabled = reservationCreatedNotificationEnabled;
        this.cancellationNotificationEnabled = cancellationNotificationEnabled;
        this.hostRatingNotificationEnabled = hostRatingNotificationEnabled;
        this.accommodationRatingNotificationEnabled = accommodationRatingNotificationEnabled;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Boolean getReservationCreatedNotificationEnabled() {
        return reservationCreatedNotificationEnabled;
    }

    public void setReservationCreatedNotificationEnabled(Boolean reservationCreatedNotificationEnabled) {
        this.reservationCreatedNotificationEnabled = reservationCreatedNotificationEnabled;
    }

    public Boolean getCancellationNotificationEnabled() {
        return cancellationNotificationEnabled;
    }

    public void setCancellationNotificationEnabled(Boolean cancellationNotificationEnabled) {
        this.cancellationNotificationEnabled = cancellationNotificationEnabled;
    }

    public Boolean getHostRatingNotificationEnabled() {
        return hostRatingNotificationEnabled;
    }

    public void setHostRatingNotificationEnabled(Boolean hostRatingNotificationEnabled) {
        this.hostRatingNotificationEnabled = hostRatingNotificationEnabled;
    }

    public Boolean getAccommodationRatingNotificationEnabled() {
        return accommodationRatingNotificationEnabled;
    }

    public void setAccommodationRatingNotificationEnabled(Boolean accommodationRatingNotificationEnabled) {
        this.accommodationRatingNotificationEnabled = accommodationRatingNotificationEnabled;
    }
}
