/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.sunco_cennik;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author Hubert
 */
public class DividedBlind extends javax.swing.JFrame {

    SimpleBlind simpleBlind = new SimpleBlind();
    int divided;
    Connection connection;
    NewBlind newBlind;
    BlindList blindList;
    NewBlindFrame newBlindFrame;

    /**
     * Creates new form DividedBlind
     */
    public DividedBlind() {
        initComponents();
    }

    public DividedBlind(NewBlindFrame newBlindFrame, NewBlind newBlind, BlindList blindList, int divNmb) throws SQLException {
        this.connection = DB.connect();
        this.newBlindFrame = newBlindFrame;
        initComponents();
        if (divNmb == 0) {
            jButton2.setEnabled(false);
        }
        minDimLabel.setVisible(false);
        maxWidthLabel.setVisible(false);
        divided = divNmb;
        this.blindList = blindList;
        this.newBlind = newBlind;
        if (newBlind.getSimpleBlind() == null) {
            jLabel1.setText("Pancerz nr. 1");
        } else {
            jLabel1.setText("Pancerz nr." + (newBlind.getSimpleBlind().size() + 1));
        }
        populateComboBox("select nazwa, moc from silniki", mechanicalBox);
        this.pack();
    }

    public DividedBlind(NewBlindFrame newBlindFrame, SimpleBlind simpleBlind, NewBlind newBlind, BlindList blindList, int divNmb) throws SQLException {

        this.connection = DB.connect();
        this.newBlindFrame = newBlindFrame;
        this.simpleBlind = simpleBlind;
        newBlind.getSimpleBlind().remove(newBlind.getSimpleBlind().size() - 1);
        initComponents();
        if (divNmb == 0) {
            jButton2.setEnabled(false);
        }
        populateComboBox("select nazwa, moc from silniki", mechanicalBox);
        widthField.setText(String.valueOf(1000 * simpleBlind.getBlindWidth()));
        heightField.setText(String.valueOf(1000 * simpleBlind.getBlindHeight()));
        if (simpleBlind.getBlindAuto().getName().equals("PRZENIESIENIE NAPĘDU")) {
            transferPowerCheckBox.setEnabled(true);
        } else {
            this.mechanicalBox.setSelectedItem(setComboBox(mechanicalBox.getModel(), simpleBlind.getBlindAuto().getName()));
        }
        minDimLabel.setVisible(false);
        maxWidthLabel.setVisible(false);
        divided = divNmb;
        this.blindList = blindList;
        this.newBlind = newBlind;
        if (newBlind.getSimpleBlind() == null) {
            jLabel1.setText("Pancerz nr. 1");
        } else {
            jLabel1.setText("Pancerz nr." + (newBlind.getSimpleBlind().size() + 1));
        }
        this.pack();
    }

    public final String setComboBox(ComboBoxModel model, String name) {
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).toString().equals(name)) {
                return (String) model.getElementAt(i);
            }
        }
        return "";
    }

    public BlindPriceList setBoxPrice(JComboBox comboBox, String dbName) {
        String[] resultArr = comboBox.getSelectedItem().toString().split(" - ");
        BlindPriceList blindPriceList = setPrice(resultArr[0], dbName);
        blindPriceList.setName(comboBox.getSelectedItem().toString());
        return blindPriceList;
    }

    public BlindPriceList setPrice(String query, String dbName) {
        BlindPriceList blindPriceList = new BlindPriceList();
        PreparedStatement pst;
        ResultSet rs;
        try {
            query = "select * from " + dbName + " where " + getColumnName(dbName) + "='" + query + "'";
            pst = connection.prepareStatement(query);
            rs = pst.executeQuery();
            rs.next();
            String priceType = "pa39cena";
            if (simpleBlind.getBlindProfile().equals("PA43") && isThere(rs, "pa43cena") && rs.getDouble("pa43cena") != 0) {
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

    public void setSimpleBlindPrice() {
        double price = 0;
        BlindPriceList blindPriceList = setPrice(newBlind.getBlindModel().getName(), "modele");
        price += calculatePrice(blindPriceList);
        blindPriceList = setPrice(newBlind.getBlindColourType().getName(), "kolory_ceny");
        price += calculatePrice(blindPriceList);
        price += calculatePrice(simpleBlind.getBlindAuto());
        simpleBlind.setSimpleBlindPrice(price);
    }

    public double calculatePrice(BlindPriceList blindPriceList) {
        double boxPrice = 0;
        switch (blindPriceList.getPriceType()) {
            case "m2" -> {
                boxPrice = ComponentPrice.squareMeter(blindPriceList.getPrice(), simpleBlind.getBlindWidth(), simpleBlind.getBlindHeightWithBox());
                break;
            }
            case "mbS" -> {
                boxPrice = ComponentPrice.runningMeter(blindPriceList.getPrice(), simpleBlind.getBlindWidth());
                break;

            }
            case "mbW" -> {
                boxPrice = ComponentPrice.runningMeter(blindPriceList.getPrice(), simpleBlind.getBlindHeight());
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

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
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

    public double maxWidth(ArrayList<SimpleBlind> simpleBlind) {
        double maxWidth = newBlind.getBlindWidth();
        for (SimpleBlind blind : simpleBlind) {
            maxWidth -= blind.getBlindWidth();
        }
        return maxWidth;
    }

    public void setSimpleBlindEngine() {
        BlindPriceList blindPriceList = new BlindPriceList();
        if (!transferPowerCheckBox.isSelected()) {
            blindPriceList = setBoxPrice(mechanicalBox, "silniki");
        } else {
            blindPriceList.setName("PRZENIESIENIE NAPĘDU");
            blindPriceList.setPrice(100);
            blindPriceList.setPriceType("szt");
        }
        simpleBlind.setBlindAuto(blindPriceList);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        heightField = new javax.swing.JTextField();
        heightLabel = new javax.swing.JLabel();
        widthField = new javax.swing.JTextField();
        widthLabel = new javax.swing.JLabel();
        currentHeightLabel = new javax.swing.JLabel();
        blindHeightLabel = new javax.swing.JLabel();
        minDimLabel = new javax.swing.JLabel();
        currentWeightLabel = new javax.swing.JLabel();
        blindWeightLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        maxWidthLabel = new javax.swing.JLabel();
        mechanicalBox = new javax.swing.JComboBox<>();
        transferPowerCheckBox = new javax.swing.JCheckBox();
        nextButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
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

        currentHeightLabel.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        currentHeightLabel.setText("Wysokość pancerza [mm]:");

        blindHeightLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        minDimLabel.setForeground(new java.awt.Color(255, 51, 51));
        minDimLabel.setText("Minimalna powierzchnia obliczeniowa dla pancerzy wynosi: 1,50m2!");

        currentWeightLabel.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        currentWeightLabel.setText("Ciężar pancerza [kg]:");

        blindWeightLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        maxWidthLabel.setForeground(new java.awt.Color(255, 51, 51));
        maxWidthLabel.setText("Szerokości skrzynek nie mogą przekraczać maksymalnej szerokości rolety!");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(heightLabel)
                            .addComponent(widthLabel))
                        .addGap(35, 35, 35)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(heightField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(widthField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(currentWeightLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(blindWeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(currentHeightLabel)
                                .addGap(18, 18, 18)
                                .addComponent(blindHeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(minDimLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(maxWidthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(widthLabel)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(widthField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(currentWeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(blindWeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(currentHeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(heightLabel)
                        .addComponent(heightField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(blindHeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(minDimLabel)
                .addGap(18, 18, 18)
                .addComponent(maxWidthLabel)
                .addContainerGap())
        );

        mechanicalBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mechanicalBoxActionPerformed(evt);
            }
        });

        transferPowerCheckBox.setText("Przeniesienie napędu");
        transferPowerCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transferPowerCheckBoxActionPerformed(evt);
            }
        });

        nextButton.setText("Dalej");
        nextButton.setEnabled(false);
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        jButton2.setText("Wstecz");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Anuluj");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(transferPowerCheckBox)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mechanicalBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nextButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(transferPowerCheckBox)
                .addGap(18, 18, 18)
                .addComponent(mechanicalBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextButton)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void heightFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_heightFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_heightFieldFocusLost

    private void heightFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightFieldActionPerformed

    }//GEN-LAST:event_heightFieldActionPerformed

    private void widthFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_widthFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_widthFieldActionPerformed

    private void transferPowerCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transferPowerCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_transferPowerCheckBoxActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        // TODO add your handling code here:
        setSimpleBlindEngine();
        setSimpleBlindPrice();
        ArrayList<SimpleBlind> tempSimpleBlindList = new ArrayList();
        if (newBlind.getSimpleBlind() != null) {
            tempSimpleBlindList.addAll(newBlind.getSimpleBlind());
        }
        tempSimpleBlindList.add(simpleBlind);
        newBlind.setSimpleBlind(tempSimpleBlindList);
        if (divided >= 0) {
            try {
                DividedBlind dividedBlind = new DividedBlind(newBlindFrame, newBlind, blindList, divided - 1);
                dividedBlind.setLocationRelativeTo(null);
                dividedBlind.setVisible(true);
                this.dispose();
            } catch (SQLException ex) {
                Logger.getLogger(DividedBlind.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            blindList.blindList.add(newBlind);
            newBlindFrame.suncoMainWindow.blindList = blindList;
            newBlindFrame.suncoMainWindow.setEnabled(true);
            newBlindFrame.dispose();
            this.dispose();
        }
    }//GEN-LAST:event_nextButtonActionPerformed

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        // TODO add your handling code here:
        if (!widthField.getText().isBlank() || !heightField.getText().isBlank()) {
            double paramTest = Double.parseDouble(widthField.getText()) * (Double.parseDouble(heightField.getText()) + newBlind.getBlindBox());
            double maxWidth = 1000 * newBlind.getBlindWidth();
            if (newBlind.getSimpleBlind() != null) {
                maxWidth = 1000 * maxWidth(newBlind.getSimpleBlind());
            }
            if (paramTest < 1500000 && Double.parseDouble(widthField.getText()) > maxWidth) {
                minDimLabel.setVisible(true);
                nextButton.setEnabled(false);
                maxWidthLabel.setVisible(true);
            } else if (paramTest < 1500000) {
                nextButton.setEnabled(false);
                minDimLabel.setVisible(true);
                maxWidthLabel.setVisible(false);
            } else if (Double.parseDouble(widthField.getText()) > maxWidth) {
                nextButton.setEnabled(false);
                minDimLabel.setVisible(false);
                maxWidthLabel.setVisible(true);
            } else {
                simpleBlind.setBlindBox(newBlind.getBlindBox());
                simpleBlind.setBlindWidth(0.001 * Double.parseDouble(widthField.getText()));
                simpleBlind.setBlindHeight(0.001 * Double.parseDouble(heightField.getText()));
                simpleBlind.setBlindHeightWithBox();
                simpleBlind.setBlindWeight();
                blindWeightLabel.setText(String.valueOf(round(simpleBlind.getBlindWeight(), 3)));
                blindHeightLabel.setText(String.valueOf(round(simpleBlind.getBlindHeightWithBox(), 3)));
                if (simpleBlind.getBlindWidth() * simpleBlind.getBlindHeight() > 6 || simpleBlind.getBlindWidth() > 2.5) {
                    simpleBlind.setBlindProfile("PA43");
                } else {
                    simpleBlind.setBlindProfile("PA39");
                }
                nextButton.setEnabled(true);
                minDimLabel.setVisible(false);
                maxWidthLabel.setVisible(false);
            }
            this.pack();
        }
    }//GEN-LAST:event_formMouseMoved

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowActivated

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        try {
            DividedBlind dividedBlind = new DividedBlind(newBlindFrame, newBlind.getSimpleBlind().get(newBlind.getSimpleBlind().size() - 1), newBlind, blindList, divided + 1);
            dividedBlind.setLocationRelativeTo(null);
            dividedBlind.setVisible(true);
            this.dispose();
        } catch (SQLException ex) {
            Logger.getLogger(DividedBlind.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        newBlind.setSimpleBlind(null);
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void mechanicalBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mechanicalBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mechanicalBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DividedBlind.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DividedBlind.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DividedBlind.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DividedBlind.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DividedBlind().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel blindHeightLabel;
    private javax.swing.JLabel blindWeightLabel;
    private javax.swing.JLabel currentHeightLabel;
    private javax.swing.JLabel currentWeightLabel;
    private javax.swing.JTextField heightField;
    private javax.swing.JLabel heightLabel;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel maxWidthLabel;
    private javax.swing.JComboBox<String> mechanicalBox;
    private javax.swing.JLabel minDimLabel;
    private javax.swing.JButton nextButton;
    private javax.swing.JCheckBox transferPowerCheckBox;
    private javax.swing.JTextField widthField;
    private javax.swing.JLabel widthLabel;
    // End of variables declaration//GEN-END:variables
}
