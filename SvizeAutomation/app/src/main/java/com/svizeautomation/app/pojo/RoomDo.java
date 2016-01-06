package com.svizeautomation.app.pojo;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sid on 08/12/2015.
 */
public class RoomDo extends RealmObject {

    public static final byte ROOM_TYPE_SWITCH_BOARD = 0;
    public static final byte ROOM_TYPE_REMOTE_CONTROLLOER = 1;

    public static final byte CONNECTOR_TYPE_BLUETOOH = 0;
    public static final byte CONNECTOR_TYPE_MOCK = 1;

    @PrimaryKey
    private int roomId;
    private String name;
    private String btMacAddress;
    private int btConnectorType = CONNECTOR_TYPE_BLUETOOH;
    private int type;
    private boolean haveCommonSwitch;
    private int commonSwitchState;

    private RealmList<SwitchDo> swiches = new RealmList<SwitchDo>();


    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBtMacAddress() {
        return btMacAddress;
    }

    public void setBtMacAddress(String btMacAddress) {
        this.btMacAddress = btMacAddress;
    }

    public boolean isHaveCommonSwitch() {
        return haveCommonSwitch;
    }

    public void setHaveCommonSwitch(boolean haveCommonSwitch) {
        this.haveCommonSwitch = haveCommonSwitch;
    }

    public RealmList<SwitchDo> getSwiches() {
        return swiches;
    }

    public void setSwiches(RealmList<SwitchDo> swiches) {
        this.swiches = swiches;
    }

    public int getBtConnectorType() {
        return btConnectorType;
    }

    public void setBtConnectorType(int btConnectorType) {
        this.btConnectorType = btConnectorType;
    }

    public int getCommonSwitchState() {
        return commonSwitchState;
    }

    public void setCommonSwitchState(int commonSwitchState) {
        this.commonSwitchState = commonSwitchState;
    }
}
