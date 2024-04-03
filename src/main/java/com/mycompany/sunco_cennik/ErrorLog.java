/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sunco_cennik;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Hubert
 */
public class ErrorLog {

    /**
     *
     * @param err
     */
    public static void logError(Exception err) {

        try {
            FileWriter New_File = new FileWriter("Error-log.txt", true);
            BufferedWriter Buff_File = new BufferedWriter(New_File);
            PrintWriter Print_File = new PrintWriter(Buff_File, true);
            Print_File.write(new Date().toString()); // Adding the date
            Print_File.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n"); // Formatted date
            err.printStackTrace(Print_File);
            Print_File.flush();
        } catch (IOException ie) {
            throw new RuntimeException("Cannot write the Exception to file", ie);
        }
    }
    
    public static void clearLogs() throws IOException{
        File  file = new File("Error-log.txt");
        if(!file.isFile()){
            file.createNewFile();
        }
        new FileWriter("Error-log.txt", false).close();
    }
}
