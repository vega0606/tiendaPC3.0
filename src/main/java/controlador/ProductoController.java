package controlador;

import modelo.Producto;
import DAO.ProductoDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la gestión de productos
 */
public class ProductoController {
    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);
    private ProductoDAO productoDAO;
    
    public ProductoController() {
        productoDAO = new ProductoDAO();
    }
    
    /**
     * Crea un nuevo producto
     * @param codigo Código del producto
     * @param nombre Nombre del producto
     * @param descripcion Descripción del producto
     * @param categoria Categoría del producto
     * @param stock Stock inicial
     * @param stockMinimo Stock mínimo para alertas
     * @param precioCompra Precio de compra
     * @param precioVenta Precio de venta
     * @param unidad Unidad de medida
     * @return El producto creado o null si ocurre un error
     */
    public Producto crearProducto(String codigo, String nombre, String descripcion, String categoria,
                                 int stock, int stockMinimo, BigDecimal precioCompra, BigDecimal precioVenta,
                                 String unidad) {
        try {
            Producto producto = new Producto();
            producto.setCodigo(codigo);
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setCategoria(categoria);
            producto.setStock(stock);
            producto.setStockMinimo(stockMinimo);
            producto.setPrecioCompra(precioCompra);
            producto.setPrecioVenta(precioVenta);
            producto.setEstado("Activo");
            producto.setUnidad(unidad);
            
            return productoDAO.crear(producto);
        } catch (Exception e) {
            logger.error("Error al crear producto: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene un producto por su código
     * @param codigo Código del producto
     * @return El producto encontrado o null si no existe
     */
    public Producto obtenerProducto(String codigo) {
        try {
            return productoDAO.buscarPorId(codigo);
        } catch (Exception e) {
            logger.error("Error al obtener producto: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene todos los productos
     * @return Lista de productos
     */
    public List<Producto> listarProductos() {
        try {
            return productoDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Error al listar productos: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Actualiza un producto existente
     * @param producto Producto con los datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarProducto(Producto producto) {
        try {
            productoDAO.actualizar(producto);
            return true;
        } catch (Exception e) {
            logger.error("Error al actualizar producto: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Elimina un producto
     * @param codigo Código del producto
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminarProducto(String codigo) {
        try {
            return productoDAO.eliminar(codigo);
        } catch (Exception e) {
            logger.error("Error al eliminar producto: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Obtiene productos por categoría
     * @param categoria Categoría de los productos
     * @return Lista de productos de la categoría
     */
    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        try {
            return productoDAO.buscarPorCategoria(categoria);
        } catch (Exception e) {
            logger.error("Error al obtener productos por categoría: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene productos con stock bajo
     * @return Lista de productos con stock bajo
     */
    public List<Producto> obtenerProductosBajoStock() {
        try {
            return productoDAO.buscarProductosBajoStock();
        } catch (Exception e) {
            logger.error("Error al obtener productos con bajo stock: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Actualiza el stock de un producto
     * @param codigo Código del producto
     * @param cantidad Cantidad a añadir (positiva) o restar (negativa)
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarStock(String codigo, int cantidad) {
        try {
            return productoDAO.actualizarStock(codigo, cantidad);
        } catch (Exception e) {
            logger.error("Error al actualizar stock del producto: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Genera un código único para un nuevo producto
     * @return Código generado
     */
    public String generarCodigoProducto() {
        try {
            List<Producto> productos = productoDAO.listarTodos();
            int maxCodigo = 0;
            
            for (Producto producto : productos) {
                try {
                    // Extraer el número del código asumiendo formato "NNN"
                    String codigoNumerico = producto.getCodigo().replaceAll("[^0-9]", "");
                    if (!codigoNumerico.isEmpty()) {
                        int codigo = Integer.parseInt(codigoNumerico);
                        if (codigo > maxCodigo) {
                            maxCodigo = codigo;
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignorar códigos no numéricos
                }
            }
            
            return String.format("%03d", maxCodigo + 1);
        } catch (Exception e) {
            logger.error("Error al generar código de producto: {}", e.getMessage(), e);
            return "001"; // Código por defecto
        }
    }
    
    /**
     * Verifica si existe un producto con el código dado
     * @param codigo Código a verificar
     * @return true si el producto existe
     */
    public boolean existeProducto(String codigo) {
        try {
            return productoDAO.buscarPorId(codigo) != null;
        } catch (Exception e) {
            logger.error("Error al verificar existencia del producto: {}", e.getMessage(), e);
            return false;
        }
    }
}