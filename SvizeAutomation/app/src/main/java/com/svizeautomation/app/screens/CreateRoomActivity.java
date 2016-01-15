package com.svizeautomation.app.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.svizeautomation.app.R;
import com.svizeautomation.app.model.LocalModel;
import com.svizeautomation.app.pojo.RoomDo;
import com.svizeautomation.app.pojo.SwitchDo;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by siddharth on 1/13/2016.
 */
public class CreateRoomActivity extends RoomsBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalModel.getInstance().setCurrentActivity(this);
        setContentView(R.layout.create_room_fragment_layout);
        setToolBar("Create Room");
        initViews();
        registerEvents();
        clearMembers();
        addSwitch(null);
    }


    public void registerEvents() {
        super.registerEvents();
    }

    @Override
    public void onSaveRoomBtnClick() {
        if (isValidate(true)) {
            launchPwdActivity();
        }
    }

    @Override
    public void pwdDailogResult(boolean isCorrect) {
        if (isCorrect) {
            createRoom();
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

    public void createRoom() {
        roomDo = new RoomDo();
        roomDo.setRoomId(LocalModel.getInstance().getRoomDoArrayList().size());
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
        Toast.makeText(this, roomDo.getName() + " Created Successfully.", Toast.LENGTH_LONG).show();
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
