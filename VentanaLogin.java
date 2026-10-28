import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Ventana de login sin base de datos: las credenciales están fijas en el código.
 * Se asume que existe una clase VentanaPrincipal (JFrame).
 */
public class VentanaLogin extends JFrame {

    // Credenciales de prueba (cambialas por las que quieras)
    private static final String USUARIO_VALIDO = "admin";
    private static final String CLAVE_VALIDA = "1234";

    private final JTextField txtUsuario = new JTextField(15);
    private final JPasswordField txtClave = new JPasswordField(15);
    private final JButton btnIngresar = new JButton("Ingresar");

    public VentanaLogin() {
        configurarVentana();
        armarComponentes();
        configurarEventos();
    }

    // Ventana: tamaño, posición y comportamiento al cerrar
    private void configurarVentana() {
        setTitle("Iniciar sesión");
        setSize(350, 200);
        setLocationRelativeTo(null);                 // centrada en pantalla
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Layout en grilla + componentes
    private void armarComponentes() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Usuario:"));
        panel.add(txtUsuario);
        panel.add(new JLabel("Clave:"));
        panel.add(txtClave);
        panel.add(new JLabel());                     // celda vacía para alinear el botón
        panel.add(btnIngresar);

        add(panel);
        getRootPane().setDefaultButton(btnIngresar); // Enter también dispara el ingreso
    }

    private void configurarEventos() {
        btnIngresar.addActionListener(e -> intentarIngreso());
    }

    // Validación local y decisión según el resultado
    private void intentarIngreso() {
        String usuario = txtUsuario.getText().trim();
        char[] clave = txtClave.getPassword();

        try {
            if (usuario.isEmpty() || clave.length == 0) {
                JOptionPane.showMessageDialog(this,
                        "Completá usuario y clave.",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean acceso = usuario.equals(USUARIO_VALIDO)
                    && new String(clave).equals(CLAVE_VALIDA);

            if (acceso) {
                dispose();                           // cierra el login
                new VentanaPrincipal().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Usuario o clave incorrectos.",
                        "Credenciales inválidas", JOptionPane.WARNING_MESSAGE);
                txtClave.setText("");
                txtClave.requestFocusInWindow();
            }
        } finally {
            Arrays.fill(clave, ' ');                 // limpia la clave de memoria
        }
    }
}
