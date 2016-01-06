package com.cocosw.bottomsheet.example;

import java.util.List;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.cocosw.bottomsheet.BottomSheet;
import com.cocosw.query.CocoQuery;
import com.materialapp.demo.R;

/**
 * Project: gradle
 * Created by LiaoKai(soarcn) on 2014/9/21.
 */
public class Main extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private ArrayAdapter<String> adapter;
    CocoQuery q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
        q = new CocoQuery(this);

        String[] items = new String[]{"From Xml", "Without Icon", "Dark Theme", "Grid", "Style", "Style from Theme", "ShareAction", "FullScreen"};
        q.id(R.id.listView)
                .adapter(adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, items))
                .itemClicked(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*Intent intent = new Intent(this, ListAcitivty.class).setFlags(position).putExtra("title", adapter.getItem(position));
        if (position == 5) {
            intent.putExtra("style", true);
        }
        startActivity(intent);*/
        showDialog(position);
    }
    
    @Override
    @Deprecated
    protected Dialog onCreateDialog(final int position, Bundle args) {
    	
    	BottomSheet sheet = getShareActions(new BottomSheet.Builder(this).grid().title("Share To "+adapter.getItem(position)),"Hello "+adapter.getItem(position)).build();
    	
    	/*BottomSheet sheet = new BottomSheet.Builder(this).sheet(R.menu.noicon).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Main.this.onClick(adapter.getItem(position), which);
            }
        }).build();*/
    	return sheet;
    }
    
    private BottomSheet.Builder getShareActions(BottomSheet.Builder builder, String text) {
        PackageManager pm = this.getPackageManager();

        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        final List<ResolveInfo> list = pm.queryIntentActivities(shareIntent, 0);

        for (int i = 0; i < list.size(); i++) {
            builder.sheet(i,list.get(i).loadIcon(pm),list.get(i).loadLabel(pm));
        }

        builder.listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityInfo activity = list.get(which).activityInfo;
                ComponentName name = new ComponentName(activity.applicationInfo.packageName,
                        activity.name);
                Intent newIntent = (Intent) shareIntent.clone();
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                newIntent.setComponent(name);
                startActivity(newIntent);
            }
        });
        return builder;
    }
    
    private void onClick(String name, int which) {
    	 q.toast("Share to Sachin");
	}

}
