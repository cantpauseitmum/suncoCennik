/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sunco_cennik;

/**
 *
 * @author Hubert
 */
public class ComponentPrice {

    public static double squareMeter(double price, double width, double height) {
        return width * height * price;
    }

    public static double runningMeter(double price, double parameter) {
        return price * parameter;
    }

    public static double percent(double price, double percent) {
        if (price * (percent / 100) < 100) {
            return 100;
        }
        return price * (percent / 100);
    }

    public static double quantity(double price, double howMuch) {
        return price * howMuch;
    }

}
