/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sunco_cennik;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import java.io.IOException;

/**
 *
 * @author Hubert
 */
public class DatabaseConfig {

    private static final Properties properties = new Properties();

    static {
        File file = new File(System.getProperty("user.dir") + "\\db.properties");
        try (InputStream input = new FileInputStream(file)) {
            if (input == null) {
                file.createNewFile();
                String dbInfo = "db.project.name\ndb.instance.name\ndb.username\ndb.password\n";
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    byte[] strToBytes = dbInfo.getBytes();
                    outputStream.write(strToBytes);
                }
            }
            properties.load(input);
        } catch (IOException e) {
        }
    }

    public static String getDbProjectName() {
        return properties.getProperty("db.project.name");
    }

    public static String getDbInstanceName() {
        return properties.getProperty("db.instance.name");
    }
    public static String getDbUsername() {
        return properties.getProperty("db.username");
    }

    public static String getDbPassword() {
        return properties.getProperty("db.password");
    }

    public static void setDbProjectName(String dbProjectName) throws FileNotFoundException, IOException {
        properties.setProperty("db.project.name", dbProjectName);
        properties.store(new FileOutputStream("db.properties"), null);
    }

    public static void setDbInstanceName(String dbInstanceName) throws FileNotFoundException, IOException {
        properties.setProperty("db.instance.name", dbInstanceName);
        properties.store(new FileOutputStream("db.properties"), null);
    }
    public static void setDbUsername(String dbUsername) throws FileNotFoundException, IOException {
        properties.setProperty("db.username", dbUsername);
        properties.store(new FileOutputStream("db.properties"), null);
    }

    public static void setDbPassword(String dbPassword) throws FileNotFoundException, IOException {
        properties.setProperty("db.password", dbPassword);
        properties.store(new FileOutputStream("db.properties"), null);
    }
}
