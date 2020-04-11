package com.example.digitalgarden.jobs;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.digitalgarden.R;
import com.example.digitalgarden.activities.MainActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;


import static com.example.digitalgarden.app.app.CHANNEL_1_ID;


public class PlantsWaterJob extends JobService {

    private NotificationManagerCompat notificationManager;

    @Override
    public boolean onStartJob(final JobParameters params) {

        notificationManager = NotificationManagerCompat.from(this);

                    Intent resultIntent = new Intent(this, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_spa_black_24dp)
                            .setContentTitle("Water!")
                            .setContentText("It's time to check on your plants").setContentIntent(pendingIntent)
                            .setAutoCancel(true).build();
                    notificationManager.notify(1, notification);

            jobFinished(params, true);

            return false;
        }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}
