import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class suncoMainWindow {

    JFrame f;
    private JPanel mainPanel;
    private JTable mainTable;
    private JButton addButton;
    private JButton submitButton;
    private JButton deleteButton;

    public suncoMainWindow() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel mainTableModel = (DefaultTableModel) mainTable.getModel();
                if(mainTable.getSelectedRowCount()>0){
                    mainTableModel.removeRow(mainTable.getSelectedRow());
                }
                else if (mainTable.getRowCount()==0){
                    JOptionPane.showMessageDialog(mainPanel, "Tabela jest pusta.");
                }
                else{
                    JOptionPane.showMessageDialog(mainPanel, "Zaznacz pola do usunięcia.");
                }
            }
        });
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        f = new JFrame("Sunco - Wycena rolet");
        f.add(mainPanel);
        String[] columnNames = {"Id.", "Model rolety", "Ilość", "Kolor", "Profil", "Dopłaty", "Dodatki", "Automatyka", "Akcesoria", "Cena"};
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
    public static void main(String[] args) {
        new suncoMainWindow();
    }
}
