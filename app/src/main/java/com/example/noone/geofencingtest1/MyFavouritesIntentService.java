package com.example.noone.geofencingtest1;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by No One on 4/21/2017.
 */

public class MyFavouritesIntentService extends IntentService {

    private Location location;
    public MyFavouritesIntentService(){
        super("MyFavouritesIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e("geofencing error", errorMessage);
            return;
        }
//         location = geofencingEvent.getTriggeringLocation();
//        Log.e("Location " ,location.getLatitude() + "Lat" + location.getLongitude());

        int geofencingtransition = geofencingEvent.getGeofenceTransition();
        if(geofencingtransition == Geofence.GEOFENCE_TRANSITION_ENTER ){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofencingtransition,
                    triggeringGeofences
            );
            sendNotification(geofenceTransitionDetails,getApplicationContext());
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofencingtransition));
        }
        }

    public String getGeofenceTransitionDetails(Context context, int geofencetransition, List<Geofence> triggeringfences) {
        String geofenceTransitionString = getTransitionString(geofencetransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringfences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
            Log.e("Check for trigerri","Check");
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }


    private void sendNotification(String notificationDetails, Context context) {
        int random = (int) Math.floor(Math.random() * 101);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pd =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        Drawable drawable= ContextCompat.getDrawable(this,R.drawable.ic_map_black_24dp);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setLargeIcon(bitmap)
                            .setContentText(notificationDetails)
                            .setContentIntent(pd)
                            .setContentTitle("Favourite Place")
                            .setSmallIcon(R.drawable.ic_favorite_border_black_24dp)
                            .setAutoCancel(true)
//                            .addAction(R.drawable.ic_favorite_border_black_24dp,"Open in maps",getMapPD(location,context))
                            .addAction(getCancelPD(context,random))
                            .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                            .setDefaults(Notification.DEFAULT_SOUND);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }
        NotificationManager notificationManager =(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(random,notificationBuilder.build());
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

    public NotificationCompat.Action getCancelPD(Context context,int random){
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(String.valueOf(random));
        PendingIntent pd1 = PendingIntent.getService(context,100, intent,PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_cancel_black_24px,"CANCEL",pd1);
        return action;
    }

//    public PendingIntent getMapPD(Location location, Context context){
//        Uri geouri;
//        String uri = "geo:0,0?q="+location.getLatitude() +","+location.getLongitude()+"("+name+")";
//        geouri = Uri.parse(uri);
//        Intent intent = new Intent(Intent.ACTION_VIEW, geouri);
//        intent.setPackage("com.google.android.apps.maps");
//        return PendingIntent.getActivity(context,25,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//    }

}



