package com.example.bookedup.model;

import com.example.bookedup.model.enums.Role;

public class Admin extends User{

    public Admin() {
    }

    public Admin(Long id, String firstName, String lastName, Address address, Integer phone, String email, String password, boolean isBlocked, boolean verified, boolean active, Photo profilePicture, Role role) {
        super(id, firstName, lastName, address, phone, email, password, isBlocked, verified, active, profilePicture, role);
    }


    public Admin(String firstName, String lastName, Address address, Integer phone, String email, String password, boolean isBlocked, boolean verified, boolean active, Photo profilePicture, Role role) {
        super(firstName, lastName, address, phone, email, password, isBlocked, verified, active, profilePicture, role);
    }
}
