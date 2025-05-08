package com.example.taskscheduler.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskscheduler.R;
import com.example.taskscheduler.adapters.TaskAdapter;
import com.example.taskscheduler.database.AppDatabase;
import com.example.taskscheduler.database.TaskDao;
import com.example.taskscheduler.models.Task;
import com.example.taskscheduler.viewmodels.TaskViewModel;
import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private String category;
    private TaskDao taskDao;
    private TaskViewModel taskViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString("category", "Home");
        }
        taskDao = AppDatabase.getInstance(requireContext()).taskDao();
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);
        
        // Observe sorting algorithm changes
        taskViewModel.getSortingAlgorithm().observe(getViewLifecycleOwner(), algorithm -> {
            taskAdapter.setSortingAlgorithm(algorithm);
            loadTasks(algorithm);
        });
        
        return view;
    }

    private void loadTasks(String algorithm) {
        switch (algorithm) {
            case "First-Come-First-Served (FCFS)":
                taskDao.getTasksByFirstComeFirstServed(category).observe(getViewLifecycleOwner(), tasks -> {
                    if (tasks != null) {
                        taskAdapter.setTasks(tasks);
                    }
                });
                break;
            case "Shortest Job First (SJF)":
                taskDao.getTasksByShortestJobFirst(category).observe(getViewLifecycleOwner(), tasks -> {
                    if (tasks != null) {
                        taskAdapter.setTasks(tasks);
                    }
                });
                break;
            case "Priority":
                taskDao.getTasksByPriority(category).observe(getViewLifecycleOwner(), tasks -> {
                    if (tasks != null) {
                        taskAdapter.setTasks(tasks);
                    }
                });
                break;
        }
    }
} 