package DAO;

import modelo.DatabaseConnector;
import modelo.MovimientoInventario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones relacionadas con movimientos de inventario
 */
public class MovimientoInventarioDAO implements DAO<MovimientoInventario, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(MovimientoInventarioDAO.class);
    private ProductoDAO productoDAO;
    
    public MovimientoInventarioDAO() {
        this.productoDAO = new ProductoDAO();
    }
    
    @Override
    public MovimientoInventario crear(MovimientoInventario movimiento) throws Exception {
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Insertar el movimiento
            String sql = "INSERT INTO movimientos_inventario (codigo_producto, tipo, cantidad, " +
                         "referencia, id_usuario) VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, movimiento.getCodigoProducto());
                stmt.setString(2, movimiento.getTipo());
                stmt.setInt(3, movimiento.getCantidad());
                stmt.setString(4, movimiento.getReferencia());
                stmt.setInt(5, movimiento.getIdUsuario());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("La creación del movimiento falló, no se insertaron filas");
                }
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        movimiento.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("La creación del movimiento falló, no se obtuvo el ID");
                    }
                }
                
                // Actualizar el stock del producto
                if (movimiento.getTipo().equals("Entrada") || movimiento.getTipo().equals("Ajuste")) {
                    productoDAO.actualizarStock(movimiento.getCodigoProducto(), movimiento.getCantidad());
                } else if (movimiento.getTipo().equals("Salida")) {
                    productoDAO.actualizarStock(movimiento.getCodigoProducto(), -movimiento.getCantidad());
                }
                
                // Commit de la transacción
                conn.commit();
                
                logger.info("Movimiento de inventario creado con ID: {}", movimiento.getId());
                return movimiento;
            }
        } catch (SQLException e) {
            // Rollback en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error al hacer rollback: {}", ex.getMessage());
                }
            }
            
            logger.error("Error al crear movimiento de inventario: {}", e.getMessage());
            throw e;
        } finally {
            // Restaurar auto-commit
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    logger.error("Error al restaurar auto-commit: {}", ex.getMessage());
                }
            }
        }
    }
    
    @Override
    public MovimientoInventario buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM movimientos_inventario WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MovimientoInventario movimiento = mapearMovimiento(rs);
                    
                    // Cargar el producto
                    if (movimiento.getCodigoProducto() != null) {
                        movimiento.setProducto(productoDAO.buscarPorId(movimiento.getCodigoProducto()));
                    }
                    
                    return movimiento;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar movimiento por ID: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<MovimientoInventario> listarTodos() throws Exception {
        String sql = "SELECT * FROM movimientos_inventario ORDER BY fecha DESC";
        List<MovimientoInventario> movimientos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }
            
            return movimientos;
        } catch (SQLException e) {
            logger.error("Error al listar movimientos: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public MovimientoInventario actualizar(MovimientoInventario movimiento) throws Exception {
        // Normalmente no se actualizan los movimientos de inventario
        // Pero implementamos el método para cumplir con la interfaz
        
        String sql = "UPDATE movimientos_inventario SET codigo_producto = ?, tipo = ?, " +
                     "cantidad = ?, referencia = ?, id_usuario = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, movimiento.getCodigoProducto());
            stmt.setString(2, movimiento.getTipo());
            stmt.setInt(3, movimiento.getCantidad());
            stmt.setString(4, movimiento.getReferencia());
            stmt.setInt(5, movimiento.getIdUsuario());
            stmt.setInt(6, movimiento.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La actualización del movimiento falló, movimiento no encontrado");
            }
            
            logger.info("Movimiento de inventario actualizado con ID: {}", movimiento.getId());
            return movimiento;
        } catch (SQLException e) {
            logger.error("Error al actualizar movimiento: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean eliminar(Integer id) throws Exception {
        // Normalmente no se eliminan los movimientos de inventario
        // Pero implementamos el método para cumplir con la interfaz
        
        String sql = "DELETE FROM movimientos_inventario WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Movimiento de inventario eliminado con ID: {}", id);
            return true;
        } catch (SQLException e) {
            logger.error("Error al eliminar movimiento: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar movimientos por producto
     * @param codigoProducto Código del producto
     * @return Lista de movimientos del producto
     * @throws Exception
     */
    public List<MovimientoInventario> buscarPorProducto(String codigoProducto) throws Exception {
        String sql = "SELECT * FROM movimientos_inventario WHERE codigo_producto = ? ORDER BY fecha DESC";
        List<MovimientoInventario> movimientos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigoProducto);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapearMovimiento(rs));
                }
            }
            
            return movimientos;
        } catch (SQLException e) {
            logger.error("Error al buscar movimientos por producto: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar movimientos por tipo
     * @param tipo Tipo de movimiento (Entrada, Salida, Ajuste)
     * @return Lista de movimientos del tipo especificado
     * @throws Exception
     */
    public List<MovimientoInventario> buscarPorTipo(String tipo) throws Exception {
        String sql = "SELECT * FROM movimientos_inventario WHERE tipo = ? ORDER BY fecha DESC";
        List<MovimientoInventario> movimientos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapearMovimiento(rs));
                }
            }
            
            return movimientos;
        } catch (SQLException e) {
            logger.error("Error al buscar movimientos por tipo: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar movimientos por referencia
     * @param referencia Referencia del movimiento
     * @return Lista de movimientos con la referencia especificada
     * @throws Exception
     */
    public List<MovimientoInventario> buscarPorReferencia(String referencia) throws Exception {
        String sql = "SELECT * FROM movimientos_inventario WHERE referencia = ? ORDER BY fecha DESC";
        List<MovimientoInventario> movimientos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, referencia);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapearMovimiento(rs));
                }
            }
            
            return movimientos;
        } catch (SQLException e) {
            logger.error("Error al buscar movimientos por referencia: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar movimientos por fecha
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de movimientos en el rango de fechas
     * @throws Exception
     */
    public List<MovimientoInventario> buscarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String sql = "SELECT * FROM movimientos_inventario WHERE DATE(fecha) BETWEEN ? AND ? ORDER BY fecha DESC";
        List<MovimientoInventario> movimientos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimientos.add(mapearMovimiento(rs));
                }
            }
            
            return movimientos;
        } catch (SQLException e) {
            logger.error("Error al buscar movimientos por fecha: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Obtener resumen de movimientos por producto
     * @param codigoProducto Código del producto
     * @return Cantidad neta de movimientos (entradas - salidas)
     * @throws Exception
     */
    public int obtenerResumenMovimientos(String codigoProducto) throws Exception {
        String sql = "SELECT SUM(CASE WHEN tipo = 'Entrada' OR tipo = 'Ajuste' THEN cantidad " +
                     "WHEN tipo = 'Salida' THEN -cantidad ELSE 0 END) AS total " +
                     "FROM movimientos_inventario WHERE codigo_producto = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigoProducto);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener resumen de movimientos: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto MovimientoInventario
     * @param rs ResultSet con los datos
     * @return objeto MovimientoInventario
     * @throws SQLException
     */
    private MovimientoInventario mapearMovimiento(ResultSet rs) throws SQLException {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setId(rs.getInt("id"));
        movimiento.setCodigoProducto(rs.getString("codigo_producto"));
        movimiento.setTipo(rs.getString("tipo"));
        movimiento.setCantidad(rs.getInt("cantidad"));
        movimiento.setReferencia(rs.getString("referencia"));
        movimiento.setIdUsuario(rs.getInt("id_usuario"));
        
        Timestamp fecha = rs.getTimestamp("fecha");
        if (fecha != null) {
            movimiento.setFecha(fecha.toLocalDateTime());
        }
        
        return movimiento;
    }
}
