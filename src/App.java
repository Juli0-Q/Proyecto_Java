public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("==================================");
        System.out.println(" SISTEMA DE GESTIÓN DE BIBLIOTECA ");
        System.out.println("==================================");

        String titulo = "El Quijote";

        // Validación simple del título
        if (titulo != null && !titulo.trim().isEmpty()) {
            System.out.println("Título registrado correctamente: " + titulo);
        } else {
            System.out.println("Error: El título no puede estar vacío.");
        }
    }
}
