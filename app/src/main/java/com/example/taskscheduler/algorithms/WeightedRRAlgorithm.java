package com.example.taskscheduler.algorithms;

import com.example.taskscheduler.models.Task;
import com.example.taskscheduler.models.GhostTask;
import java.util.ArrayList;
import java.util.List;

public class WeightedRRAlgorithm implements SchedulingAlgorithm {
    private final long quantum; // in minutes

    public WeightedRRAlgorithm(long quantum) {
        this.quantum = quantum;
    }

    // New method to generate ghost tasks for WRR
    public List<GhostTask> generateGhostTasks(List<Task> tasks, java.util.Set<String> deletedChunks) {
        List<GhostTask> ghostTasks = new ArrayList<>();
        List<Long> remaining = new ArrayList<>();
        for (Task task : tasks) {
            remaining.add(task.getDuration());
        }
        boolean tasksLeft = true;
        int chunkIndex = 0;
        while (tasksLeft) {
            tasksLeft = false;
            for (int i = 0; i < tasks.size(); i++) {
                int weight = Math.max(1, tasks.get(i).getPriority());
                for (int w = 0; w < weight; w++) {
                    if (remaining.get(i) > 0) {
                        long slice = Math.min(quantum, remaining.get(i));
                        String chunkId = tasks.get(i).getId() + "_" + chunkIndex;
                        if (deletedChunks == null || !deletedChunks.contains(chunkId)) {
                            ghostTasks.add(new GhostTask(
                                tasks.get(i).getId(),
                                tasks.get(i).getTitle(),
                                tasks.get(i).getPriority(),
                                tasks.get(i).getCategory(),
                                slice,
                                tasks.get(i).getDescription(),
                                chunkId
                            ));
                        }
                        remaining.set(i, remaining.get(i) - slice);
                        if (remaining.get(i) > 0) {
                            tasksLeft = true;
                        }
                        chunkIndex++;
                    }
                }
            }
        }
        return ghostTasks;
    }

    @Override
    public List<Task> schedule(List<Task> tasks) {
        // Not used for ghost task mode
        return new ArrayList<>(tasks);
    }

    @Override
    public String getName() {
        return "Weighted Round Robin (WRR)";
    }

    public long getQuantum() {
        return quantum;
    }
} 