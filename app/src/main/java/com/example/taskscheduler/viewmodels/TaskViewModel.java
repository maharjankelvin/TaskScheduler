package com.example.taskscheduler.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.taskscheduler.database.AppDatabase;
import com.example.taskscheduler.models.Task;
import com.example.taskscheduler.models.GhostTask;
import com.example.taskscheduler.algorithms.SchedulingAlgorithm;
import com.example.taskscheduler.algorithms.SchedulingAlgorithmFactory;
import com.example.taskscheduler.algorithms.RRAlgorithm;
import com.example.taskscheduler.algorithms.WeightedRRAlgorithm;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashSet;
import java.util.Set;
import android.content.Context;
import com.example.taskscheduler.utils.NotificationHelper;
import android.os.Handler;
import android.os.Looper;

public class TaskViewModel extends AndroidViewModel {
    private final MutableLiveData<String> sortingAlgorithm = new MutableLiveData<>(SchedulingAlgorithmFactory.getAvailableAlgorithms()[0]);
    private final MutableLiveData<List<Task>> tasks = new MutableLiveData<>();
    private final MutableLiveData<Long> quantum = new MutableLiveData<>(5L); // Default quantum in minutes
    private final MutableLiveData<List<GhostTask>> ghostTasks = new MutableLiveData<>();
    private final MutableLiveData<Boolean> ghostNotificationEnabled = new MutableLiveData<>(false);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AppDatabase database;
    private final Set<String> deletedGhostChunks = new HashSet<>();
    private static TaskViewModel instance;

    public TaskViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        instance = this;
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

    public LiveData<Long> getQuantum() {
        return quantum;
    }

    public void setQuantum(long value) {
        quantum.setValue(value);
        refreshTasks();
    }

    public LiveData<List<GhostTask>> getGhostTasks() {
        return ghostTasks;
    }

    public LiveData<Boolean> getGhostNotificationEnabled() {
        return ghostNotificationEnabled;
    }
    public void setGhostNotificationEnabled(boolean enabled) {
        ghostNotificationEnabled.setValue(enabled);
    }

    public void deleteGhostChunk(String chunkId) {
        deletedGhostChunks.add(chunkId);
        refreshTasks();
    }

    public static void handleGhostChunkDone(Context context, String chunkId) {
        if (instance != null) {
            instance.deleteGhostChunk(chunkId);
        }
    }

    private void scheduleGhostNotifications(List<GhostTask> ghostTasks) {
        // Schedule notifications for each ghost chunk
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> NotificationHelper.scheduleGhostTaskNotifications(getApplication(), ghostTasks, 0));
    }
    private void cancelGhostNotifications() {
        // TODO: Implement cancel logic if needed
    }

    public void refreshTasks() {
        executorService.execute(() -> {
            List<Task> allTasks = database.taskDao().getAllTasksSync();
            String algoName = sortingAlgorithm.getValue();
            SchedulingAlgorithm algorithm;
            if ("Round Robin (RR)".equals(algoName)) {
                RRAlgorithm rr = new RRAlgorithm(quantum.getValue() != null ? quantum.getValue() : 5L);
                List<GhostTask> ghosts = rr.generateGhostTasks(allTasks, deletedGhostChunks);
                ghostTasks.postValue(ghosts);
                if (Boolean.TRUE.equals(ghostNotificationEnabled.getValue())) {
                    scheduleGhostNotifications(ghosts);
                } else {
                    cancelGhostNotifications();
                }
                algorithm = rr;
            } else if ("Weighted Round Robin (WRR)".equals(algoName)) {
                WeightedRRAlgorithm wrr = new WeightedRRAlgorithm(quantum.getValue() != null ? quantum.getValue() : 5L);
                List<GhostTask> ghosts = wrr.generateGhostTasks(allTasks, deletedGhostChunks);
                ghostTasks.postValue(ghosts);
                if (Boolean.TRUE.equals(ghostNotificationEnabled.getValue())) {
                    scheduleGhostNotifications(ghosts);
                } else {
                    cancelGhostNotifications();
                }
                algorithm = wrr;
            } else {
                ghostTasks.postValue(null);
                deletedGhostChunks.clear(); // Reset when switching algorithms
                cancelGhostNotifications();
                algorithm = SchedulingAlgorithmFactory.getAlgorithm(algoName);
            }
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