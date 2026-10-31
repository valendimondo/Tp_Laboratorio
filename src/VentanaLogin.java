import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/** Paso 4 - Ventana de login: barrera de acceso al sistema. */
public class VentanaLogin extends JFrame {
    private final GestorBD gestor = new GestorBD();
    private final JTextField campoUsuario = new JTextField(15);
    private final JPasswordField campoClave = new JPasswordField(15);
    private final JButton botonIngresar = new JButton("Ingresar");

    public VentanaLogin() {
        super("Evaluador de Créditos - Acceso");
        setSize(340, 190);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        form.add(new JLabel("Usuario:"));
        form.add(campoUsuario);
        form.add(new JLabel("Clave:"));
        form.add(campoClave);

        JPanel boton = new JPanel();
        boton.add(botonIngresar);

        add(form, BorderLayout.CENTER);
        add(boton, BorderLayout.SOUTH);

        botonIngresar.addActionListener(e -> ingresar());
        getRootPane().setDefaultButton(botonIngresar);   // Enter = Ingresar
    }

    private void ingresar() {
        String usuario = campoUsuario.getText().trim();
        String clave = new String(campoClave.getPassword());
        if (usuario.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese usuario y clave.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (gestor.validarUsuario(usuario, clave)) {
                dispose();                                   // destruye el login
                new VentanaPrincipal().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o clave incorrectos.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                campoClave.setText("");
                campoClave.requestFocus();
            }
        } catch (SQLException ex) {
            Errores.mostrar(this, ex);
        }
    }
}