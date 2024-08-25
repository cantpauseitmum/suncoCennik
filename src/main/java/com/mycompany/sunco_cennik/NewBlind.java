/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sunco_cennik;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JList;

/**
 *
 * @author Hubert
 */
public class NewBlind implements Serializable {

    public void setBlind(NewBlind blind) {
        blindModel = blind.blindModel;
        blindBox = blind.blindBox;
        blindHeight = blind.blindHeight;
        blindWidth = blind.blindWidth;
        blindHeightWithBox = blind.blindHeightWithBox;
        blindWeight = blind.blindWeight;
        blindCount = blind.blindCount;
        blindColourType = blind.blindColourType;
        blindColour = blind.blindColour;
        blindProfile = blind.blindProfile;
        blindPrice = blind.blindPrice;
        blindAddons = blind.blindAddons;
        blindExtras = blind.blindExtras;
        blindAuto = blind.blindAuto;
        simpleBlind = blind.simpleBlind;
        hiddenList = blind.hiddenList;
        hiddenList2 = blind.hiddenList2;
    }

    public JList<String> getHiddenList() {
        return hiddenList;
    }

    public void setHiddenList(JList<String> hiddenList) {
        this.hiddenList = hiddenList;
    }

    public JList<String> getHiddenList2() {
        return hiddenList2;
    }

    public void setHiddenList2(JList<String> hiddenList2) {
        this.hiddenList2 = hiddenList2;
    }

    public double getBlindBox() {
        return blindBox;
    }

    public void setBlindBox(double blindBox) {
        this.blindBox = blindBox;
    }

    public BlindPriceList getBlindModel() {
        return blindModel;
    }

    public void setBlindModel(BlindPriceList blindModel) {
        this.blindModel = blindModel;
    }

    public double getBlindHeight() {
        return blindHeight;
    }

    public void setBlindHeight() {
        this.blindHeight = getBlindHeightWithBox() - getBlindBox();
    }

    public double getBlindWidth() {
        return blindWidth;
    }

    public void setBlindWidth(double blindWidth) {
        this.blindWidth = blindWidth;
    }

    public double getBlindWeight() {
        return blindWeight;
    }

    public int getBlindCount() {
        return blindCount;
    }

    public void setBlindCount(int blindCount) {
        this.blindCount = blindCount;
    }

    public BlindPriceList getBlindColourType() {
        return blindColourType;
    }

    public void setBlindColourType(BlindPriceList blindColourType) {
        this.blindColourType = blindColourType;
    }

    public String getBlindProfile() {
        return blindProfile;
    }

    public void setBlindProfile(String blindProfile) {
        this.blindProfile = blindProfile;
    }

    public double getBlindPrice() {
        return blindPrice;
    }

    public void setBlindPrice(double blindPrice) {
        this.blindPrice = blindPrice;
    }

    public ArrayList<BlindPriceList> getBlindAddons() {
        return blindAddons;
    }

    public void setBlindAddons(ArrayList<BlindPriceList> blindAddons) {
        this.blindAddons = blindAddons;
    }

    public ArrayList<BlindPriceList> getBlindExtras() {
        return blindExtras;
    }

    public void setBlindExtras(ArrayList<BlindPriceList> blindExtras) {
        this.blindExtras = blindExtras;
    }

    public BlindPriceList getBlindAuto() {
        return blindAuto;
    }

    public void setBlindAuto(BlindPriceList blindAuto) {
        this.blindAuto = blindAuto;
    }

    public BlindColours getBlindColour() {
        return blindColour;
    }

    public void setBlindColour(String blindColour) {
        this.blindColour = new BlindColours(blindColour);
    }

    public double getBlindHeightWithBox() {
        return blindHeightWithBox;
    }

    public void setBlindHeightWithBox(double blindHeightWithBox) {
        this.blindHeightWithBox = blindHeightWithBox;
    }

    public void setMinBlindHeightWithBox() {
        this.blindHeightWithBox = 1.5 / getBlindWidth();
    }

    public void setBlindWeight() {
        this.blindWeight = getBlindHeightWithBox() * getBlindWidth() * 3;
    }

    public ArrayList<SimpleBlind> getSimpleBlind() {
        return simpleBlind;
    }

    public void setSimpleBlind(ArrayList<SimpleBlind> simpleBlind) {
        this.simpleBlind = simpleBlind;
    }

    private BlindPriceList blindModel;
    private double blindBox;
    private double blindHeight;
    private double blindWidth;
    private double blindHeightWithBox;
    private double blindWeight;
    private int blindCount;
    private BlindPriceList blindColourType;
    public BlindColours blindColour = new BlindColours();
    private String blindProfile = "PA39";
    private double blindPrice;
    private ArrayList<BlindPriceList> blindAddons;
    private ArrayList<BlindPriceList> blindExtras;
    private BlindPriceList blindAuto;
    private ArrayList<SimpleBlind> simpleBlind;
    private javax.swing.JList<String> hiddenList = new JList();
    private javax.swing.JList<String> hiddenList2 = new JList();
}
