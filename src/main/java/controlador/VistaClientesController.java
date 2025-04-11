package controlador;

import javax.swing.*;
import java.util.List;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.sql.Connection;

import ventana.VistaClientes;
import modelo.Cliente;
import modelo.DatabaseConnector;
import DAO.ClienteDAO;

/**
 * Controlador para la vista de clientes.
 * Maneja la lógica entre la vista y el modelo de datos.
 */
public class VistaClientesController {
    
    private VistaClientes vista;
    private ClienteController clienteController;
    private ClienteDAO clienteDAO;
    
    /**
     * Constructor del controlador.
     * 
     * @param vista Vista de clientes
     * @param clienteController Controlador de la lógica de negocio de clientes
     */
    public VistaClientesController(VistaClientes vista, ClienteController clienteController) {
        this.vista = vista;
        this.clienteController = clienteController;
        this.clienteDAO = new ClienteDAO(); // Inicializar DAO directamente
        
        // Primero verificar la conexión a la base de datos
        verificarConexionBD();
        
        // Inicializar listeners adicionales
        inicializarListeners();
        
        // Cargar datos iniciales
        System.out.println("Inicializando controlador de vista de clientes");
        cargarDatos();
    }
    
    /**
     * Verifica la conexión a la base de datos
     */
    private void verificarConexionBD() {
        try {
            Connection conn = DatabaseConnector.getConnection();
            if (conn != null) {
                System.out.println("Conexión a la base de datos exitosa");
                conn.close();
            }
        } catch (Exception e) {
            System.err.println("¡Error al conectar a la base de datos! Detalles: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar un mensaje al usuario
            vista.mostrarMensaje("Error de conexión a la base de datos:\n" + e.getMessage() + 
                               "\n\nVerifique su configuración en el archivo database.properties.");
        }
    }
    
    /**
     * Carga los datos iniciales en la vista.
     */
    private void cargarDatos() {
        try {
            System.out.println("Cargando datos iniciales...");
            List<Cliente> clientes = null;
            
            // Intento 1: Usar el controlador de clientes
            try {
                clientes = clienteController.obtenerTodosLosClientes();
                System.out.println("Intento 1: Datos obtenidos a través del ClienteController");
            } catch (Exception e) {
                System.err.println("Error al obtener clientes con ClienteController: " + e.getMessage());
            }
            
            // Intento 2: Si no hay clientes o hubo error, usar DAO directamente
            if (clientes == null || clientes.isEmpty()) {
                try {
                    System.out.println("Intento 2: Utilizando ClienteDAO directamente");
                    clientes = clienteDAO.listarTodos();
                    System.out.println("Datos obtenidos a través del ClienteDAO: " + 
                                     (clientes != null ? clientes.size() : 0) + " clientes");
                } catch (Exception e) {
                    System.err.println("Error al obtener clientes con ClienteDAO: " + e.getMessage());
                }
            }
            
            // Mostrar los clientes en la vista
            if (clientes != null && !clientes.isEmpty()) {
                System.out.println("Se cargaron " + clientes.size() + " clientes");
                vista.mostrarClientes(clientes);
            } else {
                System.out.println("No se encontraron clientes en la base de datos");
                vista.mostrarMensaje("No se encontraron clientes en la base de datos.\n" +
                                   "Puede crear nuevos clientes utilizando el botón 'Nuevo Cliente'.");
            }
        } catch (Exception e) {
            System.err.println("Error general al cargar datos: " + e.getMessage());
            e.printStackTrace();
            vista.mostrarMensaje("Error al cargar datos: " + e.getMessage());
        }
    }
    
    /**
     * Inicializa listeners adicionales no configurados en la vista.
     */
    private void inicializarListeners() {
        // Doble clic en la tabla para editar
        vista.getClientesTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Cliente cliente = vista.obtenerClienteSeleccionado();
                    if (cliente != null) {
                        vista.mostrarFormularioEditar(cliente);
                    }
                }
            }
        });
    }
    
    /**
     * Busca clientes según el criterio de búsqueda.
     */
    public void buscarClientes() {
        try {
            System.out.println("Buscando clientes...");
            String criterioBusqueda = vista.getBusquedaField().getText().trim();
            String tipoCriterio = (String) vista.getCriterioComboBox().getSelectedItem();
            
            List<Cliente> resultados = null;
            
            // Si el campo de búsqueda está vacío, mostrar todos los clientes
            if (criterioBusqueda.isEmpty()) {
                System.out.println("Búsqueda vacía, mostrando todos los clientes");
                resultados = clienteDAO.listarTodos();
            } else {
                System.out.println("Buscando por criterio: " + tipoCriterio + ", valor: " + criterioBusqueda);
                
                // Buscar según criterio seleccionado
                if ("Nombre".equals(tipoCriterio)) {
                    resultados = clienteDAO.buscarPorNombre(criterioBusqueda);
                } else if ("RUC/NIT".equals(tipoCriterio)) {
                    Cliente cliente = clienteDAO.buscarPorRuc(criterioBusqueda);
                    if (cliente != null) {
                        resultados = new java.util.ArrayList<>();
                        resultados.add(cliente);
                    } else {
                        resultados = new java.util.ArrayList<>();
                    }
                } else if ("Email".equals(tipoCriterio)) {
                    // Como no hay método directo para buscar por email, hacemos filtrado manual
                    resultados = new java.util.ArrayList<>();
                    List<Cliente> todosClientes = clienteDAO.listarTodos();
                    
                    for (Cliente cliente : todosClientes) {
                        if (cliente.getEmail() != null && 
                            cliente.getEmail().toLowerCase().contains(criterioBusqueda.toLowerCase())) {
                            resultados.add(cliente);
                        }
                    }
                } else {
                    // Búsqueda general (no implementada en DAO, filtramos manualmente)
                    resultados = filtrarClientesPorCriterioGeneral(criterioBusqueda);
                }
            }
            
            // Mostrar resultados en la tabla
            if (resultados != null) {
                System.out.println("Se encontraron " + resultados.size() + " resultados");
                vista.mostrarClientes(resultados);
                
                if (resultados.isEmpty() && !criterioBusqueda.isEmpty()) {
                    vista.mostrarMensaje("No se encontraron clientes que coincidan con '" + 
                                       criterioBusqueda + "'");
                }
            } else {
                System.out.println("No se obtuvieron resultados (null)");
                vista.mostrarClientes(new java.util.ArrayList<>());
                vista.mostrarMensaje("No se encontraron resultados para la búsqueda");
            }
        } catch (Exception e) {
            System.err.println("Error al buscar clientes: " + e.getMessage());
            e.printStackTrace();
            vista.mostrarMensaje("Error al realizar la búsqueda: " + e.getMessage());
            
            // En caso de error crítico, mostrar lista vacía
            vista.mostrarClientes(new java.util.ArrayList<>());
        }
    }
    
    /**
     * Filtra clientes por un criterio general (busca en todos los campos)
     */
    private List<Cliente> filtrarClientesPorCriterioGeneral(String criterio) throws Exception {
        List<Cliente> resultados = new java.util.ArrayList<>();
        List<Cliente> todosClientes = clienteDAO.listarTodos();
        
        String criterioBusqueda = criterio.toLowerCase();
        
        for (Cliente cliente : todosClientes) {
            if ((cliente.getNombre() != null && 
                 cliente.getNombre().toLowerCase().contains(criterioBusqueda)) ||
                (cliente.getRuc() != null && 
                 cliente.getRuc().toLowerCase().contains(criterioBusqueda)) ||
                (cliente.getEmail() != null && 
                 cliente.getEmail().toLowerCase().contains(criterioBusqueda)) ||
                (cliente.getTelefono() != null && 
                 cliente.getTelefono().toLowerCase().contains(criterioBusqueda)) ||
                (cliente.getDireccion() != null && 
                 cliente.getDireccion().toLowerCase().contains(criterioBusqueda))) {
                resultados.add(cliente);
            }
        }
        
        return resultados;
    }
    
    /**
     * Guarda un cliente (nuevo o existente).
     * 
     * @param cliente El cliente a guardar
     * @param esNuevo Indica si es un cliente nuevo o existente
     * @return true si se guardó correctamente, false en caso contrario
     */
    public boolean guardarCliente(Cliente cliente, boolean esNuevo) {
        System.out.println("Guardando cliente. Es nuevo: " + esNuevo);
        try {
            // Validar datos básicos
            if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
                vista.mostrarMensaje("El nombre del cliente es obligatorio");
                return false;
            }
            
            boolean resultado = false;
            
            if (esNuevo) {
                System.out.println("Creando nuevo cliente: " + cliente.getNombre());
                
                // Configurar nuevos valores
                cliente.setId(clienteDAO.generarNuevoId());
                cliente.setEstado("Activo");
                cliente.setFechaRegistro(LocalDate.now());
                
                clienteDAO.crear(cliente);
                resultado = true;
            } else {
                System.out.println("Actualizando cliente existente: " + cliente.getNombre());
                
                // Obtener el cliente actual para preservar algunos datos
                Cliente clienteActual = clienteDAO.buscarPorId(cliente.getId());
                
                // Mantener valores que no se actualizan desde el formulario
                if (clienteActual != null) {
                    cliente.setEstado(clienteActual.getEstado());
                    cliente.setFechaRegistro(clienteActual.getFechaRegistro());
                    cliente.setSaldoPendiente(clienteActual.getSaldoPendiente());
                } else {
                    cliente.setEstado("Activo");
                    cliente.setFechaRegistro(LocalDate.now());
                }
                
                clienteDAO.actualizar(cliente);
                resultado = true;
            }
            
            if (resultado) {
                vista.mostrarMensaje("Cliente guardado con éxito");
                
                // Recargar la lista de clientes
                List<Cliente> clientes = clienteDAO.listarTodos();
                vista.mostrarClientes(clientes);
            } else {
                vista.mostrarMensaje("No se pudo guardar el cliente. Verifique los datos e intente nuevamente.");
            }
            
            return resultado;
        } catch (Exception e) {
            System.err.println("Error al guardar cliente: " + e.getMessage());
            e.printStackTrace();
            vista.mostrarMensaje("Error al guardar cliente: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina el cliente seleccionado.
     */
    public boolean eliminarCliente() {
        try {
            Cliente cliente = vista.obtenerClienteSeleccionado();
            
            if (cliente == null) {
                vista.mostrarMensaje("Debe seleccionar un cliente para eliminar");
                return false;
            }
            
            boolean confirmar = vista.mostrarConfirmacion(
                "¿Está seguro de que desea eliminar el cliente \"" + cliente.getNombre() + "\"?"
            );
            
            if (confirmar) {
                boolean resultado = clienteDAO.eliminar(cliente.getId());
                
                if (resultado) {
                    vista.mostrarMensaje("Cliente eliminado con éxito");
                    
                    // Actualizar tabla
                    List<Cliente> clientes = clienteDAO.listarTodos();
                    vista.mostrarClientes(clientes);
                    return true;
                } else {
                    vista.mostrarMensaje("No se pudo eliminar el cliente");
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            e.printStackTrace();
            vista.mostrarMensaje("Error al eliminar cliente: " + e.getMessage());
        }
        return false;
    }
}