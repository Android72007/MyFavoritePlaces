package com.example.noone.geofencingtest1;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by No One on 4/2/2017.
 */

public class NotificationIntentService extends IntentService {

    public NotificationIntentService(){
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        NotificationUtils.cancelNotification(this,action);
    }
}
