import javax.swing.*;
import java.awt.Component;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/** Punto 6: traduce excepciones a mensajes claros con JOptionPane. */
public class Errores {
    public static void mostrar(Component padre, Throwable t) {
        String msg;
        String titulo = "Error";
        int tipo = JOptionPane.ERROR_MESSAGE;

        if (t instanceof SQLException) {
            SQLException e = (SQLException) t;
            String estado = e.getSQLState();
            if (e instanceof SQLIntegrityConstraintViolationException || e.getErrorCode() == 1062) {
                msg = "El dato ingresado ya existe (valor duplicado).\nElija otro e intente de nuevo.";
                titulo = "Dato duplicado";
                tipo = JOptionPane.WARNING_MESSAGE;
            } else if (estado != null && estado.startsWith("08")) {   // clase 08 = error de conexión
                msg = "No se pudo conectar con MySQL.\nVerifique que el servidor esté encendido "
                        + "y que las credenciales sean correctas.";
                titulo = "Error de conexión";
            } else {
                msg = "Error de base de datos:\n" + e.getMessage();
            }
        } else {
            msg = "Error inesperado:\n" + t.getMessage();
        }
        JOptionPane.showMessageDialog(padre, msg, titulo, tipo);
    }

    public static void aviso(Component padre, String msg) {
        JOptionPane.showMessageDialog(padre, msg, "Atención", JOptionPane.WARNING_MESSAGE);
    }
}
