package com.example.taskscheduler.algorithms;

import com.example.taskscheduler.models.Task;
import java.util.List;
 
public interface SchedulingAlgorithm {
    List<Task> schedule(List<Task> tasks);
    String getName();
} 