package controlador;

import excepciones.EntradaInvalidaException;

/** Validaciones de entrada que se aplican antes de enviar datos al modelo. */
public final class Validador {
    private Validador() { }

    /** Convierte un texto en un código numérico positivo o lanza EntradaInvalidaException. */
    public static int codigo(String campo, String texto) throws EntradaInvalidaException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new EntradaInvalidaException("El campo \"" + campo + "\" es obligatorio.");
        }
        try {
            int valor = Integer.parseInt(texto.trim());
            if (valor <= 0) {
                throw new EntradaInvalidaException("El campo \"" + campo + "\" debe ser mayor que cero.");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new EntradaInvalidaException(
                    "El campo \"" + campo + "\" debe contener solo dígitos (máximo 9).");
        }
    }

    /** Verifica que un campo de texto obligatorio no esté vacío. */
    public static String texto(String campo, String texto) throws EntradaInvalidaException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new EntradaInvalidaException("El campo \"" + campo + "\" es obligatorio.");
        }
        return texto.trim();
    }
}
