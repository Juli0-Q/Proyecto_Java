package ExcepcionesPersonalizadas;

/**
 * Excepción personalizada que se lanza cuando se intenta registrar un elemento 
 * (como un libro o usuario) con un código que ya existe en el sistema.
 * 
 * @author Sistema Biblioteca
 * @version 3.0
 */
public class CodigoDuplicadoException extends Exception {
    
    /**
     * Constructor de la excepción.
     * @param mensaje Descripción detallada del error de duplicidad.
     */
    public CodigoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}