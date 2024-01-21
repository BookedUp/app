package com.example.bookedup.model.enums;

public enum Role {
    ADMIN("ADMIN"),
    GUEST("GUEST"),
    HOST("HOST");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
