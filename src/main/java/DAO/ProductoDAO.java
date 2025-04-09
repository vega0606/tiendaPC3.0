package DAO;

import modelo.DatabaseConnector;
import Modelo.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones relacionadas con productos
 */
public class ProductoDAO implements DAO<Producto, String> {
    private static final Logger logger = LoggerFactory.getLogger(ProductoDAO.class);
    
    @Override
    public Producto crear(Producto producto) throws Exception {
        String sql = "INSERT INTO productos (codigo, nombre, descripcion, id_categoria, stock, stock_minimo, " +
                    "precio_compra, precio_venta, estado, unidad) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setString(3, producto.getDescripcion());
            
            // Aquí deberías obtener el ID de la categoría desde el nombre
            String categoriaQuery = "SELECT id FROM categorias WHERE nombre = ?";
            int idCategoria = 0;
            
            try (PreparedStatement catStmt = conn.prepareStatement(categoriaQuery)) {
                catStmt.setString(1, producto.getCategoria());
                ResultSet rs = catStmt.executeQuery();
                if (rs.next()) {
                    idCategoria = rs.getInt("id");
                }
            }
            
            stmt.setInt(4, idCategoria);
            stmt.setInt(5, producto.getStock());
            stmt.setInt(6, producto.getStockMinimo());
            stmt.setBigDecimal(7, producto.getPrecioCompra());
            stmt.setBigDecimal(8, producto.getPrecioVenta());
            stmt.setString(9, producto.getEstado());
            stmt.setString(10, producto.getUnidad());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La creación del producto falló, no se insertaron filas");
            }
            
            logger.info("Producto creado con código: {}", producto.getCodigo());
            return producto;
        } catch (SQLException e) {
            logger.error("Error al crear producto: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Producto buscarPorId(String codigo) throws Exception {
        String sql = "SELECT p.*, c.nombre as categoria_nombre FROM productos p " +
                     "LEFT JOIN categorias c ON p.id_categoria = c.id " +
                     "WHERE p.codigo = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar producto por código: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Producto> listarTodos() throws Exception {
        String sql = "SELECT p.*, c.nombre as categoria_nombre FROM productos p " +
                     "LEFT JOIN categorias c ON p.id_categoria = c.id " +
                     "ORDER BY p.nombre";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            
            return productos;
        } catch (SQLException e) {
            logger.error("Error al listar productos: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Producto actualizar(Producto producto) throws Exception {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, id_categoria = ?, " +
                     "stock = ?, stock_minimo = ?, precio_compra = ?, precio_venta = ?, " +
                     "estado = ?, unidad = ? WHERE codigo = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            
            // Aquí deberías obtener el ID de la categoría desde el nombre
            String categoriaQuery = "SELECT id FROM categorias WHERE nombre = ?";
            int idCategoria = 0;
            
            try (PreparedStatement catStmt = conn.prepareStatement(categoriaQuery)) {
                catStmt.setString(1, producto.getCategoria());
                ResultSet rs = catStmt.executeQuery();
                if (rs.next()) {
                    idCategoria = rs.getInt("id");
                }
            }
            
            stmt.setInt(3, idCategoria);
            stmt.setInt(4, producto.getStock());
            stmt.setInt(5, producto.getStockMinimo());
            stmt.setBigDecimal(6, producto.getPrecioCompra());
            stmt.setBigDecimal(7, producto.getPrecioVenta());
            stmt.setString(8, producto.getEstado());
            stmt.setString(9, producto.getUnidad());
            stmt.setString(10, producto.getCodigo());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La actualización del producto falló, producto no encontrado");
            }
            
            logger.info("Producto actualizado con código: {}", producto.getCodigo());
            return producto;
        } catch (SQLException e) {
            logger.error("Error al actualizar producto: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean eliminar(String codigo) throws Exception {
        String sql = "DELETE FROM productos WHERE codigo = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Producto eliminado con código: {}", codigo);
            return true;
        } catch (SQLException e) {
            logger.error("Error al eliminar producto: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar productos por categoría
     * @param categoria Nombre de la categoría
     * @return Lista de productos de la categoría
     * @throws Exception
     */
    public List<Producto> buscarPorCategoria(String categoria) throws Exception {
        String sql = "SELECT p.*, c.nombre as categoria_nombre FROM productos p " +
                     "JOIN categorias c ON p.id_categoria = c.id " +
                     "WHERE c.nombre = ? ORDER BY p.nombre";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
            
            return productos;
        } catch (SQLException e) {
            logger.error("Error al buscar productos por categoría: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar productos con stock bajo (menor o igual al stock mínimo)
     * @return Lista de productos con stock bajo
     * @throws Exception
     */
    public List<Producto> buscarProductosBajoStock() throws Exception {
        String sql = "SELECT p.*, c.nombre as categoria_nombre FROM productos p " +
                     "LEFT JOIN categorias c ON p.id_categoria = c.id " +
                     "WHERE p.stock <= p.stock_minimo ORDER BY p.stock ASC";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            
            return productos;
        } catch (SQLException e) {
            logger.error("Error al buscar productos bajo stock: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Actualizar el stock de un producto
     * @param codigo Código del producto
     * @param cantidad Cantidad a añadir (positiva) o restar (negativa)
     * @return true si la operación fue exitosa
     * @throws Exception
     */
    public boolean actualizarStock(String codigo, int cantidad) throws Exception {
        String sql = "UPDATE productos SET stock = stock + ? WHERE codigo = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cantidad);
            stmt.setString(2, codigo);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Stock actualizado para producto código: {}, cantidad: {}", codigo, cantidad);
            
            // Registrar el movimiento de inventario
            registrarMovimientoInventario(conn, codigo, cantidad > 0 ? "Entrada" : "Salida", Math.abs(cantidad), null);
            
            return true;
        } catch (SQLException e) {
            logger.error("Error al actualizar stock: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Registrar un movimiento de inventario
     * @param conn Conexión a la base de datos
     * @param codigoProducto Código del producto
     * @param tipo Tipo de movimiento (Entrada, Salida, Ajuste)
     * @param cantidad Cantidad del movimiento
     * @param referencia Referencia del movimiento (ej: número de factura)
     * @throws SQLException
     */
    private void registrarMovimientoInventario(Connection conn, String codigoProducto, 
                                              String tipo, int cantidad, String referencia) throws SQLException {
        String sql = "INSERT INTO movimientos_inventario (codigo_producto, tipo, cantidad, referencia) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigoProducto);
            stmt.setString(2, tipo);
            stmt.setInt(3, cantidad);
            stmt.setString(4, referencia);
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto Producto
     * @param rs ResultSet con los datos
     * @return objeto Producto
     * @throws SQLException
     */
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setCodigo(rs.getString("codigo"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setCategoria(rs.getString("categoria_nombre"));
        producto.setStock(rs.getInt("stock"));
        producto.setStockMinimo(rs.getInt("stock_minimo"));
        producto.setPrecioCompra(rs.getBigDecimal("precio_compra"));
        producto.setPrecioVenta(rs.getBigDecimal("precio_venta"));
        producto.setEstado(rs.getString("estado"));
        producto.setUnidad(rs.getString("unidad"));
        return producto;
    }
}
