package com.example.taskscheduler.algorithms;

import java.util.HashMap;
import java.util.Map;
import com.example.taskscheduler.algorithms.FCFSAlgorithm;
import com.example.taskscheduler.algorithms.SJFAlgorithm;
import com.example.taskscheduler.algorithms.PriorityAlgorithm;
import com.example.taskscheduler.algorithms.RRAlgorithm;
import com.example.taskscheduler.algorithms.WeightedRRAlgorithm;
import com.example.taskscheduler.algorithms.SchedulingAlgorithm;

public class SchedulingAlgorithmFactory {
    private static final Map<String, SchedulingAlgorithm> algorithms = new HashMap<>();

    static {
        algorithms.put("First-Come-First-Served (FCFS)", new FCFSAlgorithm());
        algorithms.put("Shortest Job First (SJF)", new SJFAlgorithm());
        algorithms.put("Priority", new PriorityAlgorithm());
    }

    public static SchedulingAlgorithm getAlgorithm(String name) {
        return algorithms.get(name);
    }

    public static SchedulingAlgorithm getAlgorithm(String name, long quantum) {
        switch (name) {
            case "Round Robin (RR)":
                return new RRAlgorithm(quantum);
            case "Weighted Round Robin (WRR)":
                return new WeightedRRAlgorithm(quantum);
            case "First-Come-First-Served (FCFS)":
                return new FCFSAlgorithm();
            case "Shortest Job First (SJF)":
                return new SJFAlgorithm();
            case "Priority":
                return new PriorityAlgorithm();
            default:
                throw new IllegalArgumentException("Unknown algorithm type");
        }
    }

    public static String[] getAvailableAlgorithms() {
        return new String[] {
            "First-Come-First-Served (FCFS)",
            "Shortest Job First (SJF)",
            "Priority",
            "Round Robin (RR)",
            "Weighted Round Robin (WRR)"
        };
    }
} 