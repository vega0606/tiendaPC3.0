package ventana;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controlador.ClienteController;
import controlador.VistaClientesController;
import modelo.Cliente;

public class VistaClientes extends JPanel {
    // Componentes principales
    private JTextField busquedaField;
    private JComboBox<String> criterioComboBox;
    private JTable clientesTable;
    private JButton buscarButton;
    private JButton agregarButton;
    private JButton editarButton;
    private JButton eliminarButton;
   
    // Paneles para vistas múltiples
    private JPanel listadoPanel;
    private JPanel formularioPanel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Campos del formulario
    private JTextField nombreField;
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
        
        // Configurar listeners básicos aun sin controlador
        configurarListenersBasicos();
    }
    
    /**
     * Constructor con controlador.
     * 
     * @param clienteController El controlador de clientes
     */
    public VistaClientes(ClienteController clienteController) {
        setLayout(new BorderLayout());
        inicializarPanel();
        
        // Crear el controlador y asignarlo
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
        mainPanel = new JPanel(cardLayout);
        
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
     * Configura los listeners básicos que no requieren controlador
     */
    private void configurarListenersBasicos() {
        // Configurar listener para el botón de agregar que no necesita controlador
        agregarButton.addActionListener(e -> mostrarFormularioNuevo());
        
        // Configurar listener para el botón de editar que no necesita controlador
        editarButton.addActionListener(e -> {
            Cliente cliente = obtenerClienteSeleccionado();
            if (cliente != null) {
                mostrarFormularioEditar(cliente);
            } else {
                mostrarMensaje("Seleccione un cliente para editar");
            }
        });
        
        // Configurar listener para el botón de cancelar
        cancelarButton.addActionListener(e -> mostrarListado());
        
        // Listener para buscar al presionar Enter en el campo de búsqueda
        busquedaField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && controller != null) {
                    controller.buscarClientes();
                }
            }
        });
    }
    
    /**
     * Configura los listeners para los componentes.
     */
    private void configurarListeners() {
        // Primero configuramos los básicos
        configurarListenersBasicos();
        
        if (controller != null) {
            // Botones del panel de listado que necesitan controlador
            buscarButton.addActionListener(e -> controller.buscarClientes());
            eliminarButton.addActionListener(e -> controller.eliminarCliente());
           
            // Botón de guardar del formulario
            guardarButton.addActionListener(e -> {
                Cliente cliente = obtenerClienteDesdeFormulario();
                boolean resultado = controller.guardarCliente(cliente, !editando);
                if (resultado) {
                    mostrarListado();
                }
            });
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
        criterioComboBox = new JComboBox<>(new String[] {"Nombre", "RUC/NIT", "Email"});
        searchPanel.add(criterioComboBox);
        
        busquedaField = new JTextField(20);
        searchPanel.add(busquedaField);
        
        buscarButton = new JButton("Buscar");
        buscarButton.setBackground(new Color(74, 134, 232));
        buscarButton.setForeground(Color.WHITE);
        searchPanel.add(buscarButton);
        
        // Tabla de clientes
        String[] columnNames = {"ID", "Nombre", "RUC/NIT", "Teléfono", "Email", "Dirección"};
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
        
        // RUC/NIT
        gbc.gridx = 0;
        gbc.gridy = 1;
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
        gbc.gridy = 2;
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
        gbc.gridy = 3;
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
        gbc.gridy = 4;
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
        System.out.println("Mostrando listado de clientes");
        try {
            cardLayout.show(mainPanel, "LISTADO");
            
            // Si hay un controlador, intentar recargar los datos
            if (controller != null) {
                controller.buscarClientes();
            }
            
            System.out.println("Listado mostrado con éxito");
        } catch (Exception e) {
            System.err.println("Error al mostrar listado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra el panel de formulario para un nuevo cliente.
     */
    public void mostrarFormularioNuevo() {
        System.out.println("Mostrando formulario para nuevo cliente");
        try {
            limpiarFormulario();
            editando = false;
            clienteEnEdicion = null;
            
            // Actualizar título
            JLabel titleLabel = (JLabel) formularioPanel.getComponent(0);
            titleLabel.setText("Nuevo Cliente");
            
            cardLayout.show(mainPanel, "FORMULARIO");
            System.out.println("Formulario mostrado con éxito");
        } catch (Exception e) {
            System.err.println("Error al mostrar formulario: " + e.getMessage());
            e.printStackTrace();
            
            // Método alternativo si CardLayout falla
            try {
                this.remove(mainPanel);
                this.add(formularioPanel, BorderLayout.CENTER);
                this.revalidate();
                this.repaint();
                System.out.println("Formulario mostrado con método alternativo");
            } catch (Exception ex) {
                System.err.println("Error en método alternativo: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Muestra el panel de formulario para editar un cliente existente.
     * 
     * @param cliente El cliente a editar
     */
    public void mostrarFormularioEditar(Cliente cliente) {
        System.out.println("Mostrando formulario para editar cliente");
        try {
            limpiarFormulario();
            editando = true;
            clienteEnEdicion = cliente;
            
            // Cargar datos del cliente en el formulario
            nombreField.setText(cliente.getNombre());
            rucField.setText(cliente.getRuc());
            telefonoField.setText(cliente.getTelefono());
            emailField.setText(cliente.getEmail());
            direccionField.setText(cliente.getDireccion());
            
            // Actualizar título
            JLabel titleLabel = (JLabel) formularioPanel.getComponent(0);
            titleLabel.setText("Editar Cliente");
            
            cardLayout.show(mainPanel, "FORMULARIO");
            System.out.println("Formulario de edición mostrado con éxito");
        } catch (Exception e) {
            System.err.println("Error al mostrar formulario de edición: " + e.getMessage());
            e.printStackTrace();
            
            // Método alternativo si CardLayout falla
            try {
                this.remove(mainPanel);
                this.add(formularioPanel, BorderLayout.CENTER);
                this.revalidate();
                this.repaint();
                System.out.println("Formulario de edición mostrado con método alternativo");
            } catch (Exception ex) {
                System.err.println("Error en método alternativo: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        nombreField.setText("");
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
        try {
            DefaultTableModel model = (DefaultTableModel) clientesTable.getModel();
            model.setRowCount(0); // Limpiar tabla
            
            System.out.println("Actualizando tabla con " + clientes.size() + " clientes");
            
            for (Cliente cliente : clientes) {
                model.addRow(new Object[] {
                    cliente.getId(),
                    cliente.getNombre(),
                    cliente.getRuc(),
                    cliente.getTelefono(),
                    cliente.getEmail(),
                    cliente.getDireccion()
                });
                System.out.println("Añadido cliente: " + cliente.getId() + " - " + cliente.getNombre());
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar clientes en tabla: " + e.getMessage());
            e.printStackTrace();
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
        try {
            cliente.setId((String) clientesTable.getValueAt(selectedRow, 0));
            cliente.setNombre((String) clientesTable.getValueAt(selectedRow, 1));
            cliente.setRuc((String) clientesTable.getValueAt(selectedRow, 2));
            cliente.setTelefono((String) clientesTable.getValueAt(selectedRow, 3));
            cliente.setEmail((String) clientesTable.getValueAt(selectedRow, 4));
            cliente.setDireccion((String) clientesTable.getValueAt(selectedRow, 5));
        } catch (Exception e) {
            System.err.println("Error al obtener cliente seleccionado: " + e.getMessage());
            return null;
        }
        
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
    
    /**
     * Método para compatibilidad con VistaClientesController que maneja tanto clientes como proveedores.
     * @return null, ya que esta vista no maneja proveedores
     */
    public JTable getProveedoresTable() {
        return null; // Esta vista no maneja proveedores
    }
    
    /**
     * Método adicional para compatibilidad.
     * @return Panel principal
     */
    public JPanel getPanel() {
        return this;
    }
    
    /**
     * Método adicional para compatibilidad.
     * @return null, ya que esta vista no tiene pestañas
     */
    public JTabbedPane getTabbedPane() {
        return null; // Esta vista no usa pestañas
    }
    
    /**
     * Método adicional para compatibilidad.
     * @return null, ya que esta vista no tiene campo de búsqueda específico para proveedores
     */
    public JTextField getProvSearchField() {
        return null; // Esta vista no tiene campo de búsqueda para proveedores
    }
    
    /**
     * Método adicional para compatibilidad.
     * @return null, ya que esta vista no tiene botón de búsqueda específico para proveedores
     */
    public JButton getProvSearchButton() {
        return null; // Esta vista no tiene botón de búsqueda para proveedores
    }
    
    /**
     * Método adicional para compatibilidad.
     * @return null, ya que esta vista no tiene botón para nuevo proveedor
     */
    public JButton getNewProvButton() {
        return null; // Esta vista no tiene botón para nuevo proveedor
    }
    
    /**
     * Método adicional para compatibilidad.
     * @return Campo de búsqueda para clientes
     */
    public JTextField getClienteSearchField() {
        return busquedaField;
    }
    
    /**
     * Método adicional para compatibilidad.
     * @return Botón de búsqueda para clientes
     */
    public JButton getClienteSearchButton() {
        return buscarButton;
    }
    
    /**
     * Método adicional para compatibilidad.
     * @return Botón para nuevo cliente
     */
    public JButton getNewClientButton() {
        return agregarButton;
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