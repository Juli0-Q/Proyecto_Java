# SISTEMA DE GESTION Y CONTROL DE PRESTAMOS DE UNA BIBLIOTECA
En una biblioteca, llevar el control de los libros, usuarios, préstamos y devoluciones de manera manual puede generar errores y pérdida de información. Además, puede resultar difícil conocer qué libros están disponibles, quién tiene un libro prestado y cuándo debe ser devuelto. Por este motivo, se propone desarrollar un sistema de gestión de biblioteca utilizando Programación Orientada a Objetos, que permita organizar y facilitar el control de libros, usuarios y préstamos.

# INTEGRADOS
Diego Miguel Ordiñola Rodríguez
Garrido Mantilla Yohan Javier
Quiñonez Morón Julio Marcelo
Rodríguez Iñigua Fabrizio Leonardo

# DIAGRAMA DE CLASES
classDiagram
    class libro {
        -int codigo
        -String titulo
        -String autor
        -boolean disponible
        +prestar() void
        +devolver() void
        +isDisponible() boolean
        +toString() String
    }
    class usuario {
        -int codigo
        -String nombre
        +getCodigo() int
        +getNombre() String
    }
    class prestamo {
        -int codigo
        -libro libro
        -usuario usuario
        -LocalDate fecha
        -boolean activo
        +finalizarPrestamo() void
    }
    class biblioteca {
        -ArrayList~libro~ libros
        -ArrayList~usuario~ usuarios
        -ArrayList~prestamo~ prestamos
        +agregarLibro(libro) void
        +buscarLibro(int) libro
        +buscarLibro(String) libro
        +realizarPrestamo(int, int, int) void
        +devolverLibro(int) void
        +listarLibros() void
    }
    class GestorArchivos {
        +guardarLibros(List~libro~, String) void
        +cargarLibros(String) List~libro~
    }

    biblioteca "1" *-- "*" libro
    biblioteca "1" *-- "*" usuario
    biblioteca "1" *-- "*" prestamo
    prestamo "*" --> "1" libro
    prestamo "*" --> "1" usuario
    GestorArchivos ..> libro