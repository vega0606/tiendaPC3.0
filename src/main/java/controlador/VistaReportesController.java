package controlador;

import java.util.Date;
import java.util.List;
import java.util.Map;

import modelo.Producto;
import modelo.Cliente;
import modelo.Factura;
import ventana.VistaReportes;

/**
 * Controlador para la vista de reportes.
 * Gestiona la interacción entre la vista de reportes y el modelo de datos.
 */
public class VistaReportesController {
    
    private VistaReportes vista;
    private ReporteController reporteController;
    private ProductoController productoController;
    private ClienteController clienteController;
    private FacturaController facturaController;
    
    /**
     * Constructor del controlador de vista de reportes.
     * 
     * @param vista La vista de reportes
     * @param reporteController El controlador de reportes
     * @param productoController El controlador de productos
     * @param clienteController El controlador de clientes
     * @param facturaController El controlador de facturas
     */
    public VistaReportesController(VistaReportes vista, ReporteController reporteController,
                                  ProductoController productoController, ClienteController clienteController,
                                  FacturaController facturaController) {
        this.vista = vista;
        this.reporteController = reporteController;
        this.productoController = productoController;
        this.clienteController = clienteController;
        this.facturaController = facturaController;
        
        // Inicializar los listeners y componentes de la vista
        inicializarVista();
    }
    
    /**
     * Inicializa los componentes de la vista y configura los listeners.
     */
    private void inicializarVista() {
        // Configurar los listeners para los distintos tipos de reportes
        configurarListeners();
    }
    
    /**
     * Configura los listeners para los botones y componentes interactivos de la vista.
     */
    private void configurarListeners() {
        // Listener para generar reporte de ventas por período
        vista.getBtnReporteVentasPeriodo().addActionListener(e -> generarReporteVentasPorPeriodo());
        
        // Listener para generar reporte de productos más vendidos
        vista.getBtnReporteProductosMasVendidos().addActionListener(e -> generarReporteProductosMasVendidos());
        
        // Listener para generar reporte de clientes frecuentes
        vista.getBtnReporteClientesFrecuentes().addActionListener(e -> generarReporteClientesFrecuentes());
        
        // Listener para generar reporte de inventario valorizado
        vista.getBtnReporteInventarioValorizado().addActionListener(e -> generarReporteInventarioValorizado());
        
        // Listener para generar reporte de ganancias por período
        vista.getBtnReporteGananciasPeriodo().addActionListener(e -> generarReporteGananciasPorPeriodo());
        
        // Listener para generar reporte de productos con bajo stock
        vista.getBtnReporteBajoStock().addActionListener(e -> generarReporteProductosBajoStock());
        
        // Listener para generar reporte de devoluciones
        vista.getBtnReporteDevoluciones().addActionListener(e -> generarReporteDevoluciones());
        
        // Listener para exportar a PDF
        vista.getBtnExportarPDF().addActionListener(e -> exportarReporteActual("PDF"));
        
        // Listener para exportar a Excel
        vista.getBtnExportarExcel().addActionListener(e -> exportarReporteActual("Excel"));
    }
    
    /**
     * Genera un reporte de ventas por período.
     */
    public void generarReporteVentasPorPeriodo() {
        try {
            Date fechaInicio = vista.getFechaInicio().getDate();
            Date fechaFin = vista.getFechaFin().getDate();
            
            if (fechaInicio == null || fechaFin == null) {
                vista.mostrarMensaje("Por favor, seleccione un rango de fechas válido");
                return;
            }
            
            if (fechaInicio.after(fechaFin)) {
                vista.mostrarMensaje("La fecha de inicio debe ser anterior a la fecha de fin");
                return;
            }
            
            Map<String, Object> datosReporte = reporteController.generarReporteVentasPorPeriodo(fechaInicio, fechaFin);
            vista.mostrarReporteVentasPorPeriodo(datosReporte);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte de productos más vendidos.
     */
    private void generarReporteProductosMasVendidos() {
        try {
            int cantidad = vista.getCantidadTopProductos();
            Date fechaInicio = vista.getFechaInicio().getDate();
            Date fechaFin = vista.getFechaFin().getDate();
            
            if (fechaInicio == null || fechaFin == null) {
                vista.mostrarMensaje("Por favor, seleccione un rango de fechas válido");
                return;
            }
            
            if (fechaInicio.after(fechaFin)) {
                vista.mostrarMensaje("La fecha de inicio debe ser anterior a la fecha de fin");
                return;
            }
            
            List<Map<String, Object>> datosReporte = ReporteController.generarReporteProductosMasVendidos(cantidad, fechaInicio, fechaFin);
            vista.mostrarReporteProductosMasVendidos(datosReporte);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte de clientes frecuentes.
     */
    private void generarReporteClientesFrecuentes() {
        try {
            int cantidad = vista.getCantidadTopClientes();
            Date fechaInicio = vista.getFechaInicio().getDate();
            Date fechaFin = vista.getFechaFin().getDate();
            
            if (fechaInicio == null || fechaFin == null) {
                vista.mostrarMensaje("Por favor, seleccione un rango de fechas válido");
                return;
            }
            
            if (fechaInicio.after(fechaFin)) {
                vista.mostrarMensaje("La fecha de inicio debe ser anterior a la fecha de fin");
                return;
            }
            
            List<Map<String, Object>> datosReporte = reporteController.generarReporteClientesFrecuentes(cantidad, fechaInicio, fechaFin);
            vista.mostrarReporteClientesFrecuentes(datosReporte);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte de inventario valorizado.
     */
    private void generarReporteInventarioValorizado() {
        try {
            List<Producto> productos = productoController.listarProductos();
            double valorTotal = reporteController.calcularValorTotalInventario(productos);
            
            Map<String, Object> datosReporte = reporteController.generarReporteInventarioValorizado(productos);
            vista.mostrarReporteInventarioValorizado(datosReporte, valorTotal);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte de ganancias por período.
     */
    private void generarReporteGananciasPorPeriodo() {
        try {
            Date fechaInicio = vista.getFechaInicio().getDate();
            Date fechaFin = vista.getFechaFin().getDate();
            
            if (fechaInicio == null || fechaFin == null) {
                vista.mostrarMensaje("Por favor, seleccione un rango de fechas válido");
                return;
            }
            
            if (fechaInicio.after(fechaFin)) {
                vista.mostrarMensaje("La fecha de inicio debe ser anterior a la fecha de fin");
                return;
            }
            
            List<Factura> facturas = facturaController.obtenerFacturasPorPeriodo(fechaInicio, fechaFin);
            Map<String, Object> datosReporte = reporteController.generarReporteGananciasPorPeriodo(facturas);
            vista.mostrarReporteGananciasPorPeriodo(datosReporte);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte de productos con bajo stock.
     */
    private void generarReporteProductosBajoStock() {
        try {
            int limiteStock = vista.getLimiteStock();
            
            List<Producto> productosBajoStock = reporteController.generarReporteProductosBajoStock(limiteStock);
            vista.mostrarReporteProductosBajoStock(productosBajoStock, limiteStock);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte de devoluciones.
     */
    private void generarReporteDevoluciones() {
        try {
            Date fechaInicio = vista.getFechaInicio().getDate();
            Date fechaFin = vista.getFechaFin().getDate();
            
            if (fechaInicio == null || fechaFin == null) {
                vista.mostrarMensaje("Por favor, seleccione un rango de fechas válido");
                return;
            }
            
            if (fechaInicio.after(fechaFin)) {
                vista.mostrarMensaje("La fecha de inicio debe ser anterior a la fecha de fin");
                return;
            }
            
            Map<String, Object> datosReporte = reporteController.generarReporteDevoluciones(fechaInicio, fechaFin);
            vista.mostrarReporteDevoluciones(datosReporte);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Exporta el reporte actual al formato especificado.
     * 
     * @param formato El formato de exportación ("PDF" o "Excel")
     */
    private void exportarReporteActual(String formato) {
        if (!vista.hayReporteGenerado()) {
            vista.mostrarMensaje("Primero debe generar un reporte para exportarlo");
            return;
        }
        
        String tipoReporte = vista.getTipoReporteActual();
        Object datosReporte = vista.getDatosReporteActual();
        
        if (datosReporte == null) {
            vista.mostrarMensaje("No hay datos disponibles para exportar");
            return;
        }
        
        String rutaArchivo = vista.seleccionarRutaGuardado(formato);
        
        if (rutaArchivo != null) {
            boolean exportado = reporteController.exportarReporte(tipoReporte, datosReporte, rutaArchivo, formato);
            
            if (exportado) {
                vista.mostrarMensaje("Reporte exportado correctamente a " + formato);
            } else {
                vista.mostrarMensaje("Error al exportar el reporte a " + formato);
            }
        }
    }
}
