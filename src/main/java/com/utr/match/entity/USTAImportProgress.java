package com.utr.match.entity;

import java.sql.Timestamp;

public class USTAImportProgress {

    private Long id;
    private String step;
    private int progress;
    private String message;
    private String status; // "in_progress", "completed", "error"
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public USTAImportProgress() {
        this.status = "in_progress";
    }

    public USTAImportProgress(String step, int progress, String message) {
        this.step = step;
        this.progress = progress;
        this.message = message;
        this.status = "in_progress";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void markAsCompleted() {
        this.status = "completed";
        this.progress = 100;
    }

    public void markAsError(String errorMessage) {
        this.status = "error";
        this.message = errorMessage;
    }
}
