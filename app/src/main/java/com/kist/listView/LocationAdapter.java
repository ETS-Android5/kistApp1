package com.kist.listView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kist.kistapp1.R;

import java.util.ArrayList;

public class LocationAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<LocationData> Locations;

    public LocationAdapter(Context context, ArrayList<LocationData> data){
        mContext = context;
        Locations = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return Locations.size();
    }

    @Override
    public LocationData getItem(int i) {
        return Locations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.main_item, null);

        TextView LocationName = (TextView)view.findViewById(R.id.item_textView);

        LocationName.setText(Locations.get(position).getLocationName());
        return view;
    }
}
