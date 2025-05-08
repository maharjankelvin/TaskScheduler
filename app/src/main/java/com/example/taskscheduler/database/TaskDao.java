package com.example.taskscheduler.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.taskscheduler.models.Task;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM tasks")
    void deleteAllTasks();

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    LiveData<Task> getTaskById(int taskId);

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY id DESC")
    LiveData<List<Task>> getTasksByCategory(String category);

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY created_at ASC, title ASC")
    LiveData<List<Task>> getTasksByFirstComeFirstServed(String category);

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY duration ASC, created_at ASC, title ASC")
    LiveData<List<Task>> getTasksByShortestJobFirst(String category);

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY priority DESC, duration ASC, created_at ASC, title ASC")
    LiveData<List<Task>> getTasksByPriority(String category);

    // Add more queries later for categories/algorithms
} 