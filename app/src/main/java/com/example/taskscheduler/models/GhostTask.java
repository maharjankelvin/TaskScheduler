package com.example.taskscheduler.models;

public class GhostTask {
    private final int originalTaskId;
    private final String title;
    private final int priority;
    private final String category;
    private final long allocatedTime; // in minutes
    private final String description;
    private final String uniqueChunkId; // Unique identifier for this chunk

    public GhostTask(int originalTaskId, String title, int priority, String category, long allocatedTime, String description, String uniqueChunkId) {
        this.originalTaskId = originalTaskId;
        this.title = title;
        this.priority = priority;
        this.category = category;
        this.allocatedTime = allocatedTime;
        this.description = description;
        this.uniqueChunkId = uniqueChunkId;
    }

    public int getOriginalTaskId() { return originalTaskId; }
    public String getTitle() { return title; }
    public int getPriority() { return priority; }
    public String getCategory() { return category; }
    public long getAllocatedTime() { return allocatedTime; }
    public String getDescription() { return description; }
    public String getUniqueChunkId() { return uniqueChunkId; }
} 