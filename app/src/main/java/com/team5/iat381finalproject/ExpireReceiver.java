package com.team5.iat381finalproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class ExpireReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int uid = intent.getIntExtra("uid", 0);
        // https://developer.android.com/guide/topics/ui/notifiers/notifications.html#CreateNotification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                                                .setSmallIcon(R.drawable.photo_placeholder)
                                                .setContentTitle("Food Expiration Reminder!")
                                                .setContentText("Your food is expiring soon")
                                                .setAutoCancel(true);
        // explicit intent
        Intent resultIntent = new Intent(context, ItemDetailsActivity.class);
        intent.putExtra("position", uid);

        // artificial back stack for proper navigation out of activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(uid, mBuilder.build()); // save uid to allow modifying notification later
    }
}
