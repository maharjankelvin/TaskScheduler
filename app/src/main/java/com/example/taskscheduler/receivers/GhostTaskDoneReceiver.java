package com.example.taskscheduler.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.taskscheduler.utils.NotificationHelper;
import com.example.taskscheduler.viewmodels.TaskViewModel;
import android.app.PendingIntent;
import android.util.Log;

public class GhostTaskDoneReceiver extends BroadcastReceiver {
    public static final String EXTRA_CHUNK_ID = "extra_chunk_id";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DURATION = "extra_duration";

    @Override
    public void onReceive(Context context, Intent intent) {
        String chunkId = intent.getStringExtra(EXTRA_CHUNK_ID);
        String title = intent.getStringExtra(EXTRA_TITLE);
        String duration = intent.getStringExtra(EXTRA_DURATION);
        Log.d("GhostNotify", "Receiver triggered: chunkId=" + chunkId + ", title=" + title + ", duration=" + duration);
        if (chunkId != null && title != null && duration != null) {
            // Show the notification with a 'Mark as Done' action
            Intent doneIntent = new Intent(context, GhostTaskDoneReceiver.class);
            doneIntent.putExtra(EXTRA_CHUNK_ID, chunkId);
            PendingIntent donePendingIntent = PendingIntent.getBroadcast(context, chunkId.hashCode(), doneIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            NotificationHelper.createNotificationChannel(context);
            NotificationHelper.sendGhostTaskNotification(
                context,
                chunkId.hashCode(),
                title,
                "Time: " + duration + " minutes",
                donePendingIntent
            );
        } else if (chunkId != null) {
            Log.d("GhostNotify", "Mark as Done action for chunkId=" + chunkId);
            // This is the 'Mark as Done' action
            TaskViewModel.handleGhostChunkDone(context, chunkId);
        }
    }
} 