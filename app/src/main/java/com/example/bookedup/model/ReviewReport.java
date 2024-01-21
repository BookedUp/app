package com.example.bookedup.model;

public class ReviewReport {
    private Long id;
    private String reason;
    private Review reportedReview;
    private boolean status;

    public ReviewReport(Long id, String reason, Review reportedReview, boolean status) {
        this.id = id;
        this.reason = reason;
        this.reportedReview = reportedReview;
        this.status = status;
    }

    public ReviewReport(String reason, Review reportedReview, boolean status) {
        this.reason = reason;
        this.reportedReview = reportedReview;
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

    public Review getReportedReview() {
        return reportedReview;
    }

    public void setReportedReview(Review reportedReview) {
        this.reportedReview = reportedReview;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReviewReport{" +
                "id=" + id +
                ", reason='" + reason + '\'' +
                ", reportedReview=" + reportedReview +
                ", status=" + status +
                '}';
    }
}
