package com.svizeautomation.app.screens;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.svizeautomation.app.R;
import com.svizeautomation.app.adapters.NevigationDrawerListAdapter;
import com.svizeautomation.app.fragments.CreateRoomFragment;
import com.svizeautomation.app.fragments.EditRoomFragment;
import com.svizeautomation.app.fragments.ShowRoomFragment;
import com.svizeautomation.app.model.LocalModel;

/**
 * Created by sid on 08/12/2015.
 */
public class HomeScreenActivity extends AppCompatActivity {


    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final int MENU_SETTINGS = 4;


    public static final int FRAGMENT_SHOW_ROOM = 0;
    public static final int FRAGMENT_CREATE_ROOM = 1;
    public static final int FRAGMENT_EDIT_ROOM = 2;


    private int currentFragment = FRAGMENT_SHOW_ROOM;

    private CreateRoomFragment createRoomFragment;
    private ShowRoomFragment showRoomFragment;
    private EditRoomFragment editRoomFragment;


    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;

    private ListView drawerListView;
    private NevigationDrawerListAdapter nevigationDrawerListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalModel.getInstance().setCurrentActivity(this);
        setContentView(R.layout.home_screen_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        initDrawerLayout();
        initFragments();
        showFragment(FRAGMENT_SHOW_ROOM, true, 0);
    }

    private void initFragments() {
        createRoomFragment = CreateRoomFragment.newInstance();
        showRoomFragment = ShowRoomFragment.newInstance();
        editRoomFragment = EditRoomFragment.newInstance();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void showFragment(int fragmentToSet, boolean doAnimate, int pos) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (fragmentToSet) {
            case FRAGMENT_SHOW_ROOM:
                ft.replace(R.id.fragmentContainerFrameLayout, showRoomFragment);
                showRoomFragment.setRoomSelected(pos);
                break;
            case FRAGMENT_CREATE_ROOM:
                createRoomFragment = CreateRoomFragment.newInstance();
                ft.replace(R.id.fragmentContainerFrameLayout, createRoomFragment);
                break;
            case FRAGMENT_EDIT_ROOM:
                editRoomFragment = EditRoomFragment.newInstance();
                ft.replace(R.id.fragmentContainerFrameLayout, editRoomFragment);
                break;
        }

        ft.commit();

    }

    private void initViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerListView = (ListView) findViewById(R.id.drawerListView);

        nevigationDrawerListAdapter = new NevigationDrawerListAdapter(this);
        drawerListView.setAdapter(nevigationDrawerListAdapter);


    }

    public void onItemClick(final int pos) {
        drawerLayout.closeDrawers();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (pos) {
                    case FRAGMENT_SHOW_ROOM:
                        showFragment(FRAGMENT_SHOW_ROOM, true, 0);
                        break;
                    case FRAGMENT_CREATE_ROOM:
                        showFragment(FRAGMENT_CREATE_ROOM, true, 0);
                        break;
                    case FRAGMENT_EDIT_ROOM:
                        showFragment(FRAGMENT_EDIT_ROOM, true, 0);
                        break;
                }
            }
        }, 350);

    }

    private void initDrawerLayout() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
