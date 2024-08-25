/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sunco_cennik;

import java.io.Serializable;

/**
 *
 * @author Hubert
 */
public final class BlindColours implements Serializable {

    BlindColours() {

    }

    BlindColours(String colour) {
        setBoxOut(colour);
        setBoxIn(colour);
        setProfile(colour);
        setBotList(colour);
        setLeaders(colour);
        setAdapter(colour);
    }

    public boolean WholeColour() {
        return (boxOut == null ? boxIn == null : boxOut.equals(boxIn))
                && (boxOut == null ? profile == null : boxOut.equals(profile))
                && (boxOut == null ? botList == null : boxOut.equals(botList))
                && (boxOut == null ? leaders == null : boxOut.equals(leaders))
                && (boxOut == null ? adapter == null : boxOut.equals(adapter));
    }

    public String getBoxOut() {
        return boxOut;
    }

    public void setBoxOut(String boxOut) {
        this.boxOut = boxOut;
    }

    public String getBoxIn() {
        return boxIn;
    }

    public void setBoxIn(String boxIn) {
        this.boxIn = boxIn;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getBotList() {
        return botList;
    }

    public void setBotList(String botList) {
        this.botList = botList;
    }

    public String getLeaders() {
        return leaders;
    }

    public void setLeaders(String leaders) {
        this.leaders = leaders;
    }

    public String getAdapter() {
        return adapter;
    }

    public void setAdapter(String adapter) {
        this.adapter = adapter;
    }
    private String boxOut = "", boxIn = "", profile = "", botList = "", leaders = "", adapter = "";

}
