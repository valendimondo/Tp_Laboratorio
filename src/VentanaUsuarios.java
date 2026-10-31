import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Paso 4 - Gestión de usuarios: alta, listado (JTable ordenable) y baja con confirmación. */
public class VentanaUsuarios extends JFrame {
    private final GestorBD gestor = new GestorBD();

    private final JTextField campoNombreUsuario = new JTextField(15);
    private final JPasswordField campoClave = new JPasswordField(15);
    private final JButton botonCrearUsuario = new JButton("Crear usuario");
    private final JButton botonEliminarUsuario = new JButton("Eliminar seleccionado");
    private final JButton botonRefrescar = new JButton("Refrescar");
    private final JProgressBar barra = new JProgressBar();

    // El id está en el modelo (columna 0) aunque se oculta en la vista.
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Usuario"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
        @Override public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : String.class; }
    };
    private final JTable tabla = new JTable(modelo);

    // Una sola rutina de eliminación: la usan el botón y el menú contextual
    private final Action accionEliminar = new AbstractAction("Eliminar usuario") {
        @Override public void actionPerformed(ActionEvent e) { eliminarSeleccionado(); }
    };
    private final Action accionRefrescar = new AbstractAction("Refrescar listado") {
        @Override public void actionPerformed(ActionEvent e) { cargarUsuarios(); }
    };

    public VentanaUsuarios() {
        super("Gestión de usuarios");
        setSize(480, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);   // no cierra todo el programa

        botonEliminarUsuario.setAction(accionEliminar);
        botonEliminarUsuario.setText("Eliminar seleccionado");
        botonRefrescar.setAction(accionRefrescar);
        botonRefrescar.setText("Refrescar");

        // ----- Tabla: ordenable, sin editar, id oculto -----
        tabla.setRowSorter(new TableRowSorter<>(modelo));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.removeColumn(tabla.getColumnModel().getColumn(0));   // oculta la vista, no el modelo

        // ----- Menú contextual -----
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem(accionEliminar));
        popup.add(new JMenuItem(accionRefrescar));
        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { mostrarPopup(e, popup); }
            @Override public void mouseReleased(MouseEvent e) { mostrarPopup(e, popup); }
        });

        // ----- Panel superior: formulario de alta -----
        JPanel alta = new JPanel(new GridBagLayout());
        alta.setBorder(BorderFactory.createTitledBorder("Nuevo usuario"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = 0; alta.add(new JLabel("Usuario:"), g);
        g.gridx = 1;              alta.add(campoNombreUsuario, g);
        g.gridx = 0; g.gridy = 1; alta.add(new JLabel("Clave:"), g);
        g.gridx = 1;              alta.add(campoClave, g);
        g.gridx = 1; g.gridy = 2; alta.add(botonCrearUsuario, g);
        botonCrearUsuario.addActionListener(e -> crearUsuario());

        // ----- Panel inferior: listado + botones + progreso -----
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botones.add(botonEliminarUsuario);
        botones.add(botonRefrescar);
        JPanel abajo = new JPanel(new BorderLayout());
        abajo.add(new JScrollPane(tabla), BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout());
        sur.add(botones, BorderLayout.CENTER);
        sur.add(barra, BorderLayout.SOUTH);
        abajo.add(sur, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, alta, abajo);
        split.setDividerLocation(160);
        split.setOneTouchExpandable(true);
        add(split);

        cargarUsuarios();
    }

    private Component[] controles() {
        return new Component[]{botonCrearUsuario, botonEliminarUsuario, botonRefrescar};
    }

    private void mostrarPopup(MouseEvent e, JPopupMenu popup) {
        if (!e.isPopupTrigger()) return;
        int fila = tabla.rowAtPoint(e.getPoint());
        if (fila >= 0) tabla.setRowSelectionInterval(fila, fila);   // selecciona la fila bajo el cursor
        popup.show(e.getComponent(), e.getX(), e.getY());
    }

    /** Consulta el listado y vuelca el resultado en el modelo. */
    private void cargarUsuarios() {
        Tarea.ejecutar(barra, controles(),
            gestor::listarUsuarios,
            filas -> {
                modelo.setRowCount(0);
                for (Object[] f : filas) modelo.addRow(f);
            },
            ex -> Errores.mostrar(this, ex));
    }

    private void crearUsuario() {
        String nombre = campoNombreUsuario.getText().trim();
        String clave = new String(campoClave.getPassword());
        if (nombre.isEmpty() || clave.isEmpty()) {
            Errores.aviso(this, "Usuario y clave no pueden estar vacíos.");
            return;
        }
        Tarea.ejecutar(barra, controles(),
            () -> { gestor.insertarUsuario(nombre, clave); return null; },
            ok -> {
                campoNombreUsuario.setText("");
                campoClave.setText("");
                cargarUsuarios();
            },
            ex -> Errores.mostrar(this, ex));   // duplicado -> mensaje; campos intactos
    }

    private void eliminarSeleccionado() {
        if (!botonEliminarUsuario.isEnabled()) return;   // hay una operación en curso
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            Errores.aviso(this, "Seleccione un usuario de la tabla.");
            return;
        }
        // Con el sorter activo hay que convertir el índice de vista al del modelo
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        int id = (Integer) modelo.getValueAt(filaModelo, 0);
        String nombre = (String) modelo.getValueAt(filaModelo, 1);

        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al usuario \"" + nombre + "\"?", "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;

        Tarea.ejecutar(barra, controles(),
            () -> gestor.eliminarUsuario(id),
            eliminado -> {
                if (!eliminado) {
                    JOptionPane.showMessageDialog(this,
                            "No se eliminó ningún usuario (ya no existía).",
                            "Sin cambios", JOptionPane.INFORMATION_MESSAGE);
                }
                cargarUsuarios();   // refresca en ambos casos para reflejar la base real
            },
            ex -> Errores.mostrar(this, ex));
    }
}