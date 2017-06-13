package com.example.noone.geofencingtest1;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.noone.geofencingtest1.data.LocationContract;

/**
 * Created by No One on 4/21/2017.
 */

public class LocationAdapter extends CursorAdapter {

    public LocationAdapter(Context context, Cursor cursor){
        super(context,cursor,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView PlaceName = (TextView)view.findViewById(R.id.Place_name_list);
        TextView LatLng = (TextView)view.findViewById(R.id.latlang);

        PlaceName.setText(cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_PLACE_NAME)));
        double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_LATITUDE));
        double lang = cursor.getDouble(cursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_LONGITUDE));
        String latlng = "Lat "+lat + " Long "+lang;
        LatLng.setText(latlng);
    }
}
