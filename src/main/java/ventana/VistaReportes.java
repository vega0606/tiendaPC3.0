package ventana;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * Vista para la generación y visualización de reportes.
 * Permite generar diferentes tipos de reportes y exportarlos.
 */
public class VistaReportes {
    // Componentes principales
    private JPanel mainPanel;
    private JPanel panelParametros;
    private JPanel panelResultados;
    private CardLayout cardLayoutParametros;
    private CardLayout cardLayoutResultados;
    
    // Componentes comunes
    private JComboBox<String> comboTipoReporte;
    private JButton btnGenerarReporte;
    private JButton btnExportarPDF;
    private JButton btnExportarExcel;
    private JTextField fechaInicio; // Cambiado de JDateChooser para evitar dependencia externa
    private JTextField fechaFin;    // Cambiado de JDateChooser para evitar dependencia externa
    
    // Componentes específicos para cada tipo de reporte
    private JPanel panelVentasPeriodo;
    private JPanel panelProductosMasVendidos;
    private JSpinner spinnerTopProductos;
    private JPanel panelClientesFrecuentes;
    private JSpinner spinnerTopClientes;
    private JPanel panelInventarioValorizado;
    private JPanel panelGananciasPeriodo;
    private JPanel panelBajoStock;
    private JSpinner spinnerLimiteStock;
    private JPanel panelDevoluciones;
    
    // Componentes para mostrar resultados
    private JPanel panelResultadoTabla;
    private JPanel panelResultadoGrafico;
    private JPanel panelResultadoResumen;
    private JTable tablaResultados;
    private JLabel lblTotalResumen;
    
    // Variables de estado
    private boolean hayReporteGenerado = false;
    private String tipoReporteActual = "";
    private Object datosReporteActual = null;
    
    /**
     * Constructor de la vista de reportes.
     */
    public VistaReportes() {
        inicializarPanel();
    }
    
    /**
     * Inicializa el panel principal y sus componentes
     */
    protected void inicializarPanel() {
        // Panel principal
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = crearHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Título
        JLabel titleLabel = new JLabel("Generación de Reportes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Panel central con selector de reporte y parámetros
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        
        // Selector de tipo de reporte
        JPanel panelSelector = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSelector.add(new JLabel("Tipo de Reporte:"));
        
        comboTipoReporte = new JComboBox<>(new String[] {
            "Ventas por Período",
            "Productos más Vendidos",
            "Clientes Frecuentes",
            "Inventario Valorizado",
            "Ganancias por Período",
            "Productos con Bajo Stock",
            "Reporte de Devoluciones"
        });
        
        comboTipoReporte.addActionListener(e -> cambiarPanelParametros());
        panelSelector.add(comboTipoReporte);
        
        btnGenerarReporte = new JButton("Generar Reporte");
        btnGenerarReporte.setBackground(new Color(76, 175, 80));
        btnGenerarReporte.setForeground(Color.WHITE);
        panelSelector.add(btnGenerarReporte);
        
        panelCentral.add(panelSelector, BorderLayout.NORTH);
        
        // Creación de paneles de parámetros
        crearPanelesParametros();
        
        cardLayoutParametros = new CardLayout();
        panelParametros = new JPanel(cardLayoutParametros);
        panelParametros.setBorder(BorderFactory.createTitledBorder("Parámetros del Reporte"));
        
        panelParametros.add(panelVentasPeriodo, "Ventas por Período");
        panelParametros.add(panelProductosMasVendidos, "Productos más Vendidos");
        panelParametros.add(panelClientesFrecuentes, "Clientes Frecuentes");
        panelParametros.add(panelInventarioValorizado, "Inventario Valorizado");
        panelParametros.add(panelGananciasPeriodo, "Ganancias por Período");
        panelParametros.add(panelBajoStock, "Productos con Bajo Stock");
        panelParametros.add(panelDevoluciones, "Reporte de Devoluciones");
        
        // Panel de resultados
        crearPanelesResultados();
        
        cardLayoutResultados = new CardLayout();
        panelResultados = new JPanel(cardLayoutResultados);
        panelResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));
        
        panelResultados.add(panelResultadoTabla, "TABLA");
        panelResultados.add(panelResultadoGrafico, "GRAFICO");
        panelResultados.add(panelResultadoResumen, "RESUMEN");
        
        // Panel de acciones para exportación
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnExportarPDF = new JButton("Exportar a PDF");
        btnExportarPDF.setBackground(new Color(183, 28, 28));
        btnExportarPDF.setForeground(Color.WHITE);
        btnExportarPDF.setEnabled(false);
        panelAcciones.add(btnExportarPDF);
        
        btnExportarExcel = new JButton("Exportar a Excel");
        btnExportarExcel.setBackground(new Color(46, 125, 50));
        btnExportarExcel.setForeground(Color.WHITE);
        btnExportarExcel.setEnabled(false);
        panelAcciones.add(btnExportarExcel);
        
        // Componer todo
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelParametros, BorderLayout.NORTH);
        panelInferior.add(panelResultados, BorderLayout.CENTER);
        panelInferior.add(panelAcciones, BorderLayout.SOUTH);
        
        panelCentral.add(panelInferior, BorderLayout.CENTER);
        
        mainPanel.add(panelCentral, BorderLayout.CENTER);
        
        // Mostrar el primer panel de parámetros
        cambiarPanelParametros();
        
        // Configurar eventos básicos
        configurarEventosBasicos();
    }
    
    /**
     * Crea el panel header.
     */
    private JPanel crearHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBackground(new Color(52, 73, 94));
        
        JLabel titleLabel = new JLabel("Sistema de Gestión - Tienda PC");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Configura los eventos básicos para los componentes.
     */
    private void configurarEventosBasicos() {
        btnGenerarReporte.addActionListener(e -> {
            String tipoReporte = (String) comboTipoReporte.getSelectedItem();
            JOptionPane.showMessageDialog(mainPanel, 
                "Generando reporte: " + tipoReporte, 
                "Información", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnExportarPDF.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel, 
                "Función para exportar a PDF", 
                "Información", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnExportarExcel.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel, 
                "Función para exportar a Excel", 
                "Información", 
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    /**
     * Crea los paneles de parámetros para cada tipo de reporte.
     */
    private void crearPanelesParametros() {
        // Inicializar componentes de fecha compartidos
        fechaInicio = new JTextField(10);
        fechaInicio.setPreferredSize(new Dimension(150, 25));
        
        fechaFin = new JTextField(10);
        fechaFin.setPreferredSize(new Dimension(150, 25));
        
        // Panel de Ventas por Período
        panelVentasPeriodo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelVentasPeriodo.add(new JLabel("Desde:"));
        panelVentasPeriodo.add(fechaInicio);
        
        panelVentasPeriodo.add(new JLabel("Hasta:"));
        panelVentasPeriodo.add(fechaFin);
        
        // Panel de Productos más Vendidos
        panelProductosMasVendidos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelProductosMasVendidos.add(new JLabel("Top productos:"));
        spinnerTopProductos = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        spinnerTopProductos.setPreferredSize(new Dimension(80, 25));
        panelProductosMasVendidos.add(spinnerTopProductos);
        
        panelProductosMasVendidos.add(new JLabel("Desde:"));
        panelProductosMasVendidos.add(fechaInicio);
        
        panelProductosMasVendidos.add(new JLabel("Hasta:"));
        panelProductosMasVendidos.add(fechaFin);
        
        // Panel de Clientes Frecuentes
        panelClientesFrecuentes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelClientesFrecuentes.add(new JLabel("Top clientes:"));
        spinnerTopClientes = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        spinnerTopClientes.setPreferredSize(new Dimension(80, 25));
        panelClientesFrecuentes.add(spinnerTopClientes);
        
        panelClientesFrecuentes.add(new JLabel("Desde:"));
        panelClientesFrecuentes.add(fechaInicio);
        
        panelClientesFrecuentes.add(new JLabel("Hasta:"));
        panelClientesFrecuentes.add(fechaFin);
        
        // Panel de Inventario Valorizado
        panelInventarioValorizado = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelInventarioValorizado.add(new JLabel("No se requieren parámetros adicionales para este reporte"));
        
        // Panel de Ganancias por Período
        panelGananciasPeriodo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelGananciasPeriodo.add(new JLabel("Desde:"));
        panelGananciasPeriodo.add(fechaInicio);
        
        panelGananciasPeriodo.add(new JLabel("Hasta:"));
        panelGananciasPeriodo.add(fechaFin);
        
        // Panel de Productos con Bajo Stock
        panelBajoStock = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBajoStock.add(new JLabel("Límite de stock:"));
        spinnerLimiteStock = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        spinnerLimiteStock.setPreferredSize(new Dimension(80, 25));
        panelBajoStock.add(spinnerLimiteStock);
        
        // Panel de Devoluciones
        panelDevoluciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelDevoluciones.add(new JLabel("Desde:"));
        panelDevoluciones.add(fechaInicio);
        
        panelDevoluciones.add(new JLabel("Hasta:"));
        panelDevoluciones.add(fechaFin);
    }
    
    /**
     * Crea los paneles de resultados para mostrar diferentes tipos de visualización.
     */
    private void crearPanelesResultados() {
        // Panel de resultado tipo tabla
        panelResultadoTabla = new JPanel(new BorderLayout());
        
        DefaultTableModel modeloTabla = new DefaultTableModel();
        tablaResultados = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaResultados);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        panelResultadoTabla.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de resultado tipo gráfico
        panelResultadoGrafico = new JPanel(new BorderLayout());
        panelResultadoGrafico.add(new JLabel("Gráfico del reporte", JLabel.CENTER), BorderLayout.CENTER);
        
        // Panel de resultado tipo resumen
        panelResultadoResumen = new JPanel(new BorderLayout());
        JPanel innerPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel lblTituloResumen = new JLabel("Resumen del Reporte");
        lblTituloResumen.setFont(new Font("Arial", Font.BOLD, 16));
        innerPanel.add(lblTituloResumen, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 10, 20, 10);
        lblTotalResumen = new JLabel("Total: $0.00");
        lblTotalResumen.setFont(new Font("Arial", Font.BOLD, 20));
        innerPanel.add(lblTotalResumen, gbc);
        
        panelResultadoResumen.add(innerPanel, BorderLayout.CENTER);
    }
    
    /**
     * Cambia el panel de parámetros según el tipo de reporte seleccionado.
     */
    private void cambiarPanelParametros() {
        String tipoReporte = (String) comboTipoReporte.getSelectedItem();
        cardLayoutParametros.show(panelParametros, tipoReporte);
        
        // Restablecer el estado
        hayReporteGenerado = false;
        tipoReporteActual = tipoReporte;
        datosReporteActual = null;
        btnExportarPDF.setEnabled(false);
        btnExportarExcel.setEnabled(false);
        
        // Ocultar resultados
        limpiarResultados();
    }
    
    /**
     * Limpia los paneles de resultados.
     */
    private void limpiarResultados() {
        DefaultTableModel model = (DefaultTableModel) tablaResultados.getModel();
        model.setRowCount(0);
        model.setColumnCount(0);
        
        lblTotalResumen.setText("Total: $0.00");
        
        cardLayoutResultados.show(panelResultados, "TABLA");
    }
    
    /**
     * Muestra un mensaje al usuario.
     * 
     * @param mensaje El mensaje a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(mainPanel, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Muestra un diálogo de confirmación.
     * 
     * @param mensaje El mensaje de confirmación
     * @return true si el usuario confirma, false en caso contrario
     */
    public boolean mostrarConfirmacion(String mensaje) {
        return JOptionPane.showConfirmDialog(
            mainPanel,
            mensaje, 
            "Confirmación", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }
    
    /**
     * Obtiene el panel principal de la vista
     * @return El panel principal
     */
    public JPanel getPanel() {
        return mainPanel;
    }
    
    // Getters para los componentes principales
    
    public JComboBox<String> getComboTipoReporte() {
        return comboTipoReporte;
    }
    
    public JButton getBtnGenerarReporte() {
        return btnGenerarReporte;
    }
    
    public JButton getBtnExportarPDF() {
        return btnExportarPDF;
    }
    
    public JButton getBtnExportarExcel() {
        return btnExportarExcel;
    }
    
    public JTextField getFechaInicio() {
        return fechaInicio;
    }
    
    public JTextField getFechaFin() {
        return fechaFin;
    }
    
    public int getCantidadTopProductos() {
        return (Integer) spinnerTopProductos.getValue();
    }
    
    public int getCantidadTopClientes() {
        return (Integer) spinnerTopClientes.getValue();
    }
    
    public int getLimiteStock() {
        return (Integer) spinnerLimiteStock.getValue();
    }
}
