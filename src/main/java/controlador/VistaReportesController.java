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
        // No es necesario configurar los listeners aquí porque la vista ya los configura
        // en su método configurarListeners()
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
            
            if (datosReporte == null) {
                vista.mostrarMensaje("No hay datos para el período seleccionado");
                return;
            }
            
            vista.mostrarReporteVentasPorPeriodo(datosReporte);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte de productos más vendidos.
     */
    public void generarReporteProductosMasVendidos() {
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
            
            List<Map<String, Object>> datosReporte = reporteController.generarReporteProductosMasVendidos(cantidad, fechaInicio, fechaFin);
            
            if (datosReporte == null || datosReporte.isEmpty()) {
                vista.mostrarMensaje("No hay datos para el período seleccionado");
                return;
            }
            
            vista.mostrarReporteProductosMasVendidos(datosReporte);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte de clientes frecuentes.
     */
    public void generarReporteClientesFrecuentes() {
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
            
            if (datosReporte == null || datosReporte.isEmpty()) {
                vista.mostrarMensaje("No hay datos para el período seleccionado");
                return;
            }
            
            vista.mostrarReporteClientesFrecuentes(datosReporte);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte de inventario valorizado.
     */
    public void generarReporteInventarioValorizado() {
        try {
            List<Producto> productos = productoController.listarProductos();
            
            if (productos == null || productos.isEmpty()) {
                vista.mostrarMensaje("No hay productos en el inventario");
                return;
            }
            
            double valorTotal = reporteController.calcularValorTotalInventario(productos);
            Map<String, Object> datosReporte = reporteController.generarReporteInventarioValorizado(productos);
            
            if (datosReporte == null) {
                vista.mostrarMensaje("Error al generar los datos del reporte");
                return;
            }
            
            vista.mostrarReporteInventarioValorizado(datosReporte, valorTotal);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte de ganancias por período.
     */
    public void generarReporteGananciasPorPeriodo() {
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
            
            // Convertir Date a LocalDate para usar en facturaController
            java.time.LocalDate inicio = fechaInicio.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
            java.time.LocalDate fin = fechaFin.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
                
            List<Factura> facturas = facturaController.buscarFacturasPorFecha(inicio, fin);
            
            if (facturas == null || facturas.isEmpty()) {
                vista.mostrarMensaje("No hay facturas en el período seleccionado");
                return;
            }
            
            Map<String, Object> datosReporte = reporteController.generarReporteGananciasPorPeriodo(facturas);
            
            if (datosReporte == null) {
                vista.mostrarMensaje("Error al generar los datos del reporte");
                return;
            }
            
            vista.mostrarReporteGananciasPorPeriodo(datosReporte);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte de productos con bajo stock.
     */
    public void generarReporteProductosBajoStock() {
        try {
            int limiteStock = vista.getLimiteStock();
            
            List<Producto> productosBajoStock = reporteController.generarReporteProductosBajoStock(limiteStock);
            
            if (productosBajoStock == null) {
                vista.mostrarMensaje("Error al generar el reporte");
                return;
            }
            
            if (productosBajoStock.isEmpty()) {
                vista.mostrarMensaje("No hay productos con stock por debajo de " + limiteStock);
                return;
            }
            
            vista.mostrarReporteProductosBajoStock(productosBajoStock, limiteStock);
            
        } catch (Exception e) {
            vista.mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }
    
    /**
     * Genera un reporte de devoluciones.
     */
    public void generarReporteDevoluciones() {
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
            
            if (datosReporte == null) {
                vista.mostrarMensaje("Error al generar los datos del reporte");
                return;
            }
            
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
    public void exportarReporteActual(String formato) {
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