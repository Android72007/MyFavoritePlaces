package com.example.noone.geofencingtest1;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noone.geofencingtest1.data.LocationContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import static com.example.noone.geofencingtest1.R.id.nav_view;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener,
        NavigationView.OnNavigationItemSelectedListener,
        ResultCallback<Status> {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final static long INTERVAL_TIME = 1;
    private final static long MAX_WAIT_TIME = 1;
    private final static int REQUEST_PERMISSIONS_REQUEST_CODE = 225;
    final static String BROADCAST_RECEIVER_ACTION = "com.android.locationupdate";
    private static TextView mTextView;
    private LocationBroadcastReceiver myReceiver;
    private IntentFilter mIntentFilter;
    private static String TextCheck;
    private static Location listoflocations;
    private ArrayList<Geofence> geofenceArrayList;
    private EditText mPlaceName;
    private Button mSaveButton;
    private Button mCreateButton;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    public ArrayList<LocationDetails> locationDetailsList;
    private boolean isNameExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        locationDetailsList = new ArrayList<LocationDetails>();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView mNavigationView = (NavigationView) findViewById(nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        geofenceArrayList = new ArrayList<Geofence>();
        mPlaceName = (EditText) findViewById(R.id.place_name);
        mCreateButton = (Button) findViewById(R.id.getLocation);
        mSaveButton = (Button) findViewById(R.id.save_location);
        mTextView = (TextView) findViewById(R.id.view_location);
        if (!checkPermission()) {
            requestPermission();
        }
        myReceiver = new LocationBroadcastReceiver();
        mIntentFilter = new IntentFilter(BROADCAST_RECEIVER_ACTION);
        buildGoogleAPIClient();
    }

    /*For handling Navigation items clicks*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int navItemId = item.getItemId();
        switch (navItemId) {
            case R.id.my_places:
                Intent intent = new Intent(this, MyFavouritePlaces.class);
                startActivity(intent);
                break;
            case R.id.map_view:
                Intent intent1 = new Intent(this, MapsActivity.class);
                startActivity(intent1);
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    public static void setText() {
        mTextView.setText(TextCheck);
    }

    /*OnClick of save location button*/
    public void savelocation(View view) {
        if (TextUtils.isEmpty(mPlaceName.getText().toString().trim())) {
            mPlaceName.setError("Place name required");
        } else {
            String[] projection = {
                    LocationContract.LocationEntry.COLUMN_PLACE_NAME
            };
            String selection = LocationContract.LocationEntry.COLUMN_PLACE_NAME + "=?";
            String Placename = mPlaceName.getText().toString();
            String[] selectionargs = new String[]{Placename};
            Cursor c = getContentResolver().query(LocationContract.LocationEntry.CONTENT_URI, projection, selection, selectionargs, null);
            if (listoflocations != null) {
                if (!c.moveToFirst()) {
                    addGeofenceHandler();
                    mPlaceName.setText("");
                    mPlaceName.setVisibility(View.GONE);
                    mSaveButton.setVisibility(View.GONE);
                    mCreateButton.setVisibility(View.VISIBLE);
                } else {
                    mPlaceName.setError("Place name already exists");
                }
            } else {
                Toast.makeText(MainActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }

        }
    }

    void addGeofenceHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(MainActivity.this, "Google API client not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,
                    getGeoFencingRequest(),
                    getPendingServiceIntent()).setResultCallback(this);
            Log.e("succ addGeo handler", "Geo fence handler");
        } catch (SecurityException sec) {
            sec.printStackTrace();
        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.e("Result", "Success");
        } else {
            Log.e("Result", "Failed");
        }
    }

    public PendingIntent getPendingServiceIntent() {
        Intent intent = new Intent(this, MyFavouritesIntentService.class);
        return PendingIntent.getService(this, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public GeofencingRequest getGeoFencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        List<Geofence> list = populateGeoFenceList();
        builder.addGeofences(list);
        return builder.build();
    }

    public List<Geofence> populateGeoFenceList() {

        geofenceArrayList.add(new Geofence.Builder()
                .setRequestId(mPlaceName.getText().toString())
                .setCircularRegion(listoflocations.getLatitude(),
                        listoflocations.getLongitude(),
                        1000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
        insertIntoDB(mPlaceName.getText().toString(), listoflocations.getLatitude(), listoflocations.getLongitude());
        return geofenceArrayList;
    }

    /*Insert new location into database*/
    void insertIntoDB(String placename, double lat, double lan) {
        boolean isName = false;
        ContentValues values = new ContentValues();
        values.put(LocationContract.LocationEntry.COLUMN_PLACE_NAME, placename);
        values.put(LocationContract.LocationEntry.COLUMN_LATITUDE, lat);
        values.put(LocationContract.LocationEntry.COLUMN_LONGITUDE, lan);
        Uri uri = getContentResolver().insert(LocationContract.LocationEntry.CONTENT_URI, values);
        long id = ContentUris.parseId(uri);
    }


    /*Get Location on click of button*/
    public void LocationUpdate(View view) {
        if (mGoogleApiClient != null) {
            requestLocationUpdate();
            mPlaceName.setVisibility(View.VISIBLE);
            mSaveButton.setVisibility(View.VISIBLE);
            mCreateButton.setVisibility(View.GONE);
        }
    }

    /*Initiliaze GoogleAPI client*/
    public void buildGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /*Connect the GoogleAPI client on Start of the Activity*/
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    /*Disconnect the GoogleAPI client on stop of the Activity*/
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceiver(myReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

    /*Create Location Request for getting the Locations */
    public LocationRequest getLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL_TIME);
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
        mLocationRequest.setFastestInterval(INTERVAL_TIME);
        return mLocationRequest;
    }

    /*Create a Pending intent for broadcast receiver*/
    public PendingIntent getPendingIntent() {
        Intent intent = new Intent(MainActivity.this, LocationBroadcastReceiver.class);
        intent.setAction(BROADCAST_RECEIVER_ACTION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /*Create a LocationService for Location update requests*/
    public void requestLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, getLocationRequest(), getPendingIntent());
    }

    /*Remove Geofences previously added*/
    public void removeGeofencesButtonHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            //Remove all the details from the database
            getContentResolver().delete(LocationContract.LocationEntry.CONTENT_URI, null, null);

            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getPendingServiceIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            securityException.printStackTrace();
        }
    }

    /*Check permission for ACCESS_FINE_LOCATION*/
    public boolean checkPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /*Request Permssions for access fine location*/
    public void requestPermission() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Snackbar.make(
                    findViewById(R.id.main_layout),
                    "Location permission is necessary for functionlaity",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /*Check the permission result*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted. Kick off the process of building and connecting
                // GoogleApiClient.
                buildGoogleAPIClient();
            } else {
                Snackbar.make(
                        findViewById(R.id.main_layout),
                        "Permission was denied but is needed for core functionality",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    /*Connection related callbacks*/
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    /*Menu Creation*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (R.id.remove_geofences == item.getItemId()) {
            removeGeofencesButtonHandler();
        } else if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    /*Broadcast receiver class for location updates*/
    public static class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String string = "";
            List<Location> arrayList = new ArrayList<Location>();
            if (intent != null) {
                String str = intent.getAction();
                if (str.equals(MainActivity.BROADCAST_RECEIVER_ACTION)) {
                    LocationResult locationResult = LocationResult.extractResult(intent);
                    if (locationResult != null) {
                        arrayList = locationResult.getLocations();
                    }
                    for (Location location : arrayList) {
                        if (location != null) {
                            string += location.getLatitude() + " Latitude " + location.getLongitude();
                            Log.e("Check if its woeking", "Working");
                            listoflocations = new Location(location);

                        }
                    }

                }
            }
        }
    }
}
