package DAO;

import modelo.DatabaseConnector;
import modelo.Transaccion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones relacionadas con transacciones
 */
public class TransaccionDAO implements DAO<Transaccion, String> {
    private static final Logger logger = LoggerFactory.getLogger(TransaccionDAO.class);
    
    @Override
    public Transaccion crear(Transaccion transaccion) throws Exception {
        String sql = "INSERT INTO transacciones (id, fecha, entidad, tipo, referencia, " +
                     "total, estado, id_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transaccion.getId());
            stmt.setDate(2, Date.valueOf(transaccion.getFecha()));
            stmt.setString(3, transaccion.getEntidad());
            stmt.setString(4, transaccion.getTipo());
            stmt.setString(5, transaccion.getReferencia());
            stmt.setBigDecimal(6, transaccion.getTotal());
            stmt.setString(7, transaccion.getEstado());
            stmt.setInt(8, transaccion.getIdUsuario());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La creación de la transacción falló, no se insertaron filas");
            }
            
            logger.info("Transacción creada con ID: {}", transaccion.getId());
            return transaccion;
        } catch (SQLException e) {
            logger.error("Error al crear transacción: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Transaccion buscarPorId(String id) throws Exception {
        String sql = "SELECT * FROM transacciones WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearTransaccion(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar transacción por ID: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Transaccion> listarTodos() throws Exception {
        String sql = "SELECT * FROM transacciones ORDER BY fecha DESC";
        List<Transaccion> transacciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transacciones.add(mapearTransaccion(rs));
            }
            
            return transacciones;
        } catch (SQLException e) {
            logger.error("Error al listar transacciones: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Transaccion actualizar(Transaccion transaccion) throws Exception {
        String sql = "UPDATE transacciones SET fecha = ?, entidad = ?, tipo = ?, " +
                     "referencia = ?, total = ?, estado = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(transaccion.getFecha()));
            stmt.setString(2, transaccion.getEntidad());
            stmt.setString(3, transaccion.getTipo());
            stmt.setString(4, transaccion.getReferencia());
            stmt.setBigDecimal(5, transaccion.getTotal());
            stmt.setString(6, transaccion.getEstado());
            stmt.setString(7, transaccion.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La actualización de la transacción falló, transacción no encontrada");
            }
            
            logger.info("Transacción actualizada con ID: {}", transaccion.getId());
            return transaccion;
        } catch (SQLException e) {
            logger.error("Error al actualizar transacción: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean eliminar(String id) throws Exception {
        String sql = "DELETE FROM transacciones WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Transacción eliminada con ID: {}", id);
            return true;
        } catch (SQLException e) {
            logger.error("Error al eliminar transacción: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar transacciones por tipo
     * @param tipo Tipo de transacción (Venta, Compra, Devolución, etc.)
     * @return Lista de transacciones del tipo especificado
     * @throws Exception
     */
    public List<Transaccion> buscarPorTipo(String tipo) throws Exception {
        String sql = "SELECT * FROM transacciones WHERE tipo = ? ORDER BY fecha DESC";
        List<Transaccion> transacciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacciones.add(mapearTransaccion(rs));
                }
            }
            
            return transacciones;
        } catch (SQLException e) {
            logger.error("Error al buscar transacciones por tipo: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar transacciones por entidad
     * @param entidad Nombre del cliente o proveedor
     * @return Lista de transacciones de la entidad
     * @throws Exception
     */
    public List<Transaccion> buscarPorEntidad(String entidad) throws Exception {
        String sql = "SELECT * FROM transacciones WHERE entidad LIKE ? ORDER BY fecha DESC";
        List<Transaccion> transacciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + entidad + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacciones.add(mapearTransaccion(rs));
                }
            }
            
            return transacciones;
        } catch (SQLException e) {
            logger.error("Error al buscar transacciones por entidad: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar transacciones por referencia
     * @param referencia Número de factura, pedido, etc.
     * @return Lista de transacciones con la referencia especificada
     * @throws Exception
     */
    public List<Transaccion> buscarPorReferencia(String referencia) throws Exception {
        String sql = "SELECT * FROM transacciones WHERE referencia = ? ORDER BY fecha DESC";
        List<Transaccion> transacciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, referencia);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacciones.add(mapearTransaccion(rs));
                }
            }
            
            return transacciones;
        } catch (SQLException e) {
            logger.error("Error al buscar transacciones por referencia: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar transacciones por rango de fechas
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de transacciones en el rango de fechas
     * @throws Exception
     */
    public List<Transaccion> buscarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String sql = "SELECT * FROM transacciones WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        List<Transaccion> transacciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacciones.add(mapearTransaccion(rs));
                }
            }
            
            return transacciones;
        } catch (SQLException e) {
            logger.error("Error al buscar transacciones por fecha: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Obtener total de ventas por rango de fechas
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Total de ventas
     * @throws Exception
     */
    public double getTotalVentas(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String sql = "SELECT SUM(total) AS total FROM transacciones WHERE tipo = 'Venta' AND fecha BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                } else {
                    return 0.0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener total de ventas: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Obtener total de compras por rango de fechas
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Total de compras
     * @throws Exception
     */
    public double getTotalCompras(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String sql = "SELECT SUM(total) AS total FROM transacciones WHERE tipo = 'Compra' AND fecha BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                } else {
                    return 0.0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al obtener total de compras: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Generar un nuevo ID de transacción
     * @return Nuevo ID de transacción
     * @throws Exception
     */
    public String generarNuevoId() throws Exception {
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) AS ultimo_id FROM transacciones WHERE id LIKE 'T-%'";
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int ultimoId = rs.getInt("ultimo_id");
                return String.format("T-%03d", ultimoId + 1);
            } else {
                return "T-001"; // Primera transacción
            }
        } catch (SQLException e) {
            logger.error("Error al generar nuevo ID de transacción: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Cambiar el estado de una transacción
     * @param id ID de la transacción
     * @param nuevoEstado Nuevo estado
     * @return true si el cambio fue exitoso
     * @throws Exception
     */
    public boolean cambiarEstado(String id, String nuevoEstado) throws Exception {
        String sql = "UPDATE transacciones SET estado = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado);
            stmt.setString(2, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Estado de transacción {} actualizado a: {}", id, nuevoEstado);
            return true;
        } catch (SQLException e) {
            logger.error("Error al cambiar estado de transacción: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Obtener estadísticas mensuales de transacciones
     * @param anio Año para las estadísticas
     * @return Mapa con totales mensuales (clave: mes [1-12], valor: total)
     * @throws Exception
     */
    public List<Object[]> getEstadisticasMensuales(int anio) throws Exception {
        String sql = "SELECT MONTH(fecha) as mes, tipo, SUM(total) as total " +
                     "FROM transacciones " +
                     "WHERE YEAR(fecha) = ? " +
                     "GROUP BY MONTH(fecha), tipo " +
                     "ORDER BY MONTH(fecha), tipo";
        
        List<Object[]> resultados = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, anio);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[3];
                    fila[0] = rs.getInt("mes");
                    fila[1] = rs.getString("tipo");
                    fila[2] = rs.getDouble("total");
                    resultados.add(fila);
                }
            }
            
            return resultados;
        } catch (SQLException e) {
            logger.error("Error al obtener estadísticas mensuales: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto Transaccion
     * @param rs ResultSet con los datos
     * @return objeto Transaccion
     * @throws SQLException
     */
    private Transaccion mapearTransaccion(ResultSet rs) throws SQLException {
        Transaccion transaccion = new Transaccion();
        transaccion.setId(rs.getString("id"));
        
        Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            transaccion.setFecha(fecha.toLocalDate());
        }
        
        transaccion.setEntidad(rs.getString("entidad"));
        transaccion.setTipo(rs.getString("tipo"));
        transaccion.setReferencia(rs.getString("referencia"));
        transaccion.setTotal(rs.getBigDecimal("total"));
        transaccion.setEstado(rs.getString("estado"));
        transaccion.setIdUsuario(rs.getInt("id_usuario"));
        
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            transaccion.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }
        
        return transaccion;
    }
    /**
     * Buscar transacciones por estado
     * @param estado Estado de la transacción
     * @return Lista de transacciones con el estado especificado
     * @throws Exception
     */
    public List<Transaccion> buscarPorEstado(String estado) throws Exception {
        String sql = "SELECT * FROM transacciones WHERE estado = ? ORDER BY fecha DESC";
        List<Transaccion> transacciones = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transacciones.add(mapearTransaccion(rs));
                }
            }
            
            return transacciones;
        } catch (SQLException e) {
            logger.error("Error al buscar transacciones por estado: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Cancelar una transacción
     * @param id ID de la transacción a cancelar
     * @return true si la cancelación fue exitosa
     * @throws Exception
     */
    public boolean cancelarTransaccion(String id, String motivo) throws Exception {
        // Primero verificamos si la transacción puede ser cancelada
        Transaccion transaccion = buscarPorId(id);
        
        if (transaccion == null) {
            logger.error("Transacción no encontrada para cancelación: {}", id);
            return false;
        }
        
        // Verificar que no esté ya cancelada
        if ("Cancelada".equals(transaccion.getEstado())) {
            logger.info("Transacción {} ya está cancelada", id);
            return true;
        }
        
        // Método para cancelar la transacción con motivo
        String sql = "UPDATE transacciones SET estado = 'Cancelada', detalles_cancelacion = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, motivo);
            stmt.setString(2, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                logger.error("No se pudo cancelar la transacción: {}", id);
                return false;
            }
            
            logger.info("Transacción {} cancelada exitosamente. Motivo: {}", id, motivo);
            return true;
        } catch (SQLException e) {
            logger.error("Error al cancelar transacción: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Actualizar el estado de una transacción
     * @param id ID de la transacción
     * @param nuevoEstado Nuevo estado de la transacción
     * @return true si la actualización fue exitosa
     * @throws Exception
     */
    public boolean actualizarEstado(String id, String nuevoEstado) throws Exception {
        String sql = "UPDATE transacciones SET estado = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado);
            stmt.setString(2, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                logger.error("No se pudo actualizar el estado de la transacción: {}", id);
                return false;
            }
            
            logger.info("Estado de transacción {} actualizado a: {}", id, nuevoEstado);
            return true;
        } catch (SQLException e) {
            logger.error("Error al actualizar estado de transacción: {}", e.getMessage());
            throw e;
        }
    }
}
