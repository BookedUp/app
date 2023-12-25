package com.example.bookedup.model;

public class Photo {
    private int id;
    private String url;
    private String caption;
    private boolean active;

    // Konstruktori, getteri i setteri

    public Photo() {
        // Prazan konstruktor
    }

    public Photo(int id, String url, String caption, boolean active) {
        this.id = id;
        this.url = url;
        this.caption = caption;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
