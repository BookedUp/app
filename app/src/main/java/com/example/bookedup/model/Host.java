package com.example.bookedup.model;

public class Host extends User {
    private Double averageRating;
    private Boolean reservationCreatedNotificationEnabled;
    private Boolean cancellationNotificationEnabled;
    private Boolean hostRatingNotificationEnabled;
    private Boolean accommodationRatingNotificationEnabled;

    // Konstruktori

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

    // Dodatni getteri i setteri po potrebi
}
