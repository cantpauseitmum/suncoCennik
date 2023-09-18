import javax.swing.JFrame;

public class main {
    JFrame f;

    main() {
        f = new JFrame("Sunco - Wycena rolet");
//        suncoMainWindow suncoMainWindowClass = new suncoMainWindow();
        String[] columnNames = {"Id.", "Model rolety", "Ilość", "Kolor", "Profil", "Dopłaty", "Dodatki", "Automatyka", "Akcesoria", "Cena"};
        f.setSize(1200, 500);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        new main();
    }

}