package com.svizeautomation.app.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.svizeautomation.app.listeners.DialogCallback;
import com.svizeautomation.app.pojo.RoomDo;

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

    public void deleteRoom(int index) {
        Realm myRealm = Realm.getInstance(getCurrentActivity());
        myRealm.beginTransaction();
        RealmResults<RoomDo> list = myRealm.where(RoomDo.class).findAll();
        list.get(index).removeFromRealm();
        //resetting room id
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRoomId(i);
        }
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

        if (list.size() > roomDo.getRoomId())
            list.get(roomDo.getRoomId()).setCommonSwitchState(state);

        myRealm.commitTransaction();
        roomDoArrayList = getRoomDoArrayList();
    }

    public void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void showDiaog(Context context, final DialogCallback dialogCallback, String title, String body) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context);
        builder.setCancelable(true);
        if (body != null)
            builder.setMessage(body);

        if (title != null)
            builder.setTitle(title);

        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialogCallback.onYesBtnClick(null);
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialogCallback.onNoBtnClick(null);
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
