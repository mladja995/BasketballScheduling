package mosis.elfak.basketscheduling.services;

import static android.provider.Settings.System.getString;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import mosis.elfak.basketscheduling.R;
import mosis.elfak.basketscheduling.activities.ViewBasketballEventActivity;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiver";
    private static final String CHANNEL_ID = "EventsChannel";

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // Send notification and log the transition details.
            for (int i = 0; i < triggeringGeofences.size(); i++) {
                sendNotification(triggeringGeofences.get(i).getRequestId(), context);
            }
        } else {
            // Log the error.
            Log.e(TAG, String.valueOf(R.string.geofence_transition_invalid_type) + " " + Integer.toString(geofenceTransition));
        }
    }

    @SuppressLint("LongLogTag")
    private void sendNotification(String eventId, Context context){
        try {
            createNotificationChannel(context);
            String message = "There is a scheduled event near by you. Click to see details.";
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.basketball)
                    .setContentTitle("Scheduled event")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Intent i = new Intent(context, ViewBasketballEventActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle positionBundle = new Bundle();
            positionBundle.putString("eventKey", eventId);
            i.putExtras(positionBundle);

            PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(pi);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());

            Log.i(TAG, "Notification sent for event with ID: " + eventId);
        } catch (Exception e){
            Log.e(TAG, "sendNotification: failure " + e.getMessage());
        }
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = "Channel for publishing near by basketball events.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}