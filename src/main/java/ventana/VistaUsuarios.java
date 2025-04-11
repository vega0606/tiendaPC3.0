package ventana;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

import controlador.UsuarioController;
import controlador.VistaFacturacionController;
import controlador.VistaUsuariosController;
import modelo.Usuario;

/**
 * Vista para la gestión de usuarios del sistema.
 * Permite visualizar, crear, editar y eliminar usuarios, así como gestionar roles y permisos.
 */
public class VistaUsuarios extends Vista {
    // Componentes principales
    private JTextField txtBusqueda;
    private JTable tablaUsuarios;
    private JButton btnBuscar;
    private JButton btnMostrarTodos;
    private JButton btnNuevoUsuario;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnCambiarPassword;
    private JButton btnRestablecerPassword;
    private JButton btnCambiarEstado;
    private JButton btnFiltrarPorRol;
    private JButton btnExportarPDF;
    private JButton btnExportarExcel;
    private JComboBox<String> comboRol;
    
    // Paneles para vistas múltiples
    private JPanel listadoPanel;
    private JPanel formularioPanel;
    private JPanel cambioPasswordPanel;
    
    // Campos del formulario de usuario
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtNombre;
    
    private JTextField txtEmail;
    private JComboBox<String> comboRolForm;
    private JCheckBox chkActivo;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    // Campos del formulario de cambio de contraseña
    private JPasswordField txtPasswordActual;
    private JPasswordField txtPasswordNueva;
    private JPasswordField txtConfirmPasswordNueva;
    private JButton btnGuardarPassword;
    private JButton btnCancelarPassword;
    
    // Controlador
    private VistaUsuariosController controller;
    private UsuarioController usuarioController;
    
    // Variables de estado
    private boolean editando = false;
    private Usuario usuarioEnEdicion = null;
    
    /**
     * Constructor de la vista de usuarios.
     */
    public VistaUsuarios() {
        super("Gestión de Usuarios");
    }
    
    @Override
    protected void inicializarPanel() {
    	 // Header
        JPanel headerPanel = crearHeaderPanel();
        
        // Panel izquierdo - Opciones
        String[] sidebarItems = {
            "Listado de Usuarios", "Nuevo Usuario", "Permisos y Roles", 
            "Cambiar Contraseña", "Auditoría"
        };
        JPanel sidebarPanel = crearSidebarPanel(sidebarItems);
        
        // Panel principal - Contenido cambiante
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainContentPanel.setLayout(new CardLayout());
        
        // Crear panel de listado
        listadoPanel = crearPanelListado();
        
        // Crear panel de formulario
        formularioPanel = crearPanelFormulario();
        
        // Crear panel de cambio de contraseña
        cambioPasswordPanel = crearPanelCambioPassword();
        
        // Añadir paneles al panel principal
        mainContentPanel.add(listadoPanel, "LISTADO");
        mainContentPanel.add(formularioPanel, "FORMULARIO");
        mainContentPanel.add(cambioPasswordPanel, "CAMBIO_PASSWORD");
        
        // Panel principal
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(sidebarPanel, BorderLayout.WEST);
        panel.add(mainContentPanel, BorderLayout.CENTER);
        
        // Inicializar controlador
        usuarioController = new UsuarioController();
        controller = new VistaUsuariosController(this, usuarioController);
        
        // Mostrar vista de listado por defecto
        mostrarListado();
    }

    
    /**
     * Crea el panel para el listado de usuarios.
     * 
     * @return Panel configurado con la tabla y controles
     */
    private JPanel crearPanelListado() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Listado de Usuarios");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchPanel.add(new JLabel("Buscar usuario:"));
        txtBusqueda = new JTextField(15);
        searchPanel.add(txtBusqueda);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(74, 134, 232));
        btnBuscar.setForeground(Color.WHITE);
        searchPanel.add(btnBuscar);
        
        btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.setBackground(new Color(74, 134, 232));
        btnMostrarTodos.setForeground(Color.WHITE);
        searchPanel.add(btnMostrarTodos);
        
        // Panel de filtro por rol
        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        filtroPanel.add(new JLabel("Filtrar por Rol:"));
        comboRol = new JComboBox<>(new String[] {
            "Todos", "Administrador", "Supervisor", "Vendedor", "Almacenista", "Contador"
        });
        filtroPanel.add(comboRol);
        
        btnFiltrarPorRol = new JButton("Filtrar");
        btnFiltrarPorRol.setBackground(new Color(74, 134, 232));
        btnFiltrarPorRol.setForeground(Color.WHITE);
        filtroPanel.add(btnFiltrarPorRol);
        
        // Tabla de usuarios
        String[] columnNames = {"ID", "Username", "Nombre",  "Email", "Rol", "Estado"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No permitir edición directa en la tabla
            }
        };
        
        tablaUsuarios = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        // Panel de acciones
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        btnNuevoUsuario = new JButton("Nuevo Usuario");
        btnNuevoUsuario.setBackground(new Color(76, 175, 80));
        btnNuevoUsuario.setForeground(Color.WHITE);
        actionPanel.add(btnNuevoUsuario);
        
        btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 152, 0));
        btnEditar.setForeground(Color.WHITE);
        actionPanel.add(btnEditar);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(244, 67, 54));
        btnEliminar.setForeground(Color.WHITE);
        actionPanel.add(btnEliminar);
        
        btnCambiarPassword = new JButton("Cambiar Contraseña");
        btnCambiarPassword.setBackground(new Color(156, 39, 176));
        btnCambiarPassword.setForeground(Color.WHITE);
        actionPanel.add(btnCambiarPassword);
        
        // Panel de acciones secundarias
        JPanel actionPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        btnRestablecerPassword = new JButton("Restablecer Contraseña");
        btnRestablecerPassword.setBackground(new Color(63, 81, 181));
        btnRestablecerPassword.setForeground(Color.WHITE);
        actionPanel2.add(btnRestablecerPassword);
        
        btnCambiarEstado = new JButton("Activar/Desactivar");
        btnCambiarEstado.setBackground(new Color(0, 150, 136));
        btnCambiarEstado.setForeground(Color.WHITE);
        actionPanel2.add(btnCambiarEstado);
        
        // Panel de exportación
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnExportarPDF = new JButton("Exportar a PDF");
        btnExportarPDF.setBackground(new Color(183, 28, 28));
        btnExportarPDF.setForeground(Color.WHITE);
        exportPanel.add(btnExportarPDF);
        
        btnExportarExcel = new JButton("Exportar a Excel");
        btnExportarExcel.setBackground(new Color(46, 125, 50));
        btnExportarExcel.setForeground(Color.WHITE);
        exportPanel.add(btnExportarExcel);
        
        // Paneles inferiores combinados
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel combinedActionPanel = new JPanel(new GridLayout(2, 1));
        combinedActionPanel.add(actionPanel);
        combinedActionPanel.add(actionPanel2);
        bottomPanel.add(combinedActionPanel, BorderLayout.WEST);
        bottomPanel.add(exportPanel, BorderLayout.EAST);
        
        // Panel de búsqueda completo
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(filtroPanel, BorderLayout.SOUTH);
        
        // Añadir todo al panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea el panel para el formulario de usuarios.
     * 
     * @return Panel configurado con el formulario
     */
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Nuevo Usuario");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nombre de usuario
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre de Usuario:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtUsername = new JTextField(20);
        formPanel.add(txtUsername, gbc);
        
        // Contraseña
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Contraseña:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPassword = new JPasswordField(20);
        formPanel.add(txtPassword, gbc);
        
        // Confirmar Contraseña
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Confirmar Contraseña:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtConfirmPassword = new JPasswordField(20);
        formPanel.add(txtConfirmPassword, gbc);
        
        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        formPanel.add(txtNombre, gbc);
        
        
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail, gbc);
        
        // Rol
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Rol:"), gbc);
        
        gbc.gridx = 1;
        comboRolForm = new JComboBox<>(new String[] {
            "Administrador", "Supervisor", "Vendedor", "Almacenista", "Contador"
        });
        formPanel.add(comboRolForm, gbc);
        
        // Activo
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1;
        chkActivo = new JCheckBox("Usuario Activo");
        chkActivo.setSelected(true);
        formPanel.add(chkActivo, gbc);
        
        // Botones de acción
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(158, 158, 158));
        btnCancelar.setForeground(Color.WHITE);
        buttonPanel.add(btnCancelar);
        
        btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(new Color(76, 175, 80));
        btnGuardar.setForeground(Color.WHITE);
        buttonPanel.add(btnGuardar);
        
        // Añadir componentes al panel
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea el panel para cambio de contraseña.
     * 
     * @return Panel configurado para cambio de contraseña
     */
    private JPanel crearPanelCambioPassword() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Cambiar Contraseña");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Contraseña actual
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Contraseña Actual:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPasswordActual = new JPasswordField(20);
        formPanel.add(txtPasswordActual, gbc);
        
        // Nueva contraseña
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Nueva Contraseña:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPasswordNueva = new JPasswordField(20);
        formPanel.add(txtPasswordNueva, gbc);
        
        // Confirmar nueva contraseña
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Confirmar Nueva Contraseña:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtConfirmPasswordNueva = new JPasswordField(20);
        formPanel.add(txtConfirmPasswordNueva, gbc);
        
        // Botones de acción
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnCancelarPassword = new JButton("Cancelar");
        btnCancelarPassword.setBackground(new Color(158, 158, 158));
        btnCancelarPassword.setForeground(Color.WHITE);
        buttonPanel.add(btnCancelarPassword);
        
        btnGuardarPassword = new JButton("Guardar");
        btnGuardarPassword.setBackground(new Color(76, 175, 80));
        btnGuardarPassword.setForeground(Color.WHITE);
        buttonPanel.add(btnGuardarPassword);
        
        // Añadir componentes al panel
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Muestra el panel de listado.
     */
    public void mostrarListado() {
        if (panel.getParent() != null) {
            CardLayout cl = (CardLayout) panel.getParent().getLayout();
            cl.show(panel.getParent(), "LISTADO");
        } else {
            // Posponer esta operación o solo cambiar el contenido del panel principal
            // Si estamos en el constructor, simplemente no hacemos nada aquí
            JPanel mainContentPanel = (JPanel) panel.getComponent(2); // Asumiendo que mainContentPanel es el tercer componente
            if (mainContentPanel != null) {
                CardLayout cl = (CardLayout) mainContentPanel.getLayout();
                cl.show(mainContentPanel, "LISTADO");
            }
        }
    }
    
    /**
     * Muestra el panel de formulario para un nuevo usuario o edición.
     * 
     * @param usuario El usuario a editar, o null para uno nuevo
     */
    public void mostrarFormularioUsuario(Usuario usuario) {
        limpiarFormulario();
        
        if (usuario == null) {
            // Nuevo usuario
            editando = false;
            usuarioEnEdicion = null;
            
            // Mostrar campos de contraseña
            txtPassword.setEnabled(true);
            txtConfirmPassword.setEnabled(true);
            
            // Actualizar título
            JLabel titleLabel = (JLabel) formularioPanel.getComponent(0);
            titleLabel.setText("Nuevo Usuario");
            
            // Establecer valores por defecto
            chkActivo.setSelected(true);
        } else {
            // Editar usuario existente
            editando = true;
            usuarioEnEdicion = usuario;
            
            // Ocultar campos de contraseña
            txtPassword.setEnabled(false);
            txtConfirmPassword.setEnabled(false);
            txtPassword.setText("••••••••");
            txtConfirmPassword.setText("••••••••");
            
            // Cargar datos del usuario en el formulario
            txtUsername.setText(usuario.getUsername());
            txtNombre.setText(usuario.getNombre());
            
            txtEmail.setText(usuario.getEmail());
            comboRolForm.setSelectedItem(usuario.getRol());
            chkActivo.setSelected(usuario.isActivo());
            
            // Actualizar título
            JLabel titleLabel = (JLabel) formularioPanel.getComponent(0);
            titleLabel.setText("Editar Usuario");
        }
        
        CardLayout cl = (CardLayout) panel.getParent().getLayout();
        cl.show(panel.getParent(), "FORMULARIO");
    }
    
    /**
     * Muestra el panel de cambio de contraseña para un usuario.
     * 
     * @param usuario El usuario para el que se cambiará la contraseña
     */
    public void mostrarFormularioCambioPassword(Usuario usuario) {
        limpiarFormularioCambioPassword();
        
        // Guardar referencia al usuario
        usuarioEnEdicion = usuario;
        
        // Actualizar título
        JLabel titleLabel = (JLabel) cambioPasswordPanel.getComponent(0);
        titleLabel.setText("Cambiar Contraseña - " + usuario.getUsername());
        
        CardLayout cl = (CardLayout) panel.getParent().getLayout();
        cl.show(panel.getParent(), "CAMBIO_PASSWORD");
    }
    
    /**
     * Limpia todos los campos del formulario de usuario.
     */
    private void limpiarFormulario() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        txtNombre.setText("");
       
        txtEmail.setText("");
        comboRolForm.setSelectedIndex(0);
        chkActivo.setSelected(true);
    }
    
    /**
     * Limpia todos los campos del formulario de cambio de contraseña.
     */
    private void limpiarFormularioCambioPassword() {
        txtPasswordActual.setText("");
        txtPasswordNueva.setText("");
        txtConfirmPasswordNueva.setText("");
    }
    
    /**
     * Obtiene los datos del usuario desde el formulario.
     * 
     * @return Usuario con los datos ingresados
     */
    public Usuario obtenerUsuarioDesdeFormulario() {
        Usuario usuario = new Usuario();
        
        if (editando && usuarioEnEdicion != null) {
            usuario.setId(usuarioEnEdicion.getId());
        }
        
        usuario.setUsername(txtUsername.getText().trim());
        usuario.setNombre(txtNombre.getText().trim());
        
        usuario.setEmail(txtEmail.getText().trim());
        usuario.setRol(comboRolForm.getSelectedItem().toString());
        usuario.setActivo(chkActivo.isSelected());
        
        return usuario;
    }
    
    /**
     * Obtiene la contraseña ingresada en el formulario de nuevo usuario.
     * 
     * @return Contraseña ingresada
     */
    public String obtenerPasswordDesdeFormulario() {
        return new String(txtPassword.getPassword());
    }
    
    /**
     * Obtiene la confirmación de contraseña ingresada en el formulario.
     * 
     * @return Confirmación de contraseña
     */
    public String obtenerConfirmPasswordDesdeFormulario() {
        return new String(txtConfirmPassword.getPassword());
    }
    
    /**
     * Obtiene los datos para cambio de contraseña.
     * 
     * @return Array con [contraseña actual, nueva contraseña, confirmación]
     */
    public String[] obtenerDatosCambioPassword() {
        String[] datos = new String[3];
        datos[0] = new String(txtPasswordActual.getPassword());
        datos[1] = new String(txtPasswordNueva.getPassword());
        datos[2] = new String(txtConfirmPasswordNueva.getPassword());
        return datos;
    }
    
    /**
     * Muestra los usuarios en la tabla.
     * 
     * @param usuarios Lista de usuarios a mostrar
     */
    public void mostrarUsuarios(List<Usuario> usuarios) {
        DefaultTableModel model = (DefaultTableModel) tablaUsuarios.getModel();
        model.setRowCount(0); // Limpiar tabla
        
        for (Usuario usuario : usuarios) {
            model.addRow(new Object[] {
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombre(),
              
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo() ? "Activo" : "Inactivo"
            });
        }
    }
    
    /**
     * Muestra un solo usuario en la tabla.
     * 
     * @param usuario El usuario a mostrar
     */
    public void mostrarUsuario(Usuario usuario) {
        DefaultTableModel model = (DefaultTableModel) tablaUsuarios.getModel();
        model.setRowCount(0); // Limpiar tabla
        
        model.addRow(new Object[] {
            usuario.getId(),
            usuario.getUsername(),
            usuario.getNombre(),
           
            usuario.getEmail(),
            usuario.getRol(),
            usuario.isActivo() ? "Activo" : "Inactivo"
        });
    }
    
    /**
     * Obtiene el usuario seleccionado en la tabla.
     * 
     * @return Usuario seleccionado o null si no hay selección
     */
    public Usuario obtenerUsuarioSeleccionado() {
        int selectedRow = tablaUsuarios.getSelectedRow();
        
        if (selectedRow < 0) {
            return null;
        }
        
        Usuario usuario = new Usuario();
        usuario.setId((Integer) tablaUsuarios.getValueAt(selectedRow, 0));
        usuario.setUsername((String) tablaUsuarios.getValueAt(selectedRow, 1));
        usuario.setNombre((String) tablaUsuarios.getValueAt(selectedRow, 2));
        
        usuario.setEmail((String) tablaUsuarios.getValueAt(selectedRow, 4));
        usuario.setRol((String) tablaUsuarios.getValueAt(selectedRow, 5));
        usuario.setActivo(tablaUsuarios.getValueAt(selectedRow, 6).equals("Activo"));
        
        return usuario;
    }
    
    /**
     * Obtiene todos los usuarios mostrados en la tabla.
     * 
     * @return Lista con todos los usuarios mostrados
     */
    public List<Usuario> obtenerUsuariosMostrados() {
        List<Usuario> usuarios = new java.util.ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) tablaUsuarios.getModel();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            Usuario usuario = new Usuario();
            usuario.setId((Integer) model.getValueAt(i, 0));
            usuario.setUsername((String) model.getValueAt(i, 1));
            usuario.setNombre((String) model.getValueAt(i, 2));
          
            usuario.setEmail((String) model.getValueAt(i, 4));
            usuario.setRol((String) model.getValueAt(i, 5));
            usuario.setActivo(model.getValueAt(i, 6).equals("Activo"));
            
            usuarios.add(usuario);
        }
        
        return usuarios;
    }
    
    /**
     * Muestra un mensaje al usuario.
     * 
     * @param mensaje El mensaje a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(
            null,  // Centra en la pantalla
            mensaje,
            "Mensaje",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Muestra un diálogo de confirmación.
     * 
     * @param mensaje El mensaje de confirmación
     * @return true si el usuario confirma, false en caso contrario
     */
    public boolean mostrarConfirmacion(String mensaje) {
        return JOptionPane.showConfirmDialog(
            null, 
            mensaje, 
            "Confirmación", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }
    
    /**
     * Selecciona una ruta para guardar un archivo.
     * 
     * @param formato El formato del archivo (PDF o Excel)
     * @return La ruta seleccionada o null si se cancela
     */
    public String seleccionarRutaGuardado(String formato) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar " + formato);
        
        String extension = formato.equalsIgnoreCase("PDF") ? ".pdf" : ".xlsx";
        
        int resultado = fileChooser.showSaveDialog(null); // Usar null si this no funciona
        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(extension)) {
                path += extension;
            }
            return path;
        }
        
        return null;
    }
    
    // Getters para que el controlador pueda acceder a los componentes
    
    public JTextField getTxtBusqueda() {
        return txtBusqueda;
    }
    
    public JTable getTablaUsuarios() {
        return tablaUsuarios;
    }
    
    public JButton getBtnBuscar() {
        return btnBuscar;
    }
    
    public JButton getBtnMostrarTodos() {
        return btnMostrarTodos;
    }
    
    public JButton getBtnNuevoUsuario() {
        return btnNuevoUsuario;
    }
    
    public JButton getBtnEditar() {
        return btnEditar;
    }
    
    public JButton getBtnEliminar() {
        return btnEliminar;
    }
    
    public JButton getBtnCambiarPassword() {
        return btnCambiarPassword;
    }
    
    public JButton getBtnRestablecerPassword() {
        return btnRestablecerPassword;
    }
    
    public JButton getBtnCambiarEstado() {
        return btnCambiarEstado;
    }
    
    public JButton getBtnFiltrarPorRol() {
        return btnFiltrarPorRol;
    }
    
    public JButton getBtnExportarPDF() {
        return btnExportarPDF;
    }
    
    public JButton getBtnExportarExcel() {
        return btnExportarExcel;
    }
    
    public JComboBox<String> getComboRol() {
        return comboRol;
    }
    
    public JButton getBtnGuardar() {
        return btnGuardar;
    }
    
    public JButton getBtnCancelar() {
        return btnCancelar;
    }
    
    public JButton getBtnGuardarPassword() {
        return btnGuardarPassword;
    }
    
    public JButton getBtnCancelarPassword() {
        return btnCancelarPassword;
    }
    
    public Usuario getUsuarioEnEdicion() {
        return usuarioEnEdicion;
    }
    
    public boolean isEditando() {
        return editando;
    }
}
