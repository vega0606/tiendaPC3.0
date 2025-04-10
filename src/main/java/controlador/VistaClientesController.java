package controlador;

import modelo.Cliente;
import ventana.VistaClientes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador para la vista de clientes
 */
public class VistaClientesController {
    private static final Logger logger = LoggerFactory.getLogger(VistaClientesController.class);
    
    private VistaClientes vista;
    private ClienteController clienteController;
    private DefaultTableModel modeloTablaClientes;
    
    /**
     * Constructor del controlador
     * @param vista La vista de clientes
     * @param clienteController El controlador de clientes
     */
    public VistaClientesController(VistaClientes vista, ClienteController clienteController) {
        this.vista = vista;
        this.clienteController = clienteController;
        inicializar();
    }
    
    /**
     * Inicializa la vista
     */
    public void inicializar() {
        // Configurar las tablas
        JTable clientesTable = vista.getClientesTable();
        
        modeloTablaClientes = (DefaultTableModel) clientesTable.getModel();
        
        // Configurar eventos para clientes
        JTextField clienteSearchField = vista.getBusquedaField();
        JButton clienteSearchButton = vista.getBuscarButton();
        JButton newClientButton = vista.getAgregarButton();
        
        clienteSearchButton.addActionListener(e -> buscarClientes());
        clienteSearchField.addActionListener(e -> buscarClientes());
        newClientButton.addActionListener(e -> vista.mostrarFormularioNuevo());
        
        // Cargar datos iniciales
        cargarClientes();
    }
    
    /**
     * Carga la lista de clientes en la tabla
     */
    public void cargarClientes() {
        try {
            List<Cliente> clientes = clienteController.listarClientes();
            vista.mostrarClientes(clientes);
        } catch (Exception e) {
            logger.error("Error al cargar clientes: {}", e.getMessage(), e);
            vista.mostrarMensaje("Error al cargar clientes: " + e.getMessage());
        }
    }
    
    /**
     * Busca clientes según el criterio seleccionado y el término de búsqueda
     */
    public void buscarClientes() {
        String termino = vista.getBusquedaField().getText();
        
        if (termino == null || termino.trim().isEmpty()) {
            cargarClientes();
            return;
        }
        
        String criterio = (String) vista.getCriterioComboBox().getSelectedItem();
        List<Cliente> clientesEncontrados;
        
        try {
            if ("RUC/NIT".equals(criterio)) {
                Cliente cliente = clienteController.buscarClientePorRuc(termino);
                clientesEncontrados = List.of();
                if (cliente != null) {
                    clientesEncontrados = List.of(cliente);
                }
            } else {
                // Por defecto, buscar por nombre
                clientesEncontrados = clienteController.buscarClientesPorNombre(termino);
            }
            
            vista.mostrarClientes(clientesEncontrados);
            
            if (clientesEncontrados.isEmpty()) {
                vista.mostrarMensaje("No se encontraron clientes con ese criterio de búsqueda");
                cargarClientes();
            }
        } catch (Exception e) {
            logger.error("Error al buscar clientes: {}", e.getMessage(), e);
            vista.mostrarMensaje("Error al buscar clientes: " + e.getMessage());
        }
    }
    
    /**
     * Elimina el cliente seleccionado
     */
    public void eliminarCliente() {
        Cliente cliente = vista.obtenerClienteSeleccionado();
        
        if (cliente == null) {
            vista.mostrarMensaje("Seleccione un cliente para eliminar");
            return;
        }
        
        boolean confirmacion = vista.mostrarConfirmacion(
            "¿Está seguro de eliminar el cliente " + cliente.getNombre() + "?"
        );
        
        if (confirmacion) {
            try {
                boolean eliminado = clienteController.eliminarCliente(cliente.getId());
                
                if (eliminado) {
                    vista.mostrarMensaje("Cliente eliminado exitosamente");
                    cargarClientes();
                } else {
                    vista.mostrarMensaje("No se pudo eliminar el cliente");
                }
            } catch (Exception e) {
                logger.error("Error al eliminar cliente: {}", e.getMessage(), e);
                vista.mostrarMensaje("Error al eliminar cliente: " + e.getMessage());
            }
        }
    }
    
    /**
     * Exporta los clientes a un archivo PDF
     */
    public void exportarAPDF() {
        try {
            String rutaArchivo = vista.seleccionarRutaGuardado("PDF");
            
            if (rutaArchivo != null) {
                // Implementar exportación a PDF
                vista.mostrarMensaje("Funcionalidad de exportación a PDF no implementada");
            }
        } catch (Exception e) {
            logger.error("Error al exportar a PDF: {}", e.getMessage(), e);
            vista.mostrarMensaje("Error al exportar a PDF: " + e.getMessage());
        }
    }
    
    /**
     * Exporta los clientes a un archivo Excel
     */
    public void exportarAExcel() {
        try {
            String rutaArchivo = vista.seleccionarRutaGuardado("Excel");
            
            if (rutaArchivo != null) {
                // Implementar exportación a Excel
                vista.mostrarMensaje("Funcionalidad de exportación a Excel no implementada");
            }
        } catch (Exception e) {
            logger.error("Error al exportar a Excel: {}", e.getMessage(), e);
            vista.mostrarMensaje("Error al exportar a Excel: " + e.getMessage());
        }
    }
    
    /**
     * Guarda un cliente nuevo o actualizado
     * @param cliente El cliente a guardar
     * @param esNuevo Indica si es un nuevo cliente o una actualización
     * @return true si se guardó correctamente, false en caso contrario
     */
    public boolean guardarCliente(Cliente cliente, boolean esNuevo) {
        try {
            if (cliente == null) {
                vista.mostrarMensaje("Error: El cliente no puede ser nulo");
                return false;
            }
            
            // Validaciones básicas
            if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
                vista.mostrarMensaje("El nombre del cliente es obligatorio");
                return false;
            }
            
            boolean resultado;
            
            if (esNuevo) {
                // Crear nuevo cliente
                Cliente nuevoCliente = clienteController.crearCliente(
                    cliente.getNombre(),
                    cliente.getEmail(),
                    cliente.getTelefono(),
                    cliente.getDireccion(),
                    cliente.getRuc()
                );
                
                resultado = (nuevoCliente != null);
                
                if (resultado) {
                    vista.mostrarMensaje("Cliente creado exitosamente");
                } else {
                    vista.mostrarMensaje("Error al crear el cliente");
                }
            } else {
                // Actualizar cliente existente
                resultado = clienteController.actualizarCliente(cliente);
                
                if (resultado) {
                    vista.mostrarMensaje("Cliente actualizado exitosamente");
                } else {
                    vista.mostrarMensaje("Error al actualizar el cliente");
                }
            }
            
            if (resultado) {
                cargarClientes();
            }
            
            return resultado;
            
        } catch (Exception e) {
            logger.error("Error al guardar cliente: {}", e.getMessage(), e);
            vista.mostrarMensaje("Error al guardar cliente: " + e.getMessage());
            return false;
        }
    }
}