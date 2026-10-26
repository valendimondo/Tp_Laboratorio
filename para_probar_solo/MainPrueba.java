import javax.swing.SwingUtilities;

public class MainPrueba {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
