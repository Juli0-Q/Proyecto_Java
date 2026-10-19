package excepciones;

/** Error: un campo del formulario está vacío o no contiene un número válido. */
public class EntradaInvalidaException extends Exception {

    /**
     * @param mensaje texto que se mostrará al usuario en la ventana de error
     */
    public EntradaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
