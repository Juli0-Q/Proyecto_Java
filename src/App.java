import sistema.Biblioteca;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase principal del programa: aquí está el método main.
 *
 * Para abrir el sistema se ejecuta esta clase.
 */ 
public static void main(String[] args) {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // aspecto del sistema
    } catch (Exception e) {
        // si falla, se usa el aspecto normal de Java
    }
    SwingUtilities.invokeLater(() -> new Biblioteca().setVisible(true));
    
}

