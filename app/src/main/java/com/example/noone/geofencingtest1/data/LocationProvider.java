package com.example.noone.geofencingtest1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by No One on 4/21/2017.
 */

public class LocationProvider extends ContentProvider{

    private static final int LOCATION = 125;
    private static final int LOCATION_ID = 126;
    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        matcher.addURI(LocationContract.CONTENT_AUTHORITY,LocationContract.PATH_LOCATION, LOCATION);
        matcher.addURI(LocationContract.CONTENT_AUTHORITY,LocationContract.PATH_LOCATION + "/#", LOCATION_ID);
    }
    private DBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c;
            switch (matcher.match(uri)){
                case LOCATION:
                    c= db.query(LocationContract.LocationEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,null,sortOrder);
                    break;

                case LOCATION_ID:
                    selection = LocationContract.LocationEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    c = db.query(LocationContract.LocationEntry.TABLE_NAME, projection,selection,selectionArgs,null,null,null);
                    break;

                default:
                    throw new IllegalArgumentException("Cannot query URI"+uri);
            }

            c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int id = matcher.match(uri);
        long Uriid;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch(id){
            case LOCATION:
                try {
                    Uriid = db.insert(LocationContract.LocationEntry.TABLE_NAME, null, values);
                }catch (Exception e){
                    Log.e("Unique Constrain failed", "failed");
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return ContentUris.withAppendedId(uri,Uriid);

            default:
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
            int id = matcher.match(uri);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            switch (id){
                case LOCATION:
                    db.delete(LocationContract.LocationEntry.TABLE_NAME,null,null);
                    break;
                case LOCATION_ID:
                    selection = LocationContract.LocationEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    db.delete(LocationContract.LocationEntry.TABLE_NAME, selection,selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Deletion not happened"+uri);
            }
            getContext().getContentResolver().notifyChange(uri,null);
            return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
