import java.util.Scanner;
import Biblioteca.biblioteca;
import Biblioteca.libro;
import Biblioteca.usuario;

public class App {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        biblioteca Biblioteca = new biblioteca();

        // Libros iniciales
        Biblioteca.agregarLibro(
            new libro(1, "El Principito", "Antoine de Saint-Exupéry")
        );

        Biblioteca.agregarLibro(
            new libro(2, "Don Quijote", "Miguel de Cervantes")
        );

        Biblioteca.agregarLibro(
            new libro(3, "Cien años de soledad", "Gabriel García Márquez")
        );

        // Usuarios iniciales
        Biblioteca.agregarUsuario(
                new usuario(1, "Julio")
        );

        Biblioteca.agregarUsuario(
                new usuario(2, "Carlos")
        );

        int opcion = 0;

        do {

            System.out.println("\n===== SISTEMA DE BIBLIOTECA =====");
            System.out.println("1. Listar libros");
            System.out.println("2. Buscar libro por código");
            System.out.println("3. Buscar libro por título");
            System.out.println("4. Listar usuarios");
            System.out.println("5. Realizar préstamo");
            System.out.println("6. Devolver libro");
            System.out.println("7. Listar préstamos");
            System.out.println("8. Salir");
            System.out.print("Seleccione una opción: ");

            try {

                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {

                    case 1:
                        Biblioteca.listarLibros();
                        break;

                    case 2:
                        System.out.print("Ingrese el código del libro: ");
                        int codigo = Integer.parseInt(scanner.nextLine());

                        libro libro = Biblioteca.buscarLibro(codigo);

                        if (libro != null) {
                            System.out.println(libro);
                        } else {
                            System.out.println("Libro no encontrado.");
                        }
                        break;

                    case 3:
                        System.out.print("Ingrese el título del libro: ");
                        String titulo = scanner.nextLine();

                        libro libroTitulo = Biblioteca.buscarLibro(titulo);

                        if (libroTitulo != null) {
                            System.out.println(libroTitulo);
                        } else {
                            System.out.println("Libro no encontrado.");
                        }
                        break;

                    case 4:
                        Biblioteca.listarUsuarios();
                        break;

                    case 5:

                        System.out.print("Código del préstamo: ");
                        int codigoPrestamo = Integer.parseInt(scanner.nextLine());

                        System.out.print("Código del libro: ");
                        int codigoLibro = Integer.parseInt(scanner.nextLine());

                        System.out.print("Código del usuario: ");
                        int codigoUsuario = Integer.parseInt(scanner.nextLine());

                        Biblioteca.realizarPrestamo(
                                codigoPrestamo,
                                codigoLibro,
                                codigoUsuario
                        );

                        break;

                    case 6:

                        System.out.print("Código del libro a devolver: ");
                        int codigoDevolucion = Integer.parseInt(scanner.nextLine());

                        Biblioteca.devolverLibro(codigoDevolucion);

                        break;

                    case 7:
                        Biblioteca.listarPrestamos();
                        break;

                    case 8:
                        System.out.println("Programa finalizado.");
                        break;

                    default:
                        System.out.println("Opción no válida.");
                }

            } catch (NumberFormatException e) {

                System.out.println("Error: debe ingresar un número válido.");

            }

        } while (opcion != 8);

        scanner.close();
    }
}