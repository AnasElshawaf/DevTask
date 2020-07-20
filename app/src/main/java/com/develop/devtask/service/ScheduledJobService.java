package com.develop.devtask.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.develop.devtask.R;
import com.develop.devtask.database.AppExecutors;
import com.develop.devtask.database.RepoDatabase;
import com.develop.devtask.model.Repository;
import com.develop.devtask.ui.main.RepositoriesActivity;
import com.develop.devtask.utils.AppConstants;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

/**
 * Developed by Anas Elshwaf
 * anaselshawaf357@gmail.com
 */
public class ScheduledJobService extends JobService {

    private ApiRequest apiRequest;
    private RepoDatabase mDb;

    @Override
    public boolean onStartJob(final JobParameters params) {
        //Offloading work to a new thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("onStartJob","onStartJob");
                schedualedTask(params);
            }
        }).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }


    public void schedualedTask(final JobParameters parameters) {
        updateCashedData();
        jobFinished(parameters, true);

    }

    private void updateCashedData() {
        mDb = RepoDatabase.getInstance(getApplicationContext());
        apiRequest = ApiRequest.getInstance(getApplicationContext());
        apiRequest.createGetRequest(buildUrl(), Priority.IMMEDIATE, new ApiRequest.ServiceCallback<String>() {
            @Override
            public void onSuccess(String response) throws JSONException {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Repository repository = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), Repository.class);
                        repository.setLogin(jsonArray.getJSONObject(i).getJSONObject("owner").getString("login"));

                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                mDb.repoDao().insertRepo(repository);
                            }
                        });

                    }

                } catch (JSONException e) {
                    Timber.e(e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(ANError error) throws JSONException {
                Timber.e(error.getErrorBody());

            }
        });
        sendNotification();
    }

    private String buildUrl() {
        Uri builtUri;
        URL repoUrl = null;

        builtUri = Uri.parse(AppConstants.repoUrl)
                .buildUpon()
                .appendQueryParameter("per_page", "10")
                .appendQueryParameter("page", "1")
                .build();

        try {
            repoUrl = new URL(builtUri.toString());
        } catch (
                MalformedURLException e) {
            e.printStackTrace();
        }

        return repoUrl.toString();
    }


    public  void sendNotification() {

        Intent intent = new Intent(this, RepositoriesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "101";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_MAX);

            //Configure Notification Channel
            notificationChannel.setDescription(" Notifications");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Repositories Github Square")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Are Up To Date Now"));

        notificationManager.notify(0, notificationBuilder.build());


    }

}
