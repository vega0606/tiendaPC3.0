package DAO;

import modelo.DatabaseConnector;
import Modelo.Rol;
import Modelo.Permiso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones relacionadas con roles de usuario
 */
public class RolDAO implements DAO<Rol, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(RolDAO.class);
    private PermisoDAO permisoDAO;
    
    public RolDAO() {
        this.permisoDAO = new PermisoDAO();
    }
    
    @Override
    public Rol crear(Rol rol) throws Exception {
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Insertar el rol
            String sql = "INSERT INTO roles (nombre, descripcion) VALUES (?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, rol.getNombre());
                stmt.setString(2, rol.getDescripcion());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("La creación del rol falló, no se insertaron filas");
                }
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        rol.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("La creación del rol falló, no se obtuvo el ID");
                    }
                }
                
                // Insertar permisos del rol
                asignarPermisos(conn, rol.getId(), rol.getPermisos());
                
                // Commit de la transacción
                conn.commit();
                
                logger.info("Rol creado con ID: {}", rol.getId());
                return rol;
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
            
            logger.error("Error al crear rol: {}", e.getMessage());
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
     * Asigna permisos a un rol
     * @param conn Conexión activa a la base de datos
     * @param idRol ID del rol
     * @param permisos Lista de permisos a asignar
     * @throws SQLException
     */
    private void asignarPermisos(Connection conn, int idRol, List<Permiso> permisos) throws SQLException {
        if (permisos == null || permisos.isEmpty()) {
            return;
        }
        
        String sql = "INSERT INTO roles_permisos (id_rol, id_permiso) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Permiso permiso : permisos) {
                stmt.setInt(1, idRol);
                stmt.setInt(2, permiso.getId());
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
    
    @Override
    public Rol buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM roles WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Rol rol = mapearRol(rs);
                    
                    // Cargar permisos del rol
                    rol.setPermisos(buscarPermisosPorRol(id));
                    
                    return rol;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar rol por ID: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Busca los permisos asignados a un rol
     * @param idRol ID del rol
     * @return Lista de permisos del rol
     * @throws Exception
     */
    private List<Permiso> buscarPermisosPorRol(int idRol) throws Exception {
        String sql = "SELECT p.* FROM permisos p " +
                     "JOIN roles_permisos rp ON p.id = rp.id_permiso " +
                     "WHERE rp.id_rol = ?";
        List<Permiso> permisos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idRol);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permisos.add(permisoDAO.mapearPermiso(rs));
                }
            }
            
            return permisos;
        } catch (SQLException e) {
            logger.error("Error al buscar permisos por rol: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Rol> listarTodos() throws Exception {
        String sql = "SELECT * FROM roles ORDER BY nombre";
        List<Rol> roles = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Rol rol = mapearRol(rs);
                rol.setPermisos(buscarPermisosPorRol(rol.getId()));
                roles.add(rol);
            }
            
            return roles;
        } catch (SQLException e) {
            logger.error("Error al listar roles: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Rol actualizar(Rol rol) throws Exception {
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Actualizar datos del rol
            String sql = "UPDATE roles SET nombre = ?, descripcion = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, rol.getNombre());
                stmt.setString(2, rol.getDescripcion());
                stmt.setInt(3, rol.getId());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("La actualización del rol falló, rol no encontrado");
                }
                
                // Eliminar permisos antiguos
                eliminarPermisosRol(conn, rol.getId());
                
                // Asignar nuevos permisos
                asignarPermisos(conn, rol.getId(), rol.getPermisos());
                
                // Commit de la transacción
                conn.commit();
                
                logger.info("Rol actualizado con ID: {}", rol.getId());
                return rol;
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
            
            logger.error("Error al actualizar rol: {}", e.getMessage());
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
     * Elimina todos los permisos asignados a un rol
     * @param conn Conexión activa a la base de datos
     * @param idRol ID del rol
     * @throws SQLException
     */
    private void eliminarPermisosRol(Connection conn, int idRol) throws SQLException {
        String sql = "DELETE FROM roles_permisos WHERE id_rol = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRol);
            stmt.executeUpdate();
        }
    }
    
    @Override
    public boolean eliminar(Integer id) throws Exception {
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // Verificar si hay usuarios con este rol
            if (tieneUsuarios(conn, id)) {
                throw new SQLException("No se puede eliminar el rol porque tiene usuarios asignados");
            }
            
            // Eliminar permisos del rol
            eliminarPermisosRol(conn, id);
            
            // Eliminar el rol
            String sql = "DELETE FROM roles WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    return false;
                }
                
                // Commit de la transacción
                conn.commit();
                
                logger.info("Rol eliminado con ID: {}", id);
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
            
            logger.error("Error al eliminar rol: {}", e.getMessage());
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
     * Verifica si un rol tiene usuarios asignados
     * @param conn Conexión activa a la base de datos
     * @param idRol ID del rol
     * @return true si el rol tiene usuarios
     * @throws SQLException
     */
    private boolean tieneUsuarios(Connection conn, int idRol) throws SQLException {
        String sql = "SELECT COUNT(*) AS cantidad FROM usuarios WHERE rol_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRol);
            
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
     * Buscar un rol por nombre
     * @param nombre Nombre del rol
     * @return Rol encontrado o null si no existe
     * @throws Exception
     */
    public Rol buscarPorNombre(String nombre) throws Exception {
        String sql = "SELECT * FROM roles WHERE nombre = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Rol rol = mapearRol(rs);
                    rol.setPermisos(buscarPermisosPorRol(rol.getId()));
                    return rol;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar rol por nombre: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Obtener la cantidad de usuarios por rol
     * @return Mapa con la cantidad de usuarios por rol (clave: ID del rol, valor: cantidad)
     * @throws Exception
     */
    public List<Object[]> getUsuariosPorRol() throws Exception {
        String sql = "SELECT r.id, r.nombre, COUNT(u.id) AS cantidad " +
                     "FROM roles r " +
                     "LEFT JOIN usuarios u ON r.id = u.rol_id " +
                     "GROUP BY r.id, r.nombre " +
                     "ORDER BY r.nombre";
        
        List<Object[]> resultados = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] fila = new Object[3];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getInt("cantidad");
                resultados.add(fila);
            }
            
            return resultados;
        } catch (SQLException e) {
            logger.error("Error al obtener usuarios por rol: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto Rol
     * @param rs ResultSet con los datos
     * @return objeto Rol
     * @throws SQLException
     */
    private Rol mapearRol(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setId(rs.getInt("id"));
        rol.setNombre(rs.getString("nombre"));
        rol.setDescripcion(rs.getString("descripcion"));
        return rol;
    }
}
