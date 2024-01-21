package com.example.bookedup.model;

import com.example.bookedup.model.enums.AccommodationStatus;
import com.example.bookedup.model.enums.AccommodationType;
import com.example.bookedup.model.enums.Amenity;
import com.example.bookedup.model.enums.PriceType;

import java.io.Serializable;
import java.util.List;

public class Accommodation implements Serializable{
    private Long id;
    private String name;
    private String description;
    private Address address;
    private List<Amenity> amenities;
    private List<Photo> photos;
    private int minGuests;
    private int maxGuests;
    private AccommodationType type;
    private List<DateRange> availability;
    private PriceType priceType;
    private List<PriceChange> priceChanges;
    private boolean automaticReservationAcceptance;
    private AccommodationStatus status;
    private Host host;
    private double price;
    private Double totalPrice;
    private Double averageRating;
    private int cancellationDeadline;

    // Konstruktori, getteri i setteri

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public int getMinGuests() {
        return minGuests;
    }

    public void setMinGuests(int minGuests) {
        this.minGuests = minGuests;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public AccommodationType getType() {
        return type;
    }

    public void setType(AccommodationType type) {
        this.type = type;
    }

    public List<DateRange> getAvailability() {
        return availability;
    }

    public void setAvailability(List<DateRange> availability) {
        this.availability = availability;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public List<PriceChange> getPriceChanges() {
        return priceChanges;
    }

    public void setPriceChanges(List<PriceChange> priceChanges) {
        this.priceChanges = priceChanges;
    }

    public boolean isAutomaticReservationAcceptance() {
        return automaticReservationAcceptance;
    }

    public void setAutomaticReservationAcceptance(boolean automaticReservationAcceptance) {
        this.automaticReservationAcceptance = automaticReservationAcceptance;
    }

    public AccommodationStatus getStatus() {
        return status;
    }

    public void setStatus(AccommodationStatus status) {
        this.status = status;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public int getCancellationDeadline() {
        return cancellationDeadline;
    }

    public void setCancellationDeadline(int cancellationDeadline) {
        this.cancellationDeadline = cancellationDeadline;
    }

    // Dodatni getteri i setteri po potrebi



    public Accommodation(String name, List<Photo> photos, Address address, double averageRating,
                         AccommodationType type, double price, PriceType priceType) {
        this.name = name;
        this.photos = photos;
        this.address = address;
        this.averageRating = averageRating;
        this.type = type;
        this.price = price;
        this.priceType = priceType;
    }

    public Accommodation(){

    }

    public Accommodation(String name, String description, Address address, List<Amenity> amenities,
                         List<Photo> photos, int minGuests, int maxGuests, AccommodationType type,
                         List<DateRange> availability, PriceType priceType, List<PriceChange> priceChanges,
                         boolean automaticReservationAcceptance, AccommodationStatus status, Host host,
                         double price, Double totalPrice, Double averageRating, int cancellationDeadline) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.amenities = amenities;
        this.photos = photos;
        this.minGuests = minGuests;
        this.maxGuests = maxGuests;
        this.type = type;
        this.availability = availability;
        this.priceType = priceType;
        this.priceChanges = priceChanges;
        this.automaticReservationAcceptance = automaticReservationAcceptance;
        this.status = status;
        this.host = host;
        this.price = price;
        this.totalPrice = totalPrice;
        this.averageRating = averageRating;
        this.cancellationDeadline = cancellationDeadline;
    }


    public Accommodation(String name, List<Photo> photos, Address address, double averageRating) {
        this.name = name;
        this.photos = photos;
        this.address = address;
        this.averageRating = averageRating;

    }

    // Getter and Setter methods...

    // Additional methods if needed...

    @Override
    public String toString() {
        return "Accommodation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", address=" + address +
                ", amenities=" + amenities +
                ", photos=" + photos +
                ", minGuests=" + minGuests +
                ", maxGuests=" + maxGuests +
                ", type=" + type +
                ", availability=" + availability +
                ", priceType=" + priceType +
                ", priceChanges=" + priceChanges +
                ", automaticReservationAcceptance=" + automaticReservationAcceptance +
                ", status=" + status +
                ", host=" + host +
                ", price=" + price +
                ", totalPrice=" + totalPrice +
                ", averageRating=" + averageRating +
                ", cancellationDeadline=" + cancellationDeadline +
                '}';
    }
}
