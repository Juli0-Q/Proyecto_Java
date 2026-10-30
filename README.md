## SISTEMA DE GESTION Y CONTROL DE PRESTAMOS DE UNA BIBLIOTECA
En una biblioteca, llevar el control de los libros, usuarios, préstamos y devoluciones de manera manual puede generar errores y pérdida de información. Además, puede resultar difícil conocer qué libros están disponibles, quién tiene un libro prestado y cuándo debe ser devuelto. Por este motivo, se propone desarrollar un sistema de gestión de biblioteca utilizando Programación Orientada a Objetos, que permita organizar y facilitar el control de libros, usuarios y préstamos.

## INTEGRADOS
- Diego Miguel Ordinola Rodríguez 
- Garrido Mantilla Yohan Javier 
- Quiñonez Morón Julio Marcelo 
- Rodríguez Iñigua Fabrizio Leonardo

## BREVE EXPLICACION DEL CODIGO
## Arquitectura General (MVC)
El proyecto está estructurado para separar la interfaz gráfica de la lógica de datos y negocio:
Modelo (Libro, Usuario, Prestamo, Excepciones): Define las entidades con sus atributos, estados y validaciones básicas.
Controlador (ControladorBiblioteca): Centraliza todas las reglas de negocio, el manejo de listas en memoria, los cálculos de fechas y la persistencia en archivos CSV.
Vista (VentanaPrincipal): Interfaz gráfica construida con Java Swing organizada en 4 pestañas interactivas.

## Funcionamiento por Módulos (Pestañas)
📖 Catálogo de Libros: Permite dar de alta nuevos títulos ingresando título y autor. El sistema genera el código de forma automática (LIB-001, LIB-002, etc.) con stock inicial en cero. Cuenta con un buscador en tiempo real (KeyAdapter) que filtra al instante por código, título o autor.

📦 Gestión de Stock: Muestra el inventario físico actual de los libros. Seleccionando un libro de la tabla, el administrador puede ingresar una cantidad entera positiva (para sumar ejemplares) o negativa (para restar) de forma dinámica.

👥 Gestión de Usuarios: Administra el registro de los lectores con códigos automáticos (US-001). Controla visualmente si un usuario se encuentra activo o Bloqueado por multas o retrasos, e incluye un buscador en tiempo real por nombre o código.

🔄 Préstamos y Devoluciones:
- Préstamos: Valida automáticamente que el libro tenga stock disponible y que el usuario no esté bloqueado. Asigna un plazo estándar de 7 días.
- Devoluciones: Al registrar una devolución mediante el código del préstamo, el sistema calcula los días transcurridos. Si excede los 7 días, bloquea automáticamente al usuario y restituye el stock.
- Bajas y Reposiciones: Permite dar de baja un ejemplar por pérdida o daño (descontando el stock permanentemente y bloqueando al usuario) o revertir dicha baja si el libro es recuperado o pagado años después, desbloqueando al lector y reintegrando el ejemplar al inventario.

## Persistencia Automática de Datos (CSV)
Al Iniciar: El ControladorBiblioteca lee automáticamente los archivos Inventario_Biblioteca.csv y Registro_Usuarios.csv para cargar toda la información previa en la memoria RAM.

Al Cerrar: Gracias al evento WindowListener implementado en la ventana principal, en el preciso instante en que el usuario cierra la aplicación (haciendo clic en la "X"), se ejecuta un guardado automático (exportarTodoACSV), asegurando que ningún cambio se pierda.

## Características de Usabilidad
Limpieza Global: Cada pestaña cuenta con un botón "Limpiar" que no solo vacía los formularios y restablece los códigos automáticos, sino que restaura las listas completas en las tablas si se estaba realizando una búsqueda.

Búsquedas Dinámicas: Todas las pestañas disponen de campos de búsqueda en tiempo real que filtran los registros a medida que se escribe.