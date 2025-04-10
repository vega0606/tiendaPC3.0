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
import java.time.ZoneId;
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
     * Genera datos para un reporte de ventas por período sin exportar a archivo
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Mapa con los datos del reporte o null si hay un error
     */
    public Map<String, Object> generarReporteVentasPorPeriodo(Date fechaInicio, Date fechaFin) {
        try {
            // Convertir Date a LocalDate
            LocalDate inicio = fechaInicio.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            LocalDate fin = fechaFin.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
                
            // Obtener facturas del período
            List<Factura> facturas = facturaController.buscarFacturasPorFecha(inicio, fin);
            
            if (facturas.isEmpty()) {
                logger.warn("No hay facturas en el período seleccionado");
                return null;
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
            
            // Crear listado de ventas para mostrar en la vista
            List<Map<String, Object>> ventas = new ArrayList<>();
            for (Factura factura : facturas) {
                if (!"Anulada".equals(factura.getEstado())) {
                    Map<String, Object> venta = new HashMap<>();
                    venta.put("fecha", factura.getFecha());
                    venta.put("numeroFactura", factura.getNumero());
                    venta.put("cliente", factura.getCliente());
                    venta.put("total", factura.getTotal());
                    ventas.add(venta);
                }
            }
            
            // Crear mapa con datos del reporte
            Map<String, Object> datos = new HashMap<>();
            datos.put("fechaInicio", inicio);
            datos.put("fechaFin", fin);
            datos.put("ventas", ventas);
            datos.put("totalVentas", totalVentas.doubleValue());
            datos.put("totalIVA", totalIVA.doubleValue());
            datos.put("totalGeneral", totalVentas.add(totalIVA).doubleValue());
            
            return datos;
        } catch (Exception e) {
            logger.error("Error al generar datos de reporte de ventas por período: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Genera datos para un reporte de productos más vendidos
     * @param cantidad Cantidad de productos a mostrar
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista con los datos del reporte o null si hay un error
     */
    public List<Map<String, Object>> generarReporteProductosMasVendidos(int cantidad, Date fechaInicio, Date fechaFin) {
        try {
            // Convertir Date a LocalDate
            LocalDate inicio = fechaInicio.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            LocalDate fin = fechaFin.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
                
            // Obtener facturas del período
            List<Factura> facturas = facturaController.buscarFacturasPorFecha(inicio, fin);
            
            if (facturas.isEmpty()) {
                logger.warn("No hay facturas en el período seleccionado");
                return null;
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
                    item.put("totalVendido", ventasPorProducto.get(codigo).doubleValue());
                    
                    productosVendidos.add(item);
                }
            }
            
            // Ordenar productos por cantidad vendida (mayor a menor)
            productosVendidos.sort((a, b) -> {
                Integer cantA = (Integer) a.get("cantidad");
                Integer cantB = (Integer) b.get("cantidad");
                return cantB.compareTo(cantA);
            });
            
            // Limitar al número solicitado
            if (productosVendidos.size() > cantidad) {
                productosVendidos = productosVendidos.subList(0, cantidad);
            }
            
            return productosVendidos;
        } catch (Exception e) {
            logger.error("Error al generar reporte de productos más vendidos: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Genera datos para un reporte de clientes frecuentes
     * @param cantidad Cantidad de clientes a mostrar
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista con los datos del reporte o null si hay un error
     */
    public List<Map<String, Object>> generarReporteClientesFrecuentes(int cantidad, Date fechaInicio, Date fechaFin) {
        try {
            // Convertir Date a LocalDate
            LocalDate inicio = fechaInicio.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            LocalDate fin = fechaFin.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
                
            // Obtener facturas del período
            List<Factura> facturas = facturaController.buscarFacturasPorFecha(inicio, fin);
            
            if (facturas.isEmpty()) {
                logger.warn("No hay facturas en el período seleccionado");
                return null;
            }
            
            // Calcular compras por cliente
            Map<String, Integer> comprasPorCliente = new HashMap<>();
            Map<String, BigDecimal> gastosPorCliente = new HashMap<>();
            Map<String, String> nombresClientes = new HashMap<>();
            
            for (Factura factura : facturas) {
                if (!"Anulada".equals(factura.getEstado())) {
                    String idCliente = factura.getIdCliente();
                    String nombreCliente = factura.getCliente().getNombre();
                    
                    // Guardar el nombre del cliente
                    nombresClientes.put(idCliente, nombreCliente);
                    
                    // Sumar compras
                    comprasPorCliente.put(
                        idCliente, 
                        comprasPorCliente.getOrDefault(idCliente, 0) + 1
                    );
                    
                    // Sumar gastos
                    BigDecimal gastoActual = gastosPorCliente.getOrDefault(idCliente, BigDecimal.ZERO);
                    gastosPorCliente.put(
                        idCliente,
                        gastoActual.add(factura.getTotal())
                    );
                }
            }
            
            // Crear lista de clientes frecuentes
            List<Map<String, Object>> clientesFrecuentes = new ArrayList<>();
            
            for (String id : comprasPorCliente.keySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", id);
                item.put("nombre", nombresClientes.get(id));
                item.put("compras", comprasPorCliente.get(id));
                item.put("totalGastado", gastosPorCliente.get(id).doubleValue());
                
                clientesFrecuentes.add(item);
            }
            
            // Ordenar clientes por cantidad de compras (mayor a menor)
            clientesFrecuentes.sort((a, b) -> {
                Integer comprasA = (Integer) a.get("compras");
                Integer comprasB = (Integer) b.get("compras");
                return comprasB.compareTo(comprasA);
            });
            
            // Limitar al número solicitado
            if (clientesFrecuentes.size() > cantidad) {
                clientesFrecuentes = clientesFrecuentes.subList(0, cantidad);
            }
            
            return clientesFrecuentes;
        } catch (Exception e) {
            logger.error("Error al generar reporte de clientes frecuentes: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Genera datos para un reporte de inventario valorizado
     * @return Mapa con los datos del reporte y valor total del inventario
     */
    public Map<String, Object> generarReporteInventarioValorizado(List<Producto> productos) {
        try {
            if (productos == null) {
                productos = productoController.listarProductos();
            }
            
            if (productos.isEmpty()) {
                logger.warn("No hay productos para generar el reporte");
                return null;
            }
            
            // Calcular valor del inventario por producto
            double valorTotal = 0;
            List<Map<String, Object>> datosProductos = new ArrayList<>();
            
            for (Producto producto : productos) {
                double costoUnitario = producto.getPrecioCompra().doubleValue();
                int stock = producto.getStock();
                double valorProducto = costoUnitario * stock;
                
                Map<String, Object> item = new HashMap<>();
                item.put("codigo", producto.getCodigo());
                item.put("nombre", producto.getNombre());
                item.put("stock", stock);
                item.put("costoUnitario", costoUnitario);
                item.put("valorTotal", valorProducto);
                
                datosProductos.add(item);
                valorTotal += valorProducto;
            }
            
            // Crear mapa con datos del reporte
            Map<String, Object> datos = new HashMap<>();
            datos.put("productos", datosProductos);
            datos.put("fechaGeneracion", LocalDate.now());
            datos.put("valorTotal", valorTotal);
            
            return datos;
        } catch (Exception e) {
            logger.error("Error al generar reporte de inventario valorizado: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Calcula el valor total del inventario
     * @param productos Lista de productos
     * @return Valor total del inventario
     */
    public double calcularValorTotalInventario(List<Producto> productos) {
        double valorTotal = 0;
        for (Producto producto : productos) {
            valorTotal += producto.getPrecioCompra().doubleValue() * producto.getStock();
        }
        return valorTotal;
    }
    
    /**
     * Genera datos para un reporte de ganancias por período
     * @param facturas Lista de facturas
     * @return Mapa con los datos del reporte
     */
    public Map<String, Object> generarReporteGananciasPorPeriodo(List<Factura> facturas) {
        try {
            if (facturas == null || facturas.isEmpty()) {
                logger.warn("No hay facturas para generar el reporte");
                return null;
            }
            
            // Agrupar por período (mes)
            Map<String, BigDecimal> ventasPorMes = new HashMap<>();
            Map<String, BigDecimal> costosPorMes = new HashMap<>();
            
            for (Factura factura : facturas) {
                if (!"Anulada".equals(factura.getEstado())) {
                    LocalDate fecha = factura.getFecha();
                    String periodo = fecha.getYear() + "-" + fecha.getMonthValue();
                    
                    // Sumar ventas
                    BigDecimal ventaActual = ventasPorMes.getOrDefault(periodo, BigDecimal.ZERO);
                    ventasPorMes.put(periodo, ventaActual.add(factura.getTotal()));
                    
                    // Sumar costos (simulado con un margen del 35%)
                    BigDecimal costoActual = costosPorMes.getOrDefault(periodo, BigDecimal.ZERO);
                    BigDecimal costo = factura.getSubtotal().multiply(new BigDecimal("0.65"));
                    costosPorMes.put(periodo, costoActual.add(costo));
                }
            }
            
            // Calcular ganancias por período
            List<Map<String, Object>> periodos = new ArrayList<>();
            BigDecimal gananciaTotal = BigDecimal.ZERO;
            
            for (String periodo : ventasPorMes.keySet()) {
                BigDecimal ventas = ventasPorMes.get(periodo);
                BigDecimal costos = costosPorMes.get(periodo);
                BigDecimal ganancia = ventas.subtract(costos);
                
                // Calcular margen
                int margen = ganancia.multiply(new BigDecimal("100"))
                    .divide(ventas, 0, BigDecimal.ROUND_HALF_UP)
                    .intValue();
                
                Map<String, Object> item = new HashMap<>();
                item.put("nombre", periodo);
                item.put("ventas", ventas.doubleValue());
                item.put("costos", costos.doubleValue());
                item.put("ganancia", ganancia.doubleValue());
                item.put("margen", margen);
                
                periodos.add(item);
                gananciaTotal = gananciaTotal.add(ganancia);
            }
            
            // Ordenar períodos cronológicamente
            periodos.sort(Comparator.comparing(m -> (String) m.get("nombre")));
            
            // Crear mapa con datos del reporte
            Map<String, Object> datos = new HashMap<>();
            datos.put("periodos", periodos);
            datos.put("gananciaTotal", gananciaTotal.doubleValue());
            
            return datos;
        } catch (Exception e) {
            logger.error("Error al generar reporte de ganancias por período: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Genera una lista de productos con bajo stock
     * @param limiteStock Límite de stock
     * @return Lista de productos con stock por debajo del límite
     */
    public List<Producto> generarReporteProductosBajoStock(int limiteStock) {
        try {
            List<Producto> productos = productoController.listarProductos();
            List<Producto> productosBajoStock = new ArrayList<>();
            
            for (Producto producto : productos) {
                if (producto.getStock() <= limiteStock) {
                    productosBajoStock.add(producto);
                }
            }
            
            return productosBajoStock;
        } catch (Exception e) {
            logger.error("Error al generar reporte de productos con bajo stock: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Genera datos para un reporte de devoluciones
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Mapa con los datos del reporte
     */
    public Map<String, Object> generarReporteDevoluciones(Date fechaInicio, Date fechaFin) {
        try {
            // Convertir Date a LocalDate
            LocalDate inicio = fechaInicio.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            LocalDate fin = fechaFin.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
                
            // Simulación de datos de devoluciones
            List<Map<String, Object>> devoluciones = new ArrayList<>();
            
            // Simular algunas devoluciones
            for (int i = 1; i <= 5; i++) {
                Map<String, Object> devolucion = new HashMap<>();
                devolucion.put("id", "DEV-" + String.format("%03d", i));
                devolucion.put("fecha", inicio.plusDays(i));
                devolucion.put("cliente", "Cliente " + i);
                devolucion.put("producto", "Producto " + i);
                devolucion.put("cantidad", i);
                devolucion.put("motivo", "Defecto de fabricación");
                devolucion.put("estado", "Procesada");
                
                devoluciones.add(devolucion);
            }
            
            // Crear mapa con datos del reporte
            Map<String, Object> datos = new HashMap<>();
            datos.put("fechaInicio", inicio);
            datos.put("fechaFin", fin);
            datos.put("devoluciones", devoluciones);
            datos.put("totalDevoluciones", devoluciones.size());
            datos.put("valorTotal", 1500.0); // Valor simulado
            
            return datos;
        } catch (Exception e) {
            logger.error("Error al generar reporte de devoluciones: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Exporta un reporte existente al formato especificado
     * @param tipoReporte Tipo de reporte
     * @param datosReporte Datos del reporte
     * @param rutaArchivo Ruta donde guardar el reporte
     * @param formato Formato de exportación
     * @return true si la exportación fue exitosa
     */
    public boolean exportarReporte(String tipoReporte, Object datosReporte, String rutaArchivo, String formato) {
        try {
            // Generar nombre de archivo si no se especificó
            if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                String fechaStr = LocalDate.now().format(formatter);
                rutaArchivo = System.getProperty("user.home") + 
                             File.separator + "Reportes" + 
                             File.separator + tipoReporte + "_" + fechaStr;
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
            Map<String, Object> datosExportar = new HashMap<>();
            datosExportar.put("tipoReporte", tipoReporte);
            datosExportar.put("datos", datosReporte);
            datosExportar.put("fechaExportacion", LocalDate.now());
            
            switch (formato.toUpperCase()) {
                case "PDF":
                    return exportarPDF(datosExportar, rutaArchivo);
                case "EXCEL":
                    return exportarExcel(datosExportar, rutaArchivo);
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
            String mensaje = "Reporte: " + datos.get("tipoReporte") + 
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
            String mensaje = "Reporte: " + datos.get("tipoReporte") + 
                           "\nFecha: " + datos.get("fechaExportacion");
            fos.write(mensaje.getBytes());
            return true;
        } catch (Exception e) {
            logger.error("Error al exportar a Excel: {}", e.getMessage(), e);
            return false;
        }
    }
}