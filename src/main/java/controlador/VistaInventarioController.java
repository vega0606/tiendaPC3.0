package controlador;

import modelo.Producto;
import ventana.VistaInventario;
import modelo.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador para la vista de inventario
 */
public class VistaInventarioController {
    private static final Logger logger = LoggerFactory.getLogger(VistaInventarioController.class);
    
    private VistaInventario vista;
    private ProductoController productoController;
    private DefaultTableModel modeloTabla;
    
    public VistaInventarioController(VistaInventario vista) {
        this.vista = vista;
        this.productoController = new ProductoController();
    }
    
    /**
     * Inicializa la vista
     */
    public void inicializar() {
        // Configurar la tabla de productos
        JTable productTable = vista.getProductTable();
        modeloTabla = (DefaultTableModel) productTable.getModel();
        
        // Configurar eventos
        JTextField searchField = vista.getSearchField();
        JButton searchButton = vista.getSearchButton();
        JButton newProductButton = vista.getNewProductButton();
        
        // Evento para búsqueda
        searchButton.addActionListener(e -> buscarProductos(searchField.getText()));
        
        // Evento para Enter en campo de búsqueda
        searchField.addActionListener(e -> buscarProductos(searchField.getText()));
        
        // Evento para nuevo producto
        newProductButton.addActionListener(e -> nuevoProducto());
        
        // Configurar eventos para botones en la tabla
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int columna = productTable.getColumnModel().getColumnIndexAtX(evt.getX());
                int fila = evt.getY() / productTable.getRowHeight();
                
                if (fila < productTable.getRowCount() && fila >= 0 && 
                    columna < productTable.getColumnCount() && columna >= 0) {
                    // Si se hace clic en la columna "Acciones" (última columna)
                    if (columna == productTable.getColumnCount() - 1) {
                        mostrarOpcionesProducto(fila);
                    }
                }
            }
        });
        
        // Cargar datos iniciales
        cargarProductos();
    }
    
    /**
     * Carga la lista de productos en la tabla
     */
    public void cargarProductos() {
        try {
            // Limpiar tabla
            while (modeloTabla.getRowCount() > 0) {
                modeloTabla.removeRow(0);
            }
            
            // Obtener productos
            List<Producto> productos = productoController.listarProductos();
            
            // Llenar tabla
            for (Producto producto : productos) {
                Object[] rowData = {
                    producto.getCodigo(),
                    producto.getNombre(),
                    producto.getCategoria(),
                    producto.getStock(),
                    producto.getPrecioCompra().toString(),
                    producto.getPrecioVenta().toString(),
                    producto.getEstado(),
                    "Acciones" // Este campo se maneja con un renderer especial
                };
                
                modeloTabla.addRow(rowData);
            }
            
            actualizarEstadisticas();
        } catch (Exception e) {
            logger.error("Error al cargar productos: {}", e.getMessage(), e);
            mostrarError("Error al cargar productos", e);
        }
    }
    
    /**
     * Busca productos por nombre o código
     * @param termino Término de búsqueda
     */
    private void buscarProductos(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            cargarProductos();
            return;
        }
        
        try {
            // Limpiar tabla
            while (modeloTabla.getRowCount() > 0) {
                modeloTabla.removeRow(0);
            }
            
            // Buscar producto por código
            Producto productoPorCodigo = productoController.obtenerProducto(termino);
            if (productoPorCodigo != null) {
                Object[] rowData = {
                    productoPorCodigo.getCodigo(),
                    productoPorCodigo.getNombre(),
                    productoPorCodigo.getCategoria(),
                    productoPorCodigo.getStock(),
                    productoPorCodigo.getPrecioCompra().toString(),
                    productoPorCodigo.getPrecioVenta().toString(),
                    productoPorCodigo.getEstado(),
                    "Acciones"
                };
                
                modeloTabla.addRow(rowData);
                return;
            }
            
            // Si no se encontró por código, buscar por categoría
            List<Producto> productosPorCategoria = productoController.obtenerProductosPorCategoria(termino);
            
            // Si no hay productos, mostrar mensaje
            if (productosPorCategoria.isEmpty()) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "No se encontraron productos con ese término de búsqueda",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
                return;
            }
            
            // Llenar tabla con productos encontrados
            for (Producto producto : productosPorCategoria) {
                Object[] rowData = {
                    producto.getCodigo(),
                    producto.getNombre(),
                    producto.getCategoria(),
                    producto.getStock(),
                    producto.getPrecioCompra().toString(),
                    producto.getPrecioVenta().toString(),
                    producto.getEstado(),
                    "Acciones"
                };
                
                modeloTabla.addRow(rowData);
            }
        } catch (Exception e) {
            logger.error("Error al buscar productos: {}", e.getMessage(), e);
            mostrarError("Error al buscar productos", e);
        }
    }
    
    /**
     * Abre el diálogo para crear un nuevo producto
     */
    private void nuevoProducto() {
        // En un sistema real, se abriría un diálogo con un formulario
        // Aquí usamos diálogos simples para cada campo
        
        try {
            String codigo = productoController.generarCodigoProducto();
            
            // Nombre
            String nombre = JOptionPane.showInputDialog(vista.getPanel(),
                "Nombre del producto:",
                "Nuevo Producto",
                JOptionPane.QUESTION_MESSAGE);
            
            if (nombre == null || nombre.trim().isEmpty()) {
                return;
            }
            
            // Descripción
            String descripcion = JOptionPane.showInputDialog(vista.getPanel(),
                "Descripción:",
                "Nuevo Producto",
                JOptionPane.QUESTION_MESSAGE);
            
            // Categoría
            String categoria = JOptionPane.showInputDialog(vista.getPanel(),
                "Categoría:",
                "Nuevo Producto",
                JOptionPane.QUESTION_MESSAGE);
            
            if (categoria == null || categoria.trim().isEmpty()) {
                categoria = "General";
            }
            
            // Stock
            String stockStr = JOptionPane.showInputDialog(vista.getPanel(),
                "Stock inicial:",
                "Nuevo Producto",
                JOptionPane.QUESTION_MESSAGE);
            
            int stock;
            try {
                stock = Integer.parseInt(stockStr);
            } catch (NumberFormatException e) {
                stock = 0;
            }
            
            // Stock mínimo
            String stockMinimoStr = JOptionPane.showInputDialog(vista.getPanel(),
                "Stock mínimo:",
                "Nuevo Producto",
                JOptionPane.QUESTION_MESSAGE);
            
            int stockMinimo;
            try {
                stockMinimo = Integer.parseInt(stockMinimoStr);
            } catch (NumberFormatException e) {
                stockMinimo = 5;
            }
            
            // Precio de compra
            String precioCompraStr = JOptionPane.showInputDialog(vista.getPanel(),
                "Precio de compra:",
                "Nuevo Producto",
                JOptionPane.QUESTION_MESSAGE);
            
            BigDecimal precioCompra;
            try {
                precioCompra = new BigDecimal(precioCompraStr);
            } catch (NumberFormatException e) {
                precioCompra = BigDecimal.ZERO;
            }
            
            // Precio de venta
            String precioVentaStr = JOptionPane.showInputDialog(vista.getPanel(),
                "Precio de venta:",
                "Nuevo Producto",
                JOptionPane.QUESTION_MESSAGE);
            
            BigDecimal precioVenta;
            try {
                precioVenta = new BigDecimal(precioVentaStr);
            } catch (NumberFormatException e) {
                precioVenta = BigDecimal.ZERO;
            }
            
            // Unidad
            String unidad = JOptionPane.showInputDialog(vista.getPanel(),
                "Unidad de medida:",
                "Nuevo Producto",
                JOptionPane.QUESTION_MESSAGE);
            
            if (unidad == null || unidad.trim().isEmpty()) {
                unidad = "Unidad";
            }
            
            // Crear producto
            Producto producto = productoController.crearProducto(codigo, nombre, descripcion, categoria,
                                                               stock, stockMinimo, precioCompra, precioVenta,
                                                               unidad);
            
            if (producto != null) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Producto creado exitosamente",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Error al crear el producto",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("Error al crear producto: {}", e.getMessage(), e);
            mostrarError("Error al crear producto", e);
        }
    }
    
    /**
     * Muestra opciones para un producto seleccionado
     * @param fila Fila seleccionada en la tabla
     */
    private void mostrarOpcionesProducto(int fila) {
        String codigo = (String) modeloTabla.getValueAt(fila, 0);
        String nombre = (String) modeloTabla.getValueAt(fila, 1);
        
        String[] opciones = {"Editar", "Eliminar", "Ajustar Stock", "Cancelar"};
        
        int seleccion = JOptionPane.showOptionDialog(vista.getPanel(),
            "Seleccione una acción para el producto: " + nombre,
            "Opciones de Producto",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]);
        
        switch (seleccion) {
            case 0: // Editar
                editarProducto(codigo);
                break;
            case 1: // Eliminar
                eliminarProducto(codigo, nombre);
                break;
            case 2: // Ajustar Stock
                ajustarStock(codigo, nombre);
                break;
            default:
                // No hacer nada
                break;
        }
    }
    
    /**
     * Abre el diálogo para editar un producto
     * @param codigo Código del producto a editar
     */
    private void editarProducto(String codigo) {
        try {
            Producto producto = productoController.obtenerProducto(codigo);
            
            if (producto == null) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Producto no encontrado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Nombre
            String nombre = JOptionPane.showInputDialog(vista.getPanel(),
                "Nombre del producto:",
                producto.getNombre());
            
            if (nombre != null && !nombre.trim().isEmpty()) {
                producto.setNombre(nombre);
            }
            
            // Descripción
            String descripcion = JOptionPane.showInputDialog(vista.getPanel(),
                "Descripción:",
                producto.getDescripcion());
            
            if (descripcion != null) {
                producto.setDescripcion(descripcion);
            }
            
            // Categoría
            String categoria = JOptionPane.showInputDialog(vista.getPanel(),
                "Categoría:",
                producto.getCategoria());
            
            if (categoria != null && !categoria.trim().isEmpty()) {
                producto.setCategoria(categoria);
            }
            
            // Stock mínimo
            String stockMinimoStr = JOptionPane.showInputDialog(vista.getPanel(),
                "Stock mínimo:",
                String.valueOf(producto.getStockMinimo()));
            
            try {
                int stockMinimo = Integer.parseInt(stockMinimoStr);
                producto.setStockMinimo(stockMinimo);
            } catch (NumberFormatException e) {
                // Mantener valor original
            }
            
            // Precio de compra
            String precioCompraStr = JOptionPane.showInputDialog(vista.getPanel(),
                "Precio de compra:",
                producto.getPrecioCompra().toString());
            
            try {
                BigDecimal precioCompra = new BigDecimal(precioCompraStr);
                producto.setPrecioCompra(precioCompra);
            } catch (NumberFormatException e) {
                // Mantener valor original
            }
            
            // Precio de venta
            String precioVentaStr = JOptionPane.showInputDialog(vista.getPanel(),
                "Precio de venta:",
                producto.getPrecioVenta().toString());
            
            try {
                BigDecimal precioVenta = new BigDecimal(precioVentaStr);
                producto.setPrecioVenta(precioVenta);
            } catch (NumberFormatException e) {
                // Mantener valor original
            }
            
            // Unidad
            String unidad = JOptionPane.showInputDialog(vista.getPanel(),
                "Unidad de medida:",
                producto.getUnidad());
            
            if (unidad != null && !unidad.trim().isEmpty()) {
                producto.setUnidad(unidad);
            }
            
            // Estado
            String[] estados = {"Activo", "Inactivo"};
            String estado = (String) JOptionPane.showInputDialog(vista.getPanel(),
                "Estado:",
                "Editar Producto",
                JOptionPane.QUESTION_MESSAGE,
                null,
                estados,
                producto.getEstado());
            
            if (estado != null) {
                producto.setEstado(estado);
            }
            
            // Actualizar producto
            boolean actualizado = productoController.actualizarProducto(producto);
            
            if (actualizado) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Producto actualizado exitosamente",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Error al actualizar el producto",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("Error al editar producto: {}", e.getMessage(), e);
            mostrarError("Error al editar producto", e);
        }
    }
    
    /**
     * Elimina un producto
     * @param codigo Código del producto a eliminar
     * @param nombre Nombre del producto (para confirmación)
     */
    private void eliminarProducto(String codigo, String nombre) {
        try {
            int confirmacion = JOptionPane.showConfirmDialog(vista.getPanel(),
                "¿Está seguro de eliminar el producto: " + nombre + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean eliminado = productoController.eliminarProducto(codigo);
                
                if (eliminado) {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Producto eliminado exitosamente",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarProductos();
                } else {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Error al eliminar el producto",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            logger.error("Error al eliminar producto: {}", e.getMessage(), e);
            mostrarError("Error al eliminar producto", e);
        }
    }
    
    /**
     * Ajusta el stock de un producto
     * @param codigo Código del producto
     * @param nombre Nombre del producto
     */
    private void ajustarStock(String codigo, String nombre) {
        try {
            Producto producto = productoController.obtenerProducto(codigo);
            
            if (producto == null) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Producto no encontrado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String[] tiposAjuste = {"Entrada", "Salida", "Ajuste manual"};
            
            String tipoAjuste = (String) JOptionPane.showInputDialog(vista.getPanel(),
                "Tipo de ajuste:",
                "Ajustar Stock",
                JOptionPane.QUESTION_MESSAGE,
                null,
                tiposAjuste,
                tiposAjuste[0]);
            
            if (tipoAjuste == null) {
                return;
            }
            
            String cantidadStr = JOptionPane.showInputDialog(vista.getPanel(),
                "Cantidad a " + (tipoAjuste.equals("Entrada") ? "ingresar" : 
                              tipoAjuste.equals("Salida") ? "retirar" : "ajustar") + ":",
                "Ajustar Stock",
                JOptionPane.QUESTION_MESSAGE);
            
            if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
                return;
            }
            
            int cantidad;
            try {
                cantidad = Integer.parseInt(cantidadStr);
                
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "La cantidad debe ser un número positivo",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Cantidad inválida",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean exito = false;
            
            if ("Entrada".equals(tipoAjuste)) {
                exito = productoController.actualizarStock(codigo, cantidad);
            } else if ("Salida".equals(tipoAjuste)) {
                // Verificar stock suficiente
                if (producto.getStock() < cantidad) {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Stock insuficiente. Disponible: " + producto.getStock(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                exito = productoController.actualizarStock(codigo, -cantidad);
            } else { // Ajuste manual
                int nuevoStock = cantidad;
                int diferencia = nuevoStock - producto.getStock();
                
                exito = productoController.actualizarStock(codigo, diferencia);
            }
            
            if (exito) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Stock actualizado exitosamente",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Error al actualizar el stock",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("Error al ajustar stock: {}", e.getMessage(), e);
            mostrarError("Error al ajustar stock", e);
        }
    }
    
    /**
     * Actualiza las estadísticas del inventario
     */
    private void actualizarEstadisticas() {
        try {
            List<Producto> productos = productoController.listarProductos();
            List<Producto> productosBajoStock = productoController.obtenerProductosBajoStock();
            
            int totalProductos = productos.size();
            int productosBajo = productosBajoStock.size();
            
            // Calcular valor total del inventario
            BigDecimal valorTotal = BigDecimal.ZERO;
            
            for (Producto producto : productos) {
                BigDecimal valorProducto = producto.getPrecioCompra().multiply(BigDecimal.valueOf(producto.getStock()));
                valorTotal = valorTotal.add(valorProducto);
            }
            
            // Actualizar etiquetas en la vista
            vista.actualizarEstadisticas(totalProductos, productosBajo, valorTotal);
        } catch (Exception e) {
            logger.error("Error al actualizar estadísticas: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Muestra un diálogo de error
     * @param mensaje Mensaje de error
     * @param e Excepción (opcional)
     */
    private void mostrarError(String mensaje, Exception e) {
        String detalles = e != null ? ": " + e.getMessage() : "";
        JOptionPane.showMessageDialog(vista.getPanel(),
            mensaje + detalles,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
