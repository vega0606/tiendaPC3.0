package DAO;

import modelo.DatabaseConnector;
import modelo.Proveedor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones relacionadas con proveedores
 */
public class ProveedorDAO implements DAO<Proveedor, String> {
    private static final Logger logger = LoggerFactory.getLogger(ProveedorDAO.class);
    
    @Override
    public Proveedor crear(Proveedor proveedor) throws Exception {
        String sql = "INSERT INTO proveedores (id, empresa, contacto, email, telefono, direccion, ruc, " +
                     "categoria, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, proveedor.getId());
            stmt.setString(2, proveedor.getEmpresa());
            stmt.setString(3, proveedor.getContacto());
            stmt.setString(4, proveedor.getEmail());
            stmt.setString(5, proveedor.getTelefono());
            stmt.setString(6, proveedor.getDireccion());
            stmt.setString(7, proveedor.getRuc());
            stmt.setString(8, proveedor.getCategoria());
            stmt.setString(9, proveedor.getEstado());
            stmt.setDate(10, Date.valueOf(proveedor.getFechaRegistro() != null ? 
                                       proveedor.getFechaRegistro() : 
                                       LocalDate.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La creación del proveedor falló, no se insertaron filas");
            }
            
            logger.info("Proveedor creado con ID: {}", proveedor.getId());
            return proveedor;
        } catch (SQLException e) {
            logger.error("Error al crear proveedor: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Proveedor buscarPorId(String id) throws Exception {
        String sql = "SELECT * FROM proveedores WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProveedor(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar proveedor por ID: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Proveedor> listarTodos() throws Exception {
        String sql = "SELECT * FROM proveedores ORDER BY empresa";
        List<Proveedor> proveedores = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                proveedores.add(mapearProveedor(rs));
            }
            
            return proveedores;
        } catch (SQLException e) {
            logger.error("Error al listar proveedores: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Proveedor actualizar(Proveedor proveedor) throws Exception {
        String sql = "UPDATE proveedores SET empresa = ?, contacto = ?, email = ?, telefono = ?, " +
                     "direccion = ?, ruc = ?, categoria = ?, estado = ?, saldo_pendiente = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, proveedor.getEmpresa());
            stmt.setString(2, proveedor.getContacto());
            stmt.setString(3, proveedor.getEmail());
            stmt.setString(4, proveedor.getTelefono());
            stmt.setString(5, proveedor.getDireccion());
            stmt.setString(6, proveedor.getRuc());
            stmt.setString(7, proveedor.getCategoria());
            stmt.setString(8, proveedor.getEstado());
            stmt.setDouble(9, proveedor.getSaldoPendiente());
            stmt.setString(10, proveedor.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La actualización del proveedor falló, proveedor no encontrado");
            }
            
            logger.info("Proveedor actualizado con ID: {}", proveedor.getId());
            return proveedor;
        } catch (SQLException e) {
            logger.error("Error al actualizar proveedor: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean eliminar(String id) throws Exception {
        String sql = "DELETE FROM proveedores WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Proveedor eliminado con ID: {}", id);
            return true;
        } catch (SQLException e) {
            logger.error("Error al eliminar proveedor: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar proveedores por nombre de empresa o contacto
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de proveedores que coinciden con la búsqueda
     * @throws Exception
     */
    public List<Proveedor> buscarPorNombre(String nombre) throws Exception {
        String sql = "SELECT * FROM proveedores WHERE empresa LIKE ? OR contacto LIKE ? ORDER BY empresa";
        List<Proveedor> proveedores = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + nombre + "%");
            stmt.setString(2, "%" + nombre + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    proveedores.add(mapearProveedor(rs));
                }
            }
            
            return proveedores;
        } catch (SQLException e) {
            logger.error("Error al buscar proveedores por nombre: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar proveedores por RUC
     * @param ruc RUC del proveedor
     * @return Proveedor que coincide con el RUC o null si no existe
     * @throws Exception
     */
    public Proveedor buscarPorRuc(String ruc) throws Exception {
        String sql = "SELECT * FROM proveedores WHERE ruc = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ruc);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProveedor(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar proveedor por RUC: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar proveedores por categoría
     * @param categoria Categoría de los proveedores
     * @return Lista de proveedores de la categoría especificada
     * @throws Exception
     */
    public List<Proveedor> buscarPorCategoria(String categoria) throws Exception {
        String sql = "SELECT * FROM proveedores WHERE categoria = ? ORDER BY empresa";
        List<Proveedor> proveedores = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    proveedores.add(mapearProveedor(rs));
                }
            }
            
            return proveedores;
        } catch (SQLException e) {
            logger.error("Error al buscar proveedores por categoría: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Actualizar el saldo pendiente de un proveedor
     * @param id ID del proveedor
     * @param monto Monto a añadir (positivo) o restar (negativo)
     * @return true si la actualización fue exitosa
     * @throws Exception
     */
    public boolean actualizarSaldoPendiente(String id, double monto) throws Exception {
        String sql = "UPDATE proveedores SET saldo_pendiente = saldo_pendiente + ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, monto);
            stmt.setString(2, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Saldo pendiente actualizado para proveedor ID: {}, monto: {}", id, monto);
            return true;
        } catch (SQLException e) {
            logger.error("Error al actualizar saldo pendiente: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Generar un nuevo ID de proveedor basado en el último ID
     * @return Nuevo ID de proveedor
     * @throws Exception
     */
    public String generarNuevoId() throws Exception {
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 2) AS UNSIGNED)) AS ultimo_id FROM proveedores WHERE id LIKE 'P%'";
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int ultimoId = rs.getInt("ultimo_id");
                return String.format("P%03d", ultimoId + 1);
            } else {
                return "P001"; // Primer proveedor
            }
        } catch (SQLException e) {
            logger.error("Error al generar nuevo ID de proveedor: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto Proveedor
     * @param rs ResultSet con los datos
     * @return objeto Proveedor
     * @throws SQLException
     */
    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(rs.getString("id"));
        proveedor.setEmpresa(rs.getString("empresa"));
        proveedor.setContacto(rs.getString("contacto"));
        proveedor.setEmail(rs.getString("email"));
        proveedor.setTelefono(rs.getString("telefono"));
        proveedor.setDireccion(rs.getString("direccion"));
        proveedor.setRuc(rs.getString("ruc"));
        proveedor.setCategoria(rs.getString("categoria"));
        proveedor.setEstado(rs.getString("estado"));
        
        Date fechaRegistro = rs.getDate("fecha_registro");
        if (fechaRegistro != null) {
            proveedor.setFechaRegistro(fechaRegistro.toLocalDate());
        }
        
        proveedor.setSaldoPendiente(rs.getDouble("saldo_pendiente"));
        
        return proveedor;
    }
    public List<Proveedor> listarPorEstado(String estado) throws Exception {
        String sql = "SELECT * FROM proveedores WHERE estado = ? ORDER BY empresa";
        List<Proveedor> proveedores = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    proveedores.add(mapearProveedor(rs));
                }
            }
            
            return proveedores;
        } catch (SQLException e) {
            logger.error("Error al listar proveedores por estado: {}", e.getMessage());
            throw e;
        }
    }
}
