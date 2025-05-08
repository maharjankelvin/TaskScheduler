package com.example.taskscheduler.adapters;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.taskscheduler.fragments.TaskListFragment;

public class TaskPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 2;

    public TaskPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Create a new instance of TaskListFragment with the appropriate category
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putString("category", position == 0 ? "Home" : "Work");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
} 