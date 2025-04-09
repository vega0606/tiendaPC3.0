package controlador;

import modelo.Cliente;
import modelo.Producto;
import modelo.Factura;
import modelo.Pedido;
import modelo.Transaccion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controlador para la generación de reportes del sistema
 */
public class ReporteController {
    private static final Logger logger = LoggerFactory.getLogger(ReporteController.class);
    
    private FacturaController facturaController;
    private ProductoController productoController;
    private ClienteController clienteController;
    private PedidoController pedidoController;
    private TransaccionController transaccionController;
    
    public ReporteController() {
        facturaController = new FacturaController();
        productoController = new ProductoController();
        clienteController = new ClienteController();
        pedidoController = new PedidoController();
        transaccionController = new TransaccionController();
    }
    
    /**
     * Genera un reporte de ventas por período
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @param rutaArchivo Ruta donde guardar el reporte
     * @param formato Formato del reporte (PDF, Excel, CSV)
     * @return true si la generación fue exitosa
     */
    public boolean generarReporteVentasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin,
                                                String rutaArchivo, String formato) {
        try {
            // Obtener facturas del período
            List<Factura> facturas = facturaController.buscarFacturasPorFecha(fechaInicio, fechaFin);
            
            if (facturas.isEmpty()) {
                logger.warn("No hay facturas en el período seleccionado");
                return false;
            }
            
            // Calcular totales
            BigDecimal totalVentas = BigDecimal.ZERO;
            BigDecimal totalIVA = BigDecimal.ZERO;
            
            for (Factura factura : facturas) {
                if (!"Anulada".equals(factura.getEstado())) {
                    totalVentas = totalVentas.add(factura.getSubtotal());
                    totalIVA = totalIVA.add(factura.getIva());
                }
            }
            
            // Crear mapa con datos del reporte
            Map<String, Object> datos = new HashMap<>();
            datos.put("fechaInicio", fechaInicio);
            datos.put("fechaFin", fechaFin);
            datos.put("facturas", facturas);
            datos.put("totalVentas", totalVentas);
            datos.put("totalIVA", totalIVA);
            datos.put("totalGeneral", totalVentas.add(totalIVA));
            
            return exportarReporte(datos, "ReporteVentasPorPeriodo", rutaArchivo, formato);
        } catch (Exception e) {
            logger.error("Error al generar reporte de ventas por período: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Genera un reporte de productos vendidos
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @param rutaArchivo Ruta donde guardar el reporte
     * @param formato Formato del reporte (PDF, Excel, CSV)
     * @return true si la generación fue exitosa
     */
    public boolean generarReporteProductosVendidos(LocalDate fechaInicio, LocalDate fechaFin,
                                                 String rutaArchivo, String formato) {
        try {
            // Obtener facturas del período
            List<Factura> facturas = facturaController.buscarFacturasPorFecha(fechaInicio, fechaFin);
            
            if (facturas.isEmpty()) {
                logger.warn("No hay facturas en el período seleccionado");
                return false;
            }
            
            // Calcular cantidades por producto
            Map<String, Integer> cantidadesPorProducto = new HashMap<>();
            Map<String, BigDecimal> ventasPorProducto = new HashMap<>();
            
            for (Factura factura : facturas) {
                if (!"Anulada".equals(factura.getEstado())) {
                    factura.getDetalles().forEach(detalle -> {
                        String codigoProducto = detalle.getCodigoProducto();
                        
                        // Sumar cantidades
                        cantidadesPorProducto.put(
                            codigoProducto, 
                            cantidadesPorProducto.getOrDefault(codigoProducto, 0) + detalle.getCantidad()
                        );
                        
                        // Sumar ventas
                        BigDecimal ventaActual = ventasPorProducto.getOrDefault(codigoProducto, BigDecimal.ZERO);
                        ventasPorProducto.put(
                            codigoProducto,
                            ventaActual.add(detalle.getSubtotal())
                        );
                    });
                }
            }
            
            // Obtener detalles de los productos
            List<Map<String, Object>> productosVendidos = new ArrayList<>();
            
            for (String codigo : cantidadesPorProducto.keySet()) {
                Producto producto = productoController.obtenerProducto(codigo);
                
                if (producto != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("codigo", codigo);
                    item.put("nombre", producto.getNombre());
                    item.put("cantidad", cantidadesPorProducto.get(codigo));
                    item.put("ventaTotal", ventasPorProducto.get(codigo));
                    
                    productosVendidos.add(item);
                }
            }
            
            // Ordenar productos por cantidad vendida (mayor a menor)
            productosVendidos.sort((a, b) -> {
                Integer cantA = (Integer) a.get("cantidad");
                Integer cantB = (Integer) b.get("cantidad");
                return cantB.compareTo(cantA);
            });
            
            // Crear mapa con datos del reporte
            Map<String, Object> datos = new HashMap<>();
            datos.put("fechaInicio", fechaInicio);
            datos.put("fechaFin", fechaFin);
            datos.put("productosVendidos", productosVendidos);
            
            return exportarReporte(datos, "ReporteProductosVendidos", rutaArchivo, formato);
        } catch (Exception e) {
            logger.error("Error al generar reporte de productos vendidos: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Genera un reporte de rentabilidad
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @param rutaArchivo Ruta donde guardar el reporte
     * @param formato Formato del reporte (PDF, Excel, CSV)
     * @return true si la generación fue exitosa
     */
    public boolean generarReporteRentabilidad(LocalDate fechaInicio, LocalDate fechaFin,
                                            String rutaArchivo, String formato) {
        try {
            // Obtener transacciones del período
            Map<String, BigDecimal> resumen = 
                transaccionController.obtenerResumenPorTipo(fechaInicio, fechaFin);
            
            // Crear mapa con datos del reporte
            Map<String, Object> datos = new HashMap<>();
            datos.put("fechaInicio", fechaInicio);
            datos.put("fechaFin", fechaFin);
            datos.put("totalVentas", resumen.get("Venta"));
            datos.put("totalCompras", resumen.get("Compra"));
            datos.put("totalDevoluciones", resumen.get("Devolución"));
            datos.put("balance", resumen.get("Balance"));
            
            return exportarReporte(datos, "ReporteRentabilidad", rutaArchivo, formato);
        } catch (Exception e) {
            logger.error("Error al generar reporte de rentabilidad: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Genera un reporte de clientes
     * @param rutaArchivo Ruta donde guardar el reporte
     * @param formato Formato del reporte (PDF, Excel, CSV)
     * @return true si la generación fue exitosa
     */
    public boolean generarReporteClientes(String rutaArchivo, String formato) {
        try {
            // Obtener clientes
            List<Cliente> clientes = clienteController.listarClientes();
            
            if (clientes.isEmpty()) {
                logger.warn("No hay clientes para generar el reporte");
                return false;
            }
            
            // Crear mapa con datos del reporte
            Map<String, Object> datos = new HashMap<>();
            datos.put("fechaGeneracion", LocalDate.now());
            datos.put("clientes", clientes);
            
            return exportarReporte(datos, "ReporteClientes", rutaArchivo, formato);
        } catch (Exception e) {
            logger.error("Error al generar reporte de clientes: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Genera un reporte de inventario
     * @param rutaArchivo Ruta donde guardar el reporte
     * @param formato Formato del reporte (PDF, Excel, CSV)
     * @param incluirBajoStock Si true, solo incluye productos con bajo stock
     * @return true si la generación fue exitosa
     */
    public boolean generarReporteInventario(String rutaArchivo, String formato, boolean incluirBajoStock) {
        try {
            // Obtener productos
            List<Producto> productos;
            
            if (incluirBajoStock) {
                productos = productoController.obtenerProductosBajoStock();
            } else {
                productos = productoController.listarProductos();
            }
            
            if (productos.isEmpty()) {
                logger.warn("No hay productos para generar el reporte");
                return false;
            }
            
            // Calcular valor total del inventario
            BigDecimal valorTotal = BigDecimal.ZERO;
            
            for (Producto producto : productos) {
                BigDecimal valorProducto = producto.getPrecioCompra()
                    .multiply(BigDecimal.valueOf(producto.getStock()));
                valorTotal = valorTotal.add(valorProducto);
            }
            
            // Crear mapa con datos del reporte
            Map<String, Object> datos = new HashMap<>();
            datos.put("fechaGeneracion", LocalDate.now());
            datos.put("productos", productos);
            datos.put("valorTotal", valorTotal);
            datos.put("soloStockBajo", incluirBajoStock);
            
            String nombreReporte = incluirBajoStock ? "ReporteProductosBajoStock" : "ReporteInventario";
            
            return exportarReporte(datos, nombreReporte, rutaArchivo, formato);
        } catch (Exception e) {
            logger.error("Error al generar reporte de inventario: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Genera un dashboard con estadísticas del sistema
     * @param rutaArchivo Ruta donde guardar el dashboard
     * @param formato Formato del dashboard (PDF)
     * @return true si la generación fue exitosa
     */
    public boolean generarDashboard(String rutaArchivo, String formato) {
        try {
            LocalDate hoy = LocalDate.now();
            LocalDate inicioMes = hoy.withDayOfMonth(1);
            LocalDate mesAnterior = inicioMes.minusMonths(1);
            
            // Ventas del mes actual
            List<Factura> facturasMesActual = 
                facturaController.buscarFacturasPorFecha(inicioMes, hoy);
            
            BigDecimal ventasMesActual = BigDecimal.ZERO;
            for (Factura factura : facturasMesActual) {
                if (!"Anulada".equals(factura.getEstado())) {
                    ventasMesActual = ventasMesActual.add(factura.getTotal());
                }
            }
            
            // Ventas del mes anterior
            List<Factura> facturasMesAnterior = 
                facturaController.buscarFacturasPorFecha(mesAnterior, inicioMes.minusDays(1));
            
            BigDecimal ventasMesAnterior = BigDecimal.ZERO;
            for (Factura factura : facturasMesAnterior) {
                if (!"Anulada".equals(factura.getEstado())) {
                    ventasMesAnterior = ventasMesAnterior.add(factura.getTotal());
                }
            }
            
            // Calcular variación porcentual
            BigDecimal variacionVentas;
            if (ventasMesAnterior.compareTo(BigDecimal.ZERO) > 0) {
                variacionVentas = ventasMesActual
                    .subtract(ventasMesAnterior)
                    .divide(ventasMesAnterior, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            } else {
                variacionVentas = BigDecimal.valueOf(100);
            }
            
            // Productos con bajo stock
            List<Producto> productosBajoStock = productoController.obtenerProductosBajoStock();
            
            // Clientes activos
            List<Cliente> clientesActivos = clienteController.listarClientesPorEstado("Activo");
            
            // Pedidos pendientes
            List<Pedido> pedidosPendientes = pedidoController.buscarPedidosPorEstado("Pendiente");
            
            // Crear mapa con datos del dashboard
            Map<String, Object> datos = new HashMap<>();
            datos.put("fechaGeneracion", hoy);
            datos.put("ventasMesActual", ventasMesActual);
            datos.put("ventasMesAnterior", ventasMesAnterior);
            datos.put("variacionVentas", variacionVentas);
            datos.put("productosBajoStock", productosBajoStock);
            datos.put("cantidadProductosBajoStock", productosBajoStock.size());
            datos.put("clientesActivos", clientesActivos.size());
            datos.put("pedidosPendientes", pedidosPendientes.size());
            
            return exportarReporte(datos, "Dashboard", rutaArchivo, formato);
        } catch (Exception e) {
            logger.error("Error al generar dashboard: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Exporta un reporte en el formato especificado
     * @param datos Datos del reporte
     * @param nombreReporte Nombre del reporte
     * @param rutaArchivo Ruta donde guardar el reporte
     * @param formato Formato del reporte (PDF, Excel, CSV)
     * @return true si la exportación fue exitosa
     */
    private boolean exportarReporte(Map<String, Object> datos, String nombreReporte,
                                   String rutaArchivo, String formato) {
        try {
            // Añadir información general
            datos.put("nombreReporte", nombreReporte);
            datos.put("fechaExportacion", LocalDate.now());
            
            // Generar nombre de archivo si no se especificó
            if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                String fechaStr = LocalDate.now().format(formatter);
                rutaArchivo = System.getProperty("user.home") + 
                             File.separator + "Reportes" + 
                             File.separator + nombreReporte + "_" + fechaStr;
            }
            
            // Asegurar extensión correcta
            if (!rutaArchivo.toLowerCase().endsWith("." + formato.toLowerCase())) {
                rutaArchivo += "." + formato.toLowerCase();
            }
            
            // Crear directorio si no existe
            File archivo = new File(rutaArchivo);
            File directorio = archivo.getParentFile();
            if (directorio != null && !directorio.exists()) {
                directorio.mkdirs();
            }
            
            // Exportar según formato
            switch (formato.toUpperCase()) {
                case "PDF":
                    return exportarPDF(datos, rutaArchivo);
                case "EXCEL":
                    return exportarExcel(datos, rutaArchivo);
                case "CSV":
                    return exportarCSV(datos, rutaArchivo);
                default:
                    logger.error("Formato de reporte no soportado: {}", formato);
                    return false;
            }
        } catch (Exception e) {
            logger.error("Error al exportar reporte: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Exporta un reporte en formato PDF
     * @param datos Datos del reporte
     * @param rutaArchivo Ruta donde guardar el reporte
     * @return true si la exportación fue exitosa
     */
    private boolean exportarPDF(Map<String, Object> datos, String rutaArchivo) {
        // Implementación de exportación a PDF usando JasperReports
        // Esta es una implementación simulada
        logger.info("Exportando reporte a PDF: {}", rutaArchivo);
        
        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            // Aquí iría la lógica real de exportación usando JasperReports
            // Por simplicidad, solo escribimos un mensaje
            String mensaje = "Reporte: " + datos.get("nombreReporte") + 
                           "\nFecha: " + datos.get("fechaExportacion");
            fos.write(mensaje.getBytes());
            return true;
        } catch (Exception e) {
            logger.error("Error al exportar a PDF: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Exporta un reporte en formato Excel
     * @param datos Datos del reporte
     * @param rutaArchivo Ruta donde guardar el reporte
     * @return true si la exportación fue exitosa
     */
    private boolean exportarExcel(Map<String, Object> datos, String rutaArchivo) {
        // Implementación de exportación a Excel usando Apache POI
        // Esta es una implementación simulada
        logger.info("Exportando reporte a Excel: {}", rutaArchivo);
        
        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            // Aquí iría la lógica real de exportación usando Apache POI
            // Por simplicidad, solo escribimos un mensaje
            String mensaje = "Reporte: " + datos.get("nombreReporte") + 
                           "\nFecha: " + datos.get("fechaExportacion");
            fos.write(mensaje.getBytes());
            return true;
        } catch (Exception e) {
            logger.error("Error al exportar a Excel: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Exporta un reporte en formato CSV
     * @param datos Datos del reporte
     * @param rutaArchivo Ruta donde guardar el reporte
     * @return true si la exportación fue exitosa
     */
    private boolean exportarCSV(Map<String, Object> datos, String rutaArchivo) {
        // Implementación de exportación a CSV
        // Esta es una implementación simulada
        logger.info("Exportando reporte a CSV: {}", rutaArchivo);
        
        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            // Aquí iría la lógica real de exportación a CSV
            // Por simplicidad, solo escribimos un mensaje
            String mensaje = "Reporte: " + datos.get("nombreReporte") + 
                           "\nFecha: " + datos.get("fechaExportacion");
            fos.write(mensaje.getBytes());
            return true;
        } catch (Exception e) {
            logger.error("Error al exportar a CSV: {}", e.getMessage(), e);
            return false;
        }
    }
}