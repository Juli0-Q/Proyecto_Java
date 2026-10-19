## SISTEMA DE GESTION Y CONTROL DE PRESTAMOS DE UNA BIBLIOTECA
En una biblioteca, llevar el control de los libros, usuarios, préstamos y devoluciones de manera manual puede generar errores y pérdida de información. Además, puede resultar difícil conocer qué libros están disponibles, quién tiene un libro prestado y cuándo debe ser devuelto. Por este motivo, se propone desarrollar un sistema de gestión de biblioteca utilizando Programación Orientada a Objetos, que permita organizar y facilitar el control de libros, usuarios y préstamos.

## INTEGRADOS
- Diego Miguel Ordiñola Rodríguez 
- Garrido Mantilla Yohan Javier 
- Quiñonez Morón Julio Marcelo 
- Rodríguez Iñigua Fabrizio Leonardo

## BREVE EXPLICACION DEL CODIGO
El programa es un sistema para controlar los libros, los usuarios y los préstamos de una biblioteca, hecho en Java con una ventana gráfica. El código está organizado en tres partes: la clase principal App, la carpeta sistema con las cuatro clases que hacen funcionar el programa, y la carpeta excepciones con los errores propios del sistema.

App es la clase que se ejecuta. Su único trabajo es abrir la ventana principal del sistema.

La carpeta sistema tiene cuatro clases. Libro guarda el código, el título, el autor y si el libro está disponible o prestado. Usuario guarda el código y el nombre del lector. Prestamo relaciona un libro con un usuario y guarda la fecha del préstamo y si ya fue devuelto. Biblioteca es la clase central: guarda las listas de libros, usuarios y préstamos, tiene todas las operaciones (registrar, buscar, modificar, eliminar, prestar y devolver), maneja el archivo de datos y además es la ventana con la que el usuario trabaja.

La ventana tiene tres pestañas: Libros, Usuarios y Préstamos. En cada una hay una tabla con los datos y un formulario con botones. Al registrar o prestar, el programa revisa que los campos no estén vacíos y que los códigos sean números. Si algo está mal, por ejemplo un código repetido o un libro que ya está prestado, muestra un mensaje de error y sigue funcionando. Esto se logra con las excepciones propias del sistema, que se lanzan cuando se rompe una regla y se muestran al usuario en una ventana.

Después de cada cambio, el programa guarda las tres listas en un archivo llamado datos.dat, usando serialización de objetos de Java, que consiste en convertir los objetos en bytes para escribirlos en el disco. Cuando el programa se vuelve a abrir, lee ese archivo y recupera todo tal como estaba. Si el archivo no existe, empieza con unos datos de ejemplo.

Además, desde el menú Archivo se puede exportar toda la información a un archivo con extensión .csv, que se abre con Excel y muestra tres bloques: libros, usuarios y préstamos. Así los datos se pueden consultar o imprimir fuera del programa.

Para buscar libros, la clase Biblioteca tiene dos métodos con el mismo nombre, buscarLibro: uno recibe un código y otro recibe un título. Java sabe cuál usar según el tipo de dato que recibe, y a esto se le llama sobrecarga de métodos. La búsqueda por título no distingue mayúsculas de minúsculas, por lo que escribir “don quijote” encuentra “Don Quijote”.