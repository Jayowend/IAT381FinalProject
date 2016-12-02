package com.team5.iat381finalproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class ExpireReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int uid = intent.getIntExtra("uid", -1);
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.sharedprefs_filename), Context.MODE_PRIVATE);
        if (intent.getAction().equals(context.getString(R.string.intent_action_name_expire_notification_cleared))) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("alarmtime_" + uid);
            editor.apply();
        } else if (!sharedPreferences.getBoolean("notificationsOn", false)) {
            // notifications off
        } else {
            // https://developer.android.com/guide/topics/ui/notifiers/notifications.html#CreateNotification
            NotificationCompat.Builder mBuilder;
            if (intent.getAction().equals(context.getString(R.string.intent_action_name_expire_notification))) {
                mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Food Expired")
                        .setContentText("One of your items has expired.")
                        .setAutoCancel(true);
            } else {
                String expiresIn;
                // use sharedprefs to get alarm time
                switch(sharedPreferences.getInt("alarmtime_" + uid, 0)) {
                    case 0:
                        expiresIn = "7 days";
                        break;
                    case 1:
                        expiresIn = "3 days";
                        break;
                    case 2:
                        expiresIn = "1 day!";
                        break;
                    default:
                        expiresIn = "7 days";
                }
                mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Reminder: Food Expiry")
                        .setContentText("Your food is expiring soon, in " + expiresIn)
                        .setAutoCancel(true);
            }
            // explicit intent
            Intent resultIntent = new Intent(context, ItemDetailsActivity.class);
            resultIntent.setAction(intent.getAction()); // pass action
            resultIntent.putExtras(intent); // pass extras

            // artificial back stack for proper navigation out of activity
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(resultIntent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(uid, 0);
            mBuilder.setContentIntent(resultPendingIntent);

            // support for notification cleared
            Intent deleteIntent = new Intent(context.getString(R.string.intent_action_name_expire_notification_cleared));
            deleteIntent.addCategory("android.intent.category.DEFAULT");
            deleteIntent.putExtra("uid", uid);
            PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, uid, deleteIntent, 0);
            mBuilder.setDeleteIntent(deletePendingIntent);

            if (sharedPreferences.getBoolean("vibrationOn", false))
                mBuilder.setVibrate(new long[] {0, 250});
            String stringUri = sharedPreferences.getString(context.getString(R.string.settings_notification_tone_uri), "");
            if (sharedPreferences.getBoolean("soundOn", false) && stringUri != null)
                mBuilder.setSound(Uri.parse(stringUri));
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(uid, mBuilder.build());
        }
    }
}
