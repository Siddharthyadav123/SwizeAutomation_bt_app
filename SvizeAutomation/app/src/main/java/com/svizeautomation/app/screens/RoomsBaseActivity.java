package com.svizeautomation.app.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.views.LayoutRipple;
import com.svizeautomation.app.DeviceListActivity;
import com.svizeautomation.app.R;
import com.svizeautomation.app.constants.Constants;
import com.svizeautomation.app.model.LocalModel;
import com.svizeautomation.app.pojo.RoomDo;

import java.util.ArrayList;

/**
 * Created by siddharth on 1/14/2016.
 */
public abstract class RoomsBaseActivity extends AppCompatActivity {
    protected RoomDo roomDo = null;

    protected LayoutRipple plusButtonRippleLayout;
    protected LayoutRipple minusButtonRippleLayout;
    protected TextView switchCounterTextView;
    protected LinearLayout switchContainerBodyLayout;
    protected CheckBox allowAllControlCheckBox;
    protected CheckBox remoteCheckBox;
    protected EditText roomNameEditText;
    protected EditText macAddressEditText;
    protected LayoutRipple searchMacAdressRippleLayout;
    protected LayoutRipple saveRoomLayoutRipple;
    protected int btConnectorType = 0;
    protected TextView onHeaderTextview;
    protected TextView offHeaderTextview;
    protected ArrayList<LinearLayout> switchesLinearLayoutList = new ArrayList<LinearLayout>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void clearMembers() {
        switchesLinearLayoutList.clear();
    }

    public void setToolBar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle(title);
    }

    protected void initViews() {
        plusButtonRippleLayout = (LayoutRipple) findViewById(R.id.plusButtonRippleLayout);
        minusButtonRippleLayout = (LayoutRipple) findViewById(R.id.minusButtonRippleLayout);
        switchCounterTextView = (TextView) findViewById(R.id.switchCounterTextView);
        switchContainerBodyLayout = (LinearLayout) findViewById(R.id.switchContainerBodyLayout);

        allowAllControlCheckBox = (CheckBox) findViewById(R.id.allowAllControlCheckBox);
        remoteCheckBox = (CheckBox) findViewById(R.id.remoteCheckBox);


        roomNameEditText = (EditText) findViewById(R.id.roomNameEditText);
        macAddressEditText = (EditText) findViewById(R.id.macAddressEditText);
        searchMacAdressRippleLayout = (LayoutRipple) findViewById(R.id.searchMacAdressRippleLayout);
        saveRoomLayoutRipple = (LayoutRipple) findViewById(R.id.saveRoomLayoutRipple);

        saveRoomLayoutRipple.setRippleSpeed(100);

        onHeaderTextview = (TextView) findViewById(R.id.onHeaderTextview);
        offHeaderTextview = (TextView) findViewById(R.id.offHeaderTextview);

    }

    protected void registerEvents() {
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
                onSaveRoomBtnClick();
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

    public abstract void onSaveRoomBtnClick();

    protected void startDeviceListActivity() {
        Intent intent = new Intent(this, DeviceListActivity.class);
        intent.putExtra(DeviceListActivity.EXTRA_MOCK_DEVICES_ENABLED,
                true);
        startActivityForResult(intent, HomeScreenActivity.REQUEST_CONNECT_DEVICE);
    }

    protected void onRemoteDeSeleted() {
        onHeaderTextview.setText("ON Input");
        offHeaderTextview.setText("OFF Input");

        allowAllControlCheckBox.setEnabled(true);

        plusButtonRippleLayout.setEnabled(true);
        minusButtonRippleLayout.setEnabled(true);

        switchContainerBodyLayout.removeAllViews();
        switchesLinearLayoutList.clear();

        addSwitch(null);
    }

    protected void onRemoteSelected() {
        onHeaderTextview.setText("Touch Input");
        offHeaderTextview.setText("Release Input");

        allowAllControlCheckBox.setEnabled(false);

        plusButtonRippleLayout.setEnabled(false);
        minusButtonRippleLayout.setEnabled(false);

          /* Adding 4 layouts */
        switchContainerBodyLayout.removeAllViews();
        switchesLinearLayoutList.clear();

        String[] btnTextHint = {"VOL+", "VOL-", "CH+", "CH-"};
        for (int i = 0; i < 4; i++) {
            addSwitch(btnTextHint[i]);
        }
    }

    protected void addSwitch(String hintText) {
        LinearLayout newSwitchLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.new_switch_item_layout, null);
        EditText switchEditText = (EditText) newSwitchLayout.findViewById(R.id.switchEditText);
        EditText inputPinONEditText = (EditText) newSwitchLayout.findViewById(R.id.inputPinONEditText);
        EditText inputPinOFFEditText = (EditText) newSwitchLayout.findViewById(R.id.inputPinOFFEditText);

        if (hintText != null) {
            switchEditText.setText(hintText);
        } else {
            switchEditText.setHint("Switch " + (switchesLinearLayoutList.size() + 1));
        }

        switchesLinearLayoutList.add(newSwitchLayout);
        switchContainerBodyLayout.addView(newSwitchLayout);
        switchCounterTextView.setText(switchesLinearLayoutList.size() + "");
    }

    protected void removeSwich() {
        switchContainerBodyLayout.removeView(switchesLinearLayoutList.get(switchContainerBodyLayout.getChildCount() - 1));
        switchesLinearLayoutList.remove(switchesLinearLayoutList.size() - 1);
        switchCounterTextView.setText(switchesLinearLayoutList.size() + "");
    }

    protected boolean isValidate(boolean isCreate) {

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

        if (isCreate) {
            ArrayList<RoomDo> roomList = LocalModel.getInstance().getRoomDoArrayList();
            for (int i = 0; i < roomList.size(); i++) {

                if (roomList.get(i).getName().equalsIgnoreCase(roomNameEditText.getText().toString().trim())) {
                    roomNameEditText.setError("A room Already Present with this Name, Please change the Name.");
                    roomNameEditText.requestFocus();
                    return false;
                }

//                if (roomList.get(i).getBtMacAddress().equalsIgnoreCase(macAddressEditText.getText().toString().trim())) {
//                    Toast.makeText(this, "This MAC address Already Assign To the Another Room, Please Re-verify.", Toast.LENGTH_LONG).show();
//                    return false;
//                }

            }
        }


        return true;
    }

    public abstract void pwdDailogResult(boolean isCorrect);

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_CREATE_ROOM_PWD:
                if (resultCode == Activity.RESULT_OK) {
                    pwdDailogResult(true);
                } else {
                    pwdDailogResult(false);
                }
                break;
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

    protected void launchPwdActivity() {
        Intent i = new Intent(this, PasswordActivity.class);
        startActivityForResult(i, Constants.REQUEST_CODE_CREATE_ROOM_PWD);
    }
}
