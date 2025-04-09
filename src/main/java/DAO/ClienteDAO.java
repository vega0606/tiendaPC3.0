package DAO;

import modelo.DatabaseConnector;
import Modelo.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones relacionadas con clientes
 */
public class ClienteDAO implements DAO<Cliente, String> {
    private static final Logger logger = LoggerFactory.getLogger(ClienteDAO.class);
    
    @Override
    public Cliente crear(Cliente cliente) throws Exception {
        String sql = "INSERT INTO clientes (id, nombre, email, telefono, direccion, ruc, estado, fecha_registro) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cliente.getId());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefono());
            stmt.setString(5, cliente.getDireccion());
            stmt.setString(6, cliente.getRuc());
            stmt.setString(7, cliente.getEstado());
            stmt.setDate(8, Date.valueOf(cliente.getFechaRegistro() != null ? cliente.getFechaRegistro() : LocalDate.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La creación del cliente falló, no se insertaron filas");
            }
            
            logger.info("Cliente creado con ID: {}", cliente.getId());
            return cliente;
        } catch (SQLException e) {
            logger.error("Error al crear cliente: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Cliente buscarPorId(String id) throws Exception {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar cliente por ID: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Cliente> listarTodos() throws Exception {
        String sql = "SELECT * FROM clientes ORDER BY nombre";
        List<Cliente> clientes = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
            
            return clientes;
        } catch (SQLException e) {
            logger.error("Error al listar clientes: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Cliente actualizar(Cliente cliente) throws Exception {
        String sql = "UPDATE clientes SET nombre = ?, email = ?, telefono = ?, direccion = ?, " +
                     "ruc = ?, estado = ?, saldo_pendiente = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getDireccion());
            stmt.setString(5, cliente.getRuc());
            stmt.setString(6, cliente.getEstado());
            stmt.setDouble(7, cliente.getSaldoPendiente());
            stmt.setString(8, cliente.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La actualización del cliente falló, cliente no encontrado");
            }
            
            logger.info("Cliente actualizado con ID: {}", cliente.getId());
            return cliente;
        } catch (SQLException e) {
            logger.error("Error al actualizar cliente: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean eliminar(String id) throws Exception {
        String sql = "DELETE FROM clientes WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Cliente eliminado con ID: {}", id);
            return true;
        } catch (SQLException e) {
            logger.error("Error al eliminar cliente: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar clientes por nombre o parte del nombre
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de clientes que coinciden con la búsqueda
     * @throws Exception
     */
    public List<Cliente> buscarPorNombre(String nombre) throws Exception {
        String sql = "SELECT * FROM clientes WHERE nombre LIKE ? ORDER BY nombre";
        List<Cliente> clientes = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + nombre + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapearCliente(rs));
                }
            }
            
            return clientes;
        } catch (SQLException e) {
            logger.error("Error al buscar clientes por nombre: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Buscar clientes por RUC/NIT
     * @param ruc RUC/NIT del cliente
     * @return Cliente que coincide con el RUC/NIT o null si no existe
     * @throws Exception
     */
    public Cliente buscarPorRuc(String ruc) throws Exception {
        String sql = "SELECT * FROM clientes WHERE ruc = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ruc);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar cliente por RUC: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Actualizar el saldo pendiente de un cliente
     * @param id ID del cliente
     * @param monto Monto a añadir (positivo) o restar (negativo) al saldo
     * @return true si la operación fue exitosa
     * @throws Exception
     */
    public boolean actualizarSaldoPendiente(String id, double monto) throws Exception {
        String sql = "UPDATE clientes SET saldo_pendiente = saldo_pendiente + ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, monto);
            stmt.setString(2, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            logger.info("Saldo pendiente actualizado para cliente ID: {}, monto: {}", id, monto);
            return true;
        } catch (SQLException e) {
            logger.error("Error al actualizar saldo pendiente: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Listar clientes por estado
     * @param estado Estado del cliente ('Activo', 'Inactivo', etc.)
     * @return Lista de clientes con el estado especificado
     * @throws Exception
     */
    public List<Cliente> listarPorEstado(String estado) throws Exception {
        String sql = "SELECT * FROM clientes WHERE estado = ? ORDER BY nombre";
        List<Cliente> clientes = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapearCliente(rs));
                }
            }
            
            return clientes;
        } catch (SQLException e) {
            logger.error("Error al listar clientes por estado: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Mapear un registro de la base de datos a un objeto Cliente
     * @param rs ResultSet con los datos
     * @return objeto Cliente
     * @throws SQLException
     */
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getString("id"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setEmail(rs.getString("email"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setRuc(rs.getString("ruc"));
        cliente.setEstado(rs.getString("estado"));
        
        Date fechaRegistro = rs.getDate("fecha_registro");
        if (fechaRegistro != null) {
            cliente.setFechaRegistro(fechaRegistro.toLocalDate());
        }
        
        cliente.setSaldoPendiente(rs.getDouble("saldo_pendiente"));
        
        return cliente;
    }
    
    /**
     * Generar un nuevo ID de cliente basado en el último ID
     * @return Nuevo ID de cliente
     * @throws Exception
     */
    public String generarNuevoId() throws Exception {
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 2) AS UNSIGNED)) AS ultimo_id FROM clientes WHERE id LIKE 'C%'";
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int ultimoId = rs.getInt("ultimo_id");
                return String.format("C%03d", ultimoId + 1);
            } else {
                return "C001"; // Primer cliente
            }
        } catch (SQLException e) {
            logger.error("Error al generar nuevo ID de cliente: {}", e.getMessage());
            throw e;
        }
    }
}
