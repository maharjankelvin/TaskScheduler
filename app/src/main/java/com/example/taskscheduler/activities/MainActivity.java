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
import com.example.taskscheduler.fragments.AddTaskFragment;
import com.example.taskscheduler.fragments.TaskListFragment;
import com.example.taskscheduler.viewmodels.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
        String[] sortingAlgorithms = {
            "First-Come-First-Served (FCFS)",
            "Shortest Job First (SJF)",
            "Priority"
        };
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            sortingAlgorithms
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSorting.setAdapter(spinnerAdapter);

        // Set initial spinner position based on ViewModel
        taskViewModel.getSortingAlgorithm().observe(this, algorithm -> {
            int position = 0;
            for (int i = 0; i < sortingAlgorithms.length; i++) {
                if (sortingAlgorithms[i].equals(algorithm)) {
                    position = i;
                    break;
                }
            }
            spinnerSorting.setSelection(position);
        });

        spinnerSorting.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedAlgorithm = parent.getItemAtPosition(position).toString();
                taskViewModel.setSortingAlgorithm(selectedAlgorithm);
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