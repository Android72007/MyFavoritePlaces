package com.example.noone.geofencingtest1;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.noone.geofencingtest1.data.LocationContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public  ArrayList<LocationDetails> locationDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationDetails = new ArrayList<LocationDetails>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String[] projection = {
                LocationContract.LocationEntry._ID,
                LocationContract.LocationEntry.COLUMN_PLACE_NAME,
                LocationContract.LocationEntry.COLUMN_LATITUDE,
                LocationContract.LocationEntry.COLUMN_LONGITUDE
        };
        Cursor c = getContentResolver().query(LocationContract.LocationEntry.CONTENT_URI, projection, null, null, null);

        try {
           if(c.moveToFirst()) {

               do {
                   String place = c.getString(c.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_PLACE_NAME));
                   double lat = c.getDouble(c.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_LATITUDE));
                   double lon = c.getDouble(c.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_LONGITUDE));
                   Log.e("Results", place + lat + lon);
                   LocationDetails loc = new LocationDetails(place, lat, lon);
                   locationDetails.add(loc);
               } while (c.moveToNext());

           }
        }catch(Exception e){
                e.printStackTrace();
        }finally{
                c.close();
        }


        for(LocationDetails loc : locationDetails) {
                Log.e("LocationDetails inside", loc.getLatitude() + "" + loc.getLongitude() + "" + loc.getPlaceName());
                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(1).tilt(0).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(loc.getPlaceName());
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.radius(10);
            circleOptions.fillColor(R.color.colorPrimary);
            mMap.addCircle(circleOptions);
                mMap.addMarker(markerOptions);
            }
        }

}
