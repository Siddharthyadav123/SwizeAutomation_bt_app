package com.svizeautomation.app.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rey.material.widget.Spinner;
import com.svizeautomation.app.R;
import com.svizeautomation.app.model.LocalModel;
import com.svizeautomation.app.pojo.RoomDo;
import com.svizeautomation.app.pojo.SwitchDo;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by siddharth on 1/13/2016.
 */
public class EditRoomActivity extends RoomsBaseActivity {
    private RoomDo currentRoom = null;
    private Spinner roomSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalModel.getInstance().setCurrentActivity(this);
        setContentView(R.layout.edit_room_fragment_layout);
        setToolBar("Edit Room");
        initViews();
        registerEvents();
        clearMembers();
        setRoomsInSpinnerAdapter();
        addSwitchsAndRoomInfoAsPerSelectedSpinner(0);
    }


    public void initViews() {
        super.initViews();
        roomSpinner = (Spinner) findViewById(R.id.roomSpinner);
    }

    private void setRoomsInSpinnerAdapter() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, collectRoomsNamesList());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setAdapter(dataAdapter);
    }

    private List<String> collectRoomsNamesList() {
        List<String> roomsNameList = new ArrayList<String>();
        ArrayList<RoomDo> roomsList = LocalModel.getInstance().getRoomDoArrayList();
        for (int i = 0; i < roomsList.size(); i++) {
            roomsNameList.add(roomsList.get(i).getName());
        }
        return roomsNameList;
    }


    public void registerEvents() {
        super.registerEvents();
        roomSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                addSwitchsAndRoomInfoAsPerSelectedSpinner(position);
            }
        });

    }

    @Override
    public void onSaveRoomBtnClick() {
        if (isValidate(false)) {
            launchPwdActivity();
        }
    }

    @Override
    public void pwdDailogResult(boolean isCorrect) {
        editRoom();
    }

    private void addSwitchsAndRoomInfoAsPerSelectedSpinner(int roomIndex) {

        switchContainerBodyLayout.removeAllViews();
        switchesLinearLayoutList.clear();

        currentRoom = LocalModel.getInstance().getRoomDoArrayList().get(roomIndex);

        roomNameEditText.setText(currentRoom.getName());
        macAddressEditText.setText(currentRoom.getBtMacAddress());


        if (currentRoom.isHaveCommonSwitch()) {
            allowAllControlCheckBox.setChecked(true);
        } else {
            allowAllControlCheckBox.setChecked(false);
        }

        if (currentRoom.getType() == RoomDo.ROOM_TYPE_REMOTE_CONTROLLOER) {
            remoteCheckBox.setChecked(true);
        } else {
            remoteCheckBox.setChecked(false);
        }


        for (int i = 0; i < currentRoom.getSwiches().size(); i++) {
            LinearLayout newSwitchLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.new_switch_item_layout, null);
            EditText switchEditText = (EditText) newSwitchLayout.findViewById(R.id.switchEditText);
            EditText inputPinONEditText = (EditText) newSwitchLayout.findViewById(R.id.inputPinONEditText);
            EditText inputPinOFFEditText = (EditText) newSwitchLayout.findViewById(R.id.inputPinOFFEditText);

            switchEditText.setHint("Switch " + (switchesLinearLayoutList.size() + 1));
            switchEditText.setText(currentRoom.getSwiches().get(i).getName());
            inputPinONEditText.setText(currentRoom.getSwiches().get(i).getInputTextOn());
            inputPinOFFEditText.setText(currentRoom.getSwiches().get(i).getInputTextOff());

            switchesLinearLayoutList.add(newSwitchLayout);
            switchContainerBodyLayout.addView(newSwitchLayout);
            switchCounterTextView.setText(switchesLinearLayoutList.size() + "");
        }


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

    boolean isRoomCreated = false;

    public void editRoom() {
        RoomDo editedRoom = new RoomDo();
        editedRoom.setRoomId(currentRoom.getRoomId());
        editedRoom.setName(roomNameEditText.getText().toString().trim().toUpperCase());
        editedRoom.setBtConnectorType(btConnectorType);
        editedRoom.setBtMacAddress(macAddressEditText.getText().toString().trim().toUpperCase());
        editedRoom.setHaveCommonSwitch(allowAllControlCheckBox.isCheck());

        if (remoteCheckBox.isCheck()) {
            editedRoom.setType(RoomDo.ROOM_TYPE_REMOTE_CONTROLLOER);
        } else {
            editedRoom.setType(RoomDo.ROOM_TYPE_SWITCH_BOARD);
        }

        ArrayList<SwitchDo> switchDoList = collectSwitchs();
        RealmList<SwitchDo> reamMSwitchs = new RealmList<SwitchDo>();
        reamMSwitchs.addAll(switchDoList);

        editedRoom.getSwiches().clear();
        editedRoom.setSwiches(reamMSwitchs);
        LocalModel.getInstance().editRoom(editedRoom);
        Toast.makeText(this, editedRoom.getName() + " Edited Successfully.", Toast.LENGTH_LONG).show();
        isRoomCreated = true;
        finish();
    }

    @Override
    public void finish() {
        LocalModel.getInstance().hideKeyboard(this);
        if (isRoomCreated) {
            setResult(Activity.RESULT_OK);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        super.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
