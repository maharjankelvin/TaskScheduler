package com.example.taskscheduler.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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
import java.util.List;
import java.util.stream.Collectors;

public class TaskListFragment extends Fragment implements TaskAdapter.OnEditClickListener, TaskAdapter.OnDeleteClickListener {
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
        
        // Set listeners for edit and delete
        taskAdapter.setOnEditClickListener(this);
        taskAdapter.setOnDeleteClickListener(this);
        
        // Observe tasks and sorting algorithm changes
        taskViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                // Filter tasks by category
                List<Task> filteredTasks = tasks.stream()
                    .filter(task -> task.getCategory().equals(category))
                    .collect(Collectors.toList());
                taskAdapter.setTasks(filteredTasks);
            }
        });

        taskViewModel.getSortingAlgorithm().observe(getViewLifecycleOwner(), algorithm -> {
            taskAdapter.setSortingAlgorithm(algorithm);
        });
        
        return view;
    }

    @Override
    public void onEditClick(Task task) {
        AddTaskFragment fragment = new AddTaskFragment();
        Bundle args = new Bundle();
        args.putParcelable("task_to_edit", task);
        fragment.setArguments(args);
        fragment.show(requireActivity().getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onDeleteClick(Task task) {
        taskViewModel.deleteTask(task);
        Toast.makeText(getContext(), "Task deleted: " + task.getTitle(), Toast.LENGTH_SHORT).show();
    }
} 