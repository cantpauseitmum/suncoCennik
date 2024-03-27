/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.sunco_cennik;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.swing.DefaultListModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    BlindList blindList;
    SuncoMainWindow suncoMainWindow;

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
    }

    public NewBlindFrame(SuncoMainWindow suncoMainWindow, BlindList blindList, NewBlind newBlind) throws SQLException {
        initComponents();
        this.connection = DB.connect();
        this.suncoMainWindow = suncoMainWindow;
        this.blindList = blindList;
        populateBlindTables();
        this.newBlind = newBlind;
        this.modelBox.getModel();
        this.modelBox.setSelectedItem(setComboBox(modelBox.getModel(), newBlind.getBlindModel().getName()));
        this.colourBox.setSelectedItem(setComboBox(colourBox.getModel(), newBlind.getBlindColourType().getName()));
        this.mechanicalBox.setSelectedItem(setComboBox(mechanicalBox.getModel(), newBlind.getBlindAuto().getName()));
        if (newBlind.getBlindAddons() != null) {
            setList(this.selAccList, newBlind.getBlindAddons());
        }
        if (newBlind.getBlindExtras() != null) {
            setList(this.selAccList2, newBlind.getBlindExtras());
        }
        this.heightBoxField.setText(String.valueOf(1000 * newBlind.getBlindBox()));
        this.widthField.setText(String.valueOf(1000 * newBlind.getBlindWidth()));
        this.heightField.setText(String.valueOf(1000 * newBlind.getBlindHeight()));
        quantitySpinner.setValue(newBlind.getBlindCount());
        warningLabel.setVisible(false);
        minDimLabel.setVisible(false);
    }

    public NewBlindFrame(SuncoMainWindow suncoMainWindow, BlindList blindList) throws SQLException {
        initComponents();
        this.suncoMainWindow = suncoMainWindow;
        this.connection = DB.connect();
        this.blindList = blindList;
        populateBlindTables();
        newBlind.setBlindCount((int) quantitySpinner.getValue());
        warningLabel.setVisible(false);
        minDimLabel.setVisible(false);
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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
            System.out.println(e);
        }

        return dbColumnName;
    }

    private boolean isThere(ResultSet rs, String column) {
        try {
            rs.findColumn(column);
            return true;
        } catch (SQLException sqlex) {
            System.out.println(sqlex);
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
            blindPriceList.setName(rs.getString(2));
            blindPriceList.setPrice(rs.getDouble(priceType));
            blindPriceList.setPriceType(rs.getString("typceny"));
        } catch (SQLException e) {
            System.out.println(e);
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
                boxPrice = ComponentPrice.runningMeter(blindPriceList.getPrice(), newBlind.getBlindHeightWithBox()-newBlind.getBlindBox());
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
        if (newBlind.getBlindWidth() * newBlind.getBlindHeight() > 6 || newBlind.getBlindWidth() > 2.5) {
            newBlind.setBlindProfile("PA43");
        } else {
            newBlind.setBlindProfile("PA39");
        }
    }

    public void setBlindPrice() {
        if (!(heightField.getText().equals("") || widthField.getText().equals(""))) {
            double fullBlindPrice = 0;
            fullBlindPrice += calculatePrice(newBlind.getBlindModel());
            fullBlindPrice += calculatePrice(newBlind.getBlindColourType());
            fullBlindPrice += calculatePrice(newBlind.getBlindAuto());
            fullBlindPrice += calculatePrice(newBlind.getBlindAddons());
            fullBlindPrice += calculatePrice(newBlind.getBlindExtras());
            fullBlindPrice = round(fullBlindPrice, 2);
            this.blindPrice = fullBlindPrice;
            currentPriceLabelZloty.setText(String.valueOf(fullBlindPrice * newBlind.getBlindCount()) + "zł");
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
        heightLabel.setText("Wysokość okna [mm]:");

        widthField.setMinimumSize(new java.awt.Dimension(100, 22));
        widthField.setPreferredSize(new java.awt.Dimension(100, 22));
        widthField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                widthFieldActionPerformed(evt);
            }
        });

        widthLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        widthLabel.setText("Szerokość okna [mm]:");

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
        currentWidthxHeightLabel.setText("Powierzchnia pancerza [m2]:");

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
        jScrollPane6.setViewportView(fullAccList2);

        mechAddBox.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        mechAddBox.setText("Automatyka - dodatki:");

        selAccList2.setFixedCellHeight(15);
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addAccButton))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel1))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(rmAccButton)))
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
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(colourBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(colourLabel))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(mechanicalBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(mechanicalLabel)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(suggestedEnginePowerLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)))
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
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(colourLabel)
                            .addComponent(modelLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(colourBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(modelBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(suggestedEnginePowerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mechanicalLabel)
                        .addGap(18, 18, 18)
                        .addComponent(mechanicalBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed

        boolean texstsOK = heightField.getText().equals("") || widthField.getText().equals("");
        if (texstsOK) {
            warningLabel.setVisible(true);
        } else if (!texstsOK && isDivided(selAccList) > 0) {
            try {
                newBlind.setSimpleBlind(null);
                setSemiBlindPrice();
                DividedBlind dividedBlind = new DividedBlind(this, newBlind, blindList, isDivided(selAccList) - 1);
                dividedBlind.setLocationRelativeTo(null);
                dividedBlind.setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(NewBlindFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            newBlind.setBlindPrice(blindPrice);
            blindList.blindList.add(newBlind);
            suncoMainWindow.blindList = blindList;
            suncoMainWindow.setEnabled(true);
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(NewBlindFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.dispose();
        }
    }//GEN-LAST:event_nextButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        suncoMainWindow.setEnabled(true);
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(NewBlindFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowActivated

    private void rmAccButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmAccButtonActionPerformed
        // TODO add your handling code here:
        DefaultListModel dlm = (DefaultListModel) selAccList.getModel();
        if (this.selAccList.getSelectedIndices().length > 0) {
            int[] selectedIndices = selAccList.getSelectedIndices();
            for (int i = selectedIndices.length - 1; i >= 0; i--) {
                dlm.removeElementAt(selectedIndices[i]);
            }
        }
        newBlind.setBlindAddons(setListPrice(selAccList, "dopłaty"));
    }//GEN-LAST:event_rmAccButtonActionPerformed

    public void addToList(JList<String> fullList, JList<String> selList) {
        List copied = fullList.getSelectedValuesList();
        final DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < selList.getModel().getSize(); i++) {
            model.addElement(selList.getModel().getElementAt(i));
        }
        for (int i = 0; i < copied.size(); i++) {
            model.addElement(copied.get(i));
        }
        selList.setModel(model);
    }
    private void addAccButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAccButtonActionPerformed
        // TODO add your handling code here:
        addToList(fullAccList, selAccList);
        newBlind.setBlindAddons(setListPrice(selAccList, "dopłaty"));
    }//GEN-LAST:event_addAccButtonActionPerformed

    private void rmAccButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmAccButton2ActionPerformed
        // TODO add your handling code here:
        DefaultListModel dlm = (DefaultListModel) selAccList2.getModel();
        if (this.selAccList2.getSelectedIndices().length > 0) {
            int[] selectedIndices = selAccList2.getSelectedIndices();
            for (int i = selectedIndices.length - 1; i >= 0; i--) {
                dlm.removeElementAt(selectedIndices[i]);
            }
        }
        newBlind.setBlindExtras(setListPrice(selAccList2, "automatyka"));
    }//GEN-LAST:event_rmAccButton2ActionPerformed

    private void addAccButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAccButton2ActionPerformed
        // TODO add your handling code here:
        addToList(fullAccList2, selAccList2);
        newBlind.setBlindExtras(setListPrice(selAccList2, "automatyka"));

    }//GEN-LAST:event_addAccButton2ActionPerformed

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
        newBlind.setBlindColour(colourBox.getSelectedItem().toString());
        newBlind.setBlindColourType(setBoxPrice(colourBox, "kolory_ceny"));
    }//GEN-LAST:event_colourBoxActionPerformed

    private void modelBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelBoxActionPerformed
        // TODO add your handling code here:
        newBlind.setBlindModel(setBoxPrice(modelBox, "modele"));
    }//GEN-LAST:event_modelBoxActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseClicked

    private void quantitySpinnerPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_quantitySpinnerPropertyChange
        // TODO add your handling code here:

    }//GEN-LAST:event_quantitySpinnerPropertyChange

    private void quantitySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_quantitySpinnerStateChanged
        // TODO add your handling code here:
        newBlind.setBlindCount((int) quantitySpinner.getValue());
    }//GEN-LAST:event_quantitySpinnerStateChanged

    private void heightBoxFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightBoxFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_heightBoxFieldActionPerformed

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        // TODO add your handling code here:
        if (!widthField.getText().isBlank() || !heightField.getText().isBlank() || !heightBoxField.getText().isBlank()) {
            double paramTest = Double.parseDouble(widthField.getText()) * (Double.parseDouble(heightField.getText()) + Double.parseDouble(heightBoxField.getText()));
            newBlind.setBlindBox(0.001 * Double.parseDouble(heightBoxField.getText()));
            newBlind.setBlindWidth(0.001 * Double.parseDouble(widthField.getText()));
            newBlind.setBlindHeight(0.001 * Double.parseDouble(heightField.getText()));
            if (paramTest < 1500000) {
                newBlind.setMinBlindHeightWithBox();
                minDimLabel.setVisible(true);
            } else {
                newBlind.setBlindHeightWithBox();
                minDimLabel.setVisible(false);
            }
            newBlind.setBlindWeight();
            blindWeightLabel.setText(String.valueOf(round(newBlind.getBlindWeight(), 3)));
            blindHeightLabel.setText(String.valueOf(1000 * round(newBlind.getBlindHeightWithBox(), 3)));
            widthxHeightLabel.setText(String.valueOf(round(ComponentPrice.squareMeter(1, newBlind.getBlindWidth(), newBlind.getBlindHeightWithBox()), 3)));
            setProfile();
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

    private void widthxHeightLabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_widthxHeightLabelMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_widthxHeightLabelMouseMoved

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_formFocusGained

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
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
