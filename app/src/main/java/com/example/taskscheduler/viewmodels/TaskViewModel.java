package com.example.taskscheduler.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.taskscheduler.database.AppDatabase;
import com.example.taskscheduler.models.Task;
import com.example.taskscheduler.algorithms.SchedulingAlgorithm;
import com.example.taskscheduler.algorithms.SchedulingAlgorithmFactory;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskViewModel extends AndroidViewModel {
    private final MutableLiveData<String> sortingAlgorithm = new MutableLiveData<>(SchedulingAlgorithmFactory.getAvailableAlgorithms()[0]);
    private final MutableLiveData<List<Task>> tasks = new MutableLiveData<>();
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
        refreshTasks();
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public void refreshTasks() {
        executorService.execute(() -> {
            List<Task> allTasks = database.taskDao().getAllTasksSync();
            SchedulingAlgorithm algorithm = SchedulingAlgorithmFactory.getAlgorithm(sortingAlgorithm.getValue());
            if (algorithm != null) {
                List<Task> sortedTasks = algorithm.schedule(allTasks);
                tasks.postValue(sortedTasks);
            }
        });
    }

    public void deleteTask(Task task) {
        executorService.execute(() -> {
            database.taskDao().delete(task);
            refreshTasks();
        });
    }

    public void updateTask(Task task) {
        executorService.execute(() -> {
            database.taskDao().update(task);
            refreshTasks();
        });
    }

    public void insertTask(Task task) {
        executorService.execute(() -> {
            database.taskDao().insert(task);
            refreshTasks();
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
} 