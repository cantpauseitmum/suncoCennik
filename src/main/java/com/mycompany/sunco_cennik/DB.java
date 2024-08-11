/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sunco_cennik;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Hubert
 */
public class DB {

    public static Connection connect() throws SQLException {
        String testUrl = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=sunco123";
        String googleUrl = "jdbc:postgresql:///"
                + DatabaseConfig.getDbProjectName() + "?cloudSqlInstance="
                + DatabaseConfig.getDbInstanceName()
                + "&socketFactory=com.google.cloud.sql.postgres.SocketFactory&user="
                + DatabaseConfig.getDbUsername() + "&password="
                + DatabaseConfig.getDbPassword();
        String jdbcUrl = googleUrl;
        try {
            return DriverManager.getConnection(jdbcUrl);
        } catch (SQLException e) {
            ErrorLog.logError(e);
            return null;
        }
    }
}
