package com.example.taskscheduler.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.taskscheduler.database.AppDatabase;
import com.example.taskscheduler.models.Task;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskViewModel extends AndroidViewModel {
    private final MutableLiveData<String> sortingAlgorithm = new MutableLiveData<>("First-Come-First-Served (FCFS)");
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AppDatabase database;

    public TaskViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    public LiveData<String> getSortingAlgorithm() {
        return sortingAlgorithm;
    }

    public void setSortingAlgorithm(String algorithm) {
        sortingAlgorithm.setValue(algorithm);
    }

    public void deleteTask(Task task) {
        executorService.execute(() -> {
            database.taskDao().delete(task);
        });
    }

    public void updateTask(Task task) {
        executorService.execute(() -> {
            database.taskDao().update(task);
        });
    }

    public void insertTask(Task task) {
        executorService.execute(() -> {
            database.taskDao().insert(task);
        });
    }
} 