/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sunco_cennik;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Hubert
 */
public final class DBTransfer {

    Connection con;

    public DBTransfer() {

    }

    public DBTransfer(String statement, BlindList blindList) throws SQLException, IOException {
        addToTable(blindList, statement);
    }

    public DBTransfer(SuncoMainWindow suncoMainWindow, BlindList blindList, String conType) throws IOException, SQLException, UnsupportedLookAndFeelException {
        if (blindList.offerName == null || blindList.offerName.isBlank()) {
            OfferName offerNameFrame = new OfferName(suncoMainWindow, conType, blindList);
            offerNameFrame.setLocationRelativeTo(null);
            offerNameFrame.setVisible(true);
        } else if (ifExists(blindList.offerName)) {
            addToTable(blindList, "UPDATE rolety SET name = ?,data = ? WHERE name = '" + blindList.offerName + "'");
            suncoMainWindow.setEnabled(true);
        } else {
            addToTable(blindList, "INSERT INTO rolety (name, data) VALUES (?, ?)");
            suncoMainWindow.setEnabled(true);
        }
    }

    public void addToTable(BlindList blindList, String statement) throws SQLException, IOException {
        this.con = DB.connect();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(blindList.blindList);
        oos.flush();
        baos.close();
        oos.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        PreparedStatement ps;
        ps = con.prepareStatement(statement);
        ps.setString(1, blindList.offerName);
        ps.setBinaryStream(2, bais);
        ps.executeUpdate();
        ps.close();
        bais.close();
    }

    public ArrayList<DBList> getDBList() throws SQLException {
        ArrayList<DBList> dbList = new ArrayList();
        this.con = DB.connect();
        ResultSet rs;
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("SELECT id, name FROM rolety");
            rs = ps.executeQuery();
            while (rs.next()) {
                dbList.add(new DBList(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            ErrorLog.logError(e);
        }
        return dbList;
    }

    public BlindList getBlindList(int id) throws SQLException, IOException, ClassNotFoundException {
        BlindList blindList = new BlindList();
        blindList.blindList = new ArrayList();
        this.con = DB.connect();
        PreparedStatement ps = con.prepareStatement("SELECT name, data FROM rolety WHERE id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs != null) {
            while (rs.next()) {
                String name = rs.getString("name");
                byte[] data = rs.getBytes("data");
                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
                    blindList.blindList = (ArrayList<NewBlind>) ois.readObject();
                    blindList.offerName = name;
                }
            }
            rs.close();
        }
        return blindList;
    }

    public boolean ifExists(String name) throws SQLException {
        this.con = DB.connect();
        PreparedStatement ps;
        ps = con.prepareStatement("SELECT EXISTS(SELECT 1 FROM rolety WHERE name = '" + name + "');");
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getBoolean(1);
    }

}
