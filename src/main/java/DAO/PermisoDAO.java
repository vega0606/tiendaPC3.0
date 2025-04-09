package DAO;

import modelo.DatabaseConnector;
import modelo.Permiso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones relacionadas con permisos
 */
public class PermisoDAO implements DAO<Permiso, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(PermisoDAO.class);
    
    @Override
    public Permiso crear(Permiso permiso) throws Exception {
        String sql = "INSERT INTO permisos (codigo, nombre, descripcion, modulo) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, permiso.getCodigo());
            stmt.setString(2, permiso.getNombre());
            stmt.setString(3, permiso.getDescripcion());
            stmt.setString(4, permiso.getModulo());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La creación del permiso falló, no se insertaron filas");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    permiso.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La creación del permiso falló, no se obtuvo el ID");
                }
            }
            
            logger.info("Permiso creado con ID: {}", permiso.getId());
            return permiso;
        } catch (SQLException e) {
            logger.error("Error al crear permiso: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Permiso buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM permisos WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPermiso(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar permiso por ID: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Permiso> listarTodos() throws Exception {
        String sql = "SELECT * FROM permisos ORDER BY modulo, nombre";
        List<Permiso> permisos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                permisos.add(mapearPermiso(rs));
            }
            
            return permisos;
        } catch (SQLException e) {
            logger.error("Error al listar permisos: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Permiso actualizar(Permiso permiso) throws Exception {
        String sql = "UPDATE permisos SET codigo = ?, nombre = ?, descripcion = ?, modulo = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, permiso.getCodigo());
            stmt.setString(2, permiso.getNombre());
            stmt.setString(3, permiso.getDescripcion());
            stmt.setString(4, permiso.getModulo());
            stmt.setInt(5, permiso.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La actualización del permiso falló, permiso no encontrado");
            }
            
            logger.info("Permiso actualizado con ID: {}", permiso.getId());
            return permiso;
        } catch (SQLException e) {
            logger.error("Error al actualizar permiso: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean eliminar(Integer id) throws Exception {
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Verificar si el permiso está asignado a algún rol
            if (estaAsignadoARol(conn, id)) {
                throw new SQLException("No se puede eliminar el permiso porque está asignado a uno o más roles");
            }
            
            // Eliminar el permiso
            String sql = "DELETE FROM permisos WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    return false;
                }
                
                // Commit de la transacción
                conn.commit();
                
                logger.info("Permiso eliminado con ID: {}", id);
                return true;
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
            
            logger.error("Error al eliminar permiso: {}", e.getMessage());
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
     * Verifica si un permiso está asignado a algún rol
     * @param conn Conexión activa a la base de datos
     * @param idPermiso ID del permiso
     * @return true si el permiso está asignado a algún rol
     * @throws SQLException
     */
    private boolean estaAsignadoARol(Connection conn, int idPermiso) throws SQLException {
        String sql = "SELECT COUNT(*) AS cantidad FROM roles_permisos WHERE id_permiso = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPermiso);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cantidad") > 0;
                } else {
                    return false;
                }
            }
        }
    }
    
    /**
     * Buscar un permiso por código
     * @param codigo Código del permiso
     * @return Permiso encontrado o null si no existe
     * @throws Exception
     */
    public Permiso buscarPorCodigo(String codigo) throws Exception {
        String sql = "SELECT * FROM permisos WHERE codigo = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, codigo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPermiso(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar permiso por código: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Listar permisos por módulo
     * @param modulo Módulo de los permisos
     * @return Lista de permisos del módulo
     * @throws Exception
     */
    public List<Permiso> listarPorModulo(String modulo) throws Exception {
        String sql = "SELECT * FROM permisos WHERE modulo = ? ORDER BY nombre";
        List<Permiso> permisos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, modulo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permisos.add(mapearPermiso(rs));
                }
            }
            
            return permisos;
        } catch (SQLException e) {
            logger.error("Error al listar permisos por módulo: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Obtener todos los módulos disponibles
     * @return Lista de nombres de módulos
     * @throws Exception
     */
    public List<String> listarModulos() throws Exception {
        String sql = "SELECT DISTINCT modulo FROM permisos ORDER BY modulo";
        List<String> modulos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                modulos.add(rs.getString("modulo"));
            }
            
            return modulos;
        } catch (SQLException e) {
            logger.error("Error al listar módulos: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto Permiso
     * @param rs ResultSet con los datos
     * @return objeto Permiso
     * @throws SQLException
     */
    public Permiso mapearPermiso(ResultSet rs) throws SQLException {
        Permiso permiso = new Permiso();
        permiso.setId(rs.getInt("id"));
        permiso.setCodigo(rs.getString("codigo"));
        permiso.setNombre(rs.getString("nombre"));
        permiso.setDescripcion(rs.getString("descripcion"));
        permiso.setModulo(rs.getString("modulo"));
        return permiso;
    }
}
