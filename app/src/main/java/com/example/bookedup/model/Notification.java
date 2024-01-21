package com.example.bookedup.model;

import com.example.bookedup.model.enums.NotificationType;

import java.util.Date;

public class Notification {
    private Long id;
    private User fromUserDTO;
    private User toUserDTO;
    private String title;
    private String message;
    private Date timestamp;
    private NotificationType type;
    private boolean active;

    public Notification(Long id, User fromUserDTO, User toUserDTO, String title, String message, Date timestamp, NotificationType type, boolean active) {
        this.id = id;
        this.fromUserDTO = fromUserDTO;
        this.toUserDTO = toUserDTO;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.active = active;
    }

    public Notification(User fromUserDTO, User toUserDTO, String title, String message, Date timestamp, NotificationType type, boolean active) {
        this.fromUserDTO = fromUserDTO;
        this.toUserDTO = toUserDTO;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFromUserDTO() {
        return fromUserDTO;
    }

    public void setFromUserDTO(User fromUserDTO) {
        this.fromUserDTO = fromUserDTO;
    }

    public User getToUserDTO() {
        return toUserDTO;
    }

    public void setToUserDTO(User toUserDTO) {
        this.toUserDTO = toUserDTO;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", fromUserDTO=" + fromUserDTO +
                ", toUserDTO=" + toUserDTO +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", type=" + type +
                ", active=" + active +
                '}';
    }
}
