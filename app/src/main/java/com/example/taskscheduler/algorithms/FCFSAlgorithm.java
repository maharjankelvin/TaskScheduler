package com.example.taskscheduler.algorithms;

import com.example.taskscheduler.models.Task;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class FCFSAlgorithm implements SchedulingAlgorithm {
    @Override
    public List<Task> schedule(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort(Comparator.comparingLong(Task::getCreatedAt));
        return sortedTasks;
    }

    @Override
    public String getName() {
        return "First-Come-First-Served (FCFS)";
    }
}