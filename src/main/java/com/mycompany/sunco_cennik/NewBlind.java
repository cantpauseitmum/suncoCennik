/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sunco_cennik;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Hubert
 */
public class NewBlind implements Serializable {

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

    public void setBlindHeight(double blindHeight) {
        this.blindHeight = blindHeight;
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

    public String getBlindColour() {
        return blindColour;
    }

    public void setBlindColour(String blindColour) {
        this.blindColour = blindColour;
    }

    public double getBlindHeightWithBox() {
        return blindHeightWithBox;
    }

    public void setBlindHeightWithBox() {
        this.blindHeightWithBox = getBlindHeight() + getBlindBox();
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
    private String blindColour;
    private String blindProfile = "PA39";
    private double blindPrice;
    private ArrayList<BlindPriceList> blindAddons;
    private ArrayList<BlindPriceList> blindExtras;
    private BlindPriceList blindAuto;
    private ArrayList<SimpleBlind> simpleBlind;

}
