package com.svizeautomation.app.screens;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.LayoutRipple;
import com.rey.material.widget.Spinner;
import com.svizeautomation.app.BluetoothDeviceConnector;
import com.svizeautomation.app.BluetoothViewer;
import com.svizeautomation.app.DeviceConnector;
import com.svizeautomation.app.MessageHandler;
import com.svizeautomation.app.MessageHandlerImpl;
import com.svizeautomation.app.MockLineByLineConnector;
import com.svizeautomation.app.NullDeviceConnector;
import com.svizeautomation.app.R;
import com.svizeautomation.app.adapters.NevigationDrawerListAdapter;
import com.svizeautomation.app.adapters.RoomSpinnerAdatper;
import com.svizeautomation.app.constants.Constants;
import com.svizeautomation.app.listeners.DialogCallback;
import com.svizeautomation.app.model.LocalModel;
import com.svizeautomation.app.pojo.RoomDo;
import com.svizeautomation.app.pojo.SwitchDo;

import java.util.ArrayList;

/**
 * Created by sid on 08/12/2015.
 */
public class HomeScreenActivity extends AppCompatActivity {


    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final int MENU_SETTINGS = 4;

    public static final int CREATE_ROOM = 0;
    public static final int EDIT_ROOM = 1;
    public static final int ABOUT_SCREN = 2;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;

    private ListView drawerListView;
    private NevigationDrawerListAdapter nevigationDrawerListAdapter;
    private LinearLayout bodyLinearLayout;

    /**
     * show room members
     */
    private DeviceConnector mDeviceConnector = new NullDeviceConnector();
    private boolean connected;
    private ImageView btConnectedImageView;


    private Spinner roomSpinner;
    private TextView macAddressTextView;
    private LinearLayout switchsContainerBodyLayout;
    private Switch allSwitch;
    private ProgressBar loadingProgressBar;

    private RoomDo currentRoom = null;
    private ArrayList<LinearLayout> switchesLinearLayoutList = new ArrayList<LinearLayout>();
    private RoomSpinnerAdatper roomSpinnerAdatper;

    private boolean isConnecting = false;

    private RelativeLayout switchesRelativeLayout;
    private LinearLayout allSwitchContainerLinearLayout;
    private LinearLayout remoteLinearLayout;

    private ImageView btnTop;
    private ImageView btnBottom;
    private ImageView btnLeft;
    private ImageView btnRight;

    private ImageView deleteImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalModel.getInstance().setCurrentActivity(this);
        setContentView(R.layout.home_screen_layout);
        setToolBar();
        initViews();
        initDrawerLayout();
        createShowRoomViews();
    }

    private void setBodyView(View view) {
        bodyLinearLayout.removeAllViews();
        bodyLinearLayout.addView(view);
    }

    private void createShowRoomViews() {
        //if no room found
        if (LocalModel.getInstance().getRoomDoArrayList().size() == 0) {
            setBodyView(getBlankView());
        } else {
            makeShowRoomView();
        }
    }


    private void makeShowRoomView() {
        View view = getLayoutInflater().inflate(R.layout.show_room_layout, null);
        setBodyView(view);
        initViewsOfShowRoom();
        registerEventsOfShowRoom();
        setRoomsInSpinnerAdapterOfShowRoom();
    }

    private void initViewsOfShowRoom() {
        roomSpinner = (Spinner) findViewById(R.id.roomSpinner);
        macAddressTextView = (TextView) findViewById(R.id.macAddressTextView);
        macAddressTextView = (TextView) findViewById(R.id.macAddressTextView);
        switchsContainerBodyLayout = (LinearLayout) findViewById(R.id.switchsContainerBodyLayout);

        allSwitchContainerLinearLayout = (LinearLayout) findViewById(R.id.allSwitchContainerLinearLayout);
        switchesRelativeLayout = (RelativeLayout) findViewById(R.id.switchesRelativeLayout);
        remoteLinearLayout = (LinearLayout) findViewById(R.id.remoteLinearLayout);

        allSwitch = (Switch) findViewById(R.id.allSwitch);

        btConnectedImageView = (ImageView) findViewById(R.id.btConnectedImageView);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

        btnTop = (ImageView) findViewById(R.id.btnTop);
        btnBottom = (ImageView) findViewById(R.id.btnBottom);
        btnLeft = (ImageView) findViewById(R.id.btnLeft);
        btnRight = (ImageView) findViewById(R.id.btnRight);

        deleteImageView = (ImageView) findViewById(R.id.deleteImageView);

    }

    private void showRemoteSwitchLayout() {
        allSwitchContainerLinearLayout.setVisibility(View.GONE);
        switchesRelativeLayout.setVisibility(View.GONE);
        remoteLinearLayout.setVisibility(View.VISIBLE);
    }

    private void showNormalSwitchLayouts() {
        allSwitchContainerLinearLayout.setVisibility(View.VISIBLE);
        switchesRelativeLayout.setVisibility(View.VISIBLE);
        remoteLinearLayout.setVisibility(View.GONE);
    }

    private void registerEventsOfShowRoom() {

        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteButtonClick();
            }
        });

        allSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                onAllSwitchCheckedChanged(isChecked);
            }
        });


        roomSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                setInfoOnUIAndConnectToBtMAC(true);
            }
        });

        btConnectedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtConnectedClick();
            }
        });

        btnTop.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    btnTop.setBackgroundDrawable(getResources().getDrawable(R.drawable.triangle_red));
                    sendMessage(currentRoom.getSwiches().get(0).getInputTextOn());

                    Toast.makeText(HomeScreenActivity.this, "Touch:" + currentRoom.getSwiches().get(0).getInputTextOn(), Toast.LENGTH_SHORT).show();

                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    btnTop.setBackgroundDrawable(HomeScreenActivity.this.getResources().getDrawable(R.drawable.triangle));
                    sendMessage(currentRoom.getSwiches().get(0).getInputTextOff());
                    Toast.makeText(HomeScreenActivity.this, "Touch Release:" + currentRoom.getSwiches().get(0).getInputTextOff(), Toast.LENGTH_SHORT).show();

                }
                return true;
            }
        });

        btnBottom.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    btnBottom.setBackgroundDrawable(HomeScreenActivity.this.getResources().getDrawable(R.drawable.triangle_red));
                    sendMessage(currentRoom.getSwiches().get(1).getInputTextOn());
                    Toast.makeText(HomeScreenActivity.this, "Touch:" + currentRoom.getSwiches().get(1).getInputTextOn(), Toast.LENGTH_SHORT).show();

                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    btnBottom.setBackgroundDrawable(HomeScreenActivity.this.getResources().getDrawable(R.drawable.triangle));
                    sendMessage(currentRoom.getSwiches().get(1).getInputTextOn());
                    Toast.makeText(HomeScreenActivity.this, "Touch Release:" + currentRoom.getSwiches().get(1).getInputTextOff(), Toast.LENGTH_SHORT).show();

                }
                return true;
            }
        });

        btnLeft.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    btnLeft.setBackgroundDrawable(HomeScreenActivity.this.getResources().getDrawable(R.drawable.triangle_red));
                    sendMessage(currentRoom.getSwiches().get(2).getInputTextOn());
                    Toast.makeText(HomeScreenActivity.this, "Touch:" + currentRoom.getSwiches().get(2).getInputTextOn(), Toast.LENGTH_SHORT).show();

                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    btnLeft.setBackgroundDrawable(HomeScreenActivity.this.getResources().getDrawable(R.drawable.triangle));
                    sendMessage(currentRoom.getSwiches().get(2).getInputTextOn());
                    Toast.makeText(HomeScreenActivity.this, "Touch Release:" + currentRoom.getSwiches().get(2).getInputTextOff(), Toast.LENGTH_SHORT).show();

                }
                return true;
            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    btnRight.setBackgroundDrawable(HomeScreenActivity.this.getResources().getDrawable(R.drawable.triangle_red));
                    sendMessage(currentRoom.getSwiches().get(3).getInputTextOn());
                    Toast.makeText(HomeScreenActivity.this, "Touch:" + currentRoom.getSwiches().get(3).getInputTextOn(), Toast.LENGTH_SHORT).show();

                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    btnRight.setBackgroundDrawable(HomeScreenActivity.this.getResources().getDrawable(R.drawable.triangle));
                    sendMessage(currentRoom.getSwiches().get(3).getInputTextOn());
                    Toast.makeText(HomeScreenActivity.this, "Touch Release:" + currentRoom.getSwiches().get(3).getInputTextOff(), Toast.LENGTH_SHORT).show();

                }
                return true;
            }
        });


    }


    private void onDeleteButtonClick() {
//        LocalModel.getInstance().showDiaog(this,dialogCallback,"Are You sure ","");
        Intent i = new Intent(this, PasswordActivity.class);
        startActivityForResult(i, Constants.REQUEST_CODE_DELETE_ROOM_PWD);

    }

    DialogCallback dialogCallback = new DialogCallback() {
        @Override
        public void onYesBtnClick(Object extras) {

        }

        @Override
        public void onNoBtnClick(Object extras) {

        }
    };

    private void setRoomsInSpinnerAdapterOfShowRoom() {
        roomSpinnerAdatper = new RoomSpinnerAdatper(this, android.R.layout.simple_spinner_item);
        roomSpinner.setAdapter(roomSpinnerAdatper);
    }


    private View getBlankView() {
        View view = getLayoutInflater().inflate(R.layout.no_room_to_control_layout, null);
        view.findViewById(R.id.toolbar).setVisibility(View.GONE);
        view.findViewById(R.id.createRoomBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCreateRoomActivity();
            }
        });
        return view;
    }

    public void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void refreshRooms() {
        LocalModel.getInstance().hideKeyboard(this);

        if (LocalModel.getInstance().getRoomDoArrayList().size() == 0) {
            roomSpinnerAdatper = null;
            setBodyView(getBlankView());
        } else if (roomSpinnerAdatper == null) {
            makeShowRoomView();
        } else {
            roomSpinnerAdatper.refreshAdatper();
            setInfoOnUIAndConnectToBtMAC(false);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    private void initViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerListView = (ListView) findViewById(R.id.drawerListView);
        bodyLinearLayout = (LinearLayout) findViewById(R.id.bodyLinearLayout);

        nevigationDrawerListAdapter = new NevigationDrawerListAdapter(this);
        drawerListView.setAdapter(nevigationDrawerListAdapter);
    }

    public void onItemClick(final int pos) {
        drawerLayout.closeDrawers();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (pos) {
                    case CREATE_ROOM:
                        launchCreateRoomActivity();
                        break;
                    case EDIT_ROOM:
                        if (LocalModel.getInstance().getRoomDoArrayList().size() > 0) {
                            launchEditRoomActivity();
                        } else {
                            Toast.makeText(HomeScreenActivity.this, "No Room Found to Edit, Please select Create option.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ABOUT_SCREN:
                        launchAboutScreen();
                        break;
                }
            }
        }, 350);

    }

    private void launchAboutScreen() {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    private void launchCreateRoomActivity() {
        Intent i = new Intent(this, CreateRoomActivity.class);
        startActivityForResult(i, Constants.REQUEST_CODE_CREATE_ROOM);
    }

    private void launchEditRoomActivity() {
        Intent i = new Intent(this, EditRoomActivity.class);
        startActivityForResult(i, Constants.REQUEST_CODE_EDIT_ROOM);
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

    private void disconnectBtAndRemoveCallback() {

    }


    private void onBtConnectedClick() {
        if (isConnecting) {
            Toast.makeText(HomeScreenActivity.this, "Connection Already in process.", Toast.LENGTH_LONG).show();
            return;
        }

        if (connected) {
            //disconnect also
            if (mDeviceConnector != null)
                mDeviceConnector.disconnect();

            Toast.makeText(HomeScreenActivity.this, "Disconnected.", Toast.LENGTH_LONG).show();
        } else {
            connectBlueToothMAC();
        }

    }

    private void onAllSwitchCheckedChanged(boolean checked) {
        if (checked) {
            sendMessage("y");
            LocalModel.getInstance().editCommonSwitchState(currentRoom, SwitchDo.SWITCH_ON);
        } else {
            sendMessage("z");
            LocalModel.getInstance().editCommonSwitchState(currentRoom, SwitchDo.SWITCH_OFF);
        }

        changeStateOfEachButton(checked);
    }


    private void setInfoOnUIAndConnectToBtMAC(boolean connectBt) {

        switchesLinearLayoutList.clear();
        currentRoom = LocalModel.getInstance().getRoomDoArrayList().get(roomSpinner.getSelectedItemPosition());
        macAddressTextView.setText(currentRoom.getBtMacAddress());

        if (currentRoom.getType() == RoomDo.ROOM_TYPE_REMOTE_CONTROLLOER) {
            showRemoteSwitchLayout();
        } else {
            showNormalSwitchLayouts();
        }

        if (currentRoom.isHaveCommonSwitch() && currentRoom.getType() == RoomDo.ROOM_TYPE_SWITCH_BOARD) {
            allSwitchContainerLinearLayout.setVisibility(View.VISIBLE);
        } else {
            allSwitchContainerLinearLayout.setVisibility(View.GONE);
        }


        if (currentRoom.getCommonSwitchState() == SwitchDo.SWITCH_ON) {
            allSwitch.setChecked(true);
        } else {
            allSwitch.setChecked(false);
        }

        if (connected) {
            btConnectedImageView.setBackgroundColor(getResources().getColor(R.color.green));
        } else {
            btConnectedImageView.setBackgroundColor(getResources().getColor(R.color.redd));
        }


        //feeding switches in layout
        feedSwitchesInLayout(currentRoom);

        if (connectBt) {
            //connect to bt mac
            connectBlueToothMAC();

        }
    }

    private boolean enableBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BluetoothViewer.REQUEST_ENABLE_BT);
            return false;
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LocalModel.getInstance().hideKeyboard(this);
        switch (requestCode) {
            case BluetoothViewer.REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    connectBlueToothMAC();
                } else {
                    Toast.makeText(HomeScreenActivity.this, "Please allow Bluetooth connection.", Toast.LENGTH_LONG).show();
                }
                break;
            case Constants.REQUEST_CODE_CREATE_ROOM:
                if (resultCode == Activity.RESULT_OK) {
                    refreshRooms();
                }
                break;
            case Constants.REQUEST_CODE_EDIT_ROOM:
                if (resultCode == Activity.RESULT_OK) {
                    refreshRooms();
                }
                break;
            case Constants.REQUEST_CODE_DELETE_ROOM_PWD:
                if (resultCode == Activity.RESULT_OK) {

                    //delete room
                    LocalModel.getInstance().deleteRoom(roomSpinner.getSelectedItemPosition());

                    //disconnect also
                    if (mDeviceConnector != null)
                        mDeviceConnector.disconnect();

                    //refresh rooms
                    refreshRooms();
                }
                break;
        }
    }

    /**
     * method to connect with the bluetooth adapter using a MAC address
     */
    private void connectBlueToothMAC() {

        if (!enableBluetooth()) {
            return;
        }
        loadingProgressBar.setVisibility(View.VISIBLE);
        mDeviceConnector.disconnect();

        MessageHandler messageHandler = new MessageHandlerImpl(mHandler);
        switch (currentRoom.getBtConnectorType()) {
            case RoomDo.CONNECTOR_TYPE_MOCK:
                mDeviceConnector = new MockLineByLineConnector(
                        messageHandler, HomeScreenActivity.this.getAssets(), currentRoom.getBtMacAddress());
                break;
            case RoomDo.CONNECTOR_TYPE_BLUETOOH:
                System.out.println(">>sid bt mac add=" + currentRoom.getBtMacAddress());
                mDeviceConnector = new BluetoothDeviceConnector(
                        messageHandler, currentRoom.getBtMacAddress());
                break;
            default:
                return;
        }
        mDeviceConnector.connect();

    }


    private void feedSwitchesInLayout(RoomDo roomDo) {
        switchsContainerBodyLayout.removeAllViews();
        for (int i = 0; i < roomDo.getSwiches().size(); i++) {
            SwitchDo switchDo = roomDo.getSwiches().get(i);

            LinearLayout newSwitchLayout = (LinearLayout) HomeScreenActivity.this.getLayoutInflater().inflate(R.layout.switches_list_layout, null);
            TextView switchNameTextView = (TextView) newSwitchLayout.findViewById(R.id.switchName);
            SwitchCompat switchMaterialButton = (SwitchCompat) newSwitchLayout.findViewById(R.id.switchMaterialButton);

            switchMaterialButton.setThumbResource(R.drawable.basic_switch_thumb);
            switchMaterialButton.setTrackResource(R.drawable.basic_switch_track);

            View separatorView = (View) newSwitchLayout.findViewById(R.id.separatorView);

            if (i == roomDo.getSwiches().size() - 1) {
                separatorView.setVisibility(View.GONE);
            } else {
                separatorView.setVisibility(View.VISIBLE);
            }

            if (switchDo.getState() == SwitchDo.SWITCH_ON) {
                switchMaterialButton.setChecked(true);
            } else {
                switchMaterialButton.setChecked(false);
            }

            switchNameTextView.setText(switchDo.getName().toUpperCase());

            switchsContainerBodyLayout.addView(newSwitchLayout);
            switchesLinearLayoutList.add(newSwitchLayout);

            final int index = i;
            switchMaterialButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onSwitchCheckedChanged(index, isChecked);
                }
            });


        }

    }

    private void onSwitchCheckedChanged(int index, boolean checked) {

        String inputText = "";
        if (checked) {
            LocalModel.getInstance().editSwitchState(currentRoom, index, SwitchDo.SWITCH_ON);
            inputText = currentRoom.getSwiches().get(index).getInputTextOn();
        } else {
            LocalModel.getInstance().editSwitchState(currentRoom, index, SwitchDo.SWITCH_OFF);
            inputText = currentRoom.getSwiches().get(index).getInputTextOff();
        }

        if (needToSendTellBTAboutBtnchange) {
            System.out.println(">>on SwitchCheckedChanged checked=" + checked + " input=" + inputText);
            sendMessage(inputText);
        } else {
            System.out.println(">>on SwitchCheckedChanged action doesn't send");
        }


    }

    boolean needToSendTellBTAboutBtnchange = true;

    private void changeStateOfEachButton(final boolean enable) {
        needToSendTellBTAboutBtnchange = false;
        for (int i = 0; i < switchesLinearLayoutList.size(); i++) {
            LinearLayout newSwitchLayout = switchesLinearLayoutList.get(i);
            SwitchCompat switchMaterialButton = (SwitchCompat) newSwitchLayout.findViewById(R.id.switchMaterialButton);
            if (enable) {
                switchMaterialButton.setChecked(true);
            } else {
                switchMaterialButton.setChecked(false);
            }
        }
        needToSendTellBTAboutBtnchange = true;
    }

    private void sendMessage(CharSequence chars) {
        if (chars.length() > 0) {
            mDeviceConnector.sendAsciiMessage(chars);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageHandler.MSG_CONNECTED:
                    connected = true;
                    isConnecting = false;
                    roomSpinner.setEnabled(true);
                    btConnectedImageView.setBackgroundColor(getResources().getColor(R.color.green));
                    System.out.println(">>sid connected");
                    loadingProgressBar.setVisibility(View.GONE);
                    break;
                case MessageHandler.MSG_CONNECTING:
                    connected = false;
                    isConnecting = true;
                    roomSpinner.setEnabled(false);
                    btConnectedImageView.setBackgroundColor(getResources().getColor(R.color.redd));
                    System.out.println(">>sid connected MSG_CONNECTING");
                    break;
                case MessageHandler.MSG_NOT_CONNECTED:
                    connected = false;
                    isConnecting = false;
                    roomSpinner.setEnabled(true);
                    btConnectedImageView.setBackgroundColor(getResources().getColor(R.color.redd));
                    System.out.println(">>sid connected MSG_NOT_CONNECTED");
                    break;
                case MessageHandler.MSG_CONNECTION_FAILED:
                    connected = false;
                    isConnecting = false;
                    roomSpinner.setEnabled(true);
                    btConnectedImageView.setBackgroundColor(getResources().getColor(R.color.redd));
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(HomeScreenActivity.this, "Bluetooth Connection Failed, Try again.", Toast.LENGTH_LONG).show();
                    System.out.println(">>sid connected MSG_CONNECTION_FAILED");

                    break;
                case MessageHandler.MSG_CONNECTION_LOST:
                    connected = false;
                    isConnecting = false;
                    roomSpinner.setEnabled(true);
                    btConnectedImageView.setBackgroundColor(getResources().getColor(R.color.redd));
                    System.out.println(">>sid connected MSG_CONNECTION_LOST");
                    break;

            }
        }
    };
}
