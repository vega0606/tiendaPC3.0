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
    private static boolean datosInicializados = false;
    
    /**
     * Constructor del controlador.
     */
    public ClienteController() {
        System.out.println("Inicializando ClienteController");
        // Inicializar datos de prueba
        inicializarDatosPrueba();
    }
    
    /**
     * Inicializa los datos de prueba si aún no han sido inicializados.
     */
    private void inicializarDatosPrueba() {
        // Protección para evitar inicialización múltiple
        if (datosInicializados) {
            System.out.println("Datos ya inicializados, saltando inicialización");
            return;
        }
        
        System.out.println("Inicializando datos de prueba");
        
        // Limpiar la lista existente para evitar duplicados
        clientesDB.clear();
        
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
        
        // Añadir más clientes de ejemplo
        Cliente cliente3 = new Cliente();
        cliente3.setId("3");
        cliente3.setNombre("Carlos Rodríguez");
        cliente3.setRuc("30303030303");
        cliente3.setTelefono("555-9012");
        cliente3.setEmail("carlos@ejemplo.com");
        cliente3.setDireccion("Plaza Central 789");
        clientesDB.add(cliente3);
        
        Cliente cliente4 = new Cliente();
        cliente4.setId("4");
        cliente4.setNombre("Ana Martínez");
        cliente4.setRuc("40404040404");
        cliente4.setTelefono("555-3456");
        cliente4.setEmail("ana@ejemplo.com");
        cliente4.setDireccion("Avenida Norte 321");
        clientesDB.add(cliente4);
        
        nextId = 5;
        datosInicializados = true;
        
        System.out.println("Datos de prueba inicializados: " + clientesDB.size() + " clientes");
    }
    
    /**
     * Obtiene todos los clientes.
     * 
     * @return Lista de todos los clientes
     */
    public List<Cliente> obtenerTodosLosClientes() {
        System.out.println("Obteniendo todos los clientes. Total: " + clientesDB.size());
        for (Cliente c : clientesDB) {
            System.out.println("Cliente en DB: " + c.getId() + " - " + c.getNombre());
        }
        return new ArrayList<>(clientesDB);
    }
    
    /**
     * Busca clientes por nombre.
     * 
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de clientes que coinciden con el criterio
     */
    public List<Cliente> buscarClientesPorNombre(String nombre) {
        System.out.println("Buscando clientes por nombre: " + nombre);
        if (nombre == null || nombre.trim().isEmpty()) {
            return obtenerTodosLosClientes();
        }
        
        List<Cliente> resultado = clientesDB.stream()
            .filter(c -> c.getNombre().toLowerCase().contains(nombre.toLowerCase()))
            .collect(Collectors.toList());
            
        System.out.println("Resultados encontrados: " + resultado.size());
        return resultado;
    }
    
    /**
     * Busca clientes por RUC/NIT.
     * 
     * @param ruc RUC/NIT a buscar
     * @return Lista de clientes que coinciden con el criterio
     */
    public List<Cliente> buscarClientesPorRuc(String ruc) {
        System.out.println("Buscando clientes por RUC: " + ruc);
        if (ruc == null || ruc.trim().isEmpty()) {
            return obtenerTodosLosClientes();
        }
        
        List<Cliente> resultado = clientesDB.stream()
            .filter(c -> c.getRuc().contains(ruc))
            .collect(Collectors.toList());
            
        System.out.println("Resultados encontrados: " + resultado.size());
        return resultado;
    }
    
    /**
     * Busca clientes por email.
     * 
     * @param email Email a buscar
     * @return Lista de clientes que coinciden con el criterio
     */
    public List<Cliente> buscarClientesPorEmail(String email) {
        System.out.println("Buscando clientes por email: " + email);
        if (email == null || email.trim().isEmpty()) {
            return obtenerTodosLosClientes();
        }
        
        List<Cliente> resultado = clientesDB.stream()
            .filter(c -> c.getEmail().toLowerCase().contains(email.toLowerCase()))
            .collect(Collectors.toList());
            
        System.out.println("Resultados encontrados: " + resultado.size());
        return resultado;
    }
    
    /**
     * Busca clientes por cualquier criterio (nombre, RUC, email, teléfono o dirección).
     * 
     * @param criterio Texto a buscar
     * @return Lista de clientes que coinciden con el criterio
     */
    public List<Cliente> buscarClientes(String criterio) {
        System.out.println("Buscando clientes por criterio general: " + criterio);
        if (criterio == null || criterio.trim().isEmpty()) {
            return obtenerTodosLosClientes();
        }
        
        String criterioBusqueda = criterio.toLowerCase();
        
        List<Cliente> resultado = clientesDB.stream()
            .filter(c -> 
                c.getNombre().toLowerCase().contains(criterioBusqueda) ||
                c.getRuc().toLowerCase().contains(criterioBusqueda) ||
                c.getEmail().toLowerCase().contains(criterioBusqueda) ||
                c.getTelefono().toLowerCase().contains(criterioBusqueda) ||
                c.getDireccion().toLowerCase().contains(criterioBusqueda)
            )
            .collect(Collectors.toList());
            
        System.out.println("Resultados encontrados: " + resultado.size());
        return resultado;
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
                cliente.setId(String.valueOf(nextId++));
                System.out.println("Asignado ID: " + cliente.getId());
            }
            
            // Verificar que no exista un cliente con el mismo RUC
            boolean existeRuc = clientesDB.stream()
                .anyMatch(c -> c.getRuc().equals(cliente.getRuc()) && !c.getId().equals(cliente.getId()));
            
            if (existeRuc) {
                System.err.println("Ya existe un cliente con el mismo RUC/NIT: " + cliente.getRuc());
                return false;
            }
            
            // Agregar a la lista
            clientesDB.add(cliente);
            System.out.println("Cliente agregado con éxito. Total clientes: " + clientesDB.size());
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
            
            // Buscar cliente por ID
            int index = -1;
            for (int i = 0; i < clientesDB.size(); i++) {
                if (clientesDB.get(i).getId().equals(cliente.getId())) {
                    index = i;
                    break;
                }
            }
            
            if (index == -1) {
                System.err.println("Cliente no encontrado para actualizar. ID: " + cliente.getId());
                return false;
            }
            
            // Verificar RUC único
            boolean existeRuc = clientesDB.stream()
                .anyMatch(c -> c.getRuc().equals(cliente.getRuc()) && !c.getId().equals(cliente.getId()));
            
            if (existeRuc) {
                System.err.println("Ya existe otro cliente con el mismo RUC/NIT: " + cliente.getRuc());
                return false;
            }
            
            // Actualizar
            clientesDB.set(index, cliente);
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
            int sizeBefore = clientesDB.size();
            boolean removed = clientesDB.removeIf(c -> c.getId().equals(id));
            int sizeAfter = clientesDB.size();
            
            if (removed) {
                System.out.println("Cliente eliminado con éxito. Clientes restantes: " + sizeAfter);
            } else {
                System.err.println("No se encontró el cliente con ID: " + id);
            }
            
            return removed;
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
        // En una implementación real, aquí iría el código para generar el PDF
        System.out.println("Exportando " + clientes.size() + " clientes a PDF: " + rutaArchivo);
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
        System.out.println("Exportando " + clientes.size() + " clientes a Excel: " + rutaArchivo);
        return true;
    }
    
    /**
     * Crea un nuevo cliente con los datos especificados.
     * 
     * @param nombre Nombre del cliente
     * @param telefono Teléfono del cliente
     * @param email Email del cliente
     * @param direccion Dirección del cliente
     * @param ruc RUC/NIT del cliente
     * @return El cliente creado o null si hubo un error
     */
    public Cliente crearCliente(String nombre, String telefono, String email, String direccion, String ruc) {
        // Verificamos si ya existe un cliente con ese RUC
        boolean existeRuc = clientesDB.stream()
            .anyMatch(c -> c.getRuc().equals(ruc));

        if (existeRuc) {
            System.err.println("Ya existe un cliente con ese RUC: " + ruc);
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
        System.out.println("Cliente creado con éxito. ID: " + cliente.getId() + ", Nombre: " + cliente.getNombre());
        return cliente;
    }
    
    /**
     * Busca un cliente por su RUC/NIT exacto.
     * 
     * @param ruc RUC/NIT a buscar
     * @return Cliente encontrado o null si no existe
     */
    public Cliente buscarClientePorRuc(String ruc) {
        System.out.println("Buscando cliente con RUC exacto: " + ruc);
        Cliente result = clientesDB.stream()
            .filter(c -> c.getRuc().equals(ruc))
            .findFirst()
            .orElse(null);
            
        if (result != null) {
            System.out.println("Cliente encontrado: " + result.getId() + " - " + result.getNombre());
        } else {
            System.out.println("No se encontró cliente con RUC: " + ruc);
        }
        
        return result;
    }
    
    /**
     * Permite restablecer los datos de prueba (útil para pruebas).
     */
    public void reiniciarDatosPrueba() {
        System.out.println("Reiniciando datos de prueba");
        datosInicializados = false;
        inicializarDatosPrueba();
        System.out.println("Datos de prueba reiniciados exitosamente");
    }
    
    /**
     * Obtiene el número de clientes en la base de datos.
     * 
     * @return Cantidad de clientes
     */
    public int contarClientes() {
        return clientesDB.size();
    }
}