package com.example.taskscheduler.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskscheduler.R;
import com.example.taskscheduler.adapters.TaskAdapter;
import com.example.taskscheduler.database.AppDatabase;
import com.example.taskscheduler.database.TaskDao;
import com.example.taskscheduler.models.Task;
import java.util.List;

public class HomeTasksFragment extends Fragment {
    private static final String ARG_SORTING_ALGORITHM = "sorting_algorithm";
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private TaskDao taskDao;
    private String sortingAlgorithm;

    public static HomeTasksFragment newInstance(String sortingAlgorithm) {
        HomeTasksFragment fragment = new HomeTasksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SORTING_ALGORITHM, sortingAlgorithm);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sortingAlgorithm = getArguments().getString(ARG_SORTING_ALGORITHM);
        }
        taskDao = AppDatabase.getInstance(requireContext()).taskDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);

        observeTasks();
        
        return view;
    }

    private void observeTasks() {
        taskDao.getTasksByCategory("Home").observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                List<Task> sortedTasks = sortTasks(tasks);
                taskAdapter.setTasks(sortedTasks);
            }
        });
    }

    private List<Task> sortTasks(List<Task> tasks) {
        switch (sortingAlgorithm) {
            case "FCFS":
                // Sort by creation time (already sorted by Room)
                return tasks;
            case "SJF":
                // Sort by duration
                tasks.sort((t1, t2) -> Long.compare(t1.getDuration(), t2.getDuration()));
                return tasks;
            case "Priority":
                // Sort by priority
                tasks.sort((t1, t2) -> Integer.compare(t2.getPriority(), t1.getPriority()));
                return tasks;
            default:
                return tasks;
        }
    }
} 