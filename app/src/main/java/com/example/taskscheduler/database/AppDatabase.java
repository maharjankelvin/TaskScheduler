package com.example.taskscheduler.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.taskscheduler.MainActivity;
import com.example.taskscheduler.models.Task;

@Database(entities = {Task.class}, version = 2, exportSchema = false)
@TypeConverters({})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "task_scheduler_db";
    private static volatile AppDatabase instance;

    public abstract TaskDao taskDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration() // This will clear the database on version change
                    .build();
        }
        return instance;
    }
} 