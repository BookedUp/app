package com.example.bookedup.model;

public class UserReport {
    private Long id;
    private String reason;
    private User reportedUser;
    private boolean status;


    public UserReport(String reason, User reportedUser, boolean status) {
        this.reason = reason;
        this.reportedUser = reportedUser;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public User getReportedUser() {
        return reportedUser;
    }

    public void setReportedUser(User reportedUser) {
        this.reportedUser = reportedUser;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserReport{" +
                "id=" + id +
                ", reason='" + reason + '\'' +
                ", reportedUser=" + reportedUser +
                ", status=" + status +
                '}';
    }
}
