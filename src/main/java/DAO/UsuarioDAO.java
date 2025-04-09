package DAO;

import modelo.DatabaseConnector;
import Modelo.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones relacionadas con usuarios
 */
public class UsuarioDAO implements DAO<Usuario, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioDAO.class);
    
    @Override
    public Usuario crear(Usuario usuario) throws Exception {
        String sql = "INSERT INTO usuarios (nombre, username, password, email, rol, activo) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getUsername());
            stmt.setString(3, usuario.getPassword());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getRol());
            stmt.setBoolean(6, usuario.isActivo());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La creación del usuario falló, no se insertaron filas");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La creación del usuario falló, no se obtuvo el ID");
                }
            }
            
            logger.info("Usuario creado con ID: {}", usuario.getId());
            return usuario;
        } catch (SQLException e) {
            logger.error("Error al crear usuario: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Usuario buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar usuario por ID: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Usuario> listarTodos() throws Exception {
        String sql = "SELECT * FROM usuarios ORDER BY nombre";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
            
            return usuarios;
        } catch (SQLException e) {
            logger.error("Error al listar usuarios: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Usuario actualizar(Usuario usuario) throws Exception {
        String sql = "UPDATE usuarios SET nombre = ?, username = ?, password = ?, email = ?, rol = ?, activo = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getUsername());
            stmt.setString(3, usuario.getPassword());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getRol());
            stmt.setBoolean(6, usuario.isActivo());
            stmt.setInt(7, usuario.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La actualización del usuario falló, usuario no encontrado");
            }
            
            logger.info("Usuario actualizado con ID: {}", usuario.getId());
            return usuario;
        } catch (SQLException e) {
            logger.error("Error al actualizar usuario: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean eliminar(Integer id) throws Exception {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Usuario eliminado con ID: {}", id);
            return true;
        } catch (SQLException e) {
            logger.error("Error al eliminar usuario: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar usuario por nombre de usuario
     * @param username nombre de usuario
     * @return usuario encontrado o null si no existe
     * @throws Exception
     */
    public Usuario buscarPorUsername(String username) throws Exception {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar usuario por username: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar usuarios por rol
     * @param rol rol de usuario
     * @return lista de usuarios con el rol especificado
     * @throws Exception
     */
    public List<Usuario> buscarPorRol(String rol) throws Exception {
        String sql = "SELECT * FROM usuarios WHERE rol = ? ORDER BY nombre";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rol);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));
                }
            }
            
            return usuarios;
        } catch (SQLException e) {
            logger.error("Error al buscar usuarios por rol: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Actualizar la fecha de último acceso del usuario
     * @param id ID del usuario
     * @param fechaAcceso fecha de último acceso
     * @throws Exception
     */
    public void actualizarUltimoAcceso(int id, LocalDateTime fechaAcceso) throws Exception {
        String sql = "UPDATE usuarios SET ultimo_acceso = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(fechaAcceso));
            stmt.setInt(2, id);
            
            stmt.executeUpdate();
            
            logger.info("Actualizado último acceso del usuario ID: {}", id);
        } catch (SQLException e) {
            logger.error("Error al actualizar último acceso: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto Usuario
     * @param rs ResultSet con los datos
     * @return objeto Usuario
     * @throws SQLException
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPassword(rs.getString("password"));
        usuario.setEmail(rs.getString("email"));
        usuario.setRol(rs.getString("rol"));
        usuario.setActivo(rs.getBoolean("activo"));
        
        Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
        if (ultimoAcceso != null) {
            usuario.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
        }
        
        return usuario;
    }
}
