package ventana;  

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import controlador.VistaTransaccionesController;  
import controlador.TransaccionController;
import modelo.Transaccion;  
import com.toedter.calendar.JDateChooser;

/**
 * Vista para la gestión de transacciones financieras.
 * Permite visualizar, crear, editar y eliminar transacciones.
 */
public class VistaTransacciones extends JPanel {
    // Componentes principales
    private JTextField txtBusqueda;
    private JTable tablaTransacciones;
    private JButton btnBuscar;
    private JButton btnMostrarTodas;
    private JButton btnNuevaTransaccion;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnFiltrarPorTipo;
    private JButton btnFiltrarPorFecha;
    private JButton btnFiltrarPorMonto;
    private JButton btnExportarPDF;
    private JButton btnExportarExcel;
    private JComboBox<String> comboTipoTransaccion;
    private JDateChooser fechaInicio;
    private JDateChooser fechaFin;
    private JTextField txtMontoMinimo;
    private JTextField txtMontoMaximo;
    
    // Paneles para vistas múltiples
    private JPanel listadoPanel;
    private JPanel formularioPanel;
    private CardLayout cardLayout;
    
    // Campos del formulario
    private JTextField txtReferencia;
    private JComboBox<String> comboTipoForm;
    private JDateChooser fechaTransaccion;
    private JTextField txtMonto;
    private JTextField txtResponsable;
    private JTextArea txtDescripcion;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    // Controlador
    private VistaTransaccionesController controller;
    
    // Variables de estado
    private boolean editando = false;
    private Transaccion transaccionEnEdicion = null;
    
    /**
     * Constructor de la vista de transacciones.
     */
    public VistaTransacciones() {
        setLayout(new BorderLayout());
        inicializarPanel();
    }
    
    /**
     * Constructor con controlador inyectado.
     * 
     * @param transaccionController El controlador de transacciones
     */
    public VistaTransacciones(TransaccionController transaccionController) {
        setLayout(new BorderLayout());
        inicializarPanel();
        
        // Inicializar controlador
        this.controller = new VistaTransaccionesController(this, transaccionController);
        // El controlador ya configura los listeners, así que no necesitamos llamar a configurarListeners()
    }
    
    /**
     * Inicializa todos los componentes del panel.
     */
    private void inicializarPanel() {
        // Header
        JPanel headerPanel = crearHeaderPanel();
        
        // Panel principal - Contenido cambiante
        JPanel mainContentPanel = new JPanel();
        cardLayout = new CardLayout();
        mainContentPanel.setLayout(cardLayout);
        
        // Crear panel de listado
        listadoPanel = crearPanelListado();
        
        // Crear panel de formulario
        formularioPanel = crearPanelFormulario();
        
        // Añadir paneles al panel principal
        mainContentPanel.add(listadoPanel, "LISTADO");
        mainContentPanel.add(formularioPanel, "FORMULARIO");
        
        // Panel principal
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Mostrar vista de listado por defecto
        cardLayout.show(mainContentPanel, "LISTADO");
    }
    
    /**
     * Crea el panel header.
     */
    private JPanel crearHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBackground(new Color(52, 73, 94));
        
        JLabel titleLabel = new JLabel("Sistema de Gestión - Transacciones Financieras");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Crea el panel para el listado de transacciones.
     * 
     * @return Panel configurado con la tabla y controles
     */
    private JPanel crearPanelListado() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Listado de Transacciones");
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
        
        // Panel de filtros
        JPanel filtroPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        
        // Fila 1: Tipo y Fecha
        JPanel filtroFila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        filtroFila1.add(new JLabel("Tipo:"));
        comboTipoTransaccion = new JComboBox<>(new String[] {
            "Todas", "Ingreso", "Egreso", "Venta", "Compra", "Pago", "Otro"
        });
        filtroFila1.add(comboTipoTransaccion);
        
        btnFiltrarPorTipo = new JButton("Filtrar");
        btnFiltrarPorTipo.setBackground(new Color(74, 134, 232));
        btnFiltrarPorTipo.setForeground(Color.WHITE);
        filtroFila1.add(btnFiltrarPorTipo);
        
        filtroFila1.add(Box.createHorizontalStrut(20));
        
        filtroFila1.add(new JLabel("Desde:"));
        fechaInicio = new JDateChooser();
        fechaInicio.setPreferredSize(new Dimension(100, 25));
        filtroFila1.add(fechaInicio);
        
        filtroFila1.add(new JLabel("Hasta:"));
        fechaFin = new JDateChooser();
        fechaFin.setPreferredSize(new Dimension(100, 25));
        filtroFila1.add(fechaFin);
        
        btnFiltrarPorFecha = new JButton("Filtrar");
        btnFiltrarPorFecha.setBackground(new Color(74, 134, 232));
        btnFiltrarPorFecha.setForeground(Color.WHITE);
        filtroFila1.add(btnFiltrarPorFecha);
        
        // Fila 2: Monto
        JPanel filtroFila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        filtroFila2.add(new JLabel("Monto Mínimo:"));
        txtMontoMinimo = new JTextField(10);
        filtroFila2.add(txtMontoMinimo);
        
        filtroFila2.add(new JLabel("Monto Máximo:"));
        txtMontoMaximo = new JTextField(10);
        filtroFila2.add(txtMontoMaximo);
        
        btnFiltrarPorMonto = new JButton("Filtrar por Monto");
        btnFiltrarPorMonto.setBackground(new Color(74, 134, 232));
        btnFiltrarPorMonto.setForeground(Color.WHITE);
        filtroFila2.add(btnFiltrarPorMonto);
        
        // Añadir filas al panel de filtros
        filtroPanel.add(filtroFila1);
        filtroPanel.add(filtroFila2);
        
        // Tabla de transacciones
        String[] columnNames = {"ID", "Referencia", "Tipo", "Fecha", "Monto", "Responsable"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No permitir edición directa en la tabla
            }
        };
        
        tablaTransacciones = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tablaTransacciones);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        // Panel de acciones
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        btnNuevaTransaccion = new JButton("Nueva Transacción");
        btnNuevaTransaccion.setBackground(new Color(76, 175, 80));
        btnNuevaTransaccion.setForeground(Color.WHITE);
        actionPanel.add(btnNuevaTransaccion);
        
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
     * Crea el panel para el formulario de transacciones.
     * 
     * @return Panel configurado con el formulario
     */
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("Nueva Transacción");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Referencia
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Referencia:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtReferencia = new JTextField(20);
        formPanel.add(txtReferencia, gbc);
        
        // Tipo
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 1;
        comboTipoForm = new JComboBox<>(new String[] {
            "Ingreso", "Egreso", "Venta", "Compra", "Pago", "Otro"
        });
        formPanel.add(comboTipoForm, gbc);
        
        // Fecha
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Fecha:"), gbc);
        
        gbc.gridx = 1;
        fechaTransaccion = new JDateChooser();
        fechaTransaccion.setDate(new Date());
        formPanel.add(fechaTransaccion, gbc);
        
        // Monto
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Monto:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtMonto = new JTextField(15);
        formPanel.add(txtMonto, gbc);
        
        // Responsable
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Responsable:"), gbc);
        
        gbc.gridx = 1;
        txtResponsable = new JTextField(20);
        formPanel.add(txtResponsable, gbc);
        
        // Descripción
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        txtDescripcion = new JTextArea(5, 30);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtDescripcion);
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
        Container parent = getParent();
        if (parent != null) {
            cardLayout.show(parent, "LISTADO");
        }
    }
    
    /**
     * Muestra el panel de formulario para una nueva transacción o edición.
     * 
     * @param transaccion La transacción a editar, o null para una nueva
     */
    public void mostrarFormularioTransaccion(Transaccion transaccion) {
        limpiarFormulario();
        
        if (transaccion == null) {
            // Nueva transacción
            editando = false;
            transaccionEnEdicion = null;
            
            // Actualizar título
            JLabel titleLabel = (JLabel) formularioPanel.getComponent(0);
            titleLabel.setText("Nueva Transacción");
            
            // Establecer valores por defecto
            fechaTransaccion.setDate(new Date());
        } else {
            // Editar transacción existente
            editando = true;
            transaccionEnEdicion = transaccion;
            
            // Cargar datos de la transacción en el formulario
            txtReferencia.setText(transaccion.getReferencia());
            comboTipoForm.setSelectedItem(transaccion.getTipo());
            fechaTransaccion.setDate(transaccion.getFecha());
            txtMonto.setText(String.valueOf(transaccion.getMonto()));
            txtResponsable.setText(transaccion.getResponsable());
            txtDescripcion.setText(transaccion.getDescripcion());
            
            // Actualizar título
            JLabel titleLabel = (JLabel) formularioPanel.getComponent(0);
            titleLabel.setText("Editar Transacción");
        }
        
        Container parent = getParent();
        if (parent != null) {
            cardLayout.show(parent, "FORMULARIO");
        }
    }
    
    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        txtReferencia.setText("");
        comboTipoForm.setSelectedIndex(0);
        fechaTransaccion.setDate(new Date());
        txtMonto.setText("");
        txtResponsable.setText("");
        txtDescripcion.setText("");
    }
    
    /**
     * Obtiene los datos de la transacción desde el formulario.
     * 
     * @return Transacción con los datos ingresados
     */
    public Transaccion obtenerTransaccionDesdeFormulario() {
        Transaccion transaccion = new Transaccion();
        
        if (editando && transaccionEnEdicion != null) {
            transaccion.setId(transaccionEnEdicion.getId());
        }
        
        transaccion.setReferencia(txtReferencia.getText().trim());
        transaccion.setTipo(comboTipoForm.getSelectedItem().toString());
        transaccion.setFecha(fechaTransaccion.getDate());
        
        try {
            transaccion.setMonto(Double.parseDouble(txtMonto.getText().trim().replace(",", ".")));
        } catch (NumberFormatException e) {
            transaccion.setMonto(0.0);
        }
        
        transaccion.setResponsable(txtResponsable.getText().trim());
        transaccion.setDescripcion(txtDescripcion.getText().trim());
        
        return transaccion;
    }
    
    /**
     * Muestra las transacciones en la tabla.
     * 
     * @param transacciones Lista de transacciones a mostrar
     */
    public void mostrarTransacciones(List<Transaccion> transacciones) {
        DefaultTableModel model = (DefaultTableModel) tablaTransacciones.getModel();
        model.setRowCount(0); // Limpiar tabla
        
        for (Transaccion transaccion : transacciones) {
            model.addRow(new Object[] {
                transaccion.getId(),
                transaccion.getReferencia(),
                transaccion.getTipo(),
                transaccion.getFecha(),
                transaccion.getMonto(),
                transaccion.getResponsable()
            });
        }
    }
    
    /**
     * Muestra una sola transacción en la tabla.
     * 
     * @param transaccion La transacción a mostrar
     */
    public void mostrarTransaccion(Transaccion transaccion) {
        DefaultTableModel model = (DefaultTableModel) tablaTransacciones.getModel();
        model.setRowCount(0); // Limpiar tabla
        
        model.addRow(new Object[] {
            transaccion.getId(),
            transaccion.getReferencia(),
            transaccion.getTipo(),
            transaccion.getFecha(),
            transaccion.getMonto(),
            transaccion.getResponsable()
        });
    }
    
    /**
     * Obtiene la transacción seleccionada en la tabla.
     * 
     * @return Transacción seleccionada o null si no hay selección
     */
    public Transaccion obtenerTransaccionSeleccionada() {
        int selectedRow = tablaTransacciones.getSelectedRow();
        
        if (selectedRow < 0) {
            return null;
        }
        
        Transaccion transaccion = new Transaccion();
        transaccion.setId((Integer) tablaTransacciones.getValueAt(selectedRow, 0));
        transaccion.setReferencia((String) tablaTransacciones.getValueAt(selectedRow, 1));
        transaccion.setTipo((String) tablaTransacciones.getValueAt(selectedRow, 2));
        transaccion.setFecha((Date) tablaTransacciones.getValueAt(selectedRow, 3));
        transaccion.setMonto((Double) tablaTransacciones.getValueAt(selectedRow, 4));
        transaccion.setResponsable((String) tablaTransacciones.getValueAt(selectedRow, 5));
        
        return transaccion;
    }
    
    /**
     * Obtiene todas las transacciones mostradas en la tabla.
     * 
     * @return Lista con todas las transacciones mostradas
     */
    public List<Transaccion> obtenerTransaccionesMostradas() {
        List<Transaccion> transacciones = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) tablaTransacciones.getModel();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            Transaccion transaccion = new Transaccion();
            transaccion.setId((Integer) model.getValueAt(i, 0));
            transaccion.setReferencia((String) model.getValueAt(i, 1));
            transaccion.setTipo((String) model.getValueAt(i, 2));
            transaccion.setFecha((Date) model.getValueAt(i, 3));
            transaccion.setMonto((Double) model.getValueAt(i, 4));
            transaccion.setResponsable((String) model.getValueAt(i, 5));
            
            transacciones.add(transaccion);
        }
        
        return transacciones;
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
    public void setController(VistaTransaccionesController controller) {
        this.controller = controller;
    }
    
    // Getters para que el controlador pueda acceder a los componentes
    
    public JTextField getTxtBusqueda() {
        return txtBusqueda;
    }
    
    public JTable getTablaTransacciones() {
        return tablaTransacciones;
    }
    
    public JButton getBtnBuscar() {
        return btnBuscar;
    }
    
    public JButton getBtnMostrarTodas() {
        return btnMostrarTodas;
    }
    
    public JButton getBtnNuevaTransaccion() {
        return btnNuevaTransaccion;
    }
    
    public JButton getBtnEditar() {
        return btnEditar;
    }
    
    public JButton getBtnEliminar() {
        return btnEliminar;
    }
    
    public JButton getBtnFiltrarPorTipo() {
        return btnFiltrarPorTipo;
    }
    
    public JButton getBtnFiltrarPorFecha() {
        return btnFiltrarPorFecha;
    }
    
    public JButton getBtnFiltrarPorMonto() {
        return btnFiltrarPorMonto;
    }
    
    public JButton getBtnExportarPDF() {
        return btnExportarPDF;
    }
    
    public JButton getBtnExportarExcel() {
        return btnExportarExcel;
    }
    
    public JComboBox<String> getComboTipoTransaccion() {
        return comboTipoTransaccion;
    }
    
    public JDateChooser getFechaInicio() {
        return fechaInicio;
    }
    
    public JDateChooser getFechaFin() {
        return fechaFin;
    }
    
    public JTextField getTxtMontoMinimo() {
        return txtMontoMinimo;
    }
    
    public JTextField getTxtMontoMaximo() {
        return txtMontoMaximo;
    }
}