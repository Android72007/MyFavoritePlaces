package com.example.noone.geofencingtest1;

import android.app.NotificationManager;
import android.content.Context;

/**
 * Created by No One on 4/23/2017.
 */

public class NotificationUtils {

   public static void cancelNotification(Context context, String id){
       int id1 = Integer.valueOf(id);
       NotificationManager notificationManager = (NotificationManager)
               context.getSystemService(Context.NOTIFICATION_SERVICE);
       notificationManager.cancel(id1);
    }
}

