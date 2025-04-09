package DAO;

import modelo.DatabaseConnector;
import Modelo.Devolucion;
import Modelo.DetalleDevolucion;
import modelo.Factura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones relacionadas con devoluciones
 */
public class DevolucionDAO implements DAO<Devolucion, String> {
    private static final Logger logger = LoggerFactory.getLogger(DevolucionDAO.class);
    private FacturaDAO facturaDAO;
    private ProductoDAO productoDAO;
    
    public DevolucionDAO() {
        this.facturaDAO = new FacturaDAO();
        this.productoDAO = new ProductoDAO();
    }
    
    @Override
    public Devolucion crear(Devolucion devolucion) throws Exception {
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Insertar la devolución
            String sql = "INSERT INTO devoluciones (id, numero_factura, fecha, motivo, detalles, " +
                         "total, id_usuario) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, devolucion.getId());
                stmt.setString(2, devolucion.getNumeroFactura());
                stmt.setDate(3, Date.valueOf(devolucion.getFecha()));
                stmt.setString(4, devolucion.getMotivo());
                stmt.setString(5, devolucion.getDetalles());
                stmt.setBigDecimal(6, devolucion.getTotal());
                stmt.setInt(7, devolucion.getIdUsuario());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("La creación de la devolución falló, no se insertaron filas");
                }
                
                // Insertar los detalles de la devolución
                for (DetalleDevolucion detalle : devolucion.getDetalles()) {
                    insertarDetalleDevolucion(conn, detalle);
                    
                    // Actualizar el stock del producto (devolver al inventario)
                    productoDAO.actualizarStock(detalle.getCodigoProducto(), detalle.getCantidad());
                }
                
                // Commit de la transacción
                conn.commit();
                
                logger.info("Devolución creada con ID: {}", devolucion.getId());
                return devolucion;
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
            
            logger.error("Error al crear devolución: {}", e.getMessage());
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
    
    /**
     * Inserta un detalle de devolución
     * @param conn Conexión activa a la base de datos
     * @param detalle Detalle a insertar
     * @throws SQLException
     */
    private void insertarDetalleDevolucion(Connection conn, DetalleDevolucion detalle) throws SQLException {
        String sql = "INSERT INTO detalle_devoluciones (id_devolucion, codigo_producto, cantidad, " +
                    "precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, detalle.getIdDevolucion());
            stmt.setString(2, detalle.getCodigoProducto());
            stmt.setInt(3, detalle.getCantidad());
            stmt.setBigDecimal(4, detalle.getPrecioUnitario());
            stmt.setBigDecimal(5, detalle.getSubtotal());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La creación del detalle falló, no se insertaron filas");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    detalle.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La creación del detalle falló, no se obtuvo el ID");
                }
            }
        }
    }
    
    @Override
    public Devolucion buscarPorId(String id) throws Exception {
        String sql = "SELECT * FROM devoluciones WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Devolucion devolucion = mapearDevolucion(rs);
                    
                    // Cargar los detalles de la devolución
                    devolucion.setDetalles(buscarDetallesPorDevolucion(id));
                    
                    // Cargar la factura
                    if (devolucion.getNumeroFactura() != null) {
                        devolucion.setFactura(facturaDAO.buscarPorId(devolucion.getNumeroFactura()));
                    }
                    
                    return devolucion;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar devolución por ID: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Busca los detalles de una devolución
     * @param idDevolucion ID de la devolución
     * @return Lista de detalles
     * @throws Exception
     */
    private List<DetalleDevolucion> buscarDetallesPorDevolucion(String idDevolucion) throws Exception {
        String sql = "SELECT * FROM detalle_devoluciones WHERE id_devolucion = ?";
        List<DetalleDevolucion> detalles = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idDevolucion);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleDevolucion detalle = mapearDetalleDevolucion(rs);
                    
                    // Cargar el producto
                    if (detalle.getCodigoProducto() != null) {
                        detalle.setProducto(productoDAO.buscarPorId(detalle.getCodigoProducto()));
                    }
                    
                    detalles.add(detalle);
                }
            }
            
            return detalles;
        } catch (SQLException e) {
            logger.error("Error al buscar detalles de devolución: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Devolucion> listarTodos() throws Exception {
        String sql = "SELECT * FROM devoluciones ORDER BY fecha DESC";
        List<Devolucion> devoluciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                devoluciones.add(mapearDevolucion(rs));
            }
            
            return devoluciones;
        } catch (SQLException e) {
            logger.error("Error al listar devoluciones: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Devolucion actualizar(Devolucion devolucion) throws Exception {
        // Normalmente no se actualizan las devoluciones una vez creadas
        // Pero implementamos el método para cumplir con la interfaz
        
        String sql = "UPDATE devoluciones SET numero_factura = ?, fecha = ?, motivo = ?, " +
                    "detalles = ?, total = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, devolucion.getNumeroFactura());
            stmt.setDate(2, Date.valueOf(devolucion.getFecha()));
            stmt.setString(3, devolucion.getMotivo());
            stmt.setString(4, devolucion.getDetalles());
            stmt.setBigDecimal(5, devolucion.getTotal());
            stmt.setString(6, devolucion.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La actualización de la devolución falló, devolución no encontrada");
            }
            
            logger.info("Devolución actualizada con ID: {}", devolucion.getId());
            return devolucion;
        } catch (SQLException e) {
            logger.error("Error al actualizar devolución: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean eliminar(String id) throws Exception {
        // Normalmente no se eliminan las devoluciones una vez creadas
        // Pero implementamos el método para cumplir con la interfaz
        
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Primero recuperar la devolución para restaurar el stock a su estado anterior
            Devolucion devolucion = buscarPorId(id);
            if (devolucion == null) {
                return false;
            }
            
            // Restaurar stock de productos (restar lo que se devolvió al eliminar la devolución)
            for (DetalleDevolucion detalle : devolucion.getDetalles()) {
                productoDAO.actualizarStock(detalle.getCodigoProducto(), -detalle.getCantidad());
            }
            
            // Eliminar los detalles
            String sqlDetalles = "DELETE FROM detalle_devoluciones WHERE id_devolucion = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDetalles)) {
                stmt.setString(1, id);
                stmt.executeUpdate();
            }
            
            // Eliminar la devolución
            String sqlDevolucion = "DELETE FROM devoluciones WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDevolucion)) {
                stmt.setString(1, id);
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    return false;
                }
            }
            
            // Commit de la transacción
            conn.commit();
            
            logger.info("Devolución eliminada con ID: {}", id);
            return true;
        } catch (SQLException e) {
            // Rollback en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error al hacer rollback: {}", ex.getMessage());
                }
            }
            
            logger.error("Error al eliminar devolución: {}", e.getMessage());
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
    
    /**
     * Buscar devoluciones por factura
     * @param numeroFactura Número de factura
     * @return Lista de devoluciones asociadas a la factura
     * @throws Exception
     */
    public List<Devolucion> buscarPorFactura(String numeroFactura) throws Exception {
        String sql = "SELECT * FROM devoluciones WHERE numero_factura = ? ORDER BY fecha DESC";
        List<Devolucion> devoluciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numeroFactura);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    devoluciones.add(mapearDevolucion(rs));
                }
            }
            
            return devoluciones;
        } catch (SQLException e) {
            logger.error("Error al buscar devoluciones por factura: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar devoluciones por fecha
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de devoluciones en el rango de fechas
     * @throws Exception
     */
    public List<Devolucion> buscarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String sql = "SELECT * FROM devoluciones WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        List<Devolucion> devoluciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    devoluciones.add(mapearDevolucion(rs));
                }
            }
            
            return devoluciones;
        } catch (SQLException e) {
            logger.error("Error al buscar devoluciones por fecha: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Generar un nuevo ID de devolución
     * @return Nuevo ID de devolución
     * @throws Exception
     */
    public String generarNuevoId() throws Exception {
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) AS ultimo_id FROM devoluciones WHERE id LIKE 'D-%'";
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int ultimoId = rs.getInt("ultimo_id");
                return String.format("D-%04d", ultimoId + 1);
            } else {
                return "D-0001"; // Primera devolución
            }
        } catch (SQLException e) {
            logger.error("Error al generar nuevo ID de devolución: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto Devolucion
     * @param rs ResultSet con los datos
     * @return objeto Devolucion
     * @throws SQLException
     */
    private Devolucion mapearDevolucion(ResultSet rs) throws SQLException {
        Devolucion devolucion = new Devolucion();
        devolucion.setId(rs.getString("id"));
        devolucion.setNumeroFactura(rs.getString("numero_factura"));
        
        Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            devolucion.setFecha(fecha.toLocalDate());
        }
        
        devolucion.setMotivo(rs.getString("motivo"));
        devolucion.setDetalles(rs.getString("detalles"));
        devolucion.setTotal(rs.getBigDecimal("total"));
        devolucion.setIdUsuario(rs.getInt("id_usuario"));
        
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            devolucion.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }
        
        return devolucion;
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto DetalleDevolucion
     * @param rs ResultSet con los datos
     * @return objeto DetalleDevolucion
     * @throws SQLException
     */
    private DetalleDevolucion mapearDetalleDevolucion(ResultSet rs) throws SQLException {
        DetalleDevolucion detalle = new DetalleDevolucion();
        detalle.setId(rs.getInt("id"));
        detalle.setIdDevolucion(rs.getString("id_devolucion"));
        detalle.setCodigoProducto(rs.getString("codigo_producto"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        detalle.setSubtotal(rs.getBigDecimal("subtotal"));
        
        return detalle;
    }
}