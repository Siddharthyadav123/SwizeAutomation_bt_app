package com.svizeautomation.app.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.svizeautomation.app.R;
import com.svizeautomation.app.screens.HomeScreenActivity;

/**
 * Created by sid on 10/12/2015.
 */
public class NevigationDrawerListAdapter extends BaseAdapter {

    private Activity activity;
    private String[] nevigationStringArray = {"Create Room", "Edit Room", "About"};

    public NevigationDrawerListAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return nevigationStringArray.length;
    }

    @Override
    public Object getItem(int position) {
        return nevigationStringArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.nevigation_list_item_layout, null);
        }
        TextView itemNameTextView = (TextView) convertView.findViewById(R.id.itemNameTextView);
        itemNameTextView.setText(nevigationStringArray[position]);

        itemNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeScreenActivity) activity).onItemClick(position);
            }
        });

        return convertView;
    }
}
