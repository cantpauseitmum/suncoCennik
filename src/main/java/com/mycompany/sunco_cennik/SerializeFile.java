/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sunco_cennik;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Hubert
 */
public class SerializeFile {

    public SerializeFile() {

    }

    public SerializeFile(File file, BlindList blindList) {
        try (FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(blindList);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e);
            throw new RuntimeException(e);
        } catch (IOException ioe) {
            System.out.println("Error while writing data: " + ioe);
        }
    }

    public BlindList getFromFile(File file) {
        BlindList blindList = null;
        try (FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis);) {
            blindList = (BlindList) ois.readObject();
        } catch (IOException ioe) {
            System.out.println(ioe);
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found: " + c);
        }
        return blindList;
    }
}
