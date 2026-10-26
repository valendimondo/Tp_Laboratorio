import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;

/** Paso 4 - Ventana principal: pestañas, menú, toolbar, formulario, progreso y guardado. */
public class VentanaPrincipal extends JFrame {
    private final GestorBD gestor = new GestorBD();

    // ---- Formulario ----
    private final JComboBox<String> comboTipoCliente = new JComboBox<>(new String[]{"Persona Física", "Empresa"});
    private final JTextField campoNombre = new JTextField(20);
    // JSpinner con SpinnerNumberModel acotado: mínimo 0, máximo 1.000.000.000, paso 1000
    private final JSpinner spIngresos = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1_000_000_000.0, 1000.0));
    private final JSpinner spGastos   = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1_000_000_000.0, 1000.0));
    private final JCheckBox chkHistorial = new JCheckBox("Buen historial crediticio");
    private final JLabel lblRubro = new JLabel("Rubro:");
    private final JTextField campoRubro = new JTextField(20);
    private final JLabel lblDirector = new JLabel("Director:");
    private final JTextField campoDirector = new JTextField(20);
    private final JButton botonGuardar = new JButton();

    // ---- Vista de base de datos ----
    private final DefaultTableModel modeloClientes = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Ingresos", "Gastos", "Historial"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    // ---- Barra de progreso (parte inferior) ----
    private final JProgressBar barra = new JProgressBar();

    // ---- Acciones únicas: menú, toolbar y botón comparten la misma lógica ----
    private final Action accionGuardar = new AbstractAction("Guardar") {
        @Override public void actionPerformed(ActionEvent e) { guardar(); }
    };
    private final Action accionRefrescar = new AbstractAction("Refrescar") {
        @Override public void actionPerformed(ActionEvent e) { cargarClientes(); }
    };
    private final Action accionUsuarios = new AbstractAction("Gestión de usuarios") {
        @Override public void actionPerformed(ActionEvent e) { abrirUsuarios(); }
    };
    private final Action accionSalir = new AbstractAction("Salir") {
        @Override public void actionPerformed(ActionEvent e) { System.exit(0); }
    };

    public VentanaPrincipal() {
        super("Evaluador de Créditos");
        setSize(660, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        accionGuardar.putValue(Action.SHORT_DESCRIPTION, "Guardar y evaluar al cliente");
        accionRefrescar.putValue(Action.SHORT_DESCRIPTION, "Recargar el listado de clientes");
        accionUsuarios.putValue(Action.SHORT_DESCRIPTION, "Abrir la gestión de usuarios");

        setJMenuBar(construirMenu());

        JTabbedPane pestanias = new JTabbedPane();
        pestanias.addTab("Nuevo análisis", construirFormulario());
        pestanias.addTab("Base de datos", new JScrollPane(new JTable(modeloClientes)));

        add(construirToolbar(), BorderLayout.NORTH);
        add(pestanias, BorderLayout.CENTER);
        add(barra, BorderLayout.SOUTH);

        botonGuardar.setAction(accionGuardar);
        comboTipoCliente.addActionListener(e -> actualizarCamposEmpresa());
        actualizarCamposEmpresa();
        cargarClientes();
    }

    // ================= Construcción de la interfaz =================

    private JMenuBar construirMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu archivo = new JMenu("Archivo");
        archivo.add(new JMenuItem(accionGuardar));
        archivo.addSeparator();
        archivo.add(new JMenuItem(accionSalir));
        JMenu admin = new JMenu("Administración");
        admin.add(new JMenuItem(accionUsuarios));
        mb.add(archivo);
        mb.add(admin);
        return mb;
    }

    private JToolBar construirToolbar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.add(accionGuardar);        // orden lógico: crear -> consultar -> administrar
        tb.add(accionRefrescar);
        tb.addSeparator();
        tb.add(accionUsuarios);
        return tb;
    }

    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.anchor = GridBagConstraints.WEST;
        Component[][] filas = {
            {new JLabel("Tipo de cliente:"), comboTipoCliente},
            {new JLabel("Nombre:"), campoNombre},
            {new JLabel("Ingresos:"), spIngresos},
            {new JLabel("Gastos:"), spGastos},
            {new JLabel(), chkHistorial},
            {lblRubro, campoRubro},
            {lblDirector, campoDirector},
            {new JLabel(), botonGuardar}
        };
        for (int i = 0; i < filas.length; i++) {
            g.gridy = i;
            g.gridx = 0; p.add(filas[i][0], g);
            g.gridx = 1; p.add(filas[i][1], g);
        }
        return p;
    }

    /** Rubro y director solo se ven cuando el tipo seleccionado es Empresa. */
    private void actualizarCamposEmpresa() {
        boolean empresa = comboTipoCliente.getSelectedIndex() == 1;
        lblRubro.setVisible(empresa);    campoRubro.setVisible(empresa);
        lblDirector.setVisible(empresa); campoDirector.setVisible(empresa);
        revalidate();
        repaint();
    }

    // ================= Guardado =================

    private void guardar() {
        if (!botonGuardar.isEnabled()) return;   // evita doble guardado en curso

        // --- Recolectar y validar ---
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            Errores.aviso(this, "El nombre no puede estar vacío.");
            campoNombre.requestFocus();
            return;
        }
        boolean empresa = comboTipoCliente.getSelectedIndex() == 1;
        String rubro = campoRubro.getText().trim();
        String director = campoDirector.getText().trim();
        if (empresa && (rubro.isEmpty() || director.isEmpty())) {
            Errores.aviso(this, "Para una empresa, rubro y director son obligatorios.");
            return;
        }
        double ingresos, gastos;
        try {
            spIngresos.commitEdit();
            spGastos.commitEdit();
            ingresos = ((Number) spIngresos.getValue()).doubleValue();
            gastos = ((Number) spGastos.getValue()).doubleValue();
        } catch (ParseException ex) {
            Errores.aviso(this, "Ingresos o gastos con formato numérico inválido.");
            return;
        }
        if (ingresos <= 0) {
            Errores.aviso(this, "Los ingresos deben ser mayores a cero.");
            return;
        }

        // --- Construir el Cliente según el tipo (polimorfismo) ---
        final Cliente cliente = empresa
                ? new Empresa(nombre, ingresos, gastos, chkHistorial.isSelected(), rubro, director)
                : new PersonaFisica(nombre, ingresos, gastos, chkHistorial.isSelected());

        // --- Persistir: cliente (devuelve id) y luego su tabla hija usando ese id ---
        Tarea.ejecutar(barra, new Component[]{botonGuardar},
            () -> {
                int id = gestor.guardarCliente(cliente);
                if (cliente instanceof Empresa) {
                    Empresa e = (Empresa) cliente;
                    gestor.guardarEmpresa(id, e.getRubro(), e.getDirector());
                } else {
                    gestor.guardarPersonaFisica(id);
                }
                return id;
            },
            id -> {
                double r = cliente.evaluarRiesgo();
                String res = r == 1 ? "APTO" : r == 0.5 ? "APTO CON RESERVAS" : "NO APTO";
                JOptionPane.showMessageDialog(this,
                        "Cliente guardado (ID " + id + ").\nResultado de la evaluación: " + res,
                        "Evaluación", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarClientes();
            },
            ex -> Errores.mostrar(this, ex));   // el formulario conserva lo tipeado
    }

    private void limpiarFormulario() {
        campoNombre.setText("");
        campoRubro.setText("");
        campoDirector.setText("");
        spIngresos.setValue(0.0);
        spGastos.setValue(0.0);
        chkHistorial.setSelected(false);
    }

    private void cargarClientes() {
        Tarea.ejecutar(barra, new Component[0],
            gestor::listarClientes,
            filas -> {
                modeloClientes.setRowCount(0);
                for (Object[] f : filas) modeloClientes.addRow(f);
            },
            ex -> Errores.mostrar(this, ex));
    }

    // ================= Acceso a la ventana de usuarios =================

    private void abrirUsuarios() {
        barra.setIndeterminate(true);
        Timer t = new Timer(300, e -> {              // pausa breve para que la barra se vea
            try {
                // TODO: descomentar cuando exista VentanaUsuarios (la hace tu compañero)
                // new VentanaUsuarios().setVisible(true);
                JOptionPane.showMessageDialog(this,
                        "La ventana de usuarios todavía no está conectada.",
                        "Pendiente", JOptionPane.INFORMATION_MESSAGE);
            } finally {
                barra.setIndeterminate(false);       // siempre se detiene
            }
        });
        t.setRepeats(false);
        t.start();
    }
}
