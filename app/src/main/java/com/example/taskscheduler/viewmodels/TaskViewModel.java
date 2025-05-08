package com.example.taskscheduler.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TaskViewModel extends ViewModel {
    private final MutableLiveData<String> sortingAlgorithm = new MutableLiveData<>("First-Come-First-Served (FCFS)");

    public LiveData<String> getSortingAlgorithm() {
        return sortingAlgorithm;
    }

    public void setSortingAlgorithm(String algorithm) {
        sortingAlgorithm.setValue(algorithm);
    }
} 