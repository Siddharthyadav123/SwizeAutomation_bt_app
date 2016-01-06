package com.svizeautomation.app.pojo;

import io.realm.RealmObject;

/**
 * Created by sid on 08/12/2015.
 */
public class SwitchDo extends RealmObject {

    public static final byte SWITCH_ON = 0;
    public static final byte SWITCH_OFF = 1;

    private String name;
    private String inputTextOn;
    private String inputTextOff;
    private int state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInputTextOn() {
        return inputTextOn;
    }

    public void setInputTextOn(String inputTextOn) {
        this.inputTextOn = inputTextOn;
    }

    public String getInputTextOff() {
        return inputTextOff;
    }

    public void setInputTextOff(String inputTextOff) {
        this.inputTextOff = inputTextOff;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
