package com.example.noone.geofencingtest1;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.noone.geofencingtest1.data.LocationContract;

/**
 * Created by No One on 4/22/2017.
 */

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.LocationViewHolder> {

    final private LocationOnClickHandler mClickHanlder;
    private Context mContext;
    private Cursor mCursor;

    public interface LocationOnClickHandler{
        void onClick(String name, double lat, double lan);
    }

    public LocationRecyclerAdapter(Context context, LocationOnClickHandler locationOnClickHandler){
        mClickHanlder = locationOnClickHandler;
        mContext = context;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
        view.setFocusable(true);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        mCursor.moveToPosition(position);

       holder.PlaceName.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_PLACE_NAME)));
        double lat = mCursor.getDouble(mCursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_LATITUDE));
        double lang = mCursor.getDouble(mCursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_LONGITUDE));
        String latlng = "Lat "+lat + " Long "+lang;

       holder.LatLng.setText(latlng);
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView PlaceName;
        final TextView LatLng;

        LocationViewHolder(View view){
            super(view);
             PlaceName = (TextView)view.findViewById(R.id.Place_name_list);
             LatLng = (TextView)view.findViewById(R.id.latlang);

            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            double lat = mCursor.getDouble(mCursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_LATITUDE));
            double lang = mCursor.getDouble(mCursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_LONGITUDE));
            String placename =  mCursor.getString(mCursor.getColumnIndexOrThrow(LocationContract.LocationEntry.COLUMN_PLACE_NAME));
            mClickHanlder.onClick(placename,lat,lang);
        }
    }
}
