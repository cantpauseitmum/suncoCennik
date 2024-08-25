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
public class SimpleBlind implements Serializable {

    public double getBlindBox() {
        return blindBox;
    }

    public void setBlindBox(double blindBox) {
        this.blindBox = blindBox;
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

    public double getBlindHeightWithBox() {
        return blindHeightWithBox;
    }

    public void setBlindHeightWithBox(double blindHeightWithBox) {
        this.blindHeightWithBox = blindHeightWithBox;
    }

    public double getBlindWeight() {
        return blindWeight;
    }

    public void setBlindWeight() {
        this.blindWeight = getBlindHeightWithBox() * getBlindWidth() * 3;
    }

    public String getBlindProfile() {
        return blindProfile;
    }

    public void setBlindProfile(String blindProfile) {
        this.blindProfile = blindProfile;
    }

    public BlindPriceList getBlindAuto() {
        return blindAuto;
    }

    public void setBlindAuto(BlindPriceList blindAuto) {
        this.blindAuto = blindAuto;
    }

    public double getSimpleBlindPrice() {
        return simpleBlindPrice;
    }

    public void setSimpleBlindPrice(double simpleBlindPrice) {
        this.simpleBlindPrice = simpleBlindPrice;
    }

    public void setMinBlindHeightWithBox() {
        this.blindHeightWithBox = 1.5 / getBlindWidth();
    }

    private double blindBox;
    private double blindHeight;
    private double blindWidth;
    private double blindHeightWithBox;
    private double blindWeight;
    private String blindProfile = "PA39";
    private BlindPriceList blindAuto;
    private double simpleBlindPrice;
}
