package com.svizeautomation.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gc.materialdesign.views.LayoutRipple;
import com.svizeautomation.app.model.LocalModel;
import com.svizeautomation.app.pojo.RoomDo;

import java.util.ArrayList;

/**
 * Created by sid on 20/12/2015.
 */
public class RoomSpinnerAdatper extends ArrayAdapter {

    private ArrayList<RoomDo> roomsList = null;

    public RoomSpinnerAdatper(Context context, int resource) {
        super(context, resource);
        roomsList = LocalModel.getInstance().getRoomDoArrayList();
    }

//    public TextView getView(int position, View convertView, ViewGroup parent) {
//
//        TextView v = (TextView) super
//                .getView(position, convertView, parent);
//        v.setText(roomsList.get(position).getName());
//        return v;
//    }
//
//    public TextView getDropDownView(int position, View convertView,
//                                    ViewGroup parent) {
//
//        TextView v = (TextView) super
//                .getView(position, convertView, parent);
//        v.setText(roomsList.get(position).getName());
//        return v;
//    }


    @Override
    public int getCount() {
        return roomsList.size();
    }

    public void refreshAdatper() {
        roomsList = LocalModel.getInstance().getRoomDoArrayList();
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent,
                false);
        TextView text = (TextView) row.findViewById(android.R.id.text1);
        text.setPadding(10, 10, 10, 10);
        text.setText(roomsList.get(position).getName());
        return row;
    }


    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View row = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent,
                false);
        TextView text = (TextView) row.findViewById(android.R.id.text1);
        text.setPadding(10, 10, 10, 10);
        text.setText(roomsList.get(position).getName());
        return row;
    }

}
