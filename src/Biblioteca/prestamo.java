package Biblioteca;

public class prestamo  {
 private libro libro;
    private usuario usuario;
    private String fechaPrestamo;
    private String fechaDevolucion;

    public prestamo(libro libro, usuario usuario, String fechaPrestamo) {
        this.libro = libro;
        this.usuario = usuario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = "Pendiente";

        libro.prestar();
    }

    public void devolver(String fecha) {
        fechaDevolucion = fecha;
        libro.devolver();
    }

    @Override
    public String toString() {
        return "Prestamo{" +
                "libro=" + libro.getTitulo() +
                ", usuario=" + usuario.getNombre() +
                ", fechaPrestamo='" + fechaPrestamo + '\'' +
                ", fechaDevolucion='" + fechaDevolucion + '\'' +
                '}';
    }

    // CLASE ANIDADA
    public static class Informacion {

        public static void mostrarEstado(prestamo prestamo) {
            System.out.println("----- INFORMACIÓN DEL PRÉSTAMO -----");
            System.out.println("Libro: " + prestamo.libro.getTitulo());
            System.out.println("Usuario: " + prestamo.usuario.getNombre());
            System.out.println("Fecha de préstamo: " + prestamo.fechaPrestamo);
            System.out.println("Fecha de devolución: " + prestamo.fechaDevolucion);
        }
    }

}
