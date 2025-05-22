package com.example.taskscheduler.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.taskscheduler.R;
import com.example.taskscheduler.database.AppDatabase;
import com.example.taskscheduler.database.TaskDao;
import com.example.taskscheduler.models.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.lifecycle.ViewModelProvider;
import com.example.taskscheduler.viewmodels.TaskViewModel;

public class AddTaskFragment extends BottomSheetDialogFragment {
    private TextInputLayout titleInputLayout;
    private TextInputEditText titleEditText;
    private TextInputEditText descriptionEditText;
    private RadioGroup priorityRadioGroup;
    private RadioGroup categoryRadioGroup;
    private NumberPicker durationPicker;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private TaskDao taskDao;
    private ExecutorService executorService;
    private Task taskToEdit;
    private TaskViewModel taskViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        if (getArguments() != null && getArguments().containsKey("task_to_edit")) {
            taskToEdit = getArguments().getParcelable("task_to_edit");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        
        // Initialize views
        titleInputLayout = (TextInputLayout) ((ViewGroup) view.findViewById(R.id.editTextTitle).getParent()).getParent();
        titleEditText = view.findViewById(R.id.editTextTitle);
        descriptionEditText = view.findViewById(R.id.editTextDescription);
        priorityRadioGroup = view.findViewById(R.id.radioGroupPriority);
        categoryRadioGroup = view.findViewById(R.id.radioGroupCategory);
        durationPicker = view.findViewById(R.id.numberPickerDuration);
        saveButton = view.findViewById(R.id.buttonSave);
        cancelButton = view.findViewById(R.id.buttonCancel);

        // Setup duration picker
        durationPicker.setMinValue(1);
        durationPicker.setMaxValue(480); // 8 hours max
        durationPicker.setValue(30); // Default 30 minutes

        // Setup button listeners
        saveButton.setOnClickListener(v -> saveTask());
        cancelButton.setOnClickListener(v -> dismiss());

        // Populate fields if editing a task
        if (taskToEdit != null) {
            titleEditText.setText(taskToEdit.getTitle());
            descriptionEditText.setText(taskToEdit.getDescription());
            setPriorityRadioButton(taskToEdit.getPriority());
            setCategoryRadioButton(taskToEdit.getCategory());
            durationPicker.setValue((int) taskToEdit.getDuration());
        }

        return view;
    }

    private void setPriorityRadioButton(int priority) {
        int radioButtonId = -1;
        switch (priority) {
            case 1:
                radioButtonId = R.id.radioLow;
                break;
            case 2:
                radioButtonId = R.id.radioMedium;
                break;
            case 3:
                radioButtonId = R.id.radioHigh;
                break;
        }
        if (radioButtonId != -1) {
            priorityRadioGroup.check(radioButtonId);
        }
    }

    private void setCategoryRadioButton(String category) {
        int radioButtonId = -1;
        switch (category) {
            case "Home":
                radioButtonId = R.id.radioHome;
                break;
            case "Work":
                radioButtonId = R.id.radioWork;
                break;
        }
        if (radioButtonId != -1) {
            categoryRadioGroup.check(radioButtonId);
        }
    }

    private void saveTask() {
        // Validate title
        String title = titleEditText.getText().toString().trim();
        if (title.isEmpty()) {
            titleInputLayout.setError("Title is required");
            return;
        }
        titleInputLayout.setError(null);

        // Get description
        String description = descriptionEditText.getText().toString().trim();

        // Get priority
        int priorityId = priorityRadioGroup.getCheckedRadioButtonId();
        int priority;
        if (priorityId == R.id.radioLow) {
            priority = 1;
        } else if (priorityId == R.id.radioMedium) {
            priority = 2;
        } else if (priorityId == R.id.radioHigh) {
            priority = 3;
        } else {
            Toast.makeText(requireContext(), "Please select a priority", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get category
        int categoryId = categoryRadioGroup.getCheckedRadioButtonId();
        String category;
        if (categoryId == R.id.radioHome) {
            category = "Home";
        } else if (categoryId == R.id.radioWork) {
            category = "Work";
        } else {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get duration
        long duration = durationPicker.getValue();

        // Create and save task
        if (taskToEdit == null) {
            // Add new task
            Task newTask = new Task(title, description, priority, category, duration);
            taskViewModel.insertTask(newTask);
            Toast.makeText(requireContext(), "Task added successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Update existing task
            taskToEdit.setTitle(title);
            taskToEdit.setDescription(description);
            taskToEdit.setPriority(priority);
            taskToEdit.setCategory(category);
            taskToEdit.setDuration(duration);
            taskViewModel.updateTask(taskToEdit);
            Toast.makeText(requireContext(), "Task updated successfully", Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
} 