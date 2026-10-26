import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** GESTOR FALSO (en memoria) solo para probar la ventana. Lo real lo hace tu compañero. */
public class GestorBD {
    private static final List<Object[]> clientes = new ArrayList<>();
    private static int sig = 1;

    public int guardarCliente(Cliente c) throws SQLException {
        int id = sig++;
        clientes.add(new Object[]{id, c.getNombre(), c.getIngresos(), c.getGastos(),
                c.tieneBuenHistorial() ? "Bueno" : "Malo"});
        return id;
    }
    public void guardarPersonaFisica(int idCliente) throws SQLException { }
    public void guardarEmpresa(int idCliente, String rubro, String director) throws SQLException { }
    public List<Object[]> listarClientes() throws SQLException { return new ArrayList<>(clientes); }
}
