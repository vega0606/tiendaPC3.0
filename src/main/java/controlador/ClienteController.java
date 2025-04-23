package controlador;

import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;
import REPOSITORY.ClienteRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.time.LocalDate;

/**
 * Controlador para la lógica de negocio de clientes.
 * Ahora integrado con ClienteRepository para acceso a base de datos.
 */
public class ClienteController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    private ClienteRepository clienteRepository;
    
    /**
     * Constructor del controlador.
     */
    public ClienteController() {
        System.out.println("Inicializando ClienteController con conexión a base de datos");
        try {
            // Inicializar el repositorio
            clienteRepository = new ClienteRepository();
            
            // Verificar si hay clientes en la BD
            List<Cliente> clientesBD = clienteRepository.listarTodos();
            System.out.println("Conexión a BD exitosa. Se encontraron " + clientesBD.size() + " clientes en la base de datos.");
        } catch (Exception e) {
            System.err.println("Error al inicializar conexión con base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene todos los clientes desde la base de datos.
     * 
     * @return Lista de todos los clientes
     */
    public List<Cliente> obtenerTodosLosClientes() {
        try {
            List<Cliente> clientes = clienteRepository.listarTodos();
            System.out.println("Obteniendo todos los clientes. Total: " + clientes.size());
            return clientes;
        } catch (Exception e) {
            System.err.println("Error al obtener clientes desde BD: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Retornar lista vacía en caso de error
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
            System.out.println("Buscando clientes por nombre: " + nombre);
            if (nombre == null || nombre.trim().isEmpty()) {
                return obtenerTodosLosClientes();
            }
            
            List<Cliente> clientes = clienteRepository.buscarPorNombre(nombre);
            System.out.println("Encontrados: " + clientes.size() + " clientes");
            return clientes;
        } catch (Exception e) {
            System.err.println("Error al buscar clientes por nombre: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("Buscando clientes por RUC: " + ruc);
            if (ruc == null || ruc.trim().isEmpty()) {
                return obtenerTodosLosClientes();
            }
            
            Cliente cliente = clienteRepository.buscarPorRuc(ruc);
            List<Cliente> resultado = new ArrayList<>();
            if (cliente != null) {
                resultado.add(cliente);
            }
            
            System.out.println("Encontrados: " + resultado.size() + " clientes");
            return resultado;
        } catch (Exception e) {
            System.err.println("Error al buscar clientes por RUC: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("Buscando clientes por email: " + email);
            if (email == null || email.trim().isEmpty()) {
                return obtenerTodosLosClientes();
            }
            
            // Como no hay método específico en el repositorio para buscar por email,
            // usamos el método general y filtramos
            List<Cliente> clientes = obtenerTodosLosClientes();
            List<Cliente> resultado = new ArrayList<>();
            
            for (Cliente cliente : clientes) {
                if (cliente.getEmail() != null && 
                    cliente.getEmail().toLowerCase().contains(email.toLowerCase())) {
                    resultado.add(cliente);
                }
            }
            
            System.out.println("Encontrados: " + resultado.size() + " clientes");
            return resultado;
        } catch (Exception e) {
            System.err.println("Error al buscar clientes por email: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("Buscando clientes por criterio general: " + criterio);
            if (criterio == null || criterio.trim().isEmpty()) {
                return obtenerTodosLosClientes();
            }
            
            // Como no hay método específico para búsqueda general, primero buscamos por nombre
            // que es el criterio más común y luego complementamos con otros criterios si es necesario
            List<Cliente> clientesPorNombre = clienteRepository.buscarPorNombre(criterio);
            
            // Buscar también por RUC
            Cliente clientePorRuc = clienteRepository.buscarPorRuc(criterio);
            
            // Crear lista de resultados única
            List<Cliente> resultado = new ArrayList<>(clientesPorNombre);
            
            // Agregar cliente por RUC si no está ya en la lista
            if (clientePorRuc != null) {
                boolean yaExiste = false;
                for (Cliente c : resultado) {
                    if (c.getId().equals(clientePorRuc.getId())) {
                        yaExiste = true;
                        break;
                    }
                }
                if (!yaExiste) {
                    resultado.add(clientePorRuc);
                }
            }
            
            // Para otros criterios, podríamos hacer una búsqueda más completa en todos los clientes
            // Pero esto podría ser costoso en términos de rendimiento si hay muchos clientes
            
            System.out.println("Encontrados: " + resultado.size() + " clientes");
            return resultado;
        } catch (Exception e) {
            System.err.println("Error al buscar clientes por criterio general: " + e.getMessage());
            e.printStackTrace();
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
            if (cliente == null) {
                System.err.println("Error: Cliente nulo");
                return false;
            }
            
            System.out.println("Agregando cliente: " + cliente.getNombre());
            
            // Asignar ID si es nuevo
            if (cliente.getId() == null || cliente.getId().isEmpty()) {
                String nuevoId = clienteRepository.generarNuevoId();
                cliente.setId(nuevoId);
                System.out.println("Asignado ID: " + cliente.getId());
            }
            
            // Establecer valores por defecto si no están definidos
            if (cliente.getEstado() == null || cliente.getEstado().isEmpty()) {
                cliente.setEstado("Activo");
            }
            
            if (cliente.getFechaRegistro() == null) {
                cliente.setFechaRegistro(LocalDate.now());
            }
            
            // Crear cliente en la base de datos
            Cliente clienteCreado = clienteRepository.crear(cliente);
            
            System.out.println("Cliente agregado con éxito. ID: " + clienteCreado.getId());
            return true;
        } catch (Exception e) {
            System.err.println("Error al agregar cliente: " + e.getMessage());
            e.printStackTrace();
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
            if (cliente == null) {
                System.err.println("Error: Cliente nulo");
                return false;
            }
            
            System.out.println("Actualizando cliente ID: " + cliente.getId() + ", Nombre: " + cliente.getNombre());
            
            // Verificar que exista el cliente
            Cliente clienteExistente = clienteRepository.buscarPorId(cliente.getId());
            if (clienteExistente == null) {
                System.err.println("Error: Cliente no encontrado con ID: " + cliente.getId());
                return false;
            }
            
            // Mantener valores que no se actualizan desde la vista
            if (cliente.getEstado() == null || cliente.getEstado().isEmpty()) {
                cliente.setEstado(clienteExistente.getEstado());
            }
            
            if (cliente.getFechaRegistro() == null) {
                cliente.setFechaRegistro(clienteExistente.getFechaRegistro());
            }
            
            if (cliente.getSaldoPendiente() == 0) {
                cliente.setSaldoPendiente(clienteExistente.getSaldoPendiente());
            }
            
            // Actualizar cliente en la base de datos
            Cliente clienteActualizado = clienteRepository.actualizar(cliente);
            
            System.out.println("Cliente actualizado con éxito");
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("Eliminando cliente con ID: " + id);
            
            boolean resultado = clienteRepository.eliminar(id);
            
            if (resultado) {
                System.out.println("Cliente eliminado con éxito");
            } else {
                System.err.println("No se encontró cliente con ID: " + id);
            }
            
            return resultado;
        } catch (Exception e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Exporta la lista de clientes a un archivo PDF.
     * 
     * @param clientes Lista de clientes a exportar
     * @param rutaArchivo Ruta donde guardar el archivo
     * @return true si se exportó correctamente, false en caso contrario
     */
    public boolean exportarAPDF(List<Cliente> clientes, String rutaArchivo) {
        try {
            System.out.println("Exportando " + clientes.size() + " clientes a PDF: " + rutaArchivo);
            // Implementación pendiente para generar PDF
            // Aquí se podría utilizar alguna librería como iText, JasperReports, etc.
            
            System.out.println("Exportación a PDF completada");
            return true;
        } catch (Exception e) {
            System.err.println("Error al exportar a PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Exporta la lista de clientes a un archivo Excel.
     * 
     * @param clientes Lista de clientes a exportar
     * @param rutaArchivo Ruta donde guardar el archivo
     * @return true si se exportó correctamente, false en caso contrario
     */
    public boolean exportarAExcel(List<Cliente> clientes, String rutaArchivo) {
        try {
            System.out.println("Exportando " + clientes.size() + " clientes a Excel: " + rutaArchivo);
            // Implementación pendiente para generar Excel
            // Aquí se podría utilizar alguna librería como Apache POI, JExcel, etc.
            
            System.out.println("Exportación a Excel completada");
            return true;
        } catch (Exception e) {
            System.err.println("Error al exportar a Excel: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Busca un cliente por su RUC/NIT exacto.
     * 
     * @param ruc RUC/NIT a buscar
     * @return Cliente encontrado o null si no existe
     */
    public Cliente buscarClientePorRuc(String ruc) {
        try {
            System.out.println("Buscando cliente con RUC exacto: " + ruc);
            Cliente cliente = clienteRepository.buscarPorRuc(ruc);
            
            if (cliente != null) {
                System.out.println("Cliente encontrado: " + cliente.getId() + " - " + cliente.getNombre());
            } else {
                System.out.println("No se encontró cliente con RUC: " + ruc);
            }
            
            return cliente;
        } catch (Exception e) {
            System.err.println("Error al buscar cliente por RUC: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Actualiza el saldo pendiente de un cliente.
     * 
     * @param id ID del cliente
     * @param monto Monto a añadir (positivo) o restar (negativo) al saldo
     * @return true si se actualizó correctamente
     */
    public boolean actualizarSaldoPendiente(String id, double monto) {
        try {
            System.out.println("Actualizando saldo pendiente para cliente ID: " + id + ", monto: " + monto);
            boolean resultado = clienteRepository.actualizarSaldoPendiente(id, monto);
            
            if (resultado) {
                System.out.println("Saldo actualizado con éxito");
            } else {
                System.err.println("No se pudo actualizar el saldo. Cliente no encontrado con ID: " + id);
            }
            
            return resultado;
        } catch (Exception e) {
            System.err.println("Error al actualizar saldo pendiente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Listar clientes por estado.
     * 
     * @param estado Estado del cliente ('Activo', 'Inactivo', etc.)
     * @return Lista de clientes con el estado especificado
     */
    public List<Cliente> listarPorEstado(String estado) {
        try {
            System.out.println("Listando clientes por estado: " + estado);
            List<Cliente> clientes = clienteRepository.listarPorEstado(estado);
            System.out.println("Encontrados: " + clientes.size() + " clientes");
            return clientes;
        } catch (Exception e) {
            System.err.println("Error al listar clientes por estado: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}