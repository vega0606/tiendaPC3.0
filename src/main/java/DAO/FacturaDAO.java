package DAO;

import modelo.DatabaseConnector;
import modelo.Factura;
import modelo.DetalleFactura;
import modelo.Producto;
import modelo.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones relacionadas con facturas
 */
public class FacturaDAO implements DAO<Factura, String> {
    private static final Logger logger = LoggerFactory.getLogger(FacturaDAO.class);
    private ClienteDAO clienteDAO;
    private ProductoDAO productoDAO;
    
    public FacturaDAO() {
        clienteDAO = new ClienteDAO();
        productoDAO = new ProductoDAO();
    }
    
    @Override
    public Factura crear(Factura factura) throws Exception {
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Insertar la factura
            String sql = "INSERT INTO facturas (numero, id_cliente, fecha, subtotal, iva, total, " +
                         "estado, id_usuario, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, factura.getNumero());
                stmt.setString(2, factura.getIdCliente());
                stmt.setDate(3, Date.valueOf(factura.getFecha()));
                stmt.setBigDecimal(4, factura.getSubtotal());
                stmt.setBigDecimal(5, factura.getIva());
                stmt.setBigDecimal(6, factura.getTotal());
                stmt.setString(7, factura.getEstado());
                stmt.setInt(8, factura.getIdUsuario());
                stmt.setString(9, factura.getObservaciones());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("La creación de la factura falló, no se insertaron filas");
                }
                
                // Insertar los detalles de la factura
                for (DetalleFactura detalle : factura.getDetalles()) {
                    insertarDetalleFactura(conn, detalle);
                    
                    // Actualizar el stock del producto
                    productoDAO.actualizarStock(detalle.getCodigoProducto(), -detalle.getCantidad());
                }
                
                // Commit de la transacción
                conn.commit();
                
                logger.info("Factura creada con número: {}", factura.getNumero());
                return factura;
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
            
            logger.error("Error al crear factura: {}", e.getMessage());
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
     * Inserta un detalle de factura
     * @param conn Conexión activa a la base de datos
     * @param detalle Detalle a insertar
     * @throws SQLException
     */
    private void insertarDetalleFactura(Connection conn, DetalleFactura detalle) throws SQLException {
        String sql = "INSERT INTO detalle_facturas (numero_factura, codigo_producto, cantidad, " +
                    "precio_unitario, iva, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, detalle.getNumeroFactura());
            stmt.setString(2, detalle.getCodigoProducto());
            stmt.setInt(3, detalle.getCantidad());
            stmt.setBigDecimal(4, detalle.getPrecioUnitario());
            stmt.setBigDecimal(5, detalle.getIva());
            stmt.setBigDecimal(6, detalle.getSubtotal());
            
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
    public Factura buscarPorId(String numero) throws Exception {
        String sql = "SELECT * FROM facturas WHERE numero = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numero);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Factura factura = mapearFactura(rs);
                    
                    // Cargar los detalles de la factura
                    factura.setDetalles(buscarDetallesPorFactura(numero));
                    
                    // Cargar el cliente
                    if (factura.getIdCliente() != null) {
                        factura.setCliente(clienteDAO.buscarPorId(factura.getIdCliente()));
                    }
                    
                    return factura;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar factura por número: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Busca los detalles de una factura
     * @param numeroFactura Número de factura
     * @return Lista de detalles
     * @throws Exception
     */
    private List<DetalleFactura> buscarDetallesPorFactura(String numeroFactura) throws Exception {
        String sql = "SELECT * FROM detalle_facturas WHERE numero_factura = ?";
        List<DetalleFactura> detalles = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numeroFactura);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleFactura detalle = mapearDetalleFactura(rs);
                    
                    // Cargar el producto
                    if (detalle.getCodigoProducto() != null) {
                        detalle.setProducto(productoDAO.buscarPorId(detalle.getCodigoProducto()));
                    }
                    
                    detalles.add(detalle);
                }
            }
            
            return detalles;
        } catch (SQLException e) {
            logger.error("Error al buscar detalles de factura: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Factura> listarTodos() throws Exception {
        String sql = "SELECT * FROM facturas ORDER BY fecha DESC";
        List<Factura> facturas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Factura factura = mapearFactura(rs);
                facturas.add(factura);
            }
            
            return facturas;
        } catch (SQLException e) {
            logger.error("Error al listar facturas: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Factura actualizar(Factura factura) throws Exception {
        String sql = "UPDATE facturas SET id_cliente = ?, fecha = ?, subtotal = ?, iva = ?, " +
                    "total = ?, estado = ?, observaciones = ? WHERE numero = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, factura.getIdCliente());
            stmt.setDate(2, Date.valueOf(factura.getFecha()));
            stmt.setBigDecimal(3, factura.getSubtotal());
            stmt.setBigDecimal(4, factura.getIva());
            stmt.setBigDecimal(5, factura.getTotal());
            stmt.setString(6, factura.getEstado());
            stmt.setString(7, factura.getObservaciones());
            stmt.setString(8, factura.getNumero());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La actualización de la factura falló, factura no encontrada");
            }
            
            logger.info("Factura actualizada con número: {}", factura.getNumero());
            return factura;
        } catch (SQLException e) {
            logger.error("Error al actualizar factura: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean eliminar(String numero) throws Exception {
        // En sistemas de facturación no se suelen eliminar facturas, sino anularlas
        // Sin embargo, implementamos el método para cumplir con la interfaz
        
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Primero recuperar la factura para luego restaurar el stock
            Factura factura = buscarPorId(numero);
            if (factura == null) {
                return false;
            }
            
            // Eliminar los detalles
            String sqlDetalles = "DELETE FROM detalle_facturas WHERE numero_factura = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDetalles)) {
                stmt.setString(1, numero);
                stmt.executeUpdate();
            }
            
            // Eliminar la factura
            String sqlFactura = "DELETE FROM facturas WHERE numero = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlFactura)) {
                stmt.setString(1, numero);
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    return false;
                }
            }
            
            // Restaurar el stock de los productos
            for (DetalleFactura detalle : factura.getDetalles()) {
                productoDAO.actualizarStock(detalle.getCodigoProducto(), detalle.getCantidad());
            }
            
            // Commit de la transacción
            conn.commit();
            
            logger.info("Factura eliminada con número: {}", numero);
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
            
            logger.error("Error al eliminar factura: {}", e.getMessage());
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
     * Anular una factura
     * @param numero Número de factura
     * @param motivo Motivo de la anulación
     * @return true si la anulación fue exitosa
     * @throws Exception
     */
    public boolean anularFactura(String numero, String motivo) throws Exception {
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Primero recuperar la factura para luego restaurar el stock
            Factura factura = buscarPorId(numero);
            if (factura == null) {
                return false;
            }
            
            // Actualizar el estado de la factura
            String sql = "UPDATE facturas SET estado = ?, observaciones = CONCAT(observaciones, ' | Anulada: ', ?) WHERE numero = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "Anulada");
                stmt.setString(2, motivo);
                stmt.setString(3, numero);
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    return false;
                }
            }
            
            // Restaurar el stock de los productos
            for (DetalleFactura detalle : factura.getDetalles()) {
                productoDAO.actualizarStock(detalle.getCodigoProducto(), detalle.getCantidad());
            }
            
            // Commit de la transacción
            conn.commit();
            
            logger.info("Factura anulada con número: {}", numero);
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
            
            logger.error("Error al anular factura: {}", e.getMessage());
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
     * Buscar facturas por cliente
     * @param idCliente ID del cliente
     * @return Lista de facturas del cliente
     * @throws Exception
     */
    public List<Factura> buscarPorCliente(String idCliente) throws Exception {
        String sql = "SELECT * FROM facturas WHERE id_cliente = ? ORDER BY fecha DESC";
        List<Factura> facturas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idCliente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    facturas.add(mapearFactura(rs));
                }
            }
            
            return facturas;
        } catch (SQLException e) {
            logger.error("Error al buscar facturas por cliente: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar facturas por fecha
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de facturas en el rango de fechas
     * @throws Exception
     */
    public List<Factura> buscarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String sql = "SELECT * FROM facturas WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        List<Factura> facturas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    facturas.add(mapearFactura(rs));
                }
            }
            
            return facturas;
        } catch (SQLException e) {
            logger.error("Error al buscar facturas por fecha: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Generar un nuevo número de factura
     * @return Nuevo número de factura
     * @throws Exception
     */
    public String generarNuevoNumero() throws Exception {
        String sql = "SELECT MAX(CAST(SUBSTRING(numero, 3) AS UNSIGNED)) AS ultimo_numero FROM facturas WHERE numero LIKE 'F-%'";
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int ultimoNumero = rs.getInt("ultimo_numero");
                return String.format("F-%04d", ultimoNumero + 1);
            } else {
                return "F-0001"; // Primera factura
            }
        } catch (SQLException e) {
            logger.error("Error al generar nuevo número de factura: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto Factura
     * @param rs ResultSet con los datos
     * @return objeto Factura
     * @throws SQLException
     */
    private Factura mapearFactura(ResultSet rs) throws SQLException {
        Factura factura = new Factura();
        factura.setNumero(rs.getString("numero"));
        factura.setIdCliente(rs.getString("id_cliente"));
        
        Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            factura.setFecha(fecha.toLocalDate());
        }
        
        factura.setSubtotal(rs.getBigDecimal("subtotal"));
        factura.setIva(rs.getBigDecimal("iva"));
        factura.setTotal(rs.getBigDecimal("total"));
        factura.setEstado(rs.getString("estado"));
        factura.setIdUsuario(rs.getInt("id_usuario"));
        factura.setObservaciones(rs.getString("observaciones"));
        
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            factura.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }
        
        return factura;
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto DetalleFactura
     * @param rs ResultSet con los datos
     * @return objeto DetalleFactura
     * @throws SQLException
     */
    private DetalleFactura mapearDetalleFactura(ResultSet rs) throws SQLException {
        DetalleFactura detalle = new DetalleFactura();
        detalle.setId(rs.getInt("id"));
        detalle.setNumeroFactura(rs.getString("numero_factura"));
        detalle.setCodigoProducto(rs.getString("codigo_producto"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        detalle.setIva(rs.getBigDecimal("iva"));
        detalle.setSubtotal(rs.getBigDecimal("subtotal"));
        
        return detalle;
    }
}