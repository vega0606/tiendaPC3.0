package controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import modelo.Cliente;

/**
 * Controlador para la lógica de negocio de clientes.
 */
public class ClienteController {
    
    // Base de datos en memoria para pruebas
    private static List<Cliente> clientesDB = new ArrayList<>();
    private static int nextId = 1;
    
    /**
     * Constructor del controlador.
     */
    public ClienteController() {
        // Si la base de datos está vacía, inicializar con algunos datos
        if (clientesDB.isEmpty()) {
            // Crear algunos clientes de ejemplo
            Cliente cliente1 = new Cliente();
            cliente1.setId("1");
            cliente1.setNombre("Juan Pérez");
            cliente1.setRuc("10101010101");
            cliente1.setTelefono("555-1234");
            cliente1.setEmail("juan@ejemplo.com");
            cliente1.setDireccion("Av. Principal 123");
            clientesDB.add(cliente1);
            
            Cliente cliente2 = new Cliente();
            cliente2.setId("2");
            cliente2.setNombre("María García");
            cliente2.setRuc("20202020202");
            cliente2.setTelefono("555-5678");
            cliente2.setEmail("maria@ejemplo.com");
            cliente2.setDireccion("Calle Secundaria 456");
            clientesDB.add(cliente2);
            
            nextId = 3;
        }
    }
    
    /**
     * Obtiene todos los clientes.
     * 
     * @return Lista de todos los clientes
     */
    public List<Cliente> obtenerTodosLosClientes() {
        return new ArrayList<>(clientesDB);
    }
    
    /**
     * Busca clientes por nombre.
     * 
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de clientes que coinciden con el criterio
     */
    public List<Cliente> buscarClientesPorNombre(String nombre) {
        return clientesDB.stream()
            .filter(c -> c.getNombre().toLowerCase().contains(nombre.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    /**
     * Busca clientes por RUC/NIT.
     * 
     * @param ruc RUC/NIT a buscar
     * @return Lista de clientes que coinciden con el criterio
     */
    public List<Cliente> buscarClientesPorRuc(String ruc) {
        return clientesDB.stream()
            .filter(c -> c.getRuc().contains(ruc))
            .collect(Collectors.toList());
    }
    
    /**
     * Busca clientes por email.
     * 
     * @param email Email a buscar
     * @return Lista de clientes que coinciden con el criterio
     */
    public List<Cliente> buscarClientesPorEmail(String email) {
        return clientesDB.stream()
            .filter(c -> c.getEmail().toLowerCase().contains(email.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    /**
     * Busca clientes por cualquier criterio (nombre, RUC, email, teléfono o dirección).
     * 
     * @param criterio Texto a buscar
     * @return Lista de clientes que coinciden con el criterio
     */
    public List<Cliente> buscarClientes(String criterio) {
        String criterioBusqueda = criterio.toLowerCase();
        
        return clientesDB.stream()
            .filter(c -> 
                c.getNombre().toLowerCase().contains(criterioBusqueda) ||
                c.getRuc().toLowerCase().contains(criterioBusqueda) ||
                c.getEmail().toLowerCase().contains(criterioBusqueda) ||
                c.getTelefono().toLowerCase().contains(criterioBusqueda) ||
                c.getDireccion().toLowerCase().contains(criterioBusqueda)
            )
            .collect(Collectors.toList());
    }
    
    /**
     * Agrega un nuevo cliente.
     * 
     * @param cliente Cliente a agregar
     * @return true si se agregó correctamente, false en caso contrario
     */
    public boolean agregarCliente(Cliente cliente) {
        try {
            // Asignar ID si es nuevo
            if (cliente.getId() == null || cliente.getId().isEmpty()) {
                cliente.setId(String.valueOf(nextId++));
            }
            
            // Verificar que no exista un cliente con el mismo RUC
            boolean existeRuc = clientesDB.stream()
                .anyMatch(c -> c.getRuc().equals(cliente.getRuc()) && !c.getId().equals(cliente.getId()));
            
            if (existeRuc) {
                System.err.println("Ya existe un cliente con el mismo RUC/NIT");
                return false;
            }
            
            // Agregar a la lista
            clientesDB.add(cliente);
            return true;
        } catch (Exception e) {
            System.err.println("Error al agregar cliente: " + e.getMessage());
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
            // Buscar cliente por ID
            int index = -1;
            for (int i = 0; i < clientesDB.size(); i++) {
                if (clientesDB.get(i).getId().equals(cliente.getId())) {
                    index = i;
                    break;
                }
            }
            
            if (index == -1) {
                System.err.println("Cliente no encontrado para actualizar");
                return false;
            }
            
            // Verificar RUC único
            boolean existeRuc = clientesDB.stream()
                .anyMatch(c -> c.getRuc().equals(cliente.getRuc()) && !c.getId().equals(cliente.getId()));
            
            if (existeRuc) {
                System.err.println("Ya existe otro cliente con el mismo RUC/NIT");
                return false;
            }
            
            // Actualizar
            clientesDB.set(index, cliente);
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
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
            return clientesDB.removeIf(c -> c.getId().equals(id));
        } catch (Exception e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
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
        // En una implementación real, aquí iría el código para generar el PDF
        System.out.println("Exportando a PDF: " + rutaArchivo);
        return true;
    }
    
    /**
     * Exporta la lista de clientes a un archivo Excel.
     * 
     * @param clientes Lista de clientes a exportar
     * @param rutaArchivo Ruta donde guardar el archivo
     * @return true si se exportó correctamente, false en caso contrario
     */
    public boolean exportarAExcel(List<Cliente> clientes, String rutaArchivo) {
        // En una implementación real, aquí iría el código para generar el Excel
        System.out.println("Exportando a Excel: " + rutaArchivo);
        return true;
    }
    
    public Cliente crearCliente(String nombre, String telefono, String email, String direccion, String ruc) {
        // Verificamos si ya existe un cliente con ese RUC
        boolean existeRuc = clientesDB.stream()
            .anyMatch(c -> c.getRuc().equals(ruc));

        if (existeRuc) {
            System.err.println("Ya existe un cliente con ese RUC.");
            return null;
        }

        Cliente cliente = new Cliente();
        cliente.setId(String.valueOf(nextId++));
        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        cliente.setDireccion(direccion);
        cliente.setRuc(ruc);

        clientesDB.add(cliente);
        return cliente;
    }
    
    public Cliente buscarClientePorRuc(String ruc) {
        return clientesDB.stream()
            .filter(c -> c.getRuc().equals(ruc))
            .findFirst()
            .orElse(null);
    }
    
}