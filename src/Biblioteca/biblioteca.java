package Biblioteca;
import java.util.ArrayList;

public class biblioteca {

    private ArrayList<libro> libros;
    private ArrayList<usuario> usuarios;
    private ArrayList<prestamo> prestamos;

    public biblioteca() {
        libros = new ArrayList<>();
        usuarios = new ArrayList<>();
        prestamos = new ArrayList<>();
    }

    public void agregarLibro(libro libro) {
        libros.add(libro);
    }

    public void agregarUsuario(usuario usuario) {
        usuarios.add(usuario);
    }

    // Sobrecarga: buscar libro por código
    public libro buscarLibro(int codigo) {

        for (libro libro : libros) {
            if (libro.getCodigo() == codigo) {
                return libro;
            }
        }

        return null;
    }

    // Sobrecarga: buscar libro por título
    public libro buscarLibro(String titulo) {

        for (libro libro : libros) {
            if (libro.getTitulo().equalsIgnoreCase(titulo)) {
                return libro;
            }
        }

        return null;
    }

    public usuario buscarUsuario(int codigo) {

        for (usuario usuario : usuarios) {
            if (usuario.getCodigo() == codigo) {
                return usuario;
            }
        }

        return null;
    }

    public void listarLibros() {

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }

        System.out.println("\n===== LIBROS =====");

        for (libro libro : libros) {
            System.out.println(libro);
        }
    }

    public void listarUsuarios() {

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        System.out.println("\n===== USUARIOS =====");

        for (usuario usuario : usuarios) {
            System.out.println(usuario);
        }
    }

    public void realizarPrestamo(int codigoPrestamo, int codigoLibro, int codigoUsuario) {

        libro libro = buscarLibro(codigoLibro);
        usuario usuario = buscarUsuario(codigoUsuario);

        if (libro == null) {
            System.out.println("El libro no existe.");
            return;
        }

        if (usuario == null) {
            System.out.println("El usuario no existe.");
            return;
        }

        if (!libro.isDisponible()) {
            System.out.println("El libro ya está prestado.");
            return;
        }

        prestamo prestamo = new prestamo(codigoPrestamo, libro, usuario);

        prestamos.add(prestamo);
        libro.prestar();

        System.out.println("Préstamo realizado correctamente.");
    }

    public void devolverLibro(int codigoLibro) {

        libro libro = buscarLibro(codigoLibro);

        if (libro == null) {
            System.out.println("El libro no existe.");
            return;
        }

        if (libro.isDisponible()) {
            System.out.println("El libro ya está disponible.");
            return;
        }

        for (prestamo prestamo : prestamos) {

            if (prestamo.getLibro().getCodigo() == codigoLibro
                    && prestamo.isActivo()) {

                prestamo.finalizarPrestamo();
                libro.devolver();

                System.out.println("Libro devuelto correctamente.");
                return;
            }
        }

        System.out.println("No se encontró un préstamo activo.");
    }

    public void listarPrestamos() {

        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos registrados.");
            return;
        }

        System.out.println("\n===== PRÉSTAMOS =====");

        for (prestamo prestamo : prestamos) {
            System.out.println(prestamo);
        }
    }
}
