package controlador;

import modelo.Cliente;
import DAO.ClienteDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la lógica de negocio de clientes.
 * Esta implementación usa ClienteDAO para interactuar con la base de datos.
 */
public class ClienteController {
    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    private ClienteDAO clienteDAO;
    
    /**
     * Constructor del controlador.
     */
    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }
    
    /**
     * Obtiene todos los clientes.
     * 
     * @return Lista de todos los clientes
     */
    public List<Cliente> obtenerTodosLosClientes() {
        try {
            return clienteDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Error al obtener todos los clientes: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca clientes por nombre.
     * 
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de clientes que coinciden con el criterio
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
     * Busca clientes por RUC/NIT.
     * 
     * @param ruc RUC/NIT a buscar
     * @return Lista de clientes que coinciden con el criterio
     */
    public List<Cliente> buscarClientesPorRuc(String ruc) {
        try {
            Cliente cliente = clienteDAO.buscarPorRuc(ruc);
            List<Cliente> resultado = new ArrayList<>();
            if (cliente != null) {
                resultado.add(cliente);
            }
            return resultado;
        } catch (Exception e) {
            logger.error("Error al buscar clientes por RUC: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca clientes por email.
     * 
     * @param email Email a buscar
     * @return Lista de clientes que coinciden con el criterio
     */
    public List<Cliente> buscarClientesPorEmail(String email) {
        try {
            // Como no hay un método específico en el DAO, podemos filtrar los resultados de listarTodos
            List<Cliente> clientes = clienteDAO.listarTodos();
            List<Cliente> resultados = new ArrayList<>();
            
            for (Cliente cliente : clientes) {
                if (cliente.getEmail() != null && cliente.getEmail().toLowerCase().contains(email.toLowerCase())) {
                    resultados.add(cliente);
                }
            }
            
            return resultados;
        } catch (Exception e) {
            logger.error("Error al buscar clientes por email: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca clientes por cualquier criterio (nombre, RUC, email, teléfono o dirección).
     * 
     * @param criterio Texto a buscar
     * @return Lista de clientes que coinciden con el criterio
     */
    public List<Cliente> buscarClientes(String criterio) {
        try {
            // Como no hay un método específico en el DAO, podemos filtrar los resultados de listarTodos
            List<Cliente> clientes = clienteDAO.listarTodos();
            List<Cliente> resultados = new ArrayList<>();
            
            String criterioBusqueda = criterio.toLowerCase();
            
            for (Cliente cliente : clientes) {
                if ((cliente.getNombre() != null && cliente.getNombre().toLowerCase().contains(criterioBusqueda)) ||
                    (cliente.getRuc() != null && cliente.getRuc().toLowerCase().contains(criterioBusqueda)) ||
                    (cliente.getEmail() != null && cliente.getEmail().toLowerCase().contains(criterioBusqueda)) ||
                    (cliente.getTelefono() != null && cliente.getTelefono().toLowerCase().contains(criterioBusqueda)) ||
                    (cliente.getDireccion() != null && cliente.getDireccion().toLowerCase().contains(criterioBusqueda))) {
                    resultados.add(cliente);
                }
            }
            
            return resultados;
        } catch (Exception e) {
            logger.error("Error al buscar clientes por criterio general: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Agrega un nuevo cliente.
     * 
     * @param cliente Cliente a agregar
     * @return true si se agregó correctamente, false en caso contrario
     */
    public boolean agregarCliente(Cliente cliente) {
        try {
            logger.info("Intentando guardar cliente: {}", cliente.getNombre());
            
            // Verificar que no exista un cliente con el mismo RUC
            Cliente existente = clienteDAO.buscarPorRuc(cliente.getRuc());
            if (existente != null && !existente.getId().equals(cliente.getId())) {
                logger.warn("Ya existe un cliente con el RUC/NIT: {}", cliente.getRuc());
                return false;
            }
            
            // Asignar ID si es nuevo
            if (cliente.getId() == null || cliente.getId().isEmpty()) {
                cliente.setId(clienteDAO.generarNuevoId());
            }
            
            // Establecer fecha de registro si es null
            if (cliente.getFechaRegistro() == null) {
                cliente.setFechaRegistro(LocalDate.now());
            }
            
            // Establecer estado si es null
            if (cliente.getEstado() == null || cliente.getEstado().isEmpty()) {
                cliente.setEstado("Activo");
            }
            
            // Insertar el nuevo cliente
            clienteDAO.crear(cliente);
            logger.info("Cliente guardado. ID: {}", cliente.getId());
            
            return true;
        } catch (Exception e) {
            logger.error("Error al agregar cliente: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Actualiza un cliente existente.
     * 
     * @param cliente Cliente con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizarCliente(Cliente cliente) {
        try {
            logger.info("Intentando actualizar cliente ID: {}", cliente.getId());
            
            // Verificar que el cliente exista
            Cliente existente = clienteDAO.buscarPorId(cliente.getId());
            if (existente == null) {
                logger.warn("Cliente no encontrado para actualizar ID: {}", cliente.getId());
                return false;
            }
            
            // Verificar que no exista otro cliente con el mismo RUC
            Cliente clienteConMismoRuc = clienteDAO.buscarPorRuc(cliente.getRuc());
            if (clienteConMismoRuc != null && !clienteConMismoRuc.getId().equals(cliente.getId())) {
                logger.warn("Ya existe otro cliente con el RUC/NIT: {}", cliente.getRuc());
                return false;
            }
            
            // Mantener valores que no deben cambiar
            if (cliente.getFechaRegistro() == null) {
                cliente.setFechaRegistro(existente.getFechaRegistro());
            }
            
            // Actualizar el cliente
            clienteDAO.actualizar(cliente);
            logger.info("Cliente actualizado. ID: {}", cliente.getId());
            
            return true;
        } catch (Exception e) {
            logger.error("Error al actualizar cliente: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Elimina un cliente por su ID.
     * 
     * @param id ID del cliente a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
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
     * Busca un cliente exactamente por su RUC.
     * 
     * @param ruc RUC del cliente
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
     * Crea un nuevo cliente con los datos especificados.
     */
    public Cliente crearCliente(String nombre, String telefono, String email, String direccion, String ruc) {
        try {
            // Verificar si ya existe un cliente con ese RUC
            Cliente existente = clienteDAO.buscarPorRuc(ruc);
            if (existente != null) {
                logger.warn("Ya existe un cliente con el RUC/NIT: {}", ruc);
                return null;
            }
            
            // Crear nuevo cliente
            Cliente cliente = new Cliente();
            cliente.setId(clienteDAO.generarNuevoId());
            cliente.setNombre(nombre);
            cliente.setTelefono(telefono);
            cliente.setEmail(email);
            cliente.setDireccion(direccion);
            cliente.setRuc(ruc);
            cliente.setEstado("Activo");
            cliente.setFechaRegistro(LocalDate.now());
            cliente.setSaldoPendiente(0.0);
            
            // Guardar en la base de datos
            clienteDAO.crear(cliente);
            
            logger.info("Cliente creado. ID: {}, Nombre: {}", cliente.getId(), cliente.getNombre());
            return cliente;
        } catch (Exception e) {
            logger.error("Error al crear cliente: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Exporta la lista de clientes a un archivo PDF.
     */
    public boolean exportarAPDF(List<Cliente> clientes, String rutaArchivo) {
        // Implementación pendiente
        logger.info("Exportando a PDF: {}", rutaArchivo);
        return true;
    }
    
    /**
     * Exporta la lista de clientes a un archivo Excel.
     */
    public boolean exportarAExcel(List<Cliente> clientes, String rutaArchivo) {
        // Implementación pendiente
        logger.info("Exportando a Excel: {}", rutaArchivo);
        return true;
    }
}
    
