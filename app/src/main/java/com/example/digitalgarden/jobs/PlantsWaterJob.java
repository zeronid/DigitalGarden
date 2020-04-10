package com.example.digitalgarden.jobs;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

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

        if (LocalTime.now().getHour() >=1  && LocalTime.now().getHour() <=2) {
            for (int i = 0; i < MainActivity.plants.size(); i++) {
                //TODO change to LocalDate.now().getDayOfMonth()
                if (MainActivity.plants.get(i).getCurrentWater() > 0 && (MainActivity.plants.get(i).dayUpdated != 10)) {
                    MainActivity.plants.get(i).setCurrentWater(MainActivity.plants.get(i).getCurrentWater() - 1);
                    MainActivity.plants.get(i).dayUpdated = LocalDate.now().getDayOfMonth();
                }

            }
            for (int j = 0; j < MainActivity.plants.size(); j++) {
                if ((MainActivity.plants.get(j).getCurrentWater() / MainActivity.plants.get(j).getWaterLevel()) <= 0.25) {
                    Intent resultIntent = new Intent(this, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_spa_black_24dp)
                            .setContentTitle("Water!")
                            .setContentText("You have plants that need some watering").setContentIntent(pendingIntent)
                            .setAutoCancel(true).build();
                    notificationManager.notify(1, notification);
                    break;
                }
            }
            jobFinished(params, false);
        }
            return false;
        }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}
