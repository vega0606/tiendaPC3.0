package Ventana;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import controlador.LoginController;
import modelo.DatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vista de login para el sistema de facturación e inventario
 */
public class VistaLogin extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(VistaLogin.class);
    
    private JPanel mainPanel;
    private JTextField usuarioField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel mensajeError;
    private JProgressBar progressBar;
    private LoginController controller;
    
    // Pool de hilos para tareas en segundo plano
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    public VistaLogin() {
        super("Sistema de Facturación e Inventario - Login");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Inicializar controlador
        controller = new LoginController();
        
        initComponents();
        testDatabaseConnection();
    }
    
    /**
     * Inicializa los componentes de la interfaz
     */
    private void initComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Panel central con formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null); // Layout absoluto para posicionamiento preciso
        formPanel.setBackground(new Color(240, 240, 240));
        
        // Logo o título
        JLabel logoLabel = new JLabel("SISTEMA");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBounds(100, 40, 200, 50);
        formPanel.add(logoLabel);
        
        JLabel subtitleLabel = new JLabel("Facturación e Inventario");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBounds(100, 80, 200, 30);
        formPanel.add(subtitleLabel);
        
        // Campo de usuario
        JLabel usuarioLabel = new JLabel("Usuario:");
        usuarioLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usuarioLabel.setBounds(50, 140, 100, 25);
        formPanel.add(usuarioLabel);
        
        usuarioField = new JTextField();
        usuarioField.setBounds(50, 170, 300, 35);
        usuarioField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usuarioField);
        
        // Campo de contraseña
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setBounds(50, 220, 100, 25);
        formPanel.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(50, 250, 300, 35);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordField);
        
        // Botón de login
        loginButton = new JButton("Iniciar Sesión");
        loginButton.setBounds(50, 310, 300, 40);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(74, 134, 232));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(loginButton);
        
        // Mensaje de error
        mensajeError = new JLabel("");
        mensajeError.setBounds(50, 360, 300, 25);
        mensajeError.setForeground(Color.RED);
        mensajeError.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(mensajeError);
        
        // Barra de progreso
        progressBar = new JProgressBar();
        progressBar.setBounds(50, 400, 300, 20);
        progressBar.setVisible(false);
        formPanel.add(progressBar);
        
        // Añadir eventos
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
        
        // Evento Enter en campo de contraseña
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
        
        // Pre-rellenar en modo desarrollo
        if (System.getProperty("dev.mode") != null) {
            usuarioField.setText("admin");
            passwordField.setText("admin123");
        }
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(220, 220, 220));
        footerPanel.setPreferredSize(new Dimension(0, 30));
        JLabel footerLabel = new JLabel("© 2025 Sistema de Facturación e Inventario");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        footerPanel.add(footerLabel);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * Prueba la conexión con la base de datos
     */
    private void testDatabaseConnection() {
        SwingUtilities.invokeLater(() -> {
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
            loginButton.setEnabled(false);
            mensajeError.setText("Conectando a la base de datos...");
            mensajeError.setForeground(Color.BLUE);
        });
        
        executor.submit(() -> {
            try {
                // Intenta obtener una conexión
                DatabaseConnector.getConnection().close();
                
                SwingUtilities.invokeLater(() -> {
                    progressBar.setVisible(false);
                    loginButton.setEnabled(true);
                    mensajeError.setText("");
                });
                
            } catch (SQLException e) {
                logger.error("Error al conectar con base de datos: {}", e.getMessage(), e);
                
                SwingUtilities.invokeLater(() -> {
                    progressBar.setVisible(false);
                    loginButton.setEnabled(true);
                    mensajeError.setText("Error de conexión a la base de datos");
                    mensajeError.setForeground(Color.RED);
                    
                    // Mostrar diálogo con detalles del error
                    JOptionPane.showMessageDialog(
                        VistaLogin.this,
                        "No se pudo conectar a la base de datos.\nVerifique la configuración y que el servidor esté activo.\n\nError: " + e.getMessage(),
                        "Error de Conexión",
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            }
        });
    }
    
    /**
     * Intenta el inicio de sesión con las credenciales ingresadas
     */
    private void attemptLogin() {
        String usuario = usuarioField.getText();
        String password = new String(passwordField.getPassword());
        
        if (usuario.isEmpty() || password.isEmpty()) {
            mensajeError.setText("Por favor ingrese usuario y contraseña");
            return;
        }
        
        // Deshabilitar controles y mostrar barra de progreso
        usuarioField.setEnabled(false);
        passwordField.setEnabled(false);
        loginButton.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        mensajeError.setText("Verificando credenciales...");
        mensajeError.setForeground(Color.BLUE);
        
        // Validar en segundo plano
        executor.submit(() -> {
            boolean loginSuccess = controller.validarLogin(usuario, password);
            
            SwingUtilities.invokeLater(() -> {
                // Restaurar controles
                usuarioField.setEnabled(true);
                passwordField.setEnabled(true);
                loginButton.setEnabled(true);
                progressBar.setVisible(false);
                
                if (loginSuccess) {
                    mensajeError.setText("");
                    abrirSistema();
                } else {
                    mensajeError.setText("Usuario o contraseña incorrectos");
                    mensajeError.setForeground(Color.RED);
                    passwordField.setText("");
                    passwordField.requestFocus();
                }
            });
        });
    }
    
    /**
     * Abre la ventana principal del sistema
     */
    private void abrirSistema() {
        // Cerrar recursos abiertos
        executor.shutdown();
        
        this.dispose(); // Cierra la ventana de login
        
        // Iniciar el sistema principal
        SwingUtilities.invokeLater(() -> {
            try {
                SistemaFacturacionInventario sistema = new SistemaFacturacionInventario();
                sistema.setVisible(true);
            } catch (Exception e) {
                logger.error("Error al iniciar el sistema: {}", e.getMessage(), e);
                JOptionPane.showMessageDialog(
                    null,
                    "Error al iniciar el sistema: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
    
    /**
     * Método principal
     */
    public static void main(String[] args) {
        // Establecer look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.error("Error al establecer Look & Feel: {}", e.getMessage(), e);
        }
        
        // Mostrar splash screen
        JWindow splash = new JWindow();
        JLabel splashLabel = new JLabel("Cargando Sistema...", SwingConstants.CENTER);
        splashLabel.setFont(new Font("Arial", Font.BOLD, 18));
        splashLabel.setPreferredSize(new Dimension(300, 200));
        splash.getContentPane().add(splashLabel);
        splash.pack();
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        
        // Esperar un momento para mostrar el splash
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Iniciar la aplicación
        SwingUtilities.invokeLater(() -> {
            splash.dispose();
            VistaLogin login = new VistaLogin();
            login.setVisible(true);
        });
    }
}