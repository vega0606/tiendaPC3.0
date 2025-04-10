package ventana;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Date;

import controlador.VistaDevolucionesController;
import controlador.DevolucionController;
import modelo.Devolucion;
import com.toedter.calendar.JDateChooser;

/**
 * Vista para la gestión de devoluciones.
 * Permite visualizar, crear, editar y eliminar devoluciones.
 */
public class VistaDevoluciones extends JPanel {
    // Componentes principales
    private JTextField txtBusqueda;
    private JTable tablaDevoluciones;
    private JButton btnBuscar;
    private JButton btnMostrarTodas;
    private JButton btnNuevaDevolucion;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnFiltrarPorFecha;
    private JButton btnExportarPDF;
    private JButton btnExportarExcel;
    private JDateChooser fechaInicio;
    private JDateChooser fechaFin;
    
    // Paneles para vistas múltiples
    private JPanel listadoPanel;
    private JPanel formularioPanel;
    private CardLayout cardLayout;
    
    // Campos del formulario
    private JTextField txtNumeroFactura;
    private JTextField txtCliente;
    private JButton btnBuscarFactura;
    private JDateChooser fechaDevolucion;
    private JTextField txtProducto;
    private JTextField txtCantidad;
    private JComboBox<String> comboMotivo;
    private JTextArea txtObservaciones;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    // Controlador
    private VistaDevolucionesController controller;
    
    // Variables de estado
    private boolean editando = false;
    private Devolucion devolucionEnEdicion = null;
    
    /**
     * Constructor de la vista de devoluciones.
     */
    public VistaDevoluciones() {
        setLayout(new BorderLayout());
        inicializarPanel();
    }
    
    /**
     * Constructor con controlador.
     * 
     * @param devolucionController El controlador de devoluciones
     */
    public VistaDevoluciones(DevolucionController devolucionController) {
        setLayout(new BorderLayout());
        inicializarPanel();
        
        controller = new VistaDevolucionesController(this, devolucionController);
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
            btnBuscar.addActionListener(e -> controller.buscarDevolucionPorId());
            btnMostrarTodas.addActionListener(e -> controller.cargarDevoluciones());
            btnNuevaDevolucion.addActionListener(e -> controller.abrirFormularioNuevaDevolucion());
            btnEditar.addActionListener(e -> controller.editarDevolucionSeleccionada());
            btnEliminar.addActionListener(e -> controller.eliminarDevolucionSeleccionada());
            btnFiltrarPorFecha.addActionListener(e -> controller.filtrarDevolucionesPorFecha());
            btnExportarPDF.addActionListener(e -> controller.exportarAFormato("PDF"));
            btnExportarExcel.addActionListener(e -> controller.exportarAFormato("Excel"));
            
            // Botones del panel de formulario
            btnBuscarFactura.addActionListener(e -> controller.buscarFactura());
            btnGuardar.addActionListener(e -> {
                Devolucion devolucion = obtenerDevolucionDesdeFormulario();
                controller.guardarDevolucion(devolucion, !editando);
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
        
        JLabel titleLabel = new JLabel("Sistema de Gestión - Devoluciones");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Crea el panel para el listado de devoluciones.
     * 
     * @return Panel configurado con la tabla y controles
     */
    private JPanel crearPanelListado() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Listado de Devoluciones");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchPanel.add(new JLabel("Buscar por ID:"));
        txtBusqueda = new JTextField(10);
        searchPanel.add(txtBusqueda);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(74, 134, 232));
        btnBuscar.setForeground(Color.WHITE);
        searchPanel.add(btnBuscar);
        
        btnMostrarTodas = new JButton("Mostrar Todas");
        btnMostrarTodas.setBackground(new Color(74, 134, 232));
        btnMostrarTodas.setForeground(Color.WHITE);
        searchPanel.add(btnMostrarTodas);
        
        // Panel de filtro por fecha
        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtroPanel.add(new JLabel("Desde:"));
        fechaInicio = new JDateChooser();
        fechaInicio.setPreferredSize(new Dimension(100, 25));
        filtroPanel.add(fechaInicio);
        
        filtroPanel.add(new JLabel("Hasta:"));
        fechaFin = new JDateChooser();
        fechaFin.setPreferredSize(new Dimension(100, 25));
        filtroPanel.add(fechaFin);
        
        btnFiltrarPorFecha = new JButton("Filtrar por Fecha");
        btnFiltrarPorFecha.setBackground(new Color(74, 134, 232));
        btnFiltrarPorFecha.setForeground(Color.WHITE);
        filtroPanel.add(btnFiltrarPorFecha);
        
        // Tabla de devoluciones
        String[] columnNames = {"ID", "Factura", "Cliente", "Fecha", "Producto", "Cantidad", "Motivo", "Estado"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No permitir edición directa en la tabla
            }
        };
        
        tablaDevoluciones = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tablaDevoluciones);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        // Panel de acciones
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        btnNuevaDevolucion = new JButton("Nueva Devolución");
        btnNuevaDevolucion.setBackground(new Color(76, 175, 80));
        btnNuevaDevolucion.setForeground(Color.WHITE);
        actionPanel.add(btnNuevaDevolucion);
        
        btnEditar = new JButton("Editar");
        btnEditar.setBackground(new Color(255, 152, 0));
        btnEditar.setForeground(Color.WHITE);
        actionPanel.add(btnEditar);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(244, 67, 54));
        btnEliminar.setForeground(Color.WHITE);
        actionPanel.add(btnEliminar);
        
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
     * Crea el panel para el formulario de devoluciones.
     * 
     * @return Panel configurado con el formulario
     */
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Nueva Devolución");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Número de factura
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Número de Factura:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNumeroFactura = new JTextField(15);
        formPanel.add(txtNumeroFactura, gbc);
        
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        btnBuscarFactura = new JButton("Buscar");
        btnBuscarFactura.setBackground(new Color(74, 134, 232));
        btnBuscarFactura.setForeground(Color.WHITE);
        formPanel.add(btnBuscarFactura, gbc);
        
        // Cliente
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Cliente:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        txtCliente = new JTextField(30);
        txtCliente.setEditable(false);
        formPanel.add(txtCliente, gbc);
        
        // Fecha de devolución
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Fecha de Devolución:"), gbc);
        
        gbc.gridx = 1;
        fechaDevolucion = new JDateChooser();
        fechaDevolucion.setDate(new Date());
        formPanel.add(fechaDevolucion, gbc);
        
        // Producto
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Producto:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtProducto = new JTextField(30);
        formPanel.add(txtProducto, gbc);
        
        // Cantidad
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Cantidad:"), gbc);
        
        gbc.gridx = 1;
        txtCantidad = new JTextField(10);
        formPanel.add(txtCantidad, gbc);
        
        // Motivo
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Motivo:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        comboMotivo = new JComboBox<>(new String[] {
            "Producto defectuoso", 
            "Producto dañado", 
            "Producto incorrecto", 
            "No cumple expectativas", 
            "Otro"
        });
        formPanel.add(comboMotivo, gbc);
        
        // Observaciones
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Observaciones:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        txtObservaciones = new JTextArea(5, 30);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtObservaciones);
        formPanel.add(scrollPane, gbc);
        
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
            CardLayout layout = (CardLayout) ((JPanel) parent).getLayout();
            layout.show((JPanel) parent, "LISTADO");
        } else {
            cardLayout.show(getParent(), "LISTADO");
        }
    }
    
    /**
     * Muestra el panel de formulario para una nueva devolución o edición.
     * 
     * @param devolucion La devolución a editar, o null para una nueva
     */
    public void mostrarFormularioDevolucion(Devolucion devolucion) {
        limpiarFormulario();
        
        if (devolucion == null) {
            // Nueva devolución
            editando = false;
            devolucionEnEdicion = null;
            
            // Actualizar título
            JLabel titleLabel = (JLabel) formularioPanel.getComponent(0);
            titleLabel.setText("Nueva Devolución");
            
            // Establecer valores por defecto
            fechaDevolucion.setDate(new Date());
        } else {
            // Editar devolución existente
            editando = true;
            devolucionEnEdicion = devolucion;
            
            // Cargar datos de la devolución en el formulario
            txtNumeroFactura.setText(devolucion.getNumeroFactura());
            txtCliente.setText(devolucion.getCliente());
            fechaDevolucion.setDate(devolucion.getFecha());
            txtProducto.setText(devolucion.getProducto());
            txtCantidad.setText(String.valueOf(devolucion.getCantidad()));
            comboMotivo.setSelectedItem(devolucion.getMotivo());
            txtObservaciones.setText(devolucion.getObservaciones());
            
            // Actualizar título
            JLabel titleLabel = (JLabel) formularioPanel.getComponent(0);
            titleLabel.setText("Editar Devolución");
        }
        
        Component parent = getParent();
        if (parent instanceof JPanel) {
            CardLayout layout = (CardLayout) ((JPanel) parent).getLayout();
            layout.show((JPanel) parent, "FORMULARIO");
        } else {
            cardLayout.show(getParent(), "FORMULARIO");
        }
    }
    
    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        txtNumeroFactura.setText("");
        txtCliente.setText("");
        fechaDevolucion.setDate(new Date());
        txtProducto.setText("");
        txtCantidad.setText("");
        comboMotivo.setSelectedIndex(0);
        txtObservaciones.setText("");
    }
    
    /**
     * Obtiene los datos de la devolución desde el formulario.
     * 
     * @return Devolución con los datos ingresados
     */
    public Devolucion obtenerDevolucionDesdeFormulario() {
        Devolucion devolucion = new Devolucion();
        
        if (editando && devolucionEnEdicion != null) {
            devolucion.setId(devolucionEnEdicion.getId());
        }
        
        devolucion.setNumeroFactura(txtNumeroFactura.getText().trim());
        devolucion.setCliente(txtCliente.getText().trim());
        devolucion.setFecha(fechaDevolucion.getDate());
        devolucion.setProducto(txtProducto.getText().trim());
        
        try {
            devolucion.setCantidad(Integer.parseInt(txtCantidad.getText().trim()));
        } catch (NumberFormatException e) {
            devolucion.setCantidad(0);
        }
        
        devolucion.setMotivo(comboMotivo.getSelectedItem().toString());
        devolucion.setObservaciones(txtObservaciones.getText().trim());
        devolucion.setEstado("Pendiente");
        
        return devolucion;
    }
    
    /**
     * Muestra las devoluciones en la tabla.
     * 
     * @param devoluciones Lista de devoluciones a mostrar
     */
    public void mostrarDevoluciones(List<Devolucion> devoluciones) {
        DefaultTableModel model = (DefaultTableModel) tablaDevoluciones.getModel();
        model.setRowCount(0); // Limpiar tabla
        
        for (Devolucion devolucion : devoluciones) {
            model.addRow(new Object[] {
                devolucion.getId(),
                devolucion.getNumeroFactura(),
                devolucion.getCliente(),
                devolucion.getFecha(),
                devolucion.getProducto(),
                devolucion.getCantidad(),
                devolucion.getMotivo(),
                devolucion.getEstado()
            });
        }
    }
    
    /**
     * Muestra una sola devolución en la tabla.
     * 
     * @param devolucion La devolución a mostrar
     */
    public void mostrarDevolucion(Devolucion devolucion) {
        DefaultTableModel model = (DefaultTableModel) tablaDevoluciones.getModel();
        model.setRowCount(0); // Limpiar tabla
        
        model.addRow(new Object[] {
            devolucion.getId(),
            devolucion.getNumeroFactura(),
            devolucion.getCliente(),
            devolucion.getFecha(),
            devolucion.getProducto(),
            devolucion.getCantidad(),
            devolucion.getMotivo(),
            devolucion.getEstado()
        });
    }
    
    /**
     * Obtiene la devolución seleccionada en la tabla.
     * 
     * @return Devolución seleccionada o null si no hay selección
     */
    public Devolucion obtenerDevolucionSeleccionada() {
        int selectedRow = tablaDevoluciones.getSelectedRow();
        
        if (selectedRow < 0) {
            return null;
        }
        
        Devolucion devolucion = new Devolucion();
        devolucion.setId((Integer) tablaDevoluciones.getValueAt(selectedRow, 0));
        devolucion.setNumeroFactura((String) tablaDevoluciones.getValueAt(selectedRow, 1));
        devolucion.setCliente((String) tablaDevoluciones.getValueAt(selectedRow, 2));
        devolucion.setFecha((Date) tablaDevoluciones.getValueAt(selectedRow, 3));
        devolucion.setProducto((String) tablaDevoluciones.getValueAt(selectedRow, 4));
        devolucion.setCantidad((Integer) tablaDevoluciones.getValueAt(selectedRow, 5));
        devolucion.setMotivo((String) tablaDevoluciones.getValueAt(selectedRow, 6));
        devolucion.setEstado((String) tablaDevoluciones.getValueAt(selectedRow, 7));
        
        return devolucion;
    }
    
    /**
     * Obtiene todas las devoluciones mostradas en la tabla.
     * 
     * @return Lista con todas las devoluciones mostradas
     */
    public List<Devolucion> obtenerDevolucionesMostradas() {
        List<Devolucion> devoluciones = new java.util.ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) tablaDevoluciones.getModel();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            Devolucion devolucion = new Devolucion();
            devolucion.setId((Integer) model.getValueAt(i, 0));
            devolucion.setNumeroFactura((String) model.getValueAt(i, 1));
            devolucion.setCliente((String) model.getValueAt(i, 2));
            devolucion.setFecha((Date) model.getValueAt(i, 3));
            devolucion.setProducto((String) model.getValueAt(i, 4));
            devolucion.setCantidad((Integer) model.getValueAt(i, 5));
            devolucion.setMotivo((String) model.getValueAt(i, 6));
            devolucion.setEstado((String) model.getValueAt(i, 7));
            
            devoluciones.add(devolucion);
        }
        
        return devoluciones;
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
    public void setController(VistaDevolucionesController controller) {
        this.controller = controller;
        configurarListeners();
    }
    
    // Getters para que el controlador pueda acceder a los componentes
    
    public JTextField getTxtBusqueda() {
        return txtBusqueda;
    }
    
    public JTable getTablaDevoluciones() {
        return tablaDevoluciones;
    }
    
    public JButton getBtnBuscar() {
        return btnBuscar;
    }
    
    public JButton getBtnMostrarTodas() {
        return btnMostrarTodas;
    }
    
    public JButton getBtnNuevaDevolucion() {
        return btnNuevaDevolucion;
    }
    
    public JButton getBtnEditar() {
        return btnEditar;
    }
    
    public JButton getBtnEliminar() {
        return btnEliminar;
    }
    
    public JButton getBtnFiltrarPorFecha() {
        return btnFiltrarPorFecha;
    }
    
    public JButton getBtnExportarPDF() {
        return btnExportarPDF;
    }
    
    public JButton getBtnExportarExcel() {
        return btnExportarExcel;
    }
    
    public JDateChooser getFechaInicio() {
        return fechaInicio;
    }
    
    public JDateChooser getFechaFin() {
        return fechaFin;
    }
}