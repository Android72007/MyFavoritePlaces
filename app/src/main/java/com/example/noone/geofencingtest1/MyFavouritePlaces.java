package com.example.noone.geofencingtest1;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noone.geofencingtest1.data.LocationContract;

public class MyFavouritePlaces extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, LocationRecyclerAdapter.LocationOnClickHandler{
    private Cursor cursor;
    private ListView listView;
    private RecyclerView mRecyclerView;
    private LocationRecyclerAdapter locationAdapter;
    private Uri geouri;
    private TextView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favourite_places);
        imageView = (TextView) findViewById(R.id.image_empty);
        imageView.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView)findViewById(R.id.list_view);
        mRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        locationAdapter = new LocationRecyclerAdapter(this,this);
        mRecyclerView.setAdapter(locationAdapter);
        getLoaderManager().initLoader(1,null,this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = {
                    LocationContract.LocationEntry._ID,
                    LocationContract.LocationEntry.COLUMN_PLACE_NAME,
                    LocationContract.LocationEntry.COLUMN_LATITUDE,
                    LocationContract.LocationEntry.COLUMN_LONGITUDE
            };
           return new CursorLoader(this, LocationContract.LocationEntry.CONTENT_URI, projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursor = data;
        if(data.moveToFirst()){
            imageView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        locationAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        locationAdapter.swapCursor(null);
    }

    @Override
    public void onClick(String name, double lat, double lan) {
        Toast.makeText(MyFavouritePlaces.this, name+lat+lan, Toast.LENGTH_SHORT).show();

        /* "geo:0,0?q=34.99,-106.61(Treasure)"*/
        String uri = "geo:0,0?q="+lat +","+lan+"("+name+")";
        geouri = Uri.parse(uri);
        Intent intent = new Intent(Intent.ACTION_VIEW, geouri);
        intent.setPackage("com.google.android.apps.maps");
        if(intent.resolveActivity(getPackageManager())!= null){
            startActivity(intent);
        }


    }
}
