package vista;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/** DocumentFilter que solo permite dígitos (con longitud máxima) en un campo de texto. */
public class FiltroNumerico extends DocumentFilter {
    private final int maxDigitos;

    public FiltroNumerico(int maxDigitos) {
        this.maxDigitos = maxDigitos;
    }

    public static void aplicar(JTextField campo, int maxDigitos) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroNumerico(maxDigitos));
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attr)
            throws BadLocationException {
        if (texto == null) return;
        String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
        String nuevo = new StringBuilder(actual).insert(offset, texto).toString();
        if (nuevo.matches("\\d{0," + maxDigitos + "}")) {
            super.insertString(fb, offset, texto, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int longitud, String texto, AttributeSet attr)
            throws BadLocationException {
        String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
        String nuevo = new StringBuilder(actual)
                .replace(offset, offset + longitud, texto == null ? "" : texto).toString();
        if (nuevo.matches("\\d{0," + maxDigitos + "}")) {
            super.replace(fb, offset, longitud, texto, attr);
        }
    }
}
