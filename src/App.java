import controlador.BibliotecaControlador;
import vista.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Clase principal: arranca la interfaz gráfica. */
public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorada) {
            // si falla, se usa el aspecto por defecto de Swing
        }
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(new BibliotecaControlador()).setVisible(true));
    }
}
