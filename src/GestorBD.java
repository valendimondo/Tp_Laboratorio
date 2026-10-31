import modelo.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Paso 3: única clase que habla con MySQL. Todos los métodos propagan SQLException. */
public class GestorBD {
    // Ajustar a la configuración de tu MySQL
    private final String url = "jdbc:mysql://localhost:3306/evaluador_creditos";
    private final String usuario = "Torres";
    private final String password = "123";

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }

    // ================= Clientes =================

    /** Inserta en la tabla padre y devuelve el id autogenerado. */
    public int guardarCliente(Cliente c) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, ingresos, gastos, historial) VALUES (?, ?, ?, ?)";
        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setDouble(2, c.getIngresos());
            ps.setDouble(3, c.getGastos());
            ps.setBoolean(4, c.tieneBuenHistorial());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("No se obtuvo el id generado del cliente.");
            }
        }
    }

    public void guardarPersonaFisica(int idCliente) throws SQLException {
        String sql = "INSERT INTO personas_fisicas (id_cliente) VALUES (?)";
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        }
    }

    public void guardarEmpresa(int idCliente, String rubro, String director) throws SQLException {
        String sql = "INSERT INTO empresas (id_cliente, rubro, director) VALUES (?, ?, ?)";
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setString(2, rubro);
            ps.setString(3, director);
            ps.executeUpdate();
        }
    }

    /** Filas {id, nombre, ingresos, gastos, historial} para la tabla de la ventana principal. */
    public List<Object[]> listarClientes() throws SQLException {
        String sql = "SELECT id_cliente, nombre, ingresos, gastos, historial FROM clientes ORDER BY id_cliente";
        List<Object[]> filas = new ArrayList<>();
        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{
                        rs.getInt("id_cliente"),
                        rs.getString("nombre"),
                        rs.getDouble("ingresos"),
                        rs.getDouble("gastos"),
                        rs.getBoolean("historial") ? "Bueno" : "Malo"});
            }
        }
        return filas;
    }

    // ================= Usuarios =================

    /** true solo si usuario Y clave coinciden exactamente. */
    public boolean validarUsuario(String nombreUsuario, String clave) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE nombre_usuario = ? AND clave = ?";
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, clave);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Si el nombre ya existe, MySQL lanza una violación de unicidad (SQLException) que sube a la ventana. */
    public void insertarUsuario(String nombreUsuario, String clave) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre_usuario, clave) VALUES (?, ?)";
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, clave);
            ps.executeUpdate();
        }
    }

    /** Filas {id, nombre_usuario}. La clave no se devuelve. */
    public List<Object[]> listarUsuarios() throws SQLException {
        String sql = "SELECT id_usuario, nombre_usuario FROM usuarios ORDER BY id_usuario";
        List<Object[]> filas = new ArrayList<>();
        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                filas.add(new Object[]{rs.getInt("id_usuario"), rs.getString("nombre_usuario")});
            }
        }
        return filas;
    }

    /** true si se eliminó al menos una fila; false si el id no existía (no es un error). */
    public boolean eliminarUsuario(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection con = conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }
}