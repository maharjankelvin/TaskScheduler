package com.example.taskscheduler;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.taskscheduler.adapters.TaskPagerAdapter;
import com.example.taskscheduler.fragments.AddTaskFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewPager2 and TabLayout
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new TaskPagerAdapter(this));

        tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText((position == 0) ? "Home" : "Work");
        }).attach();

        // Setup FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fabAddTask);
        fab.setOnClickListener(v -> showAddTaskFragment());
    }

    private void showAddTaskFragment() {
        AddTaskFragment fragment = new AddTaskFragment();
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }
} 