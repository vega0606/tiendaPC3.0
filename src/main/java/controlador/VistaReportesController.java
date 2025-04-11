package controlador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import ventana.VistaReportes;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Controlador para la vista de reportes
 */
public class VistaReportesController {
    
    private VistaReportes vista;
    
    /**
     * Constructor del controlador
     * @param vista La vista de reportes asociada
     */
    public VistaReportesController(VistaReportes vista) {
        this.vista = vista;
        configurarEventos();
    }
    
    /**
     * Configura los eventos principales de la vista
     */
    private void configurarEventos() {
        // Configurar el evento para el botón Generar Reporte
        vista.getBtnGenerarReporte().addActionListener(e -> {
            String tipoReporte = (String) vista.getComboTipoReporte().getSelectedItem();
            
            // Validar fechas cuando corresponda
            if (requiereFechas(tipoReporte)) {
                if (!validarFechas()) {
                    return;
                }
            }
            
            // Generar el reporte según el tipo seleccionado
            switch (tipoReporte) {
                case "Ventas por Período":
                    generarReporteVentasPorPeriodo();
                    break;
                case "Productos más Vendidos":
                    generarReporteProductosMasVendidos();
                    break;
                case "Clientes Frecuentes":
                    generarReporteClientesFrecuentes();
                    break;
                case "Inventario Valorizado":
                    generarReporteInventarioValorizado();
                    break;
                case "Ganancias por Período":
                    generarReporteGananciasPorPeriodo();
                    break;
                case "Productos con Bajo Stock":
                    generarReporteProductosBajoStock();
                    break;
                case "Reporte de Devoluciones":
                    generarReporteDevoluciones();
                    break;
                default:
                    vista.mostrarMensaje("Tipo de reporte no reconocido");
                    break;
            }
        });
        
        // Configurar eventos para los botones de exportación
        vista.getBtnExportarPDF().addActionListener(e -> {
            vista.mostrarMensaje("La exportación a PDF está en desarrollo.");
        });
        
        vista.getBtnExportarExcel().addActionListener(e -> {
            vista.mostrarMensaje("La exportación a Excel está en desarrollo.");
        });
    }
    
    /**
     * Verifica si el tipo de reporte requiere fechas
     * @param tipoReporte El tipo de reporte a verificar
     * @return true si requiere fechas, false en caso contrario
     */
    private boolean requiereFechas(String tipoReporte) {
        return tipoReporte.equals("Ventas por Período") ||
               tipoReporte.equals("Productos más Vendidos") ||
               tipoReporte.equals("Clientes Frecuentes") ||
               tipoReporte.equals("Ganancias por Período") ||
               tipoReporte.equals("Reporte de Devoluciones");
    }
    
    /**
     * Valida que las fechas ingresadas sean válidas
     * @return true si las fechas son válidas, false en caso contrario
     */
    private boolean validarFechas() {
        String fechaInicioStr = vista.getFechaInicio().getText().trim();
        String fechaFinStr = vista.getFechaFin().getText().trim();
        
        if (fechaInicioStr.isEmpty() || fechaFinStr.isEmpty()) {
            vista.mostrarMensaje("Debe ingresar ambas fechas para generar el reporte.");
            return false;
        }
        
        try {
            // Intentar parsear las fechas
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            
            Date fechaInicio = sdf.parse(fechaInicioStr);
            Date fechaFin = sdf.parse(fechaFinStr);
            
            // Verificar que la fecha de inicio no sea posterior a la fecha de fin
            if (fechaInicio.after(fechaFin)) {
                vista.mostrarMensaje("La fecha de inicio no puede ser posterior a la fecha de fin.");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            vista.mostrarMensaje("Formato de fecha inválido. Utilice el formato yyyy-MM-dd (ej. 2025-04-11)");
            return false;
        }
    }
    
    /**
     * Genera un reporte de ventas por período
     */
    public void generarReporteVentasPorPeriodo() {
        // Aquí iría la lógica para obtener los datos reales
        // Por ahora, generamos datos de ejemplo
        
        Map<String, Object> datosReporte = new HashMap<>();
        List<Map<String, Object>> ventas = new ArrayList<>();
        
        // Datos de ejemplo
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> venta = new HashMap<>();
            venta.put("fecha", "2025-04-" + (i < 10 ? "0" + i : i));
            venta.put("numeroFactura", "F-2025-" + (1000 + i));
            venta.put("cliente", "Cliente " + i);
            venta.put("total", 500.0 + (i * 100));
            ventas.add(venta);
        }
        
        datosReporte.put("ventas", ventas);
        
        // Calcular total
        double totalVentas = 0.0;
        for (Map<String, Object> venta : ventas) {
            totalVentas += (Double) venta.get("total");
        }
        
        datosReporte.put("totalVentas", totalVentas);
        
        // Mostrar en la vista
        mostrarReporteVentasPorPeriodo(datosReporte);
    }
    
    /**
     * Muestra los datos del reporte de ventas por período en la vista
     * @param datosReporte Los datos del reporte
     */
    private void mostrarReporteVentasPorPeriodo(Map<String, Object> datosReporte) {
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
                String.format("$%.2f", venta.get("total"))
            });
        }
        
        Double totalVentas = (Double) datosReporte.get("totalVentas");
        
        // Actualizar la vista
        actualizarTablaResultados(model);
        actualizarResumen(String.format("Total Ventas: $%.2f", totalVentas));
        
        // Habilitar botones de exportación
        vista.getBtnExportarPDF().setEnabled(true);
        vista.getBtnExportarExcel().setEnabled(true);
    }
    
    /**
     * Genera un reporte de productos más vendidos
     */
    public void generarReporteProductosMasVendidos() {
        int cantidadTop = vista.getCantidadTopProductos();
        
        // Aquí iría la lógica para obtener los datos reales
        // Por ahora, generamos datos de ejemplo
        
        List<Map<String, Object>> datosReporte = new ArrayList<>();
        
        // Datos de ejemplo
        for (int i = 1; i <= cantidadTop; i++) {
            Map<String, Object> producto = new HashMap<>();
            producto.put("codigo", "P" + (100 + i));
            producto.put("nombre", "Producto " + i);
            producto.put("cantidad", 50 - i);
            producto.put("totalVendido", (50 - i) * 100.0);
            datosReporte.add(producto);
        }
        
        // Mostrar en la vista
        mostrarReporteProductosMasVendidos(datosReporte);
    }
    
    /**
     * Muestra los datos del reporte de productos más vendidos en la vista
     * @param datosReporte Los datos del reporte
     */
    private void mostrarReporteProductosMasVendidos(List<Map<String, Object>> datosReporte) {
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
                String.format("$%.2f", producto.get("totalVendido"))
            });
            
            totalGeneral += (Double) producto.get("totalVendido");
        }
        
        // Actualizar la vista
        actualizarTablaResultados(model);
        actualizarResumen(String.format("Total Vendido: $%.2f", totalGeneral));
        
        // Habilitar botones de exportación
        vista.getBtnExportarPDF().setEnabled(true);
        vista.getBtnExportarExcel().setEnabled(true);
    }
    
    /**
     * Genera un reporte de clientes frecuentes
     */
    public void generarReporteClientesFrecuentes() {
        int cantidadTop = vista.getCantidadTopClientes();
        
        // Aquí iría la lógica para obtener los datos reales
        // Por ahora, generamos datos de ejemplo
        
        List<Map<String, Object>> datosReporte = new ArrayList<>();
        
        // Datos de ejemplo
        for (int i = 1; i <= cantidadTop; i++) {
            Map<String, Object> cliente = new HashMap<>();
            cliente.put("id", i);
            cliente.put("nombre", "Cliente " + i);
            cliente.put("compras", 20 - i);
            cliente.put("totalGastado", (20 - i) * 200.0);
            datosReporte.add(cliente);
        }
        
        // Mostrar en la vista
        mostrarReporteClientesFrecuentes(datosReporte);
    }
    
    /**
     * Muestra los datos del reporte de clientes frecuentes en la vista
     * @param datosReporte Los datos del reporte
     */
    private void mostrarReporteClientesFrecuentes(List<Map<String, Object>> datosReporte) {
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
                String.format("$%.2f", cliente.get("totalGastado"))
            });
            
            totalGeneral += (Double) cliente.get("totalGastado");
        }
        
        // Actualizar la vista
        actualizarTablaResultados(model);
        actualizarResumen(String.format("Total Gastado: $%.2f", totalGeneral));
        
        // Habilitar botones de exportación
        vista.getBtnExportarPDF().setEnabled(true);
        vista.getBtnExportarExcel().setEnabled(true);
    }
    
    /**
     * Genera un reporte de inventario valorizado
     */
    public void generarReporteInventarioValorizado() {
        // Aquí iría la lógica para obtener los datos reales
        // Por ahora, generamos datos de ejemplo
        
        Map<String, Object> datosReporte = new HashMap<>();
        List<Map<String, Object>> productos = new ArrayList<>();
        
        double valorTotal = 0.0;
        
        // Datos de ejemplo
        for (int i = 1; i <= 15; i++) {
            Map<String, Object> producto = new HashMap<>();
            producto.put("codigo", "P" + (100 + i));
            producto.put("nombre", "Producto " + i);
            producto.put("stock", 10 * i);
            producto.put("costoUnitario", 50.0 + i);
            double valorProducto = (10 * i) * (50.0 + i);
            producto.put("valorTotal", valorProducto);
            productos.add(producto);
            
            valorTotal += valorProducto;
        }
        
        datosReporte.put("productos", productos);
        
        // Mostrar en la vista
        mostrarReporteInventarioValorizado(datosReporte, valorTotal);
    }
    
    /**
     * Muestra los datos del reporte de inventario valorizado en la vista
     * @param datosReporte Los datos del reporte
     * @param valorTotal El valor total del inventario
     */
    private void mostrarReporteInventarioValorizado(Map<String, Object> datosReporte, double valorTotal) {
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
                String.format("$%.2f", producto.get("costoUnitario")),
                String.format("$%.2f", producto.get("valorTotal"))
            });
        }
        
        // Actualizar la vista
        actualizarTablaResultados(model);
        actualizarResumen(String.format("Valor Total Inventario: $%.2f", valorTotal));
        
        // Habilitar botones de exportación
        vista.getBtnExportarPDF().setEnabled(true);
        vista.getBtnExportarExcel().setEnabled(true);
    }
    
    /**
     * Genera un reporte de ganancias por período
     */
    public void generarReporteGananciasPorPeriodo() {
        // Aquí iría la lógica para obtener los datos reales
        // Por ahora, generamos datos de ejemplo
        
        Map<String, Object> datosReporte = new HashMap<>();
        List<Map<String, Object>> periodos = new ArrayList<>();
        
        // Datos de ejemplo (meses)
        String[] nombresMeses = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        
        double gananciaTotal = 0.0;
        
        // Datos de ejemplo
        for (int i = 0; i < nombresMeses.length; i++) {
            Map<String, Object> periodo = new HashMap<>();
            double ventas = 5000.0 + (i * 500);
            double costos = 3000.0 + (i * 300);
            double ganancia = ventas - costos;
            double margen = (ganancia / ventas) * 100;
            
            periodo.put("nombre", nombresMeses[i]);
            periodo.put("ventas", ventas);
            periodo.put("costos", costos);
            periodo.put("ganancia", ganancia);
            periodo.put("margen", Math.round(margen * 10) / 10.0); // Redondear a 1 decimal
            
            periodos.add(periodo);
            gananciaTotal += ganancia;
        }
        
        datosReporte.put("periodos", periodos);
        datosReporte.put("gananciaTotal", gananciaTotal);
        
        // Mostrar en la vista
        mostrarReporteGananciasPorPeriodo(datosReporte);
    }
    
    /**
     * Muestra los datos del reporte de ganancias por período en la vista
     * @param datosReporte Los datos del reporte
     */
    private void mostrarReporteGananciasPorPeriodo(Map<String, Object> datosReporte) {
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
                String.format("$%.2f", periodo.get("ventas")),
                String.format("$%.2f", periodo.get("costos")),
                String.format("$%.2f", periodo.get("ganancia")),
                periodo.get("margen") + "%"
            });
        }
        
        Double gananciaTotal = (Double) datosReporte.get("gananciaTotal");
        
        // Actualizar la vista
        actualizarTablaResultados(model);
        actualizarResumen(String.format("Ganancia Total: $%.2f", gananciaTotal));
        
        // Habilitar botones de exportación
        vista.getBtnExportarPDF().setEnabled(true);
        vista.getBtnExportarExcel().setEnabled(true);
    }
    
    /**
     * Genera un reporte de productos con bajo stock
     */
    public void generarReporteProductosBajoStock() {
        int limiteStock = vista.getLimiteStock();
        
        // Aquí iría la lógica para obtener los datos reales
        // Por ahora, generamos datos de ejemplo
        
        List<ProductoSimple> productosBajoStock = new ArrayList<>();
        
        // Datos de ejemplo
        for (int i = 1; i <= 8; i++) {
            int stockActual = i * limiteStock / 2;
            int stockMinimo = i * limiteStock / 3 + limiteStock;
            
            if (stockActual < stockMinimo) {
                ProductoSimple producto = new ProductoSimple(
                    "P" + (100 + i),
                    "Producto con bajo stock " + i,
                    stockActual,
                    stockMinimo
                );
                productosBajoStock.add(producto);
            }
        }
        
        // Mostrar en la vista
        mostrarReporteProductosBajoStock(productosBajoStock, limiteStock);
    }
    
    /**
     * Muestra los datos del reporte de productos con bajo stock en la vista
     * @param productosBajoStock Lista de productos con bajo stock
     * @param limiteStock Límite de stock configurado
     */
    private void mostrarReporteProductosBajoStock(List<ProductoSimple> productosBajoStock, int limiteStock) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Código");
        model.addColumn("Producto");
        model.addColumn("Stock Actual");
        model.addColumn("Stock Mínimo");
        model.addColumn("Diferencia");
        
        for (ProductoSimple producto : productosBajoStock) {
            int diferencia = producto.getStock() - producto.getStockMinimo();
            model.addRow(new Object[] {
                producto.getCodigo(),
                producto.getNombre(),
                producto.getStock(),
                producto.getStockMinimo(),
                diferencia
            });
        }
        
        // Actualizar la vista
        actualizarTablaResultados(model);
        actualizarResumen(String.format("Productos con stock menor a %d: %d", 
                limiteStock, productosBajoStock.size()));
        
        // Habilitar botones de exportación
        vista.getBtnExportarPDF().setEnabled(true);
        vista.getBtnExportarExcel().setEnabled(true);
    }
    
    /**
     * Genera un reporte de devoluciones
     */
    public void generarReporteDevoluciones() {
        // Aquí iría la lógica para obtener los datos reales
        // Por ahora, generamos datos de ejemplo
        
        Map<String, Object> datosReporte = new HashMap<>();
        List<Map<String, Object>> devoluciones = new ArrayList<>();
        
        // Datos de ejemplo
        for (int i = 1; i <= 12; i++) {
            Map<String, Object> devolucion = new HashMap<>();
            devolucion.put("id", i);
            devolucion.put("fecha", "2025-04-" + (i < 10 ? "0" + i : i));
            devolucion.put("cliente", "Cliente " + i);
            devolucion.put("producto", "Producto " + (i * 2));
            devolucion.put("cantidad", i);
            
            String motivo;
            if (i % 3 == 0) {
                motivo = "Producto defectuoso";
            } else if (i % 3 == 1) {
                motivo = "Producto incorrecto";
            } else {
                motivo = "Insatisfacción con el producto";
            }
            devolucion.put("motivo", motivo);
            
            String estado;
            if (i % 3 == 0) {
                estado = "Pendiente";
            } else if (i % 3 == 1) {
                estado = "Aprobada";
            } else {
                estado = "Rechazada";
            }
            devolucion.put("estado", estado);
            
            devoluciones.add(devolucion);
        }
        
        datosReporte.put("devoluciones", devoluciones);
        datosReporte.put("totalDevoluciones", devoluciones.size());
        datosReporte.put("valorTotal", 5460.75);
        
        // Mostrar en la vista
        mostrarReporteDevoluciones(datosReporte);
    }
    
    /**
     * Muestra los datos del reporte de devoluciones en la vista
     * @param datosReporte Los datos del reporte
     */
    private void mostrarReporteDevoluciones(Map<String, Object> datosReporte) {
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
        
        Integer totalDevoluciones = (Integer) datosReporte.get("totalDevoluciones");
        Double valorTotal = (Double) datosReporte.get("valorTotal");
        
        // Actualizar la vista
        actualizarTablaResultados(model);
        actualizarResumen(String.format("Total Devoluciones: %d | Valor: $%.2f", 
                totalDevoluciones, valorTotal));
        
        // Habilitar botones de exportación
        vista.getBtnExportarPDF().setEnabled(true);
        vista.getBtnExportarExcel().setEnabled(true);
    }
    
    /**
     * Actualiza la tabla de resultados con un nuevo modelo de datos
     * @param model El modelo de datos a mostrar
     */
    private void actualizarTablaResultados(DefaultTableModel model) {
        JTable tablaResultados = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tablaResultados);
        
        // Aquí asumimos que la vista tiene métodos para actualizar su contenido
        // Esta es una implementación simplificada, ajustar según la estructura real de VistaReportes
        JOptionPane.showMessageDialog(vista.getPanel(), 
            scrollPane, 
            "Resultados del Reporte", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Actualiza el resumen del reporte
     * @param resumen El texto de resumen a mostrar
     */
    private void actualizarResumen(String resumen) {
        // Esta es una implementación simplificada, ajustar según la estructura real de VistaReportes
        JOptionPane.showMessageDialog(vista.getPanel(), 
            resumen, 
            "Resumen del Reporte", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Clase interna para representar un producto simple en reportes
     */
    private class ProductoSimple {
        private String codigo;
        private String nombre;
        private int stock;
        private int stockMinimo;
        
        public ProductoSimple(String codigo, String nombre, int stock, int stockMinimo) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.stock = stock;
            this.stockMinimo = stockMinimo;
        }
        
        public String getCodigo() {
            return codigo;
        }
        
        public String getNombre() {
            return nombre;
        }
        
        public int getStock() {
            return stock;
        }
        
        public int getStockMinimo() {
            return stockMinimo;
        }
    }
}