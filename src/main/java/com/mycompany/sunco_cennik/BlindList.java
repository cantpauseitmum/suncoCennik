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
public class BlindList implements Serializable {

    public String offerName;
    public String clientName;
    public String customOrder = "Niestandardowe prośby";
    public Float customOrderValue = 0.0f;
    public ArrayList<NewBlind> blindList;

}
