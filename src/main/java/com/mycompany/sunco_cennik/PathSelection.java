/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.sunco_cennik;

import com.itextpdf.text.DocumentException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Hubert
 */
public class PathSelection extends javax.swing.JFrame {

    BlindList blindList;
    String type;
    SuncoMainWindow suncoMainWindow;

    /**
     * Creates new form PathSelection
     */
    public PathSelection() {
        initComponents();
    }

    public PathSelection(SuncoMainWindow suncoMainWindow, BlindList blindList, String type) {
        initComponents();
        FileFilter blindFilter = new FileNameExtensionFilter("Plik BLIND", "blind");
        fileChooser.addChoosableFileFilter(blindFilter);
        FileFilter pdfFilter = new FileNameExtensionFilter("Dokument PDF ", "pdf");
        fileChooser.addChoosableFileFilter(pdfFilter);
        FileFilter multiple = new FileNameExtensionFilter("Dokument PDF, Plik BLIND ", "blind", "pdf");
        fileChooser.addChoosableFileFilter(multiple);
        if (type.equals("pdf")) {
            fileChooser.setFileFilter(pdfFilter);

        } else {
            fileChooser.setFileFilter(blindFilter);
        }
        this.suncoMainWindow = suncoMainWindow;
        this.blindList = blindList;
        this.type = type;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });

        fileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        fileChooser.setCurrentDirectory(new java.io.File("C:\\Users\\Hubert\\OneDrive\\Pulpit"));
        fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileChooserActionPerformed(evt);
            }
        });
        fileChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                fileChooserPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(fileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fileChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileChooserActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            //File testFile = new File(blindList.offerName);
            File file = fileChooser.getSelectedFile();
            //    System.out.println(testFile.getAbsolutePath()); testy
            String filePath = file.getPath();
            switch (type) {
                case "blind-ex" -> {
                    if (!filePath.toLowerCase().endsWith(".blind")) {
                        file = new File(filePath + ".blind");
                    }
                    new SerializeFile(file, blindList);
                }
                case "blind-im" -> {
                    suncoMainWindow.blindList = new SerializeFile().getFromFile(file);
                }
                case "pdf" -> {
                    if (!filePath.toLowerCase().endsWith(".pdf")) {
                        file = new File(filePath + ".pdf");
                    }
                    try {
                        new PDFCreator(file, blindList);
                    } catch (FileNotFoundException | DocumentException e) {
                        ErrorLog.logError(e);
                    } catch (IOException e) {
                        ErrorLog.logError(e);
                    }
                }
            }

        }
        suncoMainWindow.setEnabled(true);
        this.dispose();
    }//GEN-LAST:event_fileChooserActionPerformed
    private void fileChooserPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_fileChooserPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_fileChooserPropertyChange
    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowGainedFocus

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
            java.util.logging.Logger.getLogger(PathSelection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PathSelection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PathSelection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PathSelection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new PathSelection().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser fileChooser;
    // End of variables declaration//GEN-END:variables
}
