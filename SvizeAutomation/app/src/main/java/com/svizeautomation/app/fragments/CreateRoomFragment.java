package com.svizeautomation.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.views.LayoutRipple;
import com.svizeautomation.app.DeviceListActivity;
import com.svizeautomation.app.R;
import com.svizeautomation.app.model.LocalModel;
import com.svizeautomation.app.pojo.RoomDo;
import com.svizeautomation.app.pojo.SwitchDo;
import com.svizeautomation.app.screens.HomeScreenActivity;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by sid on 10/12/2015.
 */
public class CreateRoomFragment extends Fragment {

    private RoomDo roomDo = null;

    private LayoutRipple plusButtonRippleLayout;
    private LayoutRipple minusButtonRippleLayout;
    private TextView switchCounterTextView;
    private LinearLayout switchContainerBodyLayout;

    private CheckBox allowAllControlCheckBox;
    private CheckBox remoteCheckBox;
    private EditText roomNameEditText;
    private EditText macAddressEditText;
    private LayoutRipple searchMacAdressRippleLayout;
    private LayoutRipple saveRoomLayoutRipple;

    private int btConnectorType = 0;

    private TextView onHeaderTextview;
    private TextView offHeaderTextview;


    ArrayList<LinearLayout> switchesLinearLayoutList = new ArrayList<LinearLayout>();


    public static CreateRoomFragment newInstance() {
        CreateRoomFragment createRoomFragment = new CreateRoomFragment();
        Bundle bundle = new Bundle();
        createRoomFragment.setArguments(bundle);
        return createRoomFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_room_fragment_layout, container, false);
        initViews(view);
        registerEvents();
        clearMembers();
        addSwitch(null);
        return view;

    }

    private void clearMembers() {
        switchesLinearLayoutList.clear();
    }

    private void initViews(View view) {
        plusButtonRippleLayout = (LayoutRipple) view.findViewById(R.id.plusButtonRippleLayout);
        minusButtonRippleLayout = (LayoutRipple) view.findViewById(R.id.minusButtonRippleLayout);
        switchCounterTextView = (TextView) view.findViewById(R.id.switchCounterTextView);
        switchContainerBodyLayout = (LinearLayout) view.findViewById(R.id.switchContainerBodyLayout);

        allowAllControlCheckBox = (CheckBox) view.findViewById(R.id.allowAllControlCheckBox);
        remoteCheckBox = (CheckBox) view.findViewById(R.id.remoteCheckBox);


        roomNameEditText = (EditText) view.findViewById(R.id.roomNameEditText);
        macAddressEditText = (EditText) view.findViewById(R.id.macAddressEditText);
        searchMacAdressRippleLayout = (LayoutRipple) view.findViewById(R.id.searchMacAdressRippleLayout);
        saveRoomLayoutRipple = (LayoutRipple) view.findViewById(R.id.saveRoomLayoutRipple);

        saveRoomLayoutRipple.setRippleSpeed(5);

        onHeaderTextview = (TextView) view.findViewById(R.id.onHeaderTextview);
        offHeaderTextview = (TextView) view.findViewById(R.id.offHeaderTextview);

    }

    private void registerEvents() {
        plusButtonRippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchesLinearLayoutList.size() < 9) {
                    addSwitch(null);
                }
            }
        });

        minusButtonRippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchesLinearLayoutList.size() > 1) {
                    removeSwich();
                }
            }
        });

        searchMacAdressRippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDeviceListActivity();
            }
        });

        saveRoomLayoutRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveRoomButtonClick();
            }
        });


        remoteCheckBox.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(CheckBox view, boolean check) {
                if (check) {
                    onRemoteSelected();
                } else {
                    onRemoteDeSeleted();
                }
            }
        });
    }


    private void onRemoteDeSeleted() {
        onHeaderTextview.setText("ON Input");
        offHeaderTextview.setText("OFF Input");

        allowAllControlCheckBox.setEnabled(true);

        plusButtonRippleLayout.setEnabled(true);
        minusButtonRippleLayout.setEnabled(true);

        switchContainerBodyLayout.removeAllViews();
        switchesLinearLayoutList.clear();

        addSwitch(null);
    }

    private void onRemoteSelected() {
        onHeaderTextview.setText("Touch Input");
        offHeaderTextview.setText("Release Input");

        allowAllControlCheckBox.setEnabled(false);

        plusButtonRippleLayout.setEnabled(false);
        minusButtonRippleLayout.setEnabled(false);

          /* Adding 4 layouts */
        switchContainerBodyLayout.removeAllViews();
        switchesLinearLayoutList.clear();

        String[] btnTextHint = {"Top button", "Bottom button", "Left button", "Right button"};
        for (int i = 0; i < 4; i++) {
            addSwitch(btnTextHint[i]);
        }
    }


    private void onSaveRoomButtonClick() {
        if (isValidate()) {
            createRoom();
        }
    }


    private void addSwitch(String hintText) {
        LinearLayout newSwitchLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.new_switch_item_layout, null);
        EditText switchEditText = (EditText) newSwitchLayout.findViewById(R.id.switchEditText);
        EditText inputPinONEditText = (EditText) newSwitchLayout.findViewById(R.id.inputPinONEditText);
        EditText inputPinOFFEditText = (EditText) newSwitchLayout.findViewById(R.id.inputPinOFFEditText);

        if (hintText != null) {
            switchEditText.setHint(hintText);
        } else {
            switchEditText.setHint("Switch " + (switchesLinearLayoutList.size() + 1));
        }

        switchesLinearLayoutList.add(newSwitchLayout);
        switchContainerBodyLayout.addView(newSwitchLayout);
        switchCounterTextView.setText(switchesLinearLayoutList.size() + "");
    }

    private void removeSwich() {
        switchContainerBodyLayout.removeView(switchesLinearLayoutList.get(switchContainerBodyLayout.getChildCount() - 1));
        switchesLinearLayoutList.remove(switchesLinearLayoutList.size() - 1);
        switchCounterTextView.setText(switchesLinearLayoutList.size() + "");
    }

    private boolean isValidate() {

        if (roomNameEditText.getText().toString().trim().length() == 0) {
            roomNameEditText.requestFocus();
            roomNameEditText.setError("Please Enter Room Name");
            return false;
        }

        if (macAddressEditText.getText().toString().trim().length() == 0) {
            macAddressEditText.requestFocus();
            macAddressEditText.setError("Please Enter/Search Bluetooth Adapters MAC Adress");
            return false;
        }

        for (int i = 0; i < switchesLinearLayoutList.size(); i++) {
            LinearLayout swichLayout = switchesLinearLayoutList.get(i);
            EditText switchEditText = (EditText) swichLayout.findViewById(R.id.switchEditText);
            EditText inputPinONEditText = (EditText) swichLayout.findViewById(R.id.inputPinONEditText);
            EditText inputPinOFFEditText = (EditText) swichLayout.findViewById(R.id.inputPinOFFEditText);

            if (switchEditText.getText().toString().trim().length() == 0) {
                switchEditText.requestFocus();
                switchEditText.setError("Please provide a name to the switch");
                return false;
            }

            if (inputPinONEditText.getText().toString().trim().length() == 0) {
                inputPinONEditText.requestFocus();
                inputPinONEditText.setError("Pin input is must");
                return false;
            }

            if (inputPinOFFEditText.getText().toString().trim().length() == 0) {
                inputPinOFFEditText.requestFocus();
                inputPinOFFEditText.setError("Pin input is must");
                return false;
            }

        }

        ArrayList<RoomDo> roomList = LocalModel.getInstance().getRoomDoArrayList();
        for (int i = 0; i < roomList.size(); i++) {

            if (roomList.get(i).getName().equalsIgnoreCase(roomNameEditText.getText().toString().trim())) {
                roomNameEditText.setError("A room Already Present with this Name, Please change the Name.");
                roomNameEditText.requestFocus();
                return false;
            }

            if (roomList.get(i).getBtMacAddress().equalsIgnoreCase(macAddressEditText.getText().toString().trim())) {
                Toast.makeText(getActivity(), "This MAC address Already Assign To the Another Room, Please Re-verify.", Toast.LENGTH_LONG).show();
                return false;
            }

        }


        return true;
    }

    private ArrayList<SwitchDo> collectSwitchs() {
        ArrayList<SwitchDo> switchDoList = new ArrayList<SwitchDo>();
        for (int i = 0; i < switchesLinearLayoutList.size(); i++) {
            LinearLayout swichLayout = switchesLinearLayoutList.get(i);
            EditText switchEditText = (EditText) swichLayout.findViewById(R.id.switchEditText);
            EditText inputPinONEditText = (EditText) swichLayout.findViewById(R.id.inputPinONEditText);
            EditText inputPinOFFEditText = (EditText) swichLayout.findViewById(R.id.inputPinOFFEditText);

            SwitchDo switchDo = new SwitchDo();
            switchDo.setName(switchEditText.getText().toString().trim().toUpperCase());
            switchDo.setInputTextOn(inputPinONEditText.getText().toString().trim());
            switchDo.setInputTextOff(inputPinOFFEditText.getText().toString().trim());

            switchDo.setState(SwitchDo.SWITCH_OFF);
            switchDoList.add(switchDo);
        }
        return switchDoList;
    }

    public void createRoom() {
        roomDo = new RoomDo();
//        roomDo.setRoomId(LocalModel.getInstance().getRoomDoArrayList().size());
        roomDo.setName(roomNameEditText.getText().toString().trim().toUpperCase());
        roomDo.setBtConnectorType(btConnectorType);
        roomDo.setBtMacAddress(macAddressEditText.getText().toString().trim().toUpperCase());
        roomDo.setHaveCommonSwitch(allowAllControlCheckBox.isCheck());

        if (remoteCheckBox.isCheck()) {
            roomDo.setType(RoomDo.ROOM_TYPE_REMOTE_CONTROLLOER);
        } else {
            roomDo.setType(RoomDo.ROOM_TYPE_SWITCH_BOARD);
        }

        ArrayList<SwitchDo> switchDoList = collectSwitchs();

        RealmList<SwitchDo> reamMSwitchs = new RealmList<SwitchDo>();
        reamMSwitchs.addAll(switchDoList);

        roomDo.setSwiches(reamMSwitchs);
        LocalModel.getInstance().addRoom(roomDo);
        Toast.makeText(getActivity(), roomDo.getName() + " Created Successfully.", Toast.LENGTH_LONG).show();
//        ((HomeScreenActivity) getActivity()).showFragment(HomeScreenActivity.FRAGMENT_SHOW_ROOM, true, LocalModel.getInstance().getRoomDoArrayList().size() - 1);
    }

    private void startDeviceListActivity() {
        Intent intent = new Intent(getContext(), DeviceListActivity.class);
        intent.putExtra(DeviceListActivity.EXTRA_MOCK_DEVICES_ENABLED,
                true);
        startActivityForResult(intent, HomeScreenActivity.REQUEST_CONNECT_DEVICE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case HomeScreenActivity.REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {

                    String connectorTypeMsgId = DeviceListActivity.Message.DeviceConnectorType
                            .toString();
                    DeviceListActivity.ConnectorType connectorType = (DeviceListActivity.ConnectorType) data
                            .getSerializableExtra(connectorTypeMsgId);
                    switch (connectorType) {
                        case Mock:
                            String filenameMsgId = DeviceListActivity.Message.MockFilename
                                    .toString();
                            String filename = data.getStringExtra(filenameMsgId);
                            System.out.println(">>onActivity result  filenameMsgId= " + filenameMsgId);
                            System.out.println(">>onActivity result  filename= " + filename);
                            macAddressEditText.setText(filename);
                            btConnectorType = RoomDo.CONNECTOR_TYPE_MOCK;
                            break;
                        case Bluetooth:
                            String addressMsgId = DeviceListActivity.Message.BluetoothAddress
                                    .toString();
                            String address = data.getStringExtra(addressMsgId);
                            System.out.println(">>onActivity result  addressMsgId= " + addressMsgId);
                            System.out.println(">>onActivity result  address= " + address);
                            macAddressEditText.setText(address);
                            btConnectorType = RoomDo.CONNECTOR_TYPE_BLUETOOH;

                            break;
                        default:
                            return;
                    }
                }
                break;
        }
    }
}