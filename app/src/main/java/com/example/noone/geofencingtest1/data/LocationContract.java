package com.example.noone.geofencingtest1.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by No One on 4/21/2017.
 */

public class LocationContract {
    private LocationContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.noone.geofencingtest1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_LOCATION = "location";

    public static final class LocationEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_LOCATION);
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_PLACE_NAME ="placename";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
    }
}
