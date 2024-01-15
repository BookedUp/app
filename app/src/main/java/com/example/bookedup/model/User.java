package com.example.bookedup.model;

import com.example.bookedup.model.enums.Role;

public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private Address address;
    private Integer phone;
    private String email;
    private String password;
    private boolean isBlocked;
    private boolean verified;
    private boolean active;
    private Photo profilePicture;
    private Role role;

    // Konstruktori
    public User() {
    }

    public User(Long id, String firstName, String lastName, Address address, Integer phone,
                String email, String password, boolean isBlocked, boolean verified,
                boolean active, Photo profilePicture, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.isBlocked = isBlocked;
        this.verified = verified;
        this.active = active;
        this.profilePicture = profilePicture;
        this.role = role;
    }

    public User(String firstName, String lastName, Address address, Integer phone, String email, String password, boolean isBlocked, boolean verified, boolean active, Photo profilePicture, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.isBlocked = isBlocked;
        this.verified = verified;
        this.active = active;
        this.profilePicture = profilePicture;
        this.role = role;
    }

    // Getteri i setteri
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Photo getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Photo profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address=" + address +
                ", phone=" + phone +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isBlocked=" + isBlocked +
                ", verified=" + verified +
                ", active=" + active +
                ", profilePicture=" + profilePicture +
                ", role=" + role +
                '}';
    }
}