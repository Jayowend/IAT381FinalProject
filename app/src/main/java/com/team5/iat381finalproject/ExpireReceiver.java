package com.team5.iat381finalproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class ExpireReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int uid = intent.getIntExtra("uid", -1);
        if (intent.getAction().equals(context.getString(R.string.intent_action_name_expire_notification_cleared))) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.sharedprefs_filename), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("alarmtime_" + uid);
            editor.apply();
        } else {
            // https://developer.android.com/guide/topics/ui/notifiers/notifications.html#CreateNotification
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.photo_placeholder)
                    .setContentTitle("Food Expiration Reminder!")
                    .setContentText("Your food is expiring soon")
                    .setAutoCancel(true);
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

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(uid, mBuilder.build());
        }
    }
}
