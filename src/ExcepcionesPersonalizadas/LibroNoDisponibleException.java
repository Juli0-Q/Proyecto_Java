package ExcepcionesPersonalizadas;

/**
 * Excepción personalizada que se lanza al intentar realizar un préstamo 
 * de un libro que ya se encuentra con estado "Prestado".
 * 
 * @author Sistema Biblioteca
 * @version 3.0
 */
public class LibroNoDisponibleException extends Exception {
    
    /**
     * Constructor de la excepción.
     * @param mensaje Descripción detallada del motivo por el cual no está disponible.
     */
    public LibroNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}