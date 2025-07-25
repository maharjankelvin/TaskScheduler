package com.example.taskscheduler.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.taskscheduler.R;
import android.app.AlarmManager;
import android.os.SystemClock;
import com.example.taskscheduler.models.GhostTask;
import com.example.taskscheduler.receivers.GhostTaskDoneReceiver;
import java.util.List;
import android.provider.Settings;
import android.util.Log;

public class NotificationHelper {
    public static final String CHANNEL_ID = "ghost_task_channel";
    public static final String CHANNEL_NAME = "Ghost Task Notifications";

    private static final int NOTIFICATION_ID_BASE = 1000;
    private static final int NOTIFICATION_ID_MAX = 10000;

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for ghost task chunks");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void sendGhostTaskNotification(Context context, int notificationId, String title, String content, PendingIntent doneIntent) {
        Log.d("GhostNotify", "Sending notification: " + title + " | " + content);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(android.R.drawable.checkbox_on_background, "Mark as Done", doneIntent);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }
    }

    public static void cancelAllGhostTaskNotifications(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (int notificationId = NOTIFICATION_ID_BASE; notificationId < NOTIFICATION_ID_MAX; notificationId++) {
            Intent notifyIntent = new Intent(context, GhostTaskDoneReceiver.class);
            PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(context, notificationId + 10000, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            if (alarmManager != null) {
                alarmManager.cancel(notifyPendingIntent);
            }
        }
    }

    public static void scheduleGhostTaskNotifications(Context context, List<GhostTask> ghostTasks, long startDelayMillis) {
        // Cancel all previous ghost task notifications first
        cancelAllGhostTaskNotifications(context);
        Log.d("GhostNotify", "Scheduling " + ghostTasks.size() + " ghost notifications");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.d("GhostNotify", "Cannot schedule exact alarms, requesting permission");
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }
        }
        long triggerAtMillis = SystemClock.elapsedRealtime() + startDelayMillis;
        int notificationId = NOTIFICATION_ID_BASE;
        for (GhostTask ghost : ghostTasks) {
            Log.d("GhostNotify", "Scheduling notification for: " + ghost.getTitle() + " at " + triggerAtMillis);
            Intent notifyIntent = new Intent(context, GhostTaskDoneReceiver.class);
            notifyIntent.putExtra(GhostTaskDoneReceiver.EXTRA_CHUNK_ID, ghost.getUniqueChunkId());
            notifyIntent.putExtra(GhostTaskDoneReceiver.EXTRA_TITLE, ghost.getTitle());
            notifyIntent.putExtra(GhostTaskDoneReceiver.EXTRA_DURATION, String.valueOf(ghost.getAllocatedTime()));
            PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(context, notificationId + 10000, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, notifyPendingIntent);
            }
            triggerAtMillis += ghost.getAllocatedTime() * 60 * 1000;
            notificationId++;
        }
    }
} 