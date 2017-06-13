package com.example.noone.geofencingtest1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by No One on 4/21/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_SCHEME = "locations.db";
    private static final int DATABASE_VERSION = 1;
    public DBHelper(Context context){
        super(context,DATABASE_SCHEME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_QUERY = "CREATE TABLE "+ LocationContract.LocationEntry.TABLE_NAME + " ( "
                                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + LocationContract.LocationEntry.COLUMN_PLACE_NAME + " TEXT, "
                                + LocationContract.LocationEntry.COLUMN_LATITUDE + " REAL, "
                                + LocationContract.LocationEntry.COLUMN_LONGITUDE + " REAL );";
        db.execSQL(SQL_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
