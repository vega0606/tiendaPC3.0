package ventana;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Date;

import controlador.VistaPedidosController;
import controlador.PedidoController;
import controlador.ProveedorController;
import modelo.Pedido;
import modelo.Proveedor;
import com.toedter.calendar.JDateChooser;

/**
 * Vista para la gestión de pedidos a proveedores.
 * Permite visualizar, crear, editar y eliminar pedidos.
 */
public class VistaPedidos extends JPanel {
    // Componentes principales
    private JTextField txtBusqueda;
    private JTable tablaPedidos;
    private JButton btnBuscar;
    private JButton btnMostrarTodos;
    private JButton btnNuevoPedido;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnMarcarRecibido;
    private JButton btnFiltrarPorEstado;
    private JButton btnFiltrarPorProveedor;
    private JButton btnExportarPDF;
    private JButton btnExportarExcel;
    private JComboBox<String> comboEstado;
    private JComboBox<Proveedor> comboProveedor;
    
    // Paneles para vistas múltiples
    private JPanel listadoPanel;
    private JPanel formularioPanel;
    private CardLayout cardLayout;
    
    // Campos del formulario
    private JComboBox<Proveedor> comboProveedorForm;
    private JDateChooser fechaPedido;
    private JDateChooser fechaEntregaEstimada;
    private JTextArea txtProductos;
    private JTextField txtMonto;
    private JComboBox<String> comboEstadoForm;
    private JTextArea txtObservaciones;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    // Controlador
    private VistaPedidosController controller;
    
    // Variables de estado
    private boolean editando = false;
    private Pedido pedidoEnEdicion = null;
    
    /**
     * Constructor de la vista de pedidos.
     */
    public VistaPedidos() {
        setLayout(new BorderLayout());
        inicializarPanel();
    }
    
    /**
     * Constructor con controladores inyectados.
     * 
     * @param pedidoController El controlador de pedidos
     * @param proveedorController El controlador de proveedores
     */
    public VistaPedidos(PedidoController pedidoController, ProveedorController proveedorController) {
        setLayout(new BorderLayout());
        inicializarPanel();
        
        controller = new VistaPedidosController(this, pedidoController, proveedorController);
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
            btnBuscar.addActionListener(e -> controller.buscarPedidoPorNumero());
            btnMostrarTodos.addActionListener(e -> controller.cargarPedidos());
            btnNuevoPedido.addActionListener(e -> controller.abrirFormularioNuevoPedido());
            btnEditar.addActionListener(e -> controller.editarPedidoSeleccionado());
            btnEliminar.addActionListener(e -> controller.eliminarPedidoSeleccionado());
            btnMarcarRecibido.addActionListener(e -> controller.marcarPedidoComoRecibido());
            btnFiltrarPorEstado.addActionListener(e -> controller.filtrarPedidosPorEstado());
            btnFiltrarPorProveedor.addActionListener(e -> controller.filtrarPedidosPorProveedor());
            btnExportarPDF.addActionListener(e -> controller.exportarAFormato("PDF"));
            btnExportarExcel.addActionListener(e -> controller.exportarAFormato("Excel"));
            
            // Botones del panel de formulario
            btnGuardar.addActionListener(e -> {
                Pedido pedido = obtenerPedidoDesdeFormulario();
                controller.guardarPedido(pedido, !editando);
            });
            btnCancelar.addActionListener(e -> mostrarListado());
        }
    }
    
    /**
     * Crea el panel header.
     */
    private JPanel crearHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBackground(new Color(52, 73, 94));
        
        JLabel titleLabel = new JLabel("Sistema de Gestión - Pedidos a Proveedores");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Crea el panel para el listado de pedidos.
     * 
     * @return Panel configurado con la tabla y controles
     */
    private JPanel crearPanelListado() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Listado de Pedidos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchPanel.add(new JLabel("Buscar por Número:"));
        txtBusqueda = new JTextField(10);
        searchPanel.add(txtBusqueda);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(74, 134, 232));
        btnBuscar.setForeground(Color.WHITE);
        searchPanel.add(btnBuscar);
        
        btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.setBackground(new Color(74, 134, 232));
        btnMostrarTodos.setForeground(Color.WHITE);
        searchPanel.add(btnMostrarTodos);
        
        // Panel de filtros
        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        filtroPanel.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>(new String[] {
            "Todos", "Pendiente", "En tránsito", "Recibido", "Cancelado"
        });
        filtroPanel.add(comboEstado);
        
        btnFiltrarPorEstado = new JButton("Filtrar");
        btnFiltrarPorEstado.setBackground(new Color(74, 134, 232));
        btnFiltrarPorEstado.setForeground(Color.WHITE);
        filtroPanel.add(btnFiltrarPorEstado);
        
        filtroPanel.add(Box.createHorizontalStrut(20));
        
        filtroPanel.add(new JLabel("Proveedor:"));
        comboProveedor = new JComboBox<>();
        filtroPanel.add(comboProveedor);
        
        btnFiltrarPorProveedor = new JButton("Filtrar");
        btnFiltrarPorProveedor.setBackground(new Color(74, 134, 232));
        btnFiltrarPorProveedor.setForeground(Color.WHITE);
        filtroPanel.add(btnFiltrarPorProveedor);
        
        // Tabla de pedidos
        String[] columnNames = {"Número", "Proveedor", "Fecha Pedido", "Fecha Est. Entrega", "Monto", "Estado"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No permitir edición directa en la tabla
            }
        };
        
        tablaPedidos = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tablaPedidos);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        // Panel de acciones
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        btnNuevoPedido = new JButton("Nuevo Pedido");
        btnNuevoPedido.setBackground(new Color(76, 175, 80));
        btnNuevoPedido.setForeground(Color.WHITE);
        actionPanel.add(btnNuevoPedido);
        
        btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 152, 0));
        btnEditar.setForeground(Color.WHITE);
        actionPanel.add(btnEditar);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(244, 67, 54));
        btnEliminar.setForeground(Color.WHITE);
        actionPanel.add(btnEliminar);
        
        btnMarcarRecibido = new JButton("Marcar como Recibido");
        btnMarcarRecibido.setBackground(new Color(0, 150, 136));
        btnMarcarRecibido.setForeground(Color.WHITE);
        actionPanel.add(btnMarcarRecibido);
        
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
        
        // Panel inferior que combina acciones y exportación
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(actionPanel, BorderLayout.WEST);
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
     * Crea el panel para el formulario de pedidos.
     * 
     * @return Panel configurado con el formulario
     */
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Nuevo Pedido");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Proveedor
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Proveedor:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        comboProveedorForm = new JComboBox<>();
        formPanel.add(comboProveedorForm, gbc);
        
        // Fecha de pedido
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Fecha de Pedido:"), gbc);
        
        gbc.gridx = 1;
        fechaPedido = new JDateChooser();
        fechaPedido.setDate(new Date());
        formPanel.add(fechaPedido, gbc);
        
        // Fecha estimada de entrega
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Fecha Est. Entrega:"), gbc);
        
        gbc.gridx = 1;
        fechaEntregaEstimada = new JDateChooser();
        formPanel.add(fechaEntregaEstimada, gbc);
        
        // Productos
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Productos:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        txtProductos = new JTextArea(5, 30);
        txtProductos.setLineWrap(true);
        txtProductos.setWrapStyleWord(true);
        JScrollPane scrollProductos = new JScrollPane(txtProductos);
        formPanel.add(scrollProductos, gbc);
        
        // Monto
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        formPanel.add(new JLabel("Monto Total:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtMonto = new JTextField(15);
        formPanel.add(txtMonto, gbc);
        
        // Estado
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1;
        comboEstadoForm = new JComboBox<>(new String[] {
            "Pendiente", "En tránsito", "Recibido", "Cancelado"
        });
        formPanel.add(comboEstadoForm, gbc);
        
        // Observaciones
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Observaciones:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        txtObservaciones = new JTextArea(4, 30);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JScrollPane scrollObservaciones = new JScrollPane(txtObservaciones);
        formPanel.add(scrollObservaciones, gbc);
        
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
     * Muestra el panel de listado.
     */
    public void mostrarListado() {
        Component parent = getParent();
        if (parent instanceof JPanel) {
            cardLayout.show((JPanel) parent, "LISTADO");
        }
    }
    
    /**
     * Muestra el panel de formulario para un nuevo pedido o edición.
     * 
     * @param pedido El pedido a editar, o null para uno nuevo
     */
    public void mostrarFormularioPedido(Pedido pedido) {
        limpiarFormulario();
        
        if (pedido == null) {
            // Nuevo pedido
            editando = false;
            pedidoEnEdicion = null;
            
            // Actualizar título
            JLabel titleLabel = (JLabel) formularioPanel.getComponent(0);
            titleLabel.setText("Nuevo Pedido");
            
            // Establecer valores por defecto
            fechaPedido.setDate(new Date());
            comboEstadoForm.setSelectedItem("Pendiente");
        } else {
            // Editar pedido existente
            editando = true;
            pedidoEnEdicion = pedido;
            
            // Seleccionar el proveedor correspondiente
            for (int i = 0; i < comboProveedorForm.getItemCount(); i++) {
                Proveedor proveedor = comboProveedorForm.getItemAt(i);
                if (proveedor.getId() == pedido.getIdProveedor()) {
                    comboProveedorForm.setSelectedIndex(i);
                    break;
                }
            }
            
            // Cargar datos del pedido en el formulario
            fechaPedido.setDate(pedido.getFechaPedido());
            fechaEntregaEstimada.setDate(pedido.getFechaEntregaEstimada());
            txtProductos.setText(pedido.getProductos());
            txtMonto.setText(String.valueOf(pedido.getMonto()));
            comboEstadoForm.setSelectedItem(pedido.getEstado());
            txtObservaciones.setText(pedido.getObservaciones());
            
            // Actualizar título
            JLabel titleLabel = (JLabel) formularioPanel.getComponent(0);
            titleLabel.setText("Editar Pedido");
        }
        
        Component parent = getParent();
        if (parent instanceof JPanel) {
            cardLayout.show((JPanel) parent, "FORMULARIO");
        }
    }
    
    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        if (comboProveedorForm.getItemCount() > 0) {
            comboProveedorForm.setSelectedIndex(0);
        }
        fechaPedido.setDate(new Date());
        fechaEntregaEstimada.setDate(null);
        txtProductos.setText("");
        txtMonto.setText("");
        comboEstadoForm.setSelectedIndex(0);
        txtObservaciones.setText("");
    }
    
    /**
     * Obtiene los datos del pedido desde el formulario.
     * 
     * @return Pedido con los datos ingresados
     */
    public Pedido obtenerPedidoDesdeFormulario() {
        Pedido pedido = new Pedido();
        
        if (editando && pedidoEnEdicion != null) {
            pedido.setNumero(pedidoEnEdicion.getNumero());
        }
        
        Proveedor proveedorSeleccionado = (Proveedor) comboProveedorForm.getSelectedItem();
        if (proveedorSeleccionado != null) {
            pedido.setIdProveedor(proveedorSeleccionado.getId());
            pedido.setNombreProveedor(proveedorSeleccionado.getNombre());
        }
        
        pedido.setFechaPedido(fechaPedido.getDate());
        pedido.setFechaEntregaEstimada(fechaEntregaEstimada.getDate());
        pedido.setProductos(txtProductos.getText().trim());
        
        try {
            pedido.setMonto(Double.parseDouble(txtMonto.getText().trim().replace(",", ".")));
        } catch (NumberFormatException e) {
            pedido.setMonto(0.0);
        }
        
        pedido.setEstado(comboEstadoForm.getSelectedItem().toString());
        pedido.setObservaciones(txtObservaciones.getText().trim());
        
        return pedido;
    }
    
    /**
     * Muestra los pedidos en la tabla.
     * 
     * @param pedidos Lista de pedidos a mostrar
     */
    public void mostrarPedidos(List<Pedido> pedidos) {
        DefaultTableModel model = (DefaultTableModel) tablaPedidos.getModel();
        model.setRowCount(0); // Limpiar tabla
        
        for (Pedido pedido : pedidos) {
            model.addRow(new Object[] {
                pedido.getNumero(),
                pedido.getNombreProveedor(),
                pedido.getFechaPedido(),
                pedido.getFechaEntregaEstimada(),
                pedido.getMonto(),
                pedido.getEstado()
            });
        }
    }
    
    /**
     * Muestra un solo pedido en la tabla.
     * 
     * @param pedido El pedido a mostrar
     */
    public void mostrarPedido(Pedido pedido) {
        DefaultTableModel model = (DefaultTableModel) tablaPedidos.getModel();
        model.setRowCount(0); // Limpiar tabla
        
        model.addRow(new Object[] {
            pedido.getNumero(),
            pedido.getNombreProveedor(),
            pedido.getFechaPedido(),
            pedido.getFechaEntregaEstimada(),
            pedido.getMonto(),
            pedido.getEstado()
        });
    }
    
    /**
     * Obtiene el pedido seleccionado en la tabla.
     * 
     * @return Pedido seleccionado o null si no hay selección
     */
    public Pedido obtenerPedidoSeleccionado() {
        int selectedRow = tablaPedidos.getSelectedRow();
        
        if (selectedRow < 0) {
            return null;
        }
        
        Pedido pedido = new Pedido();
        pedido.setNumero((Integer) tablaPedidos.getValueAt(selectedRow, 0));
        pedido.setNombreProveedor((String) tablaPedidos.getValueAt(selectedRow, 1));
        pedido.setFechaPedido((Date) tablaPedidos.getValueAt(selectedRow, 2));
        pedido.setFechaEntregaEstimada((Date) tablaPedidos.getValueAt(selectedRow, 3));
        pedido.setMonto((Double) tablaPedidos.getValueAt(selectedRow, 4));
        pedido.setEstado((String) tablaPedidos.getValueAt(selectedRow, 5));
        
        return pedido;
    }
    
    /**
     * Obtiene todos los pedidos mostrados en la tabla.
     * 
     * @return Lista con todos los pedidos mostrados
     */
    public List<Pedido> obtenerPedidosMostrados() {
        List<Pedido> pedidos = new java.util.ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) tablaPedidos.getModel();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            Pedido pedido = new Pedido();
            pedido.setNumero((Integer) model.getValueAt(i, 0));
            pedido.setNombreProveedor((String) model.getValueAt(i, 1));
            pedido.setFechaPedido((Date) model.getValueAt(i, 2));
            pedido.setFechaEntregaEstimada((Date) model.getValueAt(i, 3));
            pedido.setMonto((Double) model.getValueAt(i, 4));
            pedido.setEstado((String) model.getValueAt(i, 5));
            
            pedidos.add(pedido);
        }
        
        return pedidos;
    }
    
    /**
     * Carga los proveedores en el combobox.
     * 
     * @param proveedores Lista de proveedores a cargar
     */
    public void cargarProveedoresEnComboBox(List<Proveedor> proveedores) {
        comboProveedor.removeAllItems();
        comboProveedorForm.removeAllItems();
        
        for (Proveedor proveedor : proveedores) {
            comboProveedor.addItem(proveedor);
            comboProveedorForm.addItem(proveedor);
        }
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
    public void setController(VistaPedidosController controller) {
        this.controller = controller;
        configurarListeners();
    }
    
    // Getters para que el controlador pueda acceder a los componentes
    
    public JTextField getTxtBusqueda() {
        return txtBusqueda;
    }
    
    public JTable getTablaPedidos() {
        return tablaPedidos;
    }
    
    public JButton getBtnBuscar() {
        return btnBuscar;
    }
    
    public JButton getBtnMostrarTodos() {
        return btnMostrarTodos;
    }
    
    public JButton getBtnNuevoPedido() {
        return btnNuevoPedido;
    }
    
    public JButton getBtnEditar() {
        return btnEditar;
    }
    
    public JButton getBtnEliminar() {
        return btnEliminar;
    }
    
    public JButton getBtnMarcarRecibido() {
        return btnMarcarRecibido;
    }
    
    public JButton getBtnFiltrarPorEstado() {
        return btnFiltrarPorEstado;
    }
    
    public JButton getBtnFiltrarPorProveedor() {
        return btnFiltrarPorProveedor;
    }
    
    public JButton getBtnExportarPDF() {
        return btnExportarPDF;
    }
    
    public JButton getBtnExportarExcel() {
        return btnExportarExcel;
    }
    
    public JComboBox<String> getComboEstado() {
        return comboEstado;
    }
    
    public JComboBox<Proveedor> getComboProveedor() {
        return comboProveedor;
    }
}