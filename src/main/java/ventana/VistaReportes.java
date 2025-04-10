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

import controlador.VistaReportesController;
import controlador.ReporteController;
import controlador.ProductoController;
import controlador.ClienteController;
import controlador.FacturaController;
import modelo.Producto;
import com.toedter.calendar.JDateChooser;

/**
 * Vista para la generación y visualización de reportes.
 * Permite generar diferentes tipos de reportes y exportarlos.
 */
public class VistaReportes extends JPanel {
    // Componentes principales
    private JPanel panelPrincipal;
    private JPanel panelParametros;
    private JPanel panelResultados;
    private CardLayout cardLayoutParametros;
    private CardLayout cardLayoutResultados;
    
    // Componentes comunes
    private JComboBox<String> comboTipoReporte;
    private JButton btnGenerarReporte;
    private JButton btnExportarPDF;
    private JButton btnExportarExcel;
    private JDateChooser fechaInicio;
    private JDateChooser fechaFin;
    
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
    
    // Controlador
    private VistaReportesController controller;
    
    // Variables de estado
    private boolean hayReporteGenerado = false;
    private String tipoReporteActual = "";
    private Object datosReporteActual = null;
    
    /**
     * Constructor de la vista de reportes.
     */
    public VistaReportes() {
        super();
        setLayout(new BorderLayout());
        inicializarPanel();
    }
    
    /**
     * Constructor con controladores inyectados.
     */
    public VistaReportes(
            ReporteController reporteController, 
            ProductoController productoController, 
            ClienteController clienteController, 
            FacturaController facturaController) {
        super();
        setLayout(new BorderLayout());
        inicializarPanel();
        
        controller = new VistaReportesController(
            this, 
            reporteController, 
            productoController, 
            clienteController, 
            facturaController
        );
        
        // Configurar listeners
        configurarListeners();
        
        
    }
    
    protected void inicializarPanel() {
        // Header
        JPanel headerPanel = crearHeaderPanel();
        
        // Panel principal - Contenido
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Título
        JLabel titleLabel = new JLabel("Generación de Reportes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panelPrincipal.add(titleLabel, BorderLayout.NORTH);
        
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
        
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        
        // Panel principal
        add(headerPanel, BorderLayout.NORTH);
        add(panelPrincipal, BorderLayout.CENTER);
        
        // Mostrar el primer panel de parámetros
        cambiarPanelParametros();
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
     * Configura los listeners para los botones y otros componentes.
     */
    private void configurarListeners() {
        if (controller != null) {
            btnGenerarReporte.addActionListener(e -> {
                String tipoReporte = (String) comboTipoReporte.getSelectedItem();
                
                if (tipoReporte.equals("Ventas por Período")) {
                    controller.generarReporteVentasPorPeriodo();
                } else if (tipoReporte.equals("Productos más Vendidos")) {
                    controller.generarReporteProductosMasVendidos();
                } else if (tipoReporte.equals("Clientes Frecuentes")) {
                    controller.generarReporteClientesFrecuentes();
                } else if (tipoReporte.equals("Inventario Valorizado")) {
                    controller.generarReporteInventarioValorizado();
                } else if (tipoReporte.equals("Ganancias por Período")) {
                    controller.generarReporteGananciasPorPeriodo();
                } else if (tipoReporte.equals("Productos con Bajo Stock")) {
                    controller.generarReporteProductosBajoStock();
                } else if (tipoReporte.equals("Reporte de Devoluciones")) {
                    controller.generarReporteDevoluciones();
                }
            });
            
            btnExportarPDF.addActionListener(e -> {
                controller.exportarReporteActual("PDF");
            });
            
            btnExportarExcel.addActionListener(e -> {
                controller.exportarReporteActual("Excel");
            });
        }
    }
    
    /**
     * Crea los paneles de parámetros para cada tipo de reporte.
     */
    private void crearPanelesParametros() {
        // Inicializar componentes de fecha compartidos
        fechaInicio = new JDateChooser();
        fechaInicio.setPreferredSize(new Dimension(150, 25));
        
        fechaFin = new JDateChooser();
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
     * Muestra el reporte de ventas por período.
     * 
     * @param datosReporte Datos del reporte a mostrar
     */
    public void mostrarReporteVentasPorPeriodo(Map<String, Object> datosReporte) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Fecha");
        model.addColumn("Número de Factura");
        model.addColumn("Cliente");
        model.addColumn("Total");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ventas = (List<Map<String, Object>>) datosReporte.get("ventas");
        
        for (Map<String, Object> venta : ventas) {
            model.addRow(new Object[] {
                venta.get("fecha"),
                venta.get("numeroFactura"),
                venta.get("cliente"),
                venta.get("total")
            });
        }
        
        tablaResultados.setModel(model);
        
        // Actualizar totales
        Double totalVentas = (Double) datosReporte.get("totalVentas");
        lblTotalResumen.setText(String.format("Total Ventas: $%.2f", totalVentas));
        
        // Mostrar resultados en formato tabla
        cardLayoutResultados.show(panelResultados, "TABLA");
        
        // Actualizar estado
        hayReporteGenerado = true;
        datosReporteActual = datosReporte;
        btnExportarPDF.setEnabled(true);
        btnExportarExcel.setEnabled(true);
    }
    
    /**
     * Muestra el reporte de productos más vendidos.
     * 
     * @param datosReporte Datos del reporte a mostrar
     */
    public void mostrarReporteProductosMasVendidos(List<Map<String, Object>> datosReporte) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Código");
        model.addColumn("Producto");
        model.addColumn("Cantidad");
        model.addColumn("Total Vendido");
        
        double totalGeneral = 0.0;
        
        for (Map<String, Object> producto : datosReporte) {
            model.addRow(new Object[] {
                producto.get("codigo"),
                producto.get("nombre"),
                producto.get("cantidad"),
                producto.get("totalVendido")
            });
            
            totalGeneral += (Double) producto.get("totalVendido");
        }
        
        tablaResultados.setModel(model);
        
        // Actualizar totales
        lblTotalResumen.setText(String.format("Total Vendido: $%.2f", totalGeneral));
        
        // Mostrar resultados en formato tabla
        cardLayoutResultados.show(panelResultados, "TABLA");
        
        // Actualizar estado
        hayReporteGenerado = true;
        datosReporteActual = datosReporte;
        btnExportarPDF.setEnabled(true);
        btnExportarExcel.setEnabled(true);
    }
    
    /**
     * Muestra el reporte de clientes frecuentes.
     * 
     * @param datosReporte Datos del reporte a mostrar
     */
    public void mostrarReporteClientesFrecuentes(List<Map<String, Object>> datosReporte) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Cliente");
        model.addColumn("Compras");
        model.addColumn("Total Gastado");
        
        double totalGeneral = 0.0;
        
        for (Map<String, Object> cliente : datosReporte) {
            model.addRow(new Object[] {
                cliente.get("id"),
                cliente.get("nombre"),
                cliente.get("compras"),
                cliente.get("totalGastado")
            });
            
            totalGeneral += (Double) cliente.get("totalGastado");
        }
        
        tablaResultados.setModel(model);
        
        // Actualizar totales
        lblTotalResumen.setText(String.format("Total Gastado: $%.2f", totalGeneral));
        
        // Mostrar resultados en formato tabla
        cardLayoutResultados.show(panelResultados, "TABLA");
        
        // Actualizar estado
        hayReporteGenerado = true;
        datosReporteActual = datosReporte;
        btnExportarPDF.setEnabled(true);
        btnExportarExcel.setEnabled(true);
    }
    
    /**
     * Muestra el reporte de inventario valorizado.
     * 
     * @param datosReporte Datos del reporte a mostrar
     * @param valorTotal Valor total del inventario
     */
    public void mostrarReporteInventarioValorizado(Map<String, Object> datosReporte, double valorTotal) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Código");
        model.addColumn("Producto");
        model.addColumn("Stock");
        model.addColumn("Costo Unitario");
        model.addColumn("Valor Total");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> productos = (List<Map<String, Object>>) datosReporte.get("productos");
        
        for (Map<String, Object> producto : productos) {
            model.addRow(new Object[] {
                producto.get("codigo"),
                producto.get("nombre"),
                producto.get("stock"),
                producto.get("costoUnitario"),
                producto.get("valorTotal")
            });
        }
        
        tablaResultados.setModel(model);
        
        // Actualizar totales
        lblTotalResumen.setText(String.format("Valor Total Inventario: $%.2f", valorTotal));
        
        // Mostrar resultados en formato tabla
        cardLayoutResultados.show(panelResultados, "TABLA");
        
        // Actualizar estado
        hayReporteGenerado = true;
        datosReporteActual = datosReporte;
        btnExportarPDF.setEnabled(true);
        btnExportarExcel.setEnabled(true);
    }
    
    /**
     * Muestra el reporte de ganancias por período.
     * 
     * @param datosReporte Datos del reporte a mostrar
     */
    public void mostrarReporteGananciasPorPeriodo(Map<String, Object> datosReporte) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Período");
        model.addColumn("Ventas");
        model.addColumn("Costos");
        model.addColumn("Ganancia");
        model.addColumn("Margen");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> periodos = (List<Map<String, Object>>) datosReporte.get("periodos");
        
        for (Map<String, Object> periodo : periodos) {
            model.addRow(new Object[] {
                periodo.get("nombre"),
                periodo.get("ventas"),
                periodo.get("costos"),
                periodo.get("ganancia"),
                periodo.get("margen") + "%"
            });
        }
        
        tablaResultados.setModel(model);
        
        // Actualizar totales
        Double gananciaTotal = (Double) datosReporte.get("gananciaTotal");
        lblTotalResumen.setText(String.format("Ganancia Total: $%.2f", gananciaTotal));
        
        // Mostrar resultados en formato tabla
        cardLayoutResultados.show(panelResultados, "TABLA");
        
        // Actualizar estado
        hayReporteGenerado = true;
        datosReporteActual = datosReporte;
        btnExportarPDF.setEnabled(true);
        btnExportarExcel.setEnabled(true);
    }
    
    /**
     * Muestra el reporte de productos con bajo stock.
     * 
     * @param productosBajoStock Lista de productos con bajo stock
     * @param limiteStock Límite de stock configurado
     */
    public void mostrarReporteProductosBajoStock(List<Producto> productosBajoStock, int limiteStock) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Código");
        model.addColumn("Producto");
        model.addColumn("Stock Actual");
        model.addColumn("Stock Mínimo");
        model.addColumn("Diferencia");
        
        for (Producto producto : productosBajoStock) {
            int stockActual = producto.getStock();
            int stockMinimo = producto.getStockMinimo();
            
            model.addRow(new Object[] {
                producto.getCodigo(),
                producto.getNombre(),
                stockActual,
                stockMinimo,
                stockActual - stockMinimo
            });
        }
        
        tablaResultados.setModel(model);
        
        // Actualizar totales
        lblTotalResumen.setText(String.format("Productos con stock menor a %d: %d", 
                limiteStock, productosBajoStock.size()));
        
        // Mostrar resultados en formato tabla
        cardLayoutResultados.show(panelResultados, "TABLA");
        
        // Actualizar estado
        hayReporteGenerado = true;
        datosReporteActual = productosBajoStock;
        btnExportarPDF.setEnabled(true);
        btnExportarExcel.setEnabled(true);
    }
    
    /**
     * Muestra el reporte de devoluciones.
     * 
     * @param datosReporte Datos del reporte a mostrar
     */
    public void mostrarReporteDevoluciones(Map<String, Object> datosReporte) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Fecha");
        model.addColumn("Cliente");
        model.addColumn("Producto");
        model.addColumn("Cantidad");
        model.addColumn("Motivo");
        model.addColumn("Estado");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> devoluciones = (List<Map<String, Object>>) datosReporte.get("devoluciones");
        
        for (Map<String, Object> devolucion : devoluciones) {
            model.addRow(new Object[] {
                devolucion.get("id"),
                devolucion.get("fecha"),
                devolucion.get("cliente"),
                devolucion.get("producto"),
                devolucion.get("cantidad"),
                devolucion.get("motivo"),
                devolucion.get("estado")
            });
        }
        
        tablaResultados.setModel(model);
        
        // Actualizar totales
        Integer totalDevoluciones = (Integer) datosReporte.get("totalDevoluciones");
        Double valorTotal = (Double) datosReporte.get("valorTotal");
        lblTotalResumen.setText(String.format("Total Devoluciones: %d | Valor: $%.2f", 
                totalDevoluciones, valorTotal));
        
        // Mostrar resultados en formato tabla
        cardLayoutResultados.show(panelResultados, "TABLA");
        
        // Actualizar estado
        hayReporteGenerado = true;
        datosReporteActual = datosReporte;
        btnExportarPDF.setEnabled(true);
        btnExportarExcel.setEnabled(true);
    }
    
    /**
     * Indica si hay un reporte generado actualmente.
     * 
     * @return true si hay un reporte generado, false en caso contrario
     */
    public boolean hayReporteGenerado() {
        return hayReporteGenerado;
    }
    
    /**
     * Obtiene el tipo de reporte actual.
     * 
     * @return Tipo de reporte actual
     */
    public String getTipoReporteActual() {
        return tipoReporteActual;
    }
    
    /**
     * Obtiene los datos del reporte actual.
     * 
     * @return Datos del reporte actual
     */
    public Object getDatosReporteActual() {
        return datosReporteActual;
    }
    
    /**
     * Obtiene la cantidad de top productos configurada.
     * 
     * @return Cantidad de top productos
     */
    public int getCantidadTopProductos() {
        return (Integer) spinnerTopProductos.getValue();
    }
    
    /**
     * Obtiene la cantidad de top clientes configurada.
     *   * 
    * @return Cantidad de top clientes
    */
   public int getCantidadTopClientes() {
       return (Integer) spinnerTopClientes.getValue();
   }
   
   /**
    * Obtiene el límite de stock configurado.
    * 
    * @return Límite de stock
    */
   public int getLimiteStock() {
       return (Integer) spinnerLimiteStock.getValue();
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
   public void setController(VistaReportesController controller) {
       this.controller = controller;
       configurarListeners();
   }
   
   // Getters para que el controlador pueda acceder a los componentes
   
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
   
   public JDateChooser getFechaInicio() {
       return fechaInicio;
   }
   
   public JDateChooser getFechaFin() {
       return fechaFin;
   }
   
   // Otros métodos de acceso para compatibilidad con el controlador
   
   public JButton getBtnReporteVentasPeriodo() {
       return btnGenerarReporte;
   }
   
   public JButton getBtnReporteProductosMasVendidos() {
       return btnGenerarReporte;
   }
   
   public JButton getBtnReporteClientesFrecuentes() {
       return btnGenerarReporte;
   }
   
   public JButton getBtnReporteInventarioValorizado() {
       return btnGenerarReporte;
   }
   
   public JButton getBtnReporteGananciasPeriodo() {
       return btnGenerarReporte;
   }
   
   public JButton getBtnReporteBajoStock() {
       return btnGenerarReporte;
   }
   
   public JButton getBtnReporteDevoluciones() {
       return btnGenerarReporte;
   }



}
