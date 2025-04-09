package DAO;

import modelo.DatabaseConnector;
import Modelo.Categoria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones relacionadas con categorías de productos
 */
public class CategoriaDAO implements DAO<Categoria, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(CategoriaDAO.class);
    
    @Override
    public Categoria crear(Categoria categoria) throws Exception {
        String sql = "INSERT INTO categorias (nombre, descripcion, activo) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, categoria.getNombre());
            stmt.setString(2, categoria.getDescripcion());
            stmt.setBoolean(3, categoria.isActivo());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La creación de la categoría falló, no se insertaron filas");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categoria.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La creación de la categoría falló, no se obtuvo el ID");
                }
            }
            
            logger.info("Categoría creada con ID: {}", categoria.getId());
            return categoria;
        } catch (SQLException e) {
            logger.error("Error al crear categoría: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Categoria buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCategoria(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar categoría por ID: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Categoria> listarTodos() throws Exception {
        String sql = "SELECT * FROM categorias ORDER BY nombre";
        List<Categoria> categorias = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }
            
            return categorias;
        } catch (SQLException e) {
            logger.error("Error al listar categorías: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Categoria actualizar(Categoria categoria) throws Exception {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ?, activo = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNombre());
            stmt.setString(2, categoria.getDescripcion());
            stmt.setBoolean(3, categoria.isActivo());
            stmt.setInt(4, categoria.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La actualización de la categoría falló, categoría no encontrada");
            }
            
            logger.info("Categoría actualizada con ID: {}", categoria.getId());
            return categoria;
        } catch (SQLException e) {
            logger.error("Error al actualizar categoría: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean eliminar(Integer id) throws Exception {
        String sql = "DELETE FROM categorias WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Categoría eliminada con ID: {}", id);
            return true;
        } catch (SQLException e) {
            logger.error("Error al eliminar categoría: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar una categoría por nombre
     * @param nombre Nombre de la categoría
     * @return Categoría encontrada o null si no existe
     * @throws Exception
     */
    public Categoria buscarPorNombre(String nombre) throws Exception {
        String sql = "SELECT * FROM categorias WHERE nombre = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCategoria(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar categoría por nombre: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Listar categorías activas
     * @return Lista de categorías activas
     * @throws Exception
     */
    public List<Categoria> listarActivas() throws Exception {
        String sql = "SELECT * FROM categorias WHERE activo = 1 ORDER BY nombre";
        List<Categoria> categorias = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }
            
            return categorias;
        } catch (SQLException e) {
            logger.error("Error al listar categorías activas: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Cambiar el estado de una categoría
     * @param id ID de la categoría
     * @param activo Nuevo estado
     * @return true si el cambio fue exitoso
     * @throws Exception
     */
    public boolean cambiarEstado(int id, boolean activo) throws Exception {
        String sql = "UPDATE categorias SET activo = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, activo);
            stmt.setInt(2, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Estado de categoría {} cambiado a: {}", id, activo);
            return true;
        } catch (SQLException e) {
            logger.error("Error al cambiar estado de categoría: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Verificar si una categoría tiene productos asociados
     * @param id ID de la categoría
     * @return true si la categoría tiene productos
     * @throws Exception
     */
    public boolean tieneProductos(int id) throws Exception {
        String sql = "SELECT COUNT(*) AS cantidad FROM productos WHERE id_categoria = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cantidad") > 0;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al verificar si categoría tiene productos: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto Categoria
     * @param rs ResultSet con los datos
     * @return objeto Categoria
     * @throws SQLException
     */
    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getInt("id"));
        categoria.setNombre(rs.getString("nombre"));
        categoria.setDescripcion(rs.getString("descripcion"));
        categoria.setActivo(rs.getBoolean("activo"));
        return categoria;
    }
}
