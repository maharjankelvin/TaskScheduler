package com.example.taskscheduler.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.example.taskscheduler.R;
import com.example.taskscheduler.adapters.TaskPagerAdapter;
import com.example.taskscheduler.algorithms.SchedulingAlgorithmFactory;
import com.example.taskscheduler.fragments.AddTaskFragment;
import com.example.taskscheduler.fragments.TaskListFragment;
import com.example.taskscheduler.viewmodels.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.os.Build;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Spinner spinnerSorting;
    private TaskPagerAdapter taskPagerAdapter;
    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize ViewPager2 and TabLayout
        viewPager = findViewById(R.id.viewPager);
        taskPagerAdapter = new TaskPagerAdapter(this);
        viewPager.setAdapter(taskPagerAdapter);

        tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText((position == 0) ? "Home" : "Work");
        }).attach();

        // Setup Sorting Spinner
        spinnerSorting = findViewById(R.id.spinnerSorting);
        String[] sortingAlgorithms = SchedulingAlgorithmFactory.getAvailableAlgorithms();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            sortingAlgorithms
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSorting.setAdapter(spinnerAdapter);

        // Set initial spinner position based on ViewModel
        taskViewModel.getSortingAlgorithm().observe(this, algorithm -> {
            for (int i = 0; i < sortingAlgorithms.length; i++) {
                if (sortingAlgorithms[i].equals(algorithm)) {
                    spinnerSorting.setSelection(i);
                    break;
                }
            }
        });

        // Handle spinner selection changes
        spinnerSorting.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedAlgorithm = sortingAlgorithms[position];
                if (selectedAlgorithm.equals("Round Robin (RR)") || selectedAlgorithm.equals("Weighted Round Robin (WRR)")) {
                    // Prompt for quantum
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Set Time Slice (minutes)");
                    final EditText input = new EditText(MainActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setHint("e.g. 5");
                    builder.setView(input);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        String value = input.getText().toString();
                        long quantum = 5L;
                        try {
                            quantum = Long.parseLong(value);
                            if (quantum <= 0) quantum = 5L;
                        } catch (Exception e) {
                            quantum = 5L;
                        }
                        taskViewModel.setQuantum(quantum);
                        // Prompt for notification enable
                        new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Enable Notifications?")
                            .setMessage("Do you want to receive notifications for each ghost task chunk in this session?")
                            .setPositiveButton("Yes", (d, w) -> {
                                taskViewModel.setGhostNotificationEnabled(true);
                                taskViewModel.setSortingAlgorithm(selectedAlgorithm);
                                Toast.makeText(MainActivity.this, "Notifications enabled!", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("No", (d, w) -> {
                                taskViewModel.setGhostNotificationEnabled(false);
                                taskViewModel.setSortingAlgorithm(selectedAlgorithm);
                            })
                            .show();
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                        // Optionally revert spinner selection
                    });
                    builder.show();
                } else {
                taskViewModel.setSortingAlgorithm(selectedAlgorithm);
                }
                Toast.makeText(MainActivity.this, "Sorting by: " + selectedAlgorithm, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Setup FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fabAddTask);
        fab.setOnClickListener(v -> showAddTaskFragment());
    }

    private void showAddTaskFragment() {
        AddTaskFragment fragment = new AddTaskFragment();
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }
} 