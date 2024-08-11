/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.sunco_cennik;

import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Toolkit;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Hubert
 */
public final class SuncoMainWindow extends javax.swing.JFrame {

    Connection con;
    BlindList blindList = new BlindList();
    ArrayList<NewBlind> copyPasteList = new ArrayList();
    double currency = 1, vat = 1;

    /**
     * Creates new form suncoMainWindow
     *
     * @throws javax.swing.UnsupportedLookAndFeelException
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public SuncoMainWindow() throws UnsupportedLookAndFeelException, IOException, SQLException {
        UIManager.setLookAndFeel(new FlatIntelliJLaf());
        initComponents();
        ErrorLog.clearLogs();
        blindList.blindList = new ArrayList();
        this.con = DB.connect();
        populateComboBox("select firma from klienci", clientBox);
    }

    public SuncoMainWindow(BlindList blindList) {
        initComponents();
        this.blindList = blindList;
        if (blindList.blindList != null && !blindList.blindList.isEmpty()) {
            populateTable(blindList.blindList);
        }
        totalPriceLabel1.setText(totalPrice());
        populateComboBox("select firma from klienci", clientBox);
    }

    public void populateComboBox(String sqlSatement, JComboBox comboBox) {
        ResultSet rs;
        PreparedStatement pst;
        try {
            pst = con.prepareStatement(sqlSatement);
            rs = pst.executeQuery();
            comboBox.removeAllItems();
            while (rs.next()) {
                String name = rs.getString(1);
                comboBox.addItem(name);

            }

        } catch (SQLException e) {
            ErrorLog.logError(e);
        }
    }

    public void populateTable(ArrayList<NewBlind> blindList) {
        DefaultTableModel holdingModel = (DefaultTableModel) jTable1.getModel();
        holdingModel.setRowCount(0);
        for (NewBlind newBlind : blindList) {
            if (newBlind.getSimpleBlind() != null) {
                double simpleBlindPrices = 0;
                ArrayList<String> blindProfiles = new ArrayList();
                ArrayList<String> blindAuto = new ArrayList();
                for (SimpleBlind simpleBlind : newBlind.getSimpleBlind()) {
                    blindProfiles.add(simpleBlind.getBlindProfile());
                    blindAuto.add(simpleBlind.getBlindAuto().getName());
                    simpleBlindPrices += simpleBlind.getSimpleBlindPrice();
                }
                holdingModel.addRow(new Object[]{
                    holdingModel.getRowCount() + 1,
                    newBlind.getBlindModel().getName(),
                    newBlind.getBlindCount(),
                    newBlind.getBlindColour(),
                    newBlind.getBlindWidth() * 1000 + "x" + newBlind.getBlindHeightWithBox() * 1000,
                    blindProfiles,
                    toArray(newBlind.getBlindAddons()),
                    toArray(newBlind.getBlindExtras()),
                    blindAuto,
                    (simpleBlindPrices + newBlind.getBlindPrice()) * newBlind.getBlindCount()
                });
            } else {
                holdingModel.addRow(new Object[]{
                    holdingModel.getRowCount() + 1,
                    newBlind.getBlindModel().getName(),
                    newBlind.getBlindCount(),
                    newBlind.getBlindColour(),
                    newBlind.getBlindWidth() * 1000 + "x" + newBlind.getBlindHeightWithBox() * 1000,
                    newBlind.getBlindProfile(),
                    toArray(newBlind.getBlindAddons()),
                    toArray(newBlind.getBlindExtras()),
                    newBlind.getBlindAuto().getName(),
                    newBlind.getBlindPrice() * newBlind.getBlindCount() * currency * vat
                });
            }
        }
        jTable1.setModel(rearangeInicies(holdingModel));
    }

    public ArrayList<String> toArray(ArrayList<BlindPriceList> blindPriceList) {
        ArrayList<String> BlindList = new ArrayList();
        if (blindPriceList != null) {
            for (BlindPriceList blindPriceList1 : blindPriceList) {
                BlindList.add(blindPriceList1.getName());
            }
        }
        return BlindList;
    }

    public String totalPrice() {
        String totalPrice;
        double price = 0;
        for (NewBlind newBlind : blindList.blindList) {
            double simplePrice = 0;
            if (newBlind.getSimpleBlind() != null) {
                for (SimpleBlind simpleBlind : newBlind.getSimpleBlind()) {
                    simplePrice += simpleBlind.getSimpleBlindPrice();
                }
            }
            price += (newBlind.getBlindPrice() + simplePrice) * newBlind.getBlindCount();
        }
        String eurToPln = "zł";
        if (currency != 1) {
            eurToPln = "€";
        }
        totalPrice = String.valueOf(round(price * currency * vat, 2)) + eurToPln + " " + vatItem.getText();
        return totalPrice;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public DefaultTableModel rearangeInicies(DefaultTableModel model) {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);
        }
        return model;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        deleteButton = new javax.swing.JButton();
        finalButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        totalPriceLabel = new javax.swing.JLabel();
        totalPriceLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        dbCheckBox = new javax.swing.JCheckBox();
        addButton1 = new javax.swing.JButton();
        clientBox = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        deleteClient = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        dbMenu = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        dbEditMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jmenu3 = new javax.swing.JMenu();
        vatItem = new javax.swing.JMenuItem();
        currencyItem = new javax.swing.JMenuItem();
        customOrder = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sunco - wycena rolet");
        setIconImage(Toolkit.getDefaultToolkit().getImage(SuncoMainWindow.class.getResource("/blinds-icon.png")));
        setLocation(new java.awt.Point(0, 0));
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });

        deleteButton.setText("Usuń");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        finalButton.setText("Zakończ");
        finalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finalButtonActionPerformed(evt);
            }
        });

        addButton.setText("Dodaj");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        totalPriceLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalPriceLabel.setText("Cena końcowa:");

        totalPriceLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        totalPriceLabel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                totalPriceLabel1ComponentShown(evt);
            }
        });

        jScrollPane2.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jScrollPane2ComponentShown(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id.", "Model rolety", "Ilość", "Kolor", "Wymiary", "Profil", "Dodatki", "Automatyka", "Silnik", "Cena"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable1.setShowGrid(false);
        jScrollPane2.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
        }

        dbCheckBox.setSelected(true);
        dbCheckBox.setText("Dołączyć do bazy");
        dbCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbCheckBoxActionPerformed(evt);
            }
        });

        addButton1.setText("Aktualizuj");
        addButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButton1ActionPerformed(evt);
            }
        });

        clientBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientBoxActionPerformed(evt);
            }
        });

        jButton1.setText("Nowy");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Zmień");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        deleteClient.setText("Usuń");
        deleteClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteClientActionPerformed(evt);
            }
        });

        jMenu1.setText("Plik");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setText("Eksportuj do pliku");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem2.setText("Importuj z pliku");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        dbMenu.setText("Baza");
        dbMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbMenuActionPerformed(evt);
            }
        });

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem3.setText("Wczytaj z bazy");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        dbMenu.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem4.setText("Zapisz w bazie");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        dbMenu.add(jMenuItem4);

        dbEditMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        dbEditMenuItem.setText("Edytuj połączenie");
        dbEditMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbEditMenuItemActionPerformed(evt);
            }
        });
        dbMenu.add(dbEditMenuItem);

        jMenuBar1.add(dbMenu);

        jMenu2.setText("Tabela");

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem5.setText("Kopiuj");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem6.setText("Wklej");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuBar1.add(jMenu2);

        jmenu3.setText("Płatności");

        vatItem.setText("Netto");
        vatItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                vatItemMouseClicked(evt);
            }
        });
        vatItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vatItemActionPerformed(evt);
            }
        });
        jmenu3.add(vatItem);

        currencyItem.setText("PLN");
        currencyItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currencyItemActionPerformed(evt);
            }
        });
        jmenu3.add(currencyItem);

        customOrder.setText("Niestandardowe");
        customOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customOrderActionPerformed(evt);
            }
        });
        jmenu3.add(customOrder);

        jMenuBar1.add(jmenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(addButton)
                                .addGap(18, 18, 18)
                                .addComponent(addButton1))
                            .addComponent(clientBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addGap(7, 7, 7)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteClient)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(finalButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(totalPriceLabel)
                                .addGap(18, 18, 18)
                                .addComponent(totalPriceLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(dbCheckBox))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalPriceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalPriceLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dbCheckBox)
                    .addComponent(clientBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(deleteClient))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(finalButton)
                    .addComponent(deleteButton)
                    .addComponent(addButton1)
                    .addComponent(addButton))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed

        try {
            this.setEnabled(false);
            if (!blindList.blindList.isEmpty()) {
                NewBlindFrame newBlindFrame = new NewBlindFrame(this, blindList.blindList.size() - 1);
                newBlindFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                newBlindFrame.setLocationRelativeTo(null);
                newBlindFrame.pack();
                newBlindFrame.setVisible(true);
            } else {
                NewBlindFrame newBlindFrame = new NewBlindFrame(this);
                newBlindFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                newBlindFrame.setLocationRelativeTo(null);
                newBlindFrame.pack();
                newBlindFrame.setVisible(true);
            }

        } catch (SQLException e) {
            ErrorLog.logError(e);
            this.setEnabled(true);
        }

    }//GEN-LAST:event_addButtonActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentShown

    private void totalPriceLabel1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_totalPriceLabel1ComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_totalPriceLabel1ComponentShown

    private void jScrollPane2ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jScrollPane2ComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane2ComponentShown

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained

    }//GEN-LAST:event_formFocusGained

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // TODO add your handling code here:
        ArrayList<Integer> toDelete = new ArrayList();
        for (int i : jTable1.getSelectedRows()) {
            toDelete.add(i);
        }
        ArrayList<NewBlind> newBlindList = new ArrayList();
        for (int i = 0; i < blindList.blindList.size(); i++) {
            boolean write = true;
            for (int j = 0; j < toDelete.size(); j++) {
                if (i == toDelete.get(j)) {
                    write = false;
                    break;
                }
            }
            if (write) {
                newBlindList.add(blindList.blindList.get(i));

            }
        }
        toDelete.clear();
        blindList.blindList = newBlindList;
        populateTable(blindList.blindList);
        totalPriceLabel1.setText(totalPrice());
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void finalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finalButtonActionPerformed
        // TODO add your handling code here:
        if (dbCheckBox.isEnabled() && blindList.offerName == null) {
            try {
                new DBTransfer(this, blindList, "toPath");
            } catch (IOException | SQLException | UnsupportedLookAndFeelException e) {
                ErrorLog.logError(e);
            }
        } else if (dbCheckBox.isEnabled()) {
            try {
                new DBTransfer(this, blindList, "notToPath");
                PathSelection pathSelection = new PathSelection(this, blindList, "pdf");
                pathSelection.setLocationRelativeTo(null);
                pathSelection.setVisible(true);
            } catch (IOException | SQLException | UnsupportedLookAndFeelException e) {
                ErrorLog.logError(e);
            }
        } else {
            PathSelection pathSelection = new PathSelection(this, blindList, "pdf");
            pathSelection.setLocationRelativeTo(null);
            pathSelection.setVisible(true);
        }
        this.dispose();
    }//GEN-LAST:event_finalButtonActionPerformed

    private void dbMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbMenuActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_dbMenuActionPerformed

    private void dbEditMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbEditMenuItemActionPerformed
        // TODO add your handling code here:
        DBEdit dbEdit = new DBEdit();
        dbEdit.pack();
        dbEdit.setVisible(true);
    }//GEN-LAST:event_dbEditMenuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        this.setEnabled(false);
        PathSelection pathSelection = new PathSelection(this, blindList, "blind-ex");
        pathSelection.setLocationRelativeTo(null);
        pathSelection.setVisible(true);

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        this.setEnabled(false);
        PathSelection pathSelection = new PathSelection(this, blindList, "blind-im");
        pathSelection.setLocationRelativeTo(null);
        pathSelection.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void dbCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dbCheckBoxActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed

        try {
            // TODO add your handling code here:
            DBListFrame dbListFrame = new DBListFrame(blindList, con);
            dbListFrame.setLocationRelativeTo(null);
            dbListFrame.setVisible(true);
            this.dispose();
        } catch (SQLException e) {
            ErrorLog.logError(e);
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        try {
            this.setEnabled(false);
            new DBTransfer(this, blindList, "setName");
        } catch (IOException | SQLException | UnsupportedLookAndFeelException e) {
            ErrorLog.logError(e);
            this.setEnabled(true);
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        ArrayList<Integer> indicies = new ArrayList();
        for (int i : jTable1.getSelectedRows()) {
            indicies.add(i);
        }
        for (int j : indicies) {
            for (int i = 0; i < blindList.blindList.size(); i++) {
                if (i == j) {
                    copyPasteList.add(blindList.blindList.get(i));
                }
            }
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        blindList.blindList.addAll(copyPasteList);
        populateTable(blindList.blindList);
        totalPriceLabel1.setText(totalPrice());
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void addButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButton1ActionPerformed
        // TODO add your handling code here:
        try {
            NewBlind newBlind = blindList.blindList.get(jTable1.getSelectedRow());
            this.setEnabled(false);
            NewBlindFrame newBlindFrame = new NewBlindFrame(this, jTable1.getSelectedRow());
            newBlindFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            newBlindFrame.setLocationRelativeTo(null);
            newBlindFrame.pack();
            newBlindFrame.setVisible(true);
        } catch (SQLException e) {
            ErrorLog.logError(e);
        }
    }//GEN-LAST:event_addButton1ActionPerformed

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        // TODO add your handling code here:
        if (blindList.blindList != null && !blindList.blindList.isEmpty()) {
            populateTable(blindList.blindList);
        }
        totalPriceLabel1.setText(totalPrice());
    }//GEN-LAST:event_formWindowGainedFocus

    private void vatItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vatItemMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_vatItemMouseClicked

    private void vatItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vatItemActionPerformed
        // TODO add your handling code here:
        if ("Netto".equals(vatItem.getText())) {
            vatItem.setText("Brutto");
            vat = 1.23;
        } else {
            vatItem.setText("Netto");
            vat = 1;
        }
        populateTable(blindList.blindList);
        totalPriceLabel1.setText(totalPrice());
    }//GEN-LAST:event_vatItemActionPerformed

    private void currencyItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currencyItemActionPerformed
        // TODO add your handling code here:
        if ("PLN".equals(currencyItem.getText())) {
            currencyItem.setText("EUR");
            currency = 0.23;
        } else {
            currencyItem.setText("PLN");
            currency = 1;
        }
        populateTable(blindList.blindList);
        totalPriceLabel1.setText(totalPrice());
    }//GEN-LAST:event_currencyItemActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        NewClientFrame newClientFrame = new NewClientFrame(this, clientBox);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            // TODO add your handling code here:
            NewClientFrame newClientFrame = new NewClientFrame(this, clientBox.getSelectedItem().toString(), clientBox);
        } catch (SQLException ex) {
            Logger.getLogger(SuncoMainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void clientBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientBoxActionPerformed
        // TODO add your handling code here:
        if (clientBox.getSelectedItem() != null) {
            blindList.clientName = clientBox.getSelectedItem().toString();
        }
    }//GEN-LAST:event_clientBoxActionPerformed

    private void deleteClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteClientActionPerformed
        try {
            // TODO add your handling code here:
            PreparedStatement pst = con.prepareStatement("delete from klienci where firma = '" + clientBox.getSelectedItem().toString() + "'");
            pst.executeUpdate();
            populateComboBox("select firma from klienci", clientBox);
        } catch (SQLException ex) {
            Logger.getLogger(SuncoMainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_deleteClientActionPerformed

    private void customOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customOrderActionPerformed
        // TODO add your handling code here:
        CustomOrderFrame customOrderFrame = new CustomOrderFrame(this);
    }//GEN-LAST:event_customOrderActionPerformed

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SuncoMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new SuncoMainWindow().setVisible(true);
            } catch (UnsupportedLookAndFeelException e) {
                ErrorLog.logError(e);
            } catch (IOException | SQLException ex) {
                Logger.getLogger(SuncoMainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton addButton1;
    private javax.swing.JComboBox<String> clientBox;
    private javax.swing.JMenuItem currencyItem;
    private javax.swing.JMenuItem customOrder;
    private javax.swing.JCheckBox dbCheckBox;
    private javax.swing.JMenuItem dbEditMenuItem;
    private javax.swing.JMenu dbMenu;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton deleteClient;
    private javax.swing.JButton finalButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JMenu jmenu3;
    private javax.swing.JLabel totalPriceLabel;
    private javax.swing.JLabel totalPriceLabel1;
    private javax.swing.JMenuItem vatItem;
    // End of variables declaration//GEN-END:variables
}
