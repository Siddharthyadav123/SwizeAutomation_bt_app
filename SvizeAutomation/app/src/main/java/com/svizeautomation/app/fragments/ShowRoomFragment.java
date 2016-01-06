package com.svizeautomation.app.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.svizeautomation.app.adapters.RoomSpinnerAdatper;
import com.svizeautomation.app.model.LocalModel;
import com.svizeautomation.app.pojo.RoomDo;
import com.svizeautomation.app.pojo.SwitchDo;
import com.svizeautomation.app.screens.HomeScreenActivity;
import com.svizeautomation.app.util.MySwitch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sid on 10/12/2015.
 */
public class ShowRoomFragment extends Fragment {

    /**
     * Bt members
     */
    private DeviceConnector mDeviceConnector = new NullDeviceConnector();
    private boolean connected;
    private LayoutRipple btConnectedRippleLayout;


    private Spinner roomSpinner;
    private TextView macAddressTextView;
    private LinearLayout switchsContainerBodyLayout;
    private LinearLayout allSwitchContainerLinearLayout;
    private MySwitch allSwitch;
    private ProgressBar loadingProgressBar;

    private RoomDo currentRoom = null;
    private ArrayList<LinearLayout> switchesLinearLayoutList = new ArrayList<LinearLayout>();
    private View view = null;
    private boolean isFragmentAttached = false;
    private RoomSpinnerAdatper roomSpinnerAdatper;

    private boolean isConnecting = false;

    public static ShowRoomFragment newInstance() {
        ShowRoomFragment showRoomFragment = new ShowRoomFragment();
        Bundle bundle = new Bundle();
        showRoomFragment.setArguments(bundle);
        return showRoomFragment;
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isFragmentAttached = true;
        if (LocalModel.getInstance().getRoomDoArrayList().size() == 0) {
            view = inflater.inflate(R.layout.no_room_to_control_layout, container, false);
            view.findViewById(R.id.createRoomBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeScreenActivity) getActivity()).showFragment(HomeScreenActivity.FRAGMENT_CREATE_ROOM, true, 0);
                }
            });
        } else {
            if (view == null) {
                view = makeShowRoomView(container, inflater);
            } else {
                //after creating first room
                if (roomSpinner == null) {
                    view = makeShowRoomView(container, inflater);
                } else {
                    roomSpinnerAdatper.refreshAdatper();
                    setInfoOnUIAndConnectToBtMAC(false);
                }

            }

        }

        return view;
    }

    private View makeShowRoomView(ViewGroup container, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.show_room_layout, container, false);
        initViews(view);
        registerEvents();
        setRoomsInSpinnerAdapter();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFragmentAttached = false;
    }

    private void initViews(View view) {
        roomSpinner = (Spinner) view.findViewById(R.id.roomSpinner);
        macAddressTextView = (TextView) view.findViewById(R.id.macAddressTextView);
        macAddressTextView = (TextView) view.findViewById(R.id.macAddressTextView);
        switchsContainerBodyLayout = (LinearLayout) view.findViewById(R.id.switchsContainerBodyLayout);
        allSwitchContainerLinearLayout = (LinearLayout) view.findViewById(R.id.allSwitchContainerLinearLayout);
        allSwitch = (MySwitch) view.findViewById(R.id.allSwitch);
        btConnectedRippleLayout = (LayoutRipple) view.findViewById(R.id.btConnectedRippleLayout);
        loadingProgressBar = (ProgressBar) view.findViewById(R.id.loadingProgressBar);
    }

    private void setRoomsInSpinnerAdapter() {
        roomSpinnerAdatper = new RoomSpinnerAdatper(getActivity(), android.R.layout.simple_spinner_item);
        roomSpinner.setAdapter(roomSpinnerAdatper);
    }

    private List<String> collectRoomsNamesList() {
        List<String> roomsNameList = new ArrayList<String>();
        ArrayList<RoomDo> roomsList = LocalModel.getInstance().getRoomDoArrayList();
        for (int i = 0; i < roomsList.size(); i++) {
            roomsNameList.add(roomsList.get(i).getName());
        }
        return roomsNameList;
    }

    public void setRoomSelected(int pos) {
        if (roomSpinner != null) {
            System.out.println(">>inside pos=" + pos);
//            roomSpinner.setSelection(pos);
        }

    }

    private void registerEvents() {
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

        btConnectedRippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtConnectedClick();
            }
        });

    }

    private void onBtConnectedClick() {
        if (isConnecting) {
            Toast.makeText(getActivity(), "Connection Already in process.", Toast.LENGTH_LONG).show();
            return;
        }


        if (connected) {
            Toast.makeText(getActivity(), "Already Connected", Toast.LENGTH_LONG).show();
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

        if (currentRoom.isHaveCommonSwitch()) {
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
            btConnectedRippleLayout.setBackgroundColor(getResources().getColor(R.color.green));
        } else {
            btConnectedRippleLayout.setBackgroundColor(getResources().getColor(R.color.redd));
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

        switch (requestCode) {
            case BluetoothViewer.REQUEST_ENABLE_BT: {
                if (resultCode == Activity.RESULT_OK) {
                    connectBlueToothMAC();
                } else {
                    Toast.makeText(getActivity(), "Please allow Bluetooth connection.", Toast.LENGTH_LONG).show();
                }
            }
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
                        messageHandler, getActivity().getAssets(), currentRoom.getBtMacAddress());
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

            LinearLayout newSwitchLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.switches_list_layout, null);
            TextView switchNameTextView = (TextView) newSwitchLayout.findViewById(R.id.switchName);
            MySwitch switchMaterialButton = (MySwitch) newSwitchLayout.findViewById(R.id.switchMaterialButton);
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
            MySwitch switchMaterialButton = (MySwitch) newSwitchLayout.findViewById(R.id.switchMaterialButton);
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
                    if (isFragmentAttached) {
                        roomSpinner.setEnabled(true);
                        btConnectedRippleLayout.setBackgroundColor(getResources().getColor(R.color.green));
                        System.out.println(">>sid connected");
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                    break;
                case MessageHandler.MSG_CONNECTING:
                    connected = false;
                    isConnecting = true;
                    if (isFragmentAttached) {
                        roomSpinner.setEnabled(false);
                        btConnectedRippleLayout.setBackgroundColor(getResources().getColor(R.color.redd));
                    }
                    System.out.println(">>sid connected MSG_CONNECTING");
                    break;
                case MessageHandler.MSG_NOT_CONNECTED:
                    connected = false;
                    isConnecting = false;
                    if (isFragmentAttached) {
                        roomSpinner.setEnabled(true);
                        btConnectedRippleLayout.setBackgroundColor(getResources().getColor(R.color.redd));
                    }
                    System.out.println(">>sid connected MSG_NOT_CONNECTED");
                    break;
                case MessageHandler.MSG_CONNECTION_FAILED:
                    connected = false;
                    isConnecting = false;
                    if (isFragmentAttached) {
                        roomSpinner.setEnabled(true);
                        btConnectedRippleLayout.setBackgroundColor(getResources().getColor(R.color.redd));
                        loadingProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Bluetooth Connection Failed, Try again.", Toast.LENGTH_LONG).show();
                        System.out.println(">>sid connected MSG_CONNECTION_FAILED");
                    }

                    break;
                case MessageHandler.MSG_CONNECTION_LOST:
                    connected = false;
                    isConnecting = false;
                    if (isFragmentAttached) {
                        roomSpinner.setEnabled(true);
                        btConnectedRippleLayout.setBackgroundColor(getResources().getColor(R.color.redd));
                    }
                    System.out.println(">>sid connected MSG_CONNECTION_LOST");
                    break;

            }
        }
    };

}