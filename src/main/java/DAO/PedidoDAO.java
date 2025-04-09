package DAO;

import modelo.DatabaseConnector;
import modelo.Pedido;
import modelo.DetallePedido;
import modelo.Proveedor;
import modelo.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones relacionadas con pedidos a proveedores
 */
public class PedidoDAO implements DAO<Pedido, String> {
    private static final Logger logger = LoggerFactory.getLogger(PedidoDAO.class);
    private ProveedorDAO proveedorDAO;
    private ProductoDAO productoDAO;
    
    public PedidoDAO() {
        this.proveedorDAO = new ProveedorDAO();
        this.productoDAO = new ProductoDAO();
    }
    
    @Override
    public Pedido crear(Pedido pedido) throws Exception {
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Insertar el pedido
            String sql = "INSERT INTO pedidos (numero, id_proveedor, fecha, fecha_entrega, total, " +
                         "estado, id_usuario, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, pedido.getNumero());
                stmt.setString(2, pedido.getIdProveedor());
                stmt.setDate(3, Date.valueOf(pedido.getFecha()));
                
                if (pedido.getFechaEntrega() != null) {
                    stmt.setDate(4, Date.valueOf(pedido.getFechaEntrega()));
                } else {
                    stmt.setNull(4, Types.DATE);
                }
                
                stmt.setBigDecimal(5, pedido.getTotal());
                stmt.setString(6, pedido.getEstado());
                stmt.setInt(7, pedido.getIdUsuario());
                stmt.setString(8, pedido.getObservaciones());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("La creación del pedido falló, no se insertaron filas");
                }
                
                // Insertar los detalles del pedido
                for (DetallePedido detalle : pedido.getDetalles()) {
                    insertarDetallePedido(conn, detalle);
                }
                
                // Commit de la transacción
                conn.commit();
                
                logger.info("Pedido creado con número: {}", pedido.getNumero());
                return pedido;
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
            
            logger.error("Error al crear pedido: {}", e.getMessage());
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
     * Inserta un detalle de pedido
     * @param conn Conexión activa a la base de datos
     * @param detalle Detalle a insertar
     * @throws SQLException
     */
    private void insertarDetallePedido(Connection conn, DetallePedido detalle) throws SQLException {
        String sql = "INSERT INTO detalle_pedidos (numero_pedido, codigo_producto, cantidad, " +
                    "precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, detalle.getNumeroPedido());
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
    public Pedido buscarPorId(String numero) throws Exception {
        String sql = "SELECT * FROM pedidos WHERE numero = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numero);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Pedido pedido = mapearPedido(rs);
                    
                    // Cargar los detalles del pedido
                    pedido.setDetalles(buscarDetallesPorPedido(numero));
                    
                    // Cargar el proveedor
                    if (pedido.getIdProveedor() != null) {
                        pedido.setProveedor(proveedorDAO.buscarPorId(pedido.getIdProveedor()));
                    }
                    
                    return pedido;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar pedido por número: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Busca los detalles de un pedido
     * @param numeroPedido Número de pedido
     * @return Lista de detalles
     * @throws Exception
     */
    private List<DetallePedido> buscarDetallesPorPedido(String numeroPedido) throws Exception {
        String sql = "SELECT * FROM detalle_pedidos WHERE numero_pedido = ?";
        List<DetallePedido> detalles = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numeroPedido);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetallePedido detalle = mapearDetallePedido(rs);
                    
                    // Cargar el producto
                    if (detalle.getCodigoProducto() != null) {
                        detalle.setProducto(productoDAO.buscarPorId(detalle.getCodigoProducto()));
                    }
                    
                    detalles.add(detalle);
                }
            }
            
            return detalles;
        } catch (SQLException e) {
            logger.error("Error al buscar detalles de pedido: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Pedido> listarTodos() throws Exception {
        String sql = "SELECT * FROM pedidos ORDER BY fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pedidos.add(mapearPedido(rs));
            }
            
            return pedidos;
        } catch (SQLException e) {
            logger.error("Error al listar pedidos: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Pedido actualizar(Pedido pedido) throws Exception {
        String sql = "UPDATE pedidos SET id_proveedor = ?, fecha = ?, fecha_entrega = ?, " +
                    "total = ?, estado = ?, observaciones = ? WHERE numero = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, pedido.getIdProveedor());
            stmt.setDate(2, Date.valueOf(pedido.getFecha()));
            
            if (pedido.getFechaEntrega() != null) {
                stmt.setDate(3, Date.valueOf(pedido.getFechaEntrega()));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            
            stmt.setBigDecimal(4, pedido.getTotal());
            stmt.setString(5, pedido.getEstado());
            stmt.setString(6, pedido.getObservaciones());
            stmt.setString(7, pedido.getNumero());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La actualización del pedido falló, pedido no encontrado");
            }
            
            logger.info("Pedido actualizado con número: {}", pedido.getNumero());
            return pedido;
        } catch (SQLException e) {
            logger.error("Error al actualizar pedido: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean eliminar(String numero) throws Exception {
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Eliminar los detalles
            String sqlDetalles = "DELETE FROM detalle_pedidos WHERE numero_pedido = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDetalles)) {
                stmt.setString(1, numero);
                stmt.executeUpdate();
            }
            
            // Eliminar el pedido
            String sqlPedido = "DELETE FROM pedidos WHERE numero = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPedido)) {
                stmt.setString(1, numero);
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    return false;
                }
            }
            
            // Commit de la transacción
            conn.commit();
            
            logger.info("Pedido eliminado con número: {}", numero);
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
            
            logger.error("Error al eliminar pedido: {}", e.getMessage());
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
     * Buscar pedidos por proveedor
     * @param idProveedor ID del proveedor
     * @return Lista de pedidos del proveedor
     * @throws Exception
     */
    public List<Pedido> buscarPorProveedor(String idProveedor) throws Exception {
        String sql = "SELECT * FROM pedidos WHERE id_proveedor = ? ORDER BY fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idProveedor);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
            
            return pedidos;
        } catch (SQLException e) {
            logger.error("Error al buscar pedidos por proveedor: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar pedidos por estado
     * @param estado Estado del pedido
     * @return Lista de pedidos con el estado especificado
     * @throws Exception
     */
    public List<Pedido> buscarPorEstado(String estado) throws Exception {
        String sql = "SELECT * FROM pedidos WHERE estado = ? ORDER BY fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
            
            return pedidos;
        } catch (SQLException e) {
            logger.error("Error al buscar pedidos por estado: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar pedidos por fecha
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de pedidos en el rango de fechas
     * @throws Exception
     */
    public List<Pedido> buscarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String sql = "SELECT * FROM pedidos WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        List<Pedido> pedidos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
            
            return pedidos;
        } catch (SQLException e) {
            logger.error("Error al buscar pedidos por fecha: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Actualizar el estado de un pedido
     * @param numero Número de pedido
     * @param nuevoEstado Nuevo estado
     * @return true si la actualización fue exitosa
     * @throws Exception
     */
    public boolean actualizarEstado(String numero, String nuevoEstado) throws Exception {
        String sql = "UPDATE pedidos SET estado = ? WHERE numero = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado);
            stmt.setString(2, numero);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Estado del pedido {} actualizado a: {}", numero, nuevoEstado);
            return true;
        } catch (SQLException e) {
            logger.error("Error al actualizar estado del pedido: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Recibir un pedido (actualiza el inventario)
     * @param numero Número de pedido
     * @return true si la recepción fue exitosa
     * @throws Exception
     */
    public boolean recibirPedido(String numero) throws Exception {
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Cambiar estado del pedido a "Entregado"
            String sqlEstado = "UPDATE pedidos SET estado = 'Entregado', fecha_entrega = ? WHERE numero = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlEstado)) {
                stmt.setDate(1, Date.valueOf(LocalDate.now()));
                stmt.setString(2, numero);
                stmt.executeUpdate();
            }
            
            // Obtener detalles del pedido
            Pedido pedido = buscarPorId(numero);
            if (pedido == null) {
                return false;
            }
            
            // Actualizar inventario para cada producto
            for (DetallePedido detalle : pedido.getDetalles()) {
                productoDAO.actualizarStock(detalle.getCodigoProducto(), detalle.getCantidad());
            }
            
            // Commit de la transacción
            conn.commit();
            
            logger.info("Pedido recibido con número: {}", numero);
            return true;
        } catch (Exception e) {
            // Rollback en caso de error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error al hacer rollback: {}", ex.getMessage());
                }
            }
            
            logger.error("Error al recibir pedido: {}", e.getMessage());
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
     * Cancelar un pedido
     * @param numero Número de pedido
     * @param motivo Motivo de la cancelación
     * @return true si la cancelación fue exitosa
     * @throws Exception
     */
    public boolean cancelarPedido(String numero, String motivo) throws Exception {
        String sql = "UPDATE pedidos SET estado = 'Cancelado', observaciones = CONCAT(observaciones, ' | Cancelado: ', ?) WHERE numero = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, motivo);
            stmt.setString(2, numero);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Pedido cancelado con número: {}", numero);
            return true;
        } catch (SQLException e) {
            logger.error("Error al cancelar pedido: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Generar un nuevo número de pedido
     * @return Nuevo número de pedido
     * @throws Exception
     */
    public String generarNuevoNumero() throws Exception {
        String sql = "SELECT MAX(CAST(SUBSTRING(numero, 4) AS UNSIGNED)) AS ultimo_numero FROM pedidos WHERE numero LIKE 'PO-%'";
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int ultimoNumero = rs.getInt("ultimo_numero");
                return String.format("PO-%04d", ultimoNumero + 1);
            } else {
                return "PO-0001"; // Primer pedido
            }
        } catch (SQLException e) {
            logger.error("Error al generar nuevo número de pedido: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto Pedido
     * @param rs ResultSet con los datos
     * @return objeto Pedido
     * @throws SQLException
     */
    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setNumero(rs.getString("numero"));
        pedido.setIdProveedor(rs.getString("id_proveedor"));
        
        Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            pedido.setFecha(fecha.toLocalDate());
        }
        
        Date fechaEntrega = rs.getDate("fecha_entrega");
        if (fechaEntrega != null) {
            pedido.setFechaEntrega(fechaEntrega.toLocalDate());
        }
        
        pedido.setTotal(rs.getBigDecimal("total"));
        pedido.setEstado(rs.getString("estado"));
        pedido.setIdUsuario(rs.getInt("id_usuario"));
        pedido.setObservaciones(rs.getString("observaciones"));
        
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            pedido.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }
        
        return pedido;
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto DetallePedido
     * @param rs ResultSet con los datos
     * @return objeto DetallePedido
     * @throws SQLException
     */
    private DetallePedido mapearDetallePedido(ResultSet rs) throws SQLException {
        DetallePedido detalle = new DetallePedido();
        detalle.setId(rs.getInt("id"));
        detalle.setNumeroPedido(rs.getString("numero_pedido"));
        detalle.setCodigoProducto(rs.getString("codigo_producto"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        detalle.setSubtotal(rs.getBigDecimal("subtotal"));
        
        return detalle;
    }
}