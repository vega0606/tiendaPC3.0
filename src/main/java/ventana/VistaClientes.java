package ventana;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import controlador.VistaClientesController;
import controlador.ClienteController;
import modelo.Cliente;

/**
 * Vista para la gestión de clientes.
 * Permite visualizar, crear, editar y eliminar clientes.
 */
public class VistaClientes extends JPanel {
    // Componentes principales
    private JTextField busquedaField;
    private JComboBox<String> criterioComboBox;
    private JTable clientesTable;
    private JButton buscarButton;
    private JButton agregarButton;
    private JButton editarButton;
    private JButton eliminarButton;
    private JButton exportarPDFButton;
    private JButton exportarExcelButton;
    
    // Paneles para vistas múltiples
    private JPanel listadoPanel;
    private JPanel formularioPanel;
    private CardLayout cardLayout;
    
    // Campos del formulario
    private JTextField nombreField;
    private JTextField apellidoField;
    private JTextField rucField;
    private JTextField telefonoField;
    private JTextField direccionField;
    private JTextField emailField;
    private JButton guardarButton;
    private JButton cancelarButton;
    
    // Controlador
    private VistaClientesController controller;
    
    // Variables de estado
    private boolean editando = false;
    private Cliente clienteEnEdicion = null;
    
    /**
     * Constructor de la vista de clientes.
     */
    public VistaClientes() {
        setLayout(new BorderLayout());
        inicializarPanel();
    }
    
    /**
     * Constructor con controlador.
     * 
     * @param clienteController El controlador de clientes
     */
    public VistaClientes(ClienteController clienteController) {
        setLayout(new BorderLayout());
        inicializarPanel();
        
        controller = new VistaClientesController(this, clienteController);
        configurarListeners();
    }
    
    /**
     * Inicializa todos los componentes del panel.
     */
    private void inicializarPanel() {
        // Header
        JPanel headerPanel = crearHeaderPanel();
        
        // Panel principal con CardLayout para cambiar entre vistas
        cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);
        
        // Crear panel de listado
        listadoPanel = crearPanelListado();
        
        // Crear panel de formulario
        formularioPanel = crearPanelFormulario();
        
        // Añadir paneles al panel principal
        mainPanel.add(listadoPanel, "LISTADO");
        mainPanel.add(formularioPanel, "FORMULARIO");
        
        // Panel principal
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        
        // Mostrar vista de listado por defecto
        cardLayout.show(mainPanel, "LISTADO");
    }
    
    /**
     * Configura los listeners para los componentes.
     */
    private void configurarListeners() {
        if (controller != null) {
            // Botones del panel de listado
            buscarButton.addActionListener(e -> controller.buscarClientes());
            agregarButton.addActionListener(e -> mostrarFormularioNuevo());
            editarButton.addActionListener(e -> {
                Cliente cliente = obtenerClienteSeleccionado();
                if (cliente != null) {
                    mostrarFormularioEditar(cliente);
                } else {
                    mostrarMensaje("Seleccione un cliente para editar");
                }
            });
            eliminarButton.addActionListener(e -> controller.eliminarCliente());
            exportarPDFButton.addActionListener(e -> controller.exportarAPDF());
            exportarExcelButton.addActionListener(e -> controller.exportarAExcel());
            
            // Botones del panel de formulario
            guardarButton.addActionListener(e -> {
                Cliente cliente = obtenerClienteDesdeFormulario();
                boolean resultado = controller.guardarCliente(cliente, !editando);
                if (resultado) {
                    mostrarListado();
                }
            });
            cancelarButton.addActionListener(e -> mostrarListado());
        }
    }
    
    /**
     * Crea el panel header.
     */
    private JPanel crearHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBackground(new Color(52, 73, 94));
        
        JLabel titleLabel = new JLabel("Sistema de Gestión - Clientes");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Crea el panel para el listado de clientes.
     * 
     * @return Panel configurado con la tabla y controles
     */
    private JPanel crearPanelListado() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Listado de Clientes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchPanel.add(new JLabel("Buscar por:"));
        criterioComboBox = new JComboBox<>(new String[] {"Nombre", "Apellido", "RUC/NIT", "Email"});
        searchPanel.add(criterioComboBox);
        
        busquedaField = new JTextField(20);
        searchPanel.add(busquedaField);
        
        buscarButton = new JButton("Buscar");
        buscarButton.setBackground(new Color(74, 134, 232));
        buscarButton.setForeground(Color.WHITE);
        searchPanel.add(buscarButton);
        
        // Tabla de clientes
        String[] columnNames = {"ID", "Nombre", "Apellido", "RUC/NIT", "Teléfono", "Email", "Dirección"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No permitir edición directa en la tabla
            }
        };
        
        clientesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(clientesTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        // Panel de acciones
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        agregarButton = new JButton("Nuevo Cliente");
        agregarButton.setBackground(new Color(76, 175, 80));
        agregarButton.setForeground(Color.WHITE);
        actionPanel.add(agregarButton);
        
        editarButton = new JButton("Editar");
        editarButton.setBackground(new Color(255, 152, 0));
        editarButton.setForeground(Color.WHITE);
        actionPanel.add(editarButton);
        
        eliminarButton = new JButton("Eliminar");
        eliminarButton.setBackground(new Color(244, 67, 54));
        eliminarButton.setForeground(Color.WHITE);
        actionPanel.add(eliminarButton);
        
        // Panel de exportación
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        exportarPDFButton = new JButton("Exportar a PDF");
        exportarPDFButton.setBackground(new Color(183, 28, 28));
        exportarPDFButton.setForeground(Color.WHITE);
        exportPanel.add(exportarPDFButton);
        
        exportarExcelButton = new JButton("Exportar a Excel");
        exportarExcelButton.setBackground(new Color(46, 125, 50));
        exportarExcelButton.setForeground(Color.WHITE);
        exportPanel.add(exportarExcelButton);
        
        // Panel inferior que combina acciones y exportación
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(actionPanel, BorderLayout.WEST);
        bottomPanel.add(exportPanel, BorderLayout.EAST);
        
        // Añadir todo al panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea el panel para el formulario de clientes.
     * 
     * @return Panel configurado con el formulario
     */
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Nuevo Cliente");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nombreField = new JTextField(20);
        formPanel.add(nombreField, gbc);
        
        // Apellido
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Apellido:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        apellidoField = new JTextField(20);
        formPanel.add(apellidoField, gbc);
        
        // RUC/NIT
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("RUC/NIT:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        rucField = new JTextField(15);
        formPanel.add(rucField, gbc);
        
        // Teléfono
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        telefonoField = new JTextField(15);
        formPanel.add(telefonoField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        // Dirección
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Dirección:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        direccionField = new JTextField(30);
        formPanel.add(direccionField, gbc);
        
        // Botones de acción
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        cancelarButton = new JButton("Cancelar");
        cancelarButton.setBackground(new Color(158, 158, 158));
        cancelarButton.setForeground(Color.WHITE);
        buttonPanel.add(cancelarButton);
        
        guardarButton = new JButton("Guardar");
        guardarButton.setBackground(new Color(76, 175, 80));
        guardarButton.setForeground(Color.WHITE);
        buttonPanel.add(guardarButton);
        
        // Añadir componentes al panel
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Muestra el panel de listado.
     */
    public void mostrarListado() {
        Component parent = getParent();
        if (parent instanceof JPanel) {
            cardLayout.show((JPanel) parent, "LISTADO");
        }
    }
    
    /**
     * Muestra el panel de formulario para un nuevo cliente.
     */
    public void mostrarFormularioNuevo() {
        limpiarFormulario();
        editando = false;
        clienteEnEdicion = null;
        
        // Actualizar título
        JLabel titleLabel = (JLabel) formularioPanel.getComponent(0);
        titleLabel.setText("Nuevo Cliente");
        
        Component parent = getParent();
        if (parent instanceof JPanel) {
            cardLayout.show((JPanel) parent, "FORMULARIO");
        }
    }
    
    /**
     * Muestra el panel de formulario para editar un cliente existente.
     * 
     * @param cliente El cliente a editar
     */
    public void mostrarFormularioEditar(Cliente cliente) {
        limpiarFormulario();
        editando = true;
        clienteEnEdicion = cliente;
        
        // Cargar datos del cliente en el formulario
        nombreField.setText(cliente.getNombre());
        apellidoField.setText(cliente.getApellido());
        rucField.setText(cliente.getRuc());
        telefonoField.setText(cliente.getTelefono());
        emailField.setText(cliente.getEmail());
        direccionField.setText(cliente.getDireccion());
        
        // Actualizar título
        JLabel titleLabel = (JLabel) formularioPanel.getComponent(0);
        titleLabel.setText("Editar Cliente");
        
        Component parent = getParent();
        if (parent instanceof JPanel) {
            cardLayout.show((JPanel) parent, "FORMULARIO");
        }
    }
    
    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        nombreField.setText("");
        apellidoField.setText("");
        rucField.setText("");
        telefonoField.setText("");
        emailField.setText("");
        direccionField.setText("");
    }
    
    /**
     * Obtiene los datos del cliente desde el formulario.
     * 
     * @return Cliente con los datos ingresados
     */
    public Cliente obtenerClienteDesdeFormulario() {
        Cliente cliente = new Cliente();
        
        if (editando && clienteEnEdicion != null) {
            cliente.setId(clienteEnEdicion.getId());
        }
        
        cliente.setNombre(nombreField.getText().trim());
        cliente.setApellido(apellidoField.getText().trim());
        cliente.setRuc(rucField.getText().trim());
        cliente.setTelefono(telefonoField.getText().trim());
        cliente.setEmail(emailField.getText().trim());
        cliente.setDireccion(direccionField.getText().trim());
        
        return cliente;
    }
    
    /**
     * Muestra los clientes en la tabla.
     * 
     * @param clientes Lista de clientes a mostrar
     */
    public void mostrarClientes(List<Cliente> clientes) {
        DefaultTableModel model = (DefaultTableModel) clientesTable.getModel();
        model.setRowCount(0); // Limpiar tabla
        
        for (Cliente cliente : clientes) {
            model.addRow(new Object[] {
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getRuc(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.getDireccion()
            });
        }
    }
    
    /**
     * Obtiene el cliente seleccionado en la tabla.
     * 
     * @return Cliente seleccionado o null si no hay selección
     */
    public Cliente obtenerClienteSeleccionado() {
        int selectedRow = clientesTable.getSelectedRow();
        
        if (selectedRow < 0) {
            return null;
        }
        
        Cliente cliente = new Cliente();
        cliente.setId((Integer) clientesTable.getValueAt(selectedRow, 0));
        cliente.setNombre((String) clientesTable.getValueAt(selectedRow, 1));
        cliente.setApellido((String) clientesTable.getValueAt(selectedRow, 2));
        cliente.setRuc((String) clientesTable.getValueAt(selectedRow, 3));
        cliente.setTelefono((String) clientesTable.getValueAt(selectedRow, 4));
        cliente.setEmail((String) clientesTable.getValueAt(selectedRow, 5));
        cliente.setDireccion((String) clientesTable.getValueAt(selectedRow, 6));
        
        return cliente;
    }
    
    /**
     * Muestra un mensaje al usuario.
     * 
     * @param mensaje El mensaje a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Muestra un diálogo de confirmación.
     * 
     * @param mensaje El mensaje de confirmación
     * @return true si el usuario confirma, false en caso contrario
     */
    public boolean mostrarConfirmacion(String mensaje) {
        return JOptionPane.showConfirmDialog(
            this,
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
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(extension)) {
                path += extension;
            }
            return path;
        }
        
        return null;
    }
    
    /**
     * Establece el controlador para esta vista.
     * 
     * @param controller El controlador a establecer
     */
    public void setController(VistaClientesController controller) {
        this.controller = controller;
        configurarListeners();
    }
    
    // Getters para que el controlador pueda acceder a los componentes
    
    public JTextField getBusquedaField() {
        return busquedaField;
    }
    
    public JComboBox<String> getCriterioComboBox() {
        return criterioComboBox;
    }
    
    public JTable getClientesTable() {
        return clientesTable;
    }
    
    public JButton getBuscarButton() {
        return buscarButton;
    }
    
    public JButton getAgregarButton() {
        return agregarButton;
    }
    
    public JButton getEditarButton() {
        return editarButton;
    }
    
    public JButton getEliminarButton() {
        return eliminarButton;
    }
    
    public JButton getExportarPDFButton() {
        return exportarPDFButton;
    }
    
    public JButton getExportarExcelButton() {
        return exportarExcelButton;
    }
    
    public JButton getGuardarButton() {
        return guardarButton;
    }
    
    public JButton getCancelarButton() {
        return cancelarButton;
    }
    
    public boolean isEditando() {
        return editando;
    }
    
    public Cliente getClienteEnEdicion() {
        return clienteEnEdicion;
    }
}
