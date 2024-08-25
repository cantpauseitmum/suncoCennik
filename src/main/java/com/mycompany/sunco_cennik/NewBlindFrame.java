/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.sunco_cennik;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.swing.DefaultListModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;

/**
 *
 * @author Hubert
 */
public final class NewBlindFrame extends javax.swing.JFrame {

    Connection connection;
    double blindPrice;
    NewBlind newBlind = new NewBlind();
    SuncoMainWindow suncoMainWindow;
    int blindIndex = -1;
    boolean profileLock = false;
    private javax.swing.JList<String> hiddenList = new JList();
    private javax.swing.JList<String> hiddenList2 = new JList();

    /**
     * Creates new form NewBlindFrame
     *
     * @throws java.sql.SQLException
     */
    public NewBlindFrame() throws SQLException {
        this.connection = DB.connect();
        initComponents();
        populateBlindTables();
        newBlind.setBlindCount((int) quantitySpinner.getValue());
        warningLabel.setVisible(false);
        minDimLabel.setVisible(false);
        manageColours(false);
    }

    public NewBlindFrame(SuncoMainWindow suncoMainWindow, int blindIndex, boolean update) throws SQLException {
        initComponents();
        this.blindIndex = blindIndex;
        this.connection = suncoMainWindow.con;
        this.suncoMainWindow = suncoMainWindow;
        populateBlindTables();
        newBlind.setBlind(suncoMainWindow.blindList.blindList.get(blindIndex));
        if (!update) {
            this.blindIndex = -1;
        }
        this.modelBox.setSelectedItem(setComboBox(modelBox.getModel(), newBlind.getBlindModel().getName()));
        this.colourBox.setSelectedItem(setComboBox(colourBox.getModel(), newBlind.getBlindColour().getBoxOut()));
        this.mechanicalBox.setSelectedItem(setComboBox(mechanicalBox.getModel(), newBlind.getBlindAuto().getName()));
        if (newBlind.getBlindAddons() != null) {
            this.hiddenList = newBlind.getHiddenList();
            selAccList.setModel(setModelCount(newBlind.getBlindAddons()));
        }
        if (newBlind.getBlindExtras() != null) {
            this.hiddenList2 = newBlind.getHiddenList2();
            selAccList2.setModel(setModelCount(newBlind.getBlindExtras()));
        }
        this.heightBoxField.setText(String.valueOf(1000 * newBlind.getBlindBox()));
        this.widthField.setText(String.valueOf(1000 * newBlind.getBlindWidth()));
        this.heightField.setText(String.valueOf(1000 * newBlind.getBlindHeightWithBox()));
        quantitySpinner.setValue(newBlind.getBlindCount());
        warningLabel.setVisible(false);
        minDimLabel.setVisible(false);
        manageColours(false);
    }

    public NewBlindFrame(SuncoMainWindow suncoMainWindow) throws SQLException {
        initComponents();
        this.suncoMainWindow = suncoMainWindow;
        this.connection = suncoMainWindow.con;
        populateBlindTables();
        newBlind.setBlindCount((int) quantitySpinner.getValue());
        warningLabel.setVisible(false);
        minDimLabel.setVisible(false);
        manageColours(false);
    }

    public String setComboBox(ComboBoxModel model, String name) {
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).toString().contains(name)) {
                return (String) model.getElementAt(i);
            }
        }
        return "";
    }

    public void setList(JList list, ArrayList<BlindPriceList> blindPriceList) {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < blindPriceList.size(); i++) {
            model.add(i, blindPriceList.get(i).getName());
        }
        list.setModel(model);
    }

    public void populateBlindTables() {
        populateComboBox("select model, opis from modele", modelBox);
        populateComboBox("select rodzaj, nazwa from kolory", colourBox);
        populateComboBox("select rodzaj, nazwa from kolory", jComboBox1);
        populateComboBox("select rodzaj, nazwa from kolory", jComboBox2);
        populateComboBox("select rodzaj, nazwa from kolory", jComboBox3);
        populateComboBox("select rodzaj, nazwa from kolory", jComboBox4);
        populateComboBox("select rodzaj, nazwa from kolory", jComboBox5);
        populateComboBox("select nazwa, moc from silniki", mechanicalBox);
        populateList("select nazwa from automatyka", fullAccList2);
        populateList("select nazwa from dopłaty", fullAccList);
    }

    public void populateComboBox(String sqlSatement, JComboBox comboBox) {
        ResultSet rs;
        PreparedStatement pst;
        try {
            pst = connection.prepareStatement(sqlSatement);
            rs = pst.executeQuery();
            while (rs.next()) {
                String name = rs.getString(1) + " - " + rs.getString(2);
                comboBox.addItem(name);

            }

        } catch (SQLException e) {
            ErrorLog.logError(e);
        }
    }

    public void populateList(String sqlSatement, JList jList) {
        DefaultListModel model = new DefaultListModel();
        ResultSet rs;
        PreparedStatement pst;
        try {
            pst = connection.prepareStatement(sqlSatement);
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addElement(rs.getString(1));
            }
            jList.setModel(model);
        } catch (SQLException e) {
            ErrorLog.logError(e);
        }
    }

    public String getColumnName(String dbName) {
        String dbColumnName = null;
        ResultSet rs;
        PreparedStatement pst;
        try {
            String tempStatement = "SELECT column_name FROM INFORMATION_SCHEMA.columns WHERE TABLE_NAME = N'" + dbName + "' order by ordinal_position offset 1 limit 1";
            pst = connection.prepareStatement(tempStatement);
            rs = pst.executeQuery();
            rs.next();
            dbColumnName = rs.getString(1);
        } catch (SQLException e) {
            ErrorLog.logError(e);
        }

        return dbColumnName;
    }

    private boolean isThere(ResultSet rs, String column) {
        try {
            rs.findColumn(column);
            return true;
        } catch (SQLException e) {
            ErrorLog.logError(e);
        }

        return false;
    }

    public BlindPriceList setBoxPrice(JComboBox comboBox, String dbName) {

        String[] resultArr = comboBox.getSelectedItem().toString().split(" - ");
        return setPrice(resultArr[0], resultArr[1], dbName);

    }

    public ArrayList<BlindPriceList> setListPrice(JList jList, String dbName) {
        ArrayList<BlindPriceList> blindPriceList = new ArrayList<>();
        for (int i = 0; i < jList.getModel().getSize(); i++) {
            blindPriceList.add(setPrice((String) jList.getModel().getElementAt(i), "", dbName));
        }
        return blindPriceList;
    }

    public BlindPriceList setPrice(String query, String query2, String dbName) {
        BlindPriceList blindPriceList = new BlindPriceList();
        PreparedStatement pst;
        ResultSet rs;
        try {
            if (dbName.equals("silniki")) {
                query = "select * from " + dbName + " where " + getColumnName(dbName) + "='" + query + "' and moc='" + query2 + "'";
            } else {
                query = "select * from " + dbName + " where " + getColumnName(dbName) + "='" + query + "'";
            }
            pst = connection.prepareStatement(query);
            rs = pst.executeQuery();
            rs.next();
            String priceType = "pa39cena";
            if (newBlind.getBlindProfile().equals("PA43") && isThere(rs, "pa43cena") && rs.getDouble("pa43cena") != 0) {
                priceType = "pa43cena";
            }
            if (dbName.equals("silniki")) {
                blindPriceList.setName(rs.getString(2) + " - " + rs.getString(3));
            } else {
                blindPriceList.setName(rs.getString(2));
            }
            blindPriceList.setPrice(rs.getDouble(priceType));
            blindPriceList.setPriceType(rs.getString("typceny"));
        } catch (SQLException e) {
            ErrorLog.logError(e);
        }
        return blindPriceList;
    }

    public double calculatePrice(BlindPriceList blindPriceList) {
        double boxPrice = 0;
        switch (blindPriceList.getPriceType()) {
            case "m2" -> {
                boxPrice = ComponentPrice.squareMeter(blindPriceList.getPrice(), newBlind.getBlindWidth(), newBlind.getBlindHeightWithBox());
                break;
            }
            case "mbS" -> {
                boxPrice = ComponentPrice.runningMeter(blindPriceList.getPrice(), newBlind.getBlindWidth());
                break;

            }
            case "mbW" -> {
//                System.out.println(newBlind.getBlindHeightWithBox() - newBlind.getBlindBox() + " | " + blindPriceList.getPrice() + " | " + ComponentPrice.runningMeter(blindPriceList.getPrice(), newBlind.getBlindHeightWithBox() - newBlind.getBlindBox()));
                boxPrice = ComponentPrice.runningMeter(blindPriceList.getPrice(), newBlind.getBlindHeightWithBox() - newBlind.getBlindBox());
                break;

            }
            case "szt" -> {
                boxPrice = ComponentPrice.quantity(blindPriceList.getPrice(), 1);
                break;

            }
            case "%" -> {
                boxPrice = ComponentPrice.percent(calculatePrice(newBlind.getBlindModel()), blindPriceList.getPrice());
                if (boxPrice < 100) {
                    boxPrice = 100;
                }
                break;

            }
        }
        return boxPrice;
    }

    public double calculatePrice(ArrayList<BlindPriceList> blindPriceList) {
        double listPrice = 0;
        if (blindPriceList == null) {
            return 0;
        }
        for (int i = 0; i < blindPriceList.size(); i++) {
            listPrice += calculatePrice(blindPriceList.get(i));
        }
        return listPrice;
    }

    public void setProfile() {
        if (newBlind.getBlindWidth() * newBlind.getBlindHeightWithBox() > 6 || newBlind.getBlindWidth() > 2.5) {
            newBlind.setBlindProfile("PA43");
        } else {
            newBlind.setBlindProfile("PA39");
        }
    }

    public void setBlindPrice() {
        if (!(heightField.getText().equals("") || widthField.getText().equals(""))) {
            double fullBlindPrice = 0;
            if (!profileLock) {
                setProfile();

            }
            profileTogglejButton.setText(newBlind.getBlindProfile());
            newBlind.setBlindModel(setBoxPrice(modelBox, "modele"));
            fullBlindPrice += calculatePrice(newBlind.getBlindModel());
//            System.out.println(fullBlindPrice);
            newBlind.setBlindColourType(setBoxPrice(colourBox, "kolory_ceny"));
            fullBlindPrice += calculatePrice(newBlind.getBlindColourType());
//            System.out.println(fullBlindPrice);
            newBlind.setBlindAuto(setBoxPrice(mechanicalBox, "silniki"));
            fullBlindPrice += calculatePrice(newBlind.getBlindAuto());
//            System.out.println(fullBlindPrice);
            newBlind.setBlindAddons(setListPrice(hiddenList, "dopłaty"));
            fullBlindPrice += calculatePrice(newBlind.getBlindAddons());
//            System.out.println(fullBlindPrice);
            newBlind.setBlindExtras(setListPrice(hiddenList2, "automatyka"));
            fullBlindPrice += calculatePrice(newBlind.getBlindExtras());
//            System.out.println(fullBlindPrice);
            String currency;
            if (suncoMainWindow.currency == 1) {
                currency = "zł";
            } else {
                currency = "€";
            }
            String vat;
            if (suncoMainWindow.vat == 1) {
                vat = "Netto";
            } else {
                vat = "Brutto";
            }
            double fakePrice = round(fullBlindPrice * suncoMainWindow.currency * suncoMainWindow.vat, 2);
            fullBlindPrice = round(fullBlindPrice, 2);
            this.blindPrice = fullBlindPrice;
            currentPriceLabelZloty.setText(String.valueOf(fakePrice * newBlind.getBlindCount()) + currency + " " + vat);
        }
    }

    public void setSemiBlindPrice() {
        double semiBlindPrice = 0;
        semiBlindPrice += calculatePrice(newBlind.getBlindAddons());
        for (BlindPriceList blindPriceList : newBlind.getBlindAddons()) {
            if (blindPriceList.getName().equals("PRZENIESIENIE NAPĘDU")) {
                semiBlindPrice = semiBlindPrice - 100;
            }
        }
        semiBlindPrice += calculatePrice(newBlind.getBlindExtras());
        semiBlindPrice = round(semiBlindPrice, 2);
        newBlind.setBlindPrice(semiBlindPrice);
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public int isDivided(JList jList) {
        int divided = 0;
        for (int i = 0; i < jList.getModel().getSize(); i++) {
            if (jList.getModel().getElementAt(i).toString().equals("PODZIAŁ ROLET")) {
                divided++;
            }
        }
        return divided;
    }

    public DefaultListModel setModelCount(ArrayList<BlindPriceList> blindPriceList) {
        List tempList = new ArrayList();
        for (BlindPriceList list : blindPriceList) {
            tempList.add(list.getName());
        }
        DefaultListModel dlm = new DefaultListModel();
        HashSet<String> duplicates = new HashSet<>(tempList);
        int i = 0;
        for (String duplicate : duplicates) {
            dlm.add(i, duplicate + " :  " + Collections.frequency(tempList, duplicate));
            i++;
        }
        return dlm;
    }

    public void manageColours(boolean change) {
        if (change) {
            jLabel2.setText("Skrzynka Zew:");
        } else {
            jLabel2.setText("Całość:");
        }

        jLabel3.setVisible(change);
        jLabel4.setVisible(change);
        jLabel5.setVisible(change);
        jLabel6.setVisible(change);
        jLabel7.setVisible(change);
        jComboBox1.setVisible(change);
        jComboBox2.setVisible(change);
        jComboBox3.setVisible(change);
        jComboBox4.setVisible(change);
        jComboBox5.setVisible(change);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        modelBox = new javax.swing.JComboBox<>();
        modelLabel = new javax.swing.JLabel();
        colourBox = new javax.swing.JComboBox<>();
        colourLabel = new javax.swing.JLabel();
        mechanicalLabel = new javax.swing.JLabel();
        mechanicalBox = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        heightField = new javax.swing.JTextField();
        heightLabel = new javax.swing.JLabel();
        widthField = new javax.swing.JTextField();
        widthLabel = new javax.swing.JLabel();
        widthLabel1 = new javax.swing.JLabel();
        heightBoxField = new javax.swing.JTextField();
        currentHeightLabel = new javax.swing.JLabel();
        blindHeightLabel = new javax.swing.JLabel();
        minDimLabel = new javax.swing.JLabel();
        currentWeightLabel = new javax.swing.JLabel();
        blindWeightLabel = new javax.swing.JLabel();
        currentWidthxHeightLabel = new javax.swing.JLabel();
        widthxHeightLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        fullAccList2 = new javax.swing.JList<>();
        mechAddBox = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        selAccList2 = new javax.swing.JList<>();
        jScrollPane8 = new javax.swing.JScrollPane();
        fullAccList3 = new javax.swing.JList<>();
        jScrollPane9 = new javax.swing.JScrollPane();
        selAccList3 = new javax.swing.JList<>();
        addAccButton2 = new javax.swing.JButton();
        rmAccButton2 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        fullAccList = new javax.swing.JList<>();
        addAccButton = new javax.swing.JButton();
        rmAccButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        selAccList = new javax.swing.JList<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        fullAccList1 = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        selAccList1 = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        nextButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        warningLabel = new javax.swing.JLabel();
        quantitySpinner = new javax.swing.JSpinner();
        quantityLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        currentPriceLabel = new javax.swing.JLabel();
        currentPriceLabelZloty = new javax.swing.JLabel();
        suggestedEnginePowerLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        profileTogglejButton = new javax.swing.JToggleButton();
        profilejLabel = new javax.swing.JLabel();
        colourButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1250, 808));
        setResizable(false);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        modelBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modelBoxActionPerformed(evt);
            }
        });

        modelLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        modelLabel.setText("Model:");

        colourBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colourBoxActionPerformed(evt);
            }
        });

        colourLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        colourLabel.setText("Kolor:");

        mechanicalLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        mechanicalLabel.setText("Automatyka:");

        mechanicalBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mechanicalBoxActionPerformed(evt);
            }
        });

        heightField.setMinimumSize(new java.awt.Dimension(100, 22));
        heightField.setPreferredSize(new java.awt.Dimension(100, 22));
        heightField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                heightFieldFocusLost(evt);
            }
        });
        heightField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                heightFieldActionPerformed(evt);
            }
        });

        heightLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        heightLabel.setText("Wysokość [mm]:");

        widthField.setMinimumSize(new java.awt.Dimension(100, 22));
        widthField.setPreferredSize(new java.awt.Dimension(100, 22));
        widthField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                widthFieldActionPerformed(evt);
            }
        });

        widthLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        widthLabel.setText("Szerokość [mm]:");

        widthLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        widthLabel1.setText("Wysokość skrzynki [mm]:");

        heightBoxField.setMinimumSize(new java.awt.Dimension(100, 22));
        heightBoxField.setPreferredSize(new java.awt.Dimension(100, 22));
        heightBoxField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                heightBoxFieldActionPerformed(evt);
            }
        });

        currentHeightLabel.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        currentHeightLabel.setText("Wysokość pancerza [mm]:");

        blindHeightLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        minDimLabel.setForeground(new java.awt.Color(255, 51, 51));
        minDimLabel.setText("Minimalna powierzchnia obliczeniowa dla pancerzy wynosi: 1,50m2!");

        currentWeightLabel.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        currentWeightLabel.setText("Ciężar pancerza [kg]:");

        blindWeightLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        currentWidthxHeightLabel.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        currentWidthxHeightLabel.setText("Powierzchnia rolety [m2]:");

        widthxHeightLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        widthxHeightLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                widthxHeightLabelMouseMoved(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(minDimLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(widthLabel1)
                            .addComponent(heightLabel)
                            .addComponent(widthLabel))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(heightField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(widthField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(heightBoxField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(currentWeightLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(blindWeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(currentHeightLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(blindHeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(currentWidthxHeightLabel)
                                .addGap(18, 18, 18)
                                .addComponent(widthxHeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(widthLabel1)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(heightBoxField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(currentWeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(blindWeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(widthLabel)
                                .addComponent(widthField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(blindHeightLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(currentHeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 18, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(heightLabel)
                            .addComponent(heightField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(currentWidthxHeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(widthxHeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(minDimLabel)
                .addContainerGap())
        );

        fullAccList2.setFixedCellHeight(15);
        fullAccList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fullAccList2MouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(fullAccList2);

        mechAddBox.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        mechAddBox.setText("Automatyka - dodatki:");

        selAccList2.setFixedCellHeight(15);
        selAccList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selAccList2MouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(selAccList2);

        jScrollPane8.setViewportView(fullAccList3);

        jScrollPane9.setViewportView(selAccList3);

        addAccButton2.setText("Dodaj");
        addAccButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAccButton2ActionPerformed(evt);
            }
        });

        rmAccButton2.setText("Usuń");
        rmAccButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rmAccButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rmAccButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addAccButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mechAddBox)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane7)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(mechAddBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addAccButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rmAccButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        fullAccList.setFixedCellHeight(20);
        fullAccList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fullAccListMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(fullAccList);

        addAccButton.setText("Dodaj");
        addAccButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAccButtonActionPerformed(evt);
            }
        });

        rmAccButton.setText("Usuń");
        rmAccButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rmAccButtonActionPerformed(evt);
            }
        });

        selAccList.setFixedCellHeight(20);
        selAccList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selAccListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(selAccList);

        jScrollPane4.setViewportView(fullAccList1);

        jScrollPane2.setViewportView(selAccList1);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Dopłaty:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane3)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(addAccButton))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(29, 29, 29)
                            .addComponent(rmAccButton)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jLabel1)))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(addAccButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rmAccButton))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonPanel.setMinimumSize(new java.awt.Dimension(362, 60));

        nextButton.setText("Dodaj");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Anuluj");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        warningLabel.setForeground(new java.awt.Color(255, 51, 51));
        warningLabel.setText("Wszystkie pola muszą być prawidłowo uzupełnione!");

        quantitySpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        quantitySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                quantitySpinnerStateChanged(evt);
            }
        });
        quantitySpinner.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                quantitySpinnerPropertyChange(evt);
            }
        });

        quantityLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        quantityLabel.setText("Ilość:");

        currentPriceLabel.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        currentPriceLabel.setText("Cena:");

        currentPriceLabelZloty.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(currentPriceLabel)
                .addGap(18, 18, 18)
                .addComponent(currentPriceLabelZloty, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(currentPriceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(currentPriceLabelZloty, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(buttonPanelLayout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(warningLabel)
                        .addGap(18, 18, 18)
                        .addComponent(nextButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(quantityLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(quantitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(buttonPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(quantitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(quantityLabel))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextButton)
                    .addComponent(cancelButton)
                    .addComponent(warningLabel))
                .addContainerGap())
        );

        suggestedEnginePowerLabel.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        suggestedEnginePowerLabel.setText("Sugerowana minimalna moc silnika - ");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 70, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 69, Short.MAX_VALUE)
        );

        profileTogglejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profileTogglejButtonActionPerformed(evt);
            }
        });

        profilejLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        profilejLabel.setText("Profil:");

        colourButton.setText("Rozwiń");
        colourButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colourButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Całość:");

        jLabel3.setText("Skrzynka Wew:");

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Profil:");

        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel5.setText("Prowadnice:");

        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jLabel6.setText("Dolna Listwa:");

        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jLabel7.setText("Adapter:");

        jComboBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(modelLabel)
                                    .addComponent(modelBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(147, 147, 147)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(mechanicalBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mechanicalLabel)
                            .addComponent(suggestedEnginePowerLabel)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(profilejLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(profileTogglejButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(18, 18, 18))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(colourButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(colourBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(colourLabel)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))
                        .addGap(53, 53, 53)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(buttonPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(modelLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(modelBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(profilejLabel)
                                    .addComponent(profileTogglejButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(suggestedEnginePowerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mechanicalLabel)
                                .addGap(18, 18, 18)
                                .addComponent(mechanicalBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(colourLabel)
                                    .addComponent(colourButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(colourBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowActivated

    public void addToList(JList<String> fullList, JList<String> selList) {
        List copied = fullList.getSelectedValuesList();
        final DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < selList.getModel().getSize(); i++) {
            model.addElement(selList.getModel().getElementAt(i));
        }
        model.addAll(copied);
        selList.setModel(model);
    }
    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseClicked

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        // TODO add your handling code here:
        if (!widthField.getText().isBlank() || !heightField.getText().isBlank() || !heightBoxField.getText().isBlank()) {
            double paramTest = Double.parseDouble(widthField.getText()) * (Double.parseDouble(heightField.getText()) + Double.parseDouble(heightBoxField.getText()));
            newBlind.setBlindBox(0.001 * Double.parseDouble(heightBoxField.getText()));
            newBlind.setBlindWidth(0.001 * Double.parseDouble(widthField.getText()));
            if (paramTest < 1500000) {
                newBlind.setMinBlindHeightWithBox();
                minDimLabel.setVisible(true);
            } else {
                newBlind.setBlindHeightWithBox(0.001 * Double.parseDouble(heightField.getText()));
                minDimLabel.setVisible(false);
            }
            newBlind.setBlindHeight();
            newBlind.setBlindWeight();
            blindWeightLabel.setText(String.valueOf(round(newBlind.getBlindWeight(), 2)));
            blindHeightLabel.setText(String.valueOf(1000 * round(newBlind.getBlindHeightWithBox(), 3)));
            widthxHeightLabel.setText(String.valueOf(round(ComponentPrice.squareMeter(1, newBlind.getBlindWidth(), newBlind.getBlindHeightWithBox()), 2)));
            double weight = newBlind.getBlindWeight();
            String enginePower;
            if (weight <= 6) {
                enginePower = "6";
            } else if (weight > 6 && weight <= 10) {
                enginePower = "10";
            } else if (weight > 10 && weight <= 15) {
                enginePower = "15";
            } else if (weight > 15 && weight <= 20) {
                enginePower = "20";
            } else {
                enginePower = "30";
            }
            suggestedEnginePowerLabel.setText("Sugerowana minimalna moc silnika - " + enginePower + "Nm");
        }
        setBlindPrice();
    }//GEN-LAST:event_formMouseMoved

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_formFocusGained

    private void profileTogglejButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profileTogglejButtonActionPerformed
        // TODO add your handling code here:
        profileLock = true;
        if ("PA39".equals(newBlind.getBlindProfile())) {
            newBlind.setBlindProfile("PA43");
        } else {
            newBlind.setBlindProfile("PA39");
        }
    }//GEN-LAST:event_profileTogglejButtonActionPerformed

    private void quantitySpinnerPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_quantitySpinnerPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_quantitySpinnerPropertyChange

    private void quantitySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_quantitySpinnerStateChanged
        // TODO add your handling code here:
        newBlind.setBlindCount((int) quantitySpinner.getValue());
    }//GEN-LAST:event_quantitySpinnerStateChanged

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        suncoMainWindow.setEnabled(true);
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    @SuppressWarnings("unused")
    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed

        boolean texstsOK = heightField.getText().equals("") || widthField.getText().equals("");
        newBlind.setSimpleBlind(null);
        if (texstsOK) {
            warningLabel.setVisible(true);
        } else if (!texstsOK && isDivided(hiddenList) > 0) {
            try {
                setSemiBlindPrice();
                if (blindIndex >= 0) {
                    DividedBlind dividedBlind = new DividedBlind(this, newBlind, isDivided(hiddenList) - 1, blindIndex);
                    dividedBlind.setLocationRelativeTo(null);
                    dividedBlind.setVisible(true);
                } else {
                    DividedBlind dividedBlind = new DividedBlind(this, newBlind, isDivided(hiddenList) - 1);
                    dividedBlind.setLocationRelativeTo(null);
                    dividedBlind.setVisible(true);
                }

            } catch (SQLException | IOException e) {
                ErrorLog.logError(e);
            }
        } else {
            newBlind.setBlindPrice(blindPrice);
            if (blindIndex >= 0) {
                suncoMainWindow.blindList.blindList.set(blindIndex, newBlind);
            } else {
                suncoMainWindow.blindList.blindList.add(newBlind);
            }

            suncoMainWindow.setEnabled(true);
            this.dispose();
        }
    }//GEN-LAST:event_nextButtonActionPerformed

    private void rmAccButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmAccButtonActionPerformed
        // TODO add your handling code here:
        DefaultListModel dlm = (DefaultListModel) hiddenList.getModel();
        if (this.selAccList.getSelectedIndices().length > 0) {
            List<String> tempList = selAccList.getSelectedValuesList();
            for (String one : tempList) {
                String[] temp = one.split(":");
                dlm.removeElement(temp[0].substring(0, temp[0].length() - 1));
            }
        }
        newBlind.setHiddenList(this.hiddenList);
        newBlind.setBlindAddons(setListPrice(hiddenList, "dopłaty"));
        selAccList.setModel(setModelCount(newBlind.getBlindAddons()));
    }//GEN-LAST:event_rmAccButtonActionPerformed

    private void addAccButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAccButtonActionPerformed
        // TODO add your handling code here:
        addToList(fullAccList, hiddenList);
        newBlind.setHiddenList(this.hiddenList);
        newBlind.setBlindAddons(setListPrice(hiddenList, "dopłaty"));
        selAccList.setModel(setModelCount(newBlind.getBlindAddons()));
    }//GEN-LAST:event_addAccButtonActionPerformed

    private void rmAccButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmAccButton2ActionPerformed
        // TODO add your handling code here:
        DefaultListModel dlm = (DefaultListModel) hiddenList2.getModel();
        if (this.selAccList2.getSelectedIndices().length > 0) {
            List<String> tempList = selAccList2.getSelectedValuesList();
            for (String one : tempList) {
                String[] temp = one.split(":");
                dlm.removeElement(temp[0].substring(0, temp[0].length() - 1));
            }
        }
        newBlind.setHiddenList2(this.hiddenList2);
        newBlind.setBlindExtras(setListPrice(hiddenList2, "automatyka"));
        selAccList2.setModel(setModelCount(newBlind.getBlindExtras()));
    }//GEN-LAST:event_rmAccButton2ActionPerformed

    private void addAccButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAccButton2ActionPerformed
        // TODO add your handling code here:
        addToList(fullAccList2, hiddenList2);
        newBlind.setHiddenList2(this.hiddenList2);
        newBlind.setBlindExtras(setListPrice(hiddenList2, "automatyka"));
        selAccList2.setModel(setModelCount(newBlind.getBlindExtras()));
    }//GEN-LAST:event_addAccButton2ActionPerformed

    @SuppressWarnings("unused")
    private void widthxHeightLabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_widthxHeightLabelMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_widthxHeightLabelMouseMoved

    private void heightBoxFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightBoxFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_heightBoxFieldActionPerformed

    private void widthFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_widthFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_widthFieldActionPerformed

    private void heightFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightFieldActionPerformed

    }//GEN-LAST:event_heightFieldActionPerformed

    private void heightFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_heightFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_heightFieldFocusLost

    private void mechanicalBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mechanicalBoxActionPerformed
        // TODO add your handling code here:
        newBlind.setBlindAuto(setBoxPrice(mechanicalBox, "silniki"));
    }//GEN-LAST:event_mechanicalBoxActionPerformed

    private void colourBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colourBoxActionPerformed
        if (!jComboBox1.isVisible()) {
            newBlind.setBlindColour(colourBox.getSelectedItem().toString());
        } else {
            newBlind.blindColour.setBoxOut(colourBox.getSelectedItem().toString());
        }
        newBlind.setBlindColourType(setBoxPrice(colourBox, "kolory_ceny"));
    }//GEN-LAST:event_colourBoxActionPerformed

    private void modelBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelBoxActionPerformed
        // TODO add your handling code here:
        newBlind.setBlindModel(setBoxPrice(modelBox, "modele"));
    }//GEN-LAST:event_modelBoxActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        suncoMainWindow.setEnabled(true);
    }//GEN-LAST:event_formWindowClosing

    private void fullAccListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fullAccListMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            addToList(fullAccList, hiddenList);
            newBlind.setHiddenList(this.hiddenList);
            newBlind.setBlindAddons(setListPrice(hiddenList, "dopłaty"));
            selAccList.setModel(setModelCount(newBlind.getBlindAddons()));
        }

    }//GEN-LAST:event_fullAccListMouseClicked

    private void fullAccList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fullAccList2MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            addToList(fullAccList2, hiddenList2);
            newBlind.setHiddenList2(this.hiddenList2);
            newBlind.setBlindExtras(setListPrice(hiddenList2, "automatyka"));
            selAccList2.setModel(setModelCount(newBlind.getBlindExtras()));
        }
    }//GEN-LAST:event_fullAccList2MouseClicked

    private void selAccListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_selAccListMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            DefaultListModel dlm = (DefaultListModel) hiddenList.getModel();
            if (this.selAccList.getSelectedIndices().length > 0) {
                List<String> tempList = selAccList.getSelectedValuesList();
                for (String one : tempList) {
                    String[] temp = one.split(":");
                    dlm.removeElement(temp[0].substring(0, temp[0].length() - 1));
                }
            }
            newBlind.setHiddenList(this.hiddenList);
            newBlind.setBlindAddons(setListPrice(hiddenList, "dopłaty"));
            selAccList.setModel(setModelCount(newBlind.getBlindAddons()));
        }
    }//GEN-LAST:event_selAccListMouseClicked

    private void selAccList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_selAccList2MouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            DefaultListModel dlm = (DefaultListModel) hiddenList2.getModel();
            if (this.selAccList2.getSelectedIndices().length > 0) {
                List<String> tempList = selAccList2.getSelectedValuesList();
                for (String one : tempList) {
                    String[] temp = one.split(":");
                    dlm.removeElement(temp[0].substring(0, temp[0].length() - 1));
                }
            }
            newBlind.setHiddenList2(this.hiddenList2);
            newBlind.setBlindExtras(setListPrice(hiddenList2, "automatyka"));
            selAccList2.setModel(setModelCount(newBlind.getBlindExtras()));
        }
    }//GEN-LAST:event_selAccList2MouseClicked

    private void colourButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colourButtonActionPerformed
        // TODO add your handling code here:
        if (colourButton.getText().equals("Rozwiń")) {
            colourButton.setText("Zwiń");
            manageColours(true);
        } else {
            colourButton.setText("Rozwiń");
            manageColours(false);
        }
        this.pack();
    }//GEN-LAST:event_colourButtonActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        newBlind.blindColour.setBoxIn(jComboBox1.getSelectedItem().toString());
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
        newBlind.blindColour.setProfile(jComboBox2.getSelectedItem().toString());
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        // TODO add your handling code here:
        newBlind.blindColour.setLeaders(jComboBox4.getSelectedItem().toString());
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
        newBlind.blindColour.setBotList(jComboBox3.getSelectedItem().toString());
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox5ActionPerformed
        // TODO add your handling code here:
        newBlind.blindColour.setAdapter(jComboBox5.getSelectedItem().toString());
    }//GEN-LAST:event_jComboBox5ActionPerformed

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     */
    public static void main(String args[]) throws SQLException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
        /* Create and display the form */
        new NewBlindFrame().setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAccButton;
    private javax.swing.JButton addAccButton2;
    private javax.swing.JLabel blindHeightLabel;
    private javax.swing.JLabel blindWeightLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox<String> colourBox;
    private javax.swing.JButton colourButton;
    private javax.swing.JLabel colourLabel;
    private javax.swing.JLabel currentHeightLabel;
    private javax.swing.JLabel currentPriceLabel;
    private javax.swing.JLabel currentPriceLabelZloty;
    private javax.swing.JLabel currentWeightLabel;
    private javax.swing.JLabel currentWidthxHeightLabel;
    private javax.swing.JList<String> fullAccList;
    private javax.swing.JList<String> fullAccList1;
    private javax.swing.JList<String> fullAccList2;
    private javax.swing.JList<String> fullAccList3;
    private javax.swing.JTextField heightBoxField;
    private javax.swing.JTextField heightField;
    private javax.swing.JLabel heightLabel;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JLabel mechAddBox;
    private javax.swing.JComboBox<String> mechanicalBox;
    private javax.swing.JLabel mechanicalLabel;
    private javax.swing.JLabel minDimLabel;
    private javax.swing.JComboBox<String> modelBox;
    private javax.swing.JLabel modelLabel;
    private javax.swing.JButton nextButton;
    private javax.swing.JToggleButton profileTogglejButton;
    private javax.swing.JLabel profilejLabel;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JSpinner quantitySpinner;
    private javax.swing.JButton rmAccButton;
    private javax.swing.JButton rmAccButton2;
    private javax.swing.JList<String> selAccList;
    private javax.swing.JList<String> selAccList1;
    private javax.swing.JList<String> selAccList2;
    private javax.swing.JList<String> selAccList3;
    private javax.swing.JLabel suggestedEnginePowerLabel;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JTextField widthField;
    private javax.swing.JLabel widthLabel;
    private javax.swing.JLabel widthLabel1;
    private javax.swing.JLabel widthxHeightLabel;
    // End of variables declaration//GEN-END:variables
}
