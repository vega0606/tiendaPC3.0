package DAO;

import modelo.Alerta;
import modelo.DatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones relacionadas con alertas del sistema
 */
public class AlertaDAO implements DAO<Alerta, String> {
    private static final Logger logger = LoggerFactory.getLogger(AlertaDAO.class);
    
    @Override
    public Alerta crear(Alerta alerta) throws Exception {
        String sql = "INSERT INTO alertas (id, tipo, descripcion, fecha, prioridad, estado, referencia) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, alerta.getId());
            stmt.setString(2, alerta.getTipo());
            stmt.setString(3, alerta.getDescripcion());
            stmt.setDate(4, Date.valueOf(alerta.getFecha()));
            stmt.setString(5, alerta.getPrioridad());
            stmt.setString(6, alerta.getEstado());
            stmt.setString(7, alerta.getReferencia());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La creación de la alerta falló, no se insertaron filas");
            }
            
            logger.info("Alerta creada con ID: {}", alerta.getId());
            return alerta;
        } catch (SQLException e) {
            logger.error("Error al crear alerta: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Alerta buscarPorId(String id) throws Exception {
        String sql = "SELECT * FROM alertas WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearAlerta(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar alerta por ID: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Alerta> listarTodos() throws Exception {
        String sql = "SELECT * FROM alertas ORDER BY fecha DESC, prioridad";
        List<Alerta> alertas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                alertas.add(mapearAlerta(rs));
            }
            
            return alertas;
        } catch (SQLException e) {
            logger.error("Error al listar alertas: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Alerta actualizar(Alerta alerta) throws Exception {
        String sql = "UPDATE alertas SET tipo = ?, descripcion = ?, fecha = ?, " +
                     "prioridad = ?, estado = ?, referencia = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, alerta.getTipo());
            stmt.setString(2, alerta.getDescripcion());
            stmt.setDate(3, Date.valueOf(alerta.getFecha()));
            stmt.setString(4, alerta.getPrioridad());
            stmt.setString(5, alerta.getEstado());
            stmt.setString(6, alerta.getReferencia());
            stmt.setString(7, alerta.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La actualización de la alerta falló, alerta no encontrada");
            }
            
            logger.info("Alerta actualizada con ID: {}", alerta.getId());
            return alerta;
        } catch (SQLException e) {
            logger.error("Error al actualizar alerta: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean eliminar(String id) throws Exception {
        String sql = "DELETE FROM alertas WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Alerta eliminada con ID: {}", id);
            return true;
        } catch (SQLException e) {
            logger.error("Error al eliminar alerta: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Listar alertas por tipo
     * @param tipo Tipo de alertas a buscar
     * @return Lista de alertas del tipo especificado
     * @throws Exception
     */
    public List<Alerta> listarPorTipo(String tipo) throws Exception {
        String sql = "SELECT * FROM alertas WHERE tipo = ? ORDER BY fecha DESC, prioridad";
        List<Alerta> alertas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alertas.add(mapearAlerta(rs));
                }
            }
            
            return alertas;
        } catch (SQLException e) {
            logger.error("Error al listar alertas por tipo: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Listar alertas por prioridad
     * @param prioridad Prioridad de las alertas a buscar
     * @return Lista de alertas con la prioridad especificada
     * @throws Exception
     */
    public List<Alerta> listarPorPrioridad(String prioridad) throws Exception {
        String sql = "SELECT * FROM alertas WHERE prioridad = ? ORDER BY fecha DESC";
        List<Alerta> alertas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, prioridad);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alertas.add(mapearAlerta(rs));
                }
            }
            
            return alertas;
        } catch (SQLException e) {
            logger.error("Error al listar alertas por prioridad: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Cambiar el estado de una alerta
     * @param id ID de la alerta
     * @param nuevoEstado Nuevo estado
     * @return true si el cambio fue exitoso
     * @throws Exception
     */
    public boolean cambiarEstado(String id, String nuevoEstado) throws Exception {
        String sql = "UPDATE alertas SET estado = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado);
            stmt.setString(2, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Estado de alerta ID: {} cambiado a: {}", id, nuevoEstado);
            return true;
        } catch (SQLException e) {
            logger.error("Error al cambiar estado de alerta: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Contar alertas por tipo y estado
     * @param tipo Tipo de alertas (null para todos)
     * @param estado Estado de alertas (null para todos)
     * @return Cantidad de alertas
     * @throws Exception
     */
    public int contarAlertas(String tipo, String estado) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM alertas WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (tipo != null && !tipo.equals("Todas")) {
            sql.append(" AND tipo = ?");
            params.add(tipo);
        }
        
        if (estado != null && !estado.equals("Todas")) {
            sql.append(" AND estado = ?");
            params.add(estado);
        }
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al contar alertas: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Generar un nuevo ID de alerta basado en el último ID
     * @return Nuevo ID de alerta
     * @throws Exception
     */
    public String generarNuevoId() throws Exception {
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 2) AS UNSIGNED)) AS ultimo_id FROM alertas WHERE id LIKE 'A%'";
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int ultimoId = rs.getInt("ultimo_id");
                return String.format("A%03d", ultimoId + 1);
            } else {
                return "A001"; // Primera alerta
            }
        } catch (SQLException e) {
            logger.error("Error al generar nuevo ID de alerta: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto Alerta
     * @param rs ResultSet con los datos
     * @return objeto Alerta
     * @throws SQLException
     */
    private Alerta mapearAlerta(ResultSet rs) throws SQLException {
        Alerta alerta = new Alerta();
        alerta.setId(rs.getString("id"));
        alerta.setTipo(rs.getString("tipo"));
        alerta.setDescripcion(rs.getString("descripcion"));
        
        Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            alerta.setFecha(fecha.toLocalDate());
        }
        
        alerta.setPrioridad(rs.getString("prioridad"));
        alerta.setEstado(rs.getString("estado"));
        alerta.setReferencia(rs.getString("referencia"));
        
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            alerta.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }
        
        return alerta;
    }
}