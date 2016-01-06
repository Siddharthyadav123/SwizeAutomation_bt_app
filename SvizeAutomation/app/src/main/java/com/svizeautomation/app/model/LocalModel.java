package com.svizeautomation.app.model;

import android.app.Activity;

import com.svizeautomation.app.application.BluetoothViewerFullApplication;
import com.svizeautomation.app.pojo.RoomDo;
import com.svizeautomation.app.pojo.SwitchDo;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sid on 11/12/2015.
 */
public class LocalModel {

    public static LocalModel localModel;

    private Activity currentActivity;
    private ArrayList<RoomDo> roomDoArrayList = new ArrayList<RoomDo>();


    public static LocalModel getInstance() {
        if (localModel == null) {
            localModel = new LocalModel();
        }
        return localModel;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public ArrayList<RoomDo> getRoomDoArrayList() {
        roomDoArrayList.clear();
        roomDoArrayList.addAll(Realm.getInstance(getCurrentActivity()).where(RoomDo.class).findAll());
        return roomDoArrayList;
    }

    public void setRoomDoArrayList(ArrayList<RoomDo> roomDoArrayList) {
        this.roomDoArrayList = roomDoArrayList;
    }

    public void addRoom(RoomDo roomDo) {
        roomDo.setRoomId(getRoomDoArrayList().size());
        Realm myRealm = Realm.getInstance(getCurrentActivity());
        myRealm.beginTransaction();
        myRealm.copyToRealm(roomDo);
        myRealm.commitTransaction();
        roomDoArrayList = getRoomDoArrayList();
    }

    public void editRoom(RoomDo roomDo) {
        Realm myRealm = Realm.getInstance(getCurrentActivity());
        myRealm.beginTransaction();
        myRealm.copyToRealmOrUpdate(roomDo);
        myRealm.commitTransaction();
        roomDoArrayList = getRoomDoArrayList();
    }

    public void editSwitchState(RoomDo roomDo, int pos, int state) {
        Realm myRealm = Realm.getInstance(getCurrentActivity());
        myRealm.beginTransaction();
        RealmResults<RoomDo> list = myRealm.where(RoomDo.class).findAll();
        list.get(roomDo.getRoomId()).getSwiches().get(pos).setState(state);
        myRealm.commitTransaction();
        roomDoArrayList = getRoomDoArrayList();
    }

    public void editCommonSwitchState(RoomDo roomDo, int state) {
        Realm myRealm = Realm.getInstance(getCurrentActivity());
        myRealm.beginTransaction();
        RealmResults<RoomDo> list = myRealm.where(RoomDo.class).findAll();
        list.get(roomDo.getRoomId()).setCommonSwitchState(state);
        myRealm.commitTransaction();
        roomDoArrayList = getRoomDoArrayList();
    }


}
