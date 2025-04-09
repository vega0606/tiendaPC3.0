package controlador;

import modelo.Cliente;
import DAO.ClienteDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la gestión de clientes
 */
public class ClienteController {
    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    private ClienteDAO clienteDAO;
    
    public ClienteController() {
        clienteDAO = new ClienteDAO();
    }
    
    /**
     * Crea un nuevo cliente
     * @param nombre Nombre del cliente
     * @param email Email del cliente
     * @param telefono Teléfono del cliente
     * @param direccion Dirección del cliente
     * @param ruc RUC/NIT del cliente
     * @return El cliente creado o null si ocurre un error
     */
    public Cliente crearCliente(String nombre, String email, String telefono, String direccion, String ruc) {
        try {
            // Generar ID para el nuevo cliente
            String id = clienteDAO.generarNuevoId();
            
            Cliente cliente = new Cliente();
            cliente.setId(id);
            cliente.setNombre(nombre);
            cliente.setEmail(email);
            cliente.setTelefono(telefono);
            cliente.setDireccion(direccion);
            cliente.setRuc(ruc);
            cliente.setEstado("Activo");
            cliente.setFechaRegistro(LocalDate.now());
            cliente.setSaldoPendiente(0.0);
            
            return clienteDAO.crear(cliente);
        } catch (Exception e) {
            logger.error("Error al crear cliente: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene un cliente por su ID
     * @param id ID del cliente
     * @return El cliente encontrado o null si no existe
     */
    public Cliente obtenerCliente(String id) {
        try {
            return clienteDAO.buscarPorId(id);
        } catch (Exception e) {
            logger.error("Error al obtener cliente: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene todos los clientes
     * @return Lista de clientes
     */
    public List<Cliente> listarClientes() {
        try {
            return clienteDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Error al listar clientes: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Actualiza un cliente existente
     * @param cliente Cliente con los datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarCliente(Cliente cliente) {
        try {
            clienteDAO.actualizar(cliente);
            return true;
        } catch (Exception e) {
            logger.error("Error al actualizar cliente: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Elimina un cliente
     * @param id ID del cliente
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminarCliente(String id) {
        try {
            return clienteDAO.eliminar(id);
        } catch (Exception e) {
            logger.error("Error al eliminar cliente: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Busca clientes por nombre
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de clientes que coinciden con la búsqueda
     */
    public List<Cliente> buscarClientesPorNombre(String nombre) {
        try {
            return clienteDAO.buscarPorNombre(nombre);
        } catch (Exception e) {
            logger.error("Error al buscar clientes por nombre: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca un cliente por RUC/NIT
     * @param ruc RUC/NIT del cliente
     * @return Cliente encontrado o null si no existe
     */
    public Cliente buscarClientePorRuc(String ruc) {
        try {
            return clienteDAO.buscarPorRuc(ruc);
        } catch (Exception e) {
            logger.error("Error al buscar cliente por RUC: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Actualiza el saldo pendiente de un cliente
     * @param id ID del cliente
     * @param monto Monto a añadir (positivo) o restar (negativo)
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarSaldoPendiente(String id, double monto) {
        try {
            return clienteDAO.actualizarSaldoPendiente(id, monto);
        } catch (Exception e) {
            logger.error("Error al actualizar saldo pendiente: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Obtiene clientes por estado
     * @param estado Estado de los clientes ('Activo', 'Inactivo', etc.)
     * @return Lista de clientes con el estado especificado
     */
    public List<Cliente> listarClientesPorEstado(String estado) {
        try {
            return clienteDAO.listarPorEstado(estado);
        } catch (Exception e) {
            logger.error("Error al listar clientes por estado: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Verifica si existe un cliente con el ID dado
     * @param id ID a verificar
     * @return true si el cliente existe
     */
    public boolean existeCliente(String id) {
        try {
            return clienteDAO.buscarPorId(id) != null;
        } catch (Exception e) {
            logger.error("Error al verificar existencia del cliente: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Cambia el estado de un cliente
     * @param id ID del cliente
     * @param nuevoEstado Nuevo estado
     * @return true si el cambio fue exitoso
     */
    public boolean cambiarEstadoCliente(String id, String nuevoEstado) {
        try {
            Cliente cliente = clienteDAO.buscarPorId(id);
            if (cliente == null) {
                return false;
            }
            
            cliente.setEstado(nuevoEstado);
            clienteDAO.actualizar(cliente);
            return true;
        } catch (Exception e) {
            logger.error("Error al cambiar estado del cliente: {}", e.getMessage(), e);
            return false;
        }
    }
}
