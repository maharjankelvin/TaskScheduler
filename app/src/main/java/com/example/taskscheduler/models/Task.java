package com.example.taskscheduler.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "priority")
    private int priority; // 1=Low, 2=Medium, 3=High

    @ColumnInfo(name = "category")
    private String category; // "Home" or "Work"

    @ColumnInfo(name = "duration")
    private long duration; // in minutes

    @ColumnInfo(name = "created_at")
    private long createdAt; // timestamp for FCFS sorting

    public Task(@NonNull String title, String description, int priority, String category, long duration) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.duration = duration;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        if (priority < 1 || priority > 3) {
            throw new IllegalArgumentException("Priority must be between 1 and 3");
        }
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (!category.equals("Home") && !category.equals("Work")) {
            throw new IllegalArgumentException("Category must be either 'Home' or 'Work'");
        }
        this.category = category;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
        this.duration = duration;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
} 