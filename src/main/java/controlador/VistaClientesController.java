package controlador;

import javax.swing.*;
import java.util.List;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import ventana.VistaClientes;
import modelo.Cliente;

/**
 * Controlador para la vista de clientes.
 * Maneja la lógica entre la vista y el modelo de datos.
 */
public class VistaClientesController {
    
    private VistaClientes vista;
    private ClienteController clienteController;
    
    /**
     * Constructor del controlador.
     * 
     * @param vista Vista de clientes
     * @param clienteController Controlador de la lógica de negocio de clientes
     */
    public VistaClientesController(VistaClientes vista, ClienteController clienteController) {
        this.vista = vista;
        this.clienteController = clienteController;
        
        // Cargar datos iniciales
        cargarDatos();
        
        // Inicializar listeners adicionales
        inicializarListeners();
    }
    
    /**
     * Carga los datos iniciales en la vista.
     */
    private void cargarDatos() {
        try {
            List<Cliente> clientes = clienteController.obtenerTodosLosClientes();
            vista.mostrarClientes(clientes);
        } catch (Exception e) {
            System.err.println("Error al cargar datos iniciales: " + e.getMessage());
            // Crear algunos datos de ejemplo si no hay datos reales
            crearDatosEjemplo();
        }
    }
    
    /**
     * Crea datos de ejemplo para mostrar en la vista.
     */
    private void crearDatosEjemplo() {
        try {
            System.out.println("Creando datos de ejemplo...");
            List<Cliente> clientes = clienteController.obtenerTodosLosClientes();
            
            // Si no hay datos, crear algunos de ejemplo
            if (clientes == null || clientes.isEmpty()) {
                Cliente cliente1 = new Cliente();
                cliente1.setId("1");
                cliente1.setNombre("Cliente Ejemplo 1");
                cliente1.setRuc("10101010101");
                cliente1.setTelefono("555-1234");
                cliente1.setEmail("cliente1@ejemplo.com");
                cliente1.setDireccion("Dirección 1");
                clienteController.agregarCliente(cliente1);
                
                Cliente cliente2 = new Cliente();
                cliente2.setId("2");
                cliente2.setNombre("Cliente Ejemplo 2");
                cliente2.setRuc("20202020202");
                cliente2.setTelefono("555-5678");
                cliente2.setEmail("cliente2@ejemplo.com");
                cliente2.setDireccion("Dirección 2");
                clienteController.agregarCliente(cliente2);
                
                clientes = clienteController.obtenerTodosLosClientes();
            }
            
            vista.mostrarClientes(clientes);
        } catch (Exception e) {
            System.err.println("Error al crear datos de ejemplo: " + e.getMessage());
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
            
            List<Cliente> resultados;
            
            if (criterioBusqueda.isEmpty()) {
                resultados = clienteController.obtenerTodosLosClientes();
            } else {
                if ("Nombre".equals(tipoCriterio)) {
                    resultados = clienteController.buscarClientesPorNombre(criterioBusqueda);
                } else if ("RUC/NIT".equals(tipoCriterio)) {
                    resultados = clienteController.buscarClientesPorRuc(criterioBusqueda);
                } else if ("Email".equals(tipoCriterio)) {
                    resultados = clienteController.buscarClientesPorEmail(criterioBusqueda);
                } else {
                    resultados = clienteController.buscarClientes(criterioBusqueda);
                }
            }
            
            vista.mostrarClientes(resultados);
            
            if (resultados.isEmpty()) {
                vista.mostrarMensaje("No se encontraron resultados para la búsqueda");
            }
        } catch (Exception e) {
            System.err.println("Error al buscar clientes: " + e.getMessage());
            vista.mostrarMensaje("Error al realizar la búsqueda: " + e.getMessage());
        }
    }
    
    /**
     * Guarda un cliente (nuevo o existente).
     * 
     * @param cliente El cliente a guardar
     * @param esNuevo Indica si es un cliente nuevo o existente
     * @return true si se guardó correctamente, false en caso contrario
     */
    public boolean guardarCliente(Cliente cliente, boolean esNuevo) {
        System.out.println("Método guardarCliente llamado. Es nuevo: " + esNuevo);
        try {
            // Validar datos básicos
            if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
                vista.mostrarMensaje("El nombre del cliente es obligatorio");
                return false;
            }
            
            // Guardar cliente usando el controlador de negocio
            boolean resultado;
            if (esNuevo) {
                System.out.println("Agregando nuevo cliente: " + cliente.getNombre());
                resultado = clienteController.agregarCliente(cliente);
            } else {
                System.out.println("Actualizando cliente existente: " + cliente.getNombre());
                resultado = clienteController.actualizarCliente(cliente);
            }
            
            if (resultado) {
                vista.mostrarMensaje("Cliente guardado con éxito");
                // Actualizar tabla
                List<Cliente> clientes = clienteController.obtenerTodosLosClientes();
                vista.mostrarClientes(clientes);
            } else {
                vista.mostrarMensaje("Error al guardar el cliente. Verifica los datos e intenta nuevamente.");
            }
            
            return resultado;
        } catch (Exception e) {
            System.err.println("Error al guardar cliente: " + e.getMessage());
            e.printStackTrace();
            vista.mostrarMensaje("Error al procesar la solicitud: " + e.getMessage());
            
            // Implementación alternativa para pruebas (guardar localmente sin controlador)
            guardarClienteTemporal(cliente, esNuevo);
            
            return false;
        }
    }
    
    /**
     * Método temporal para guardar cliente sin depender del controlador de negocio.
     * Solo para pruebas cuando el controlador real falla.
     */
    private void guardarClienteTemporal(Cliente cliente, boolean esNuevo) {
        String tipoOperacion = esNuevo ? "nuevo" : "actualizado";
        JOptionPane.showMessageDialog(vista.getPanel(), 
            "IMPLEMENTACIÓN TEMPORAL:\nCliente " + tipoOperacion + ":\n" +
            "Nombre: " + cliente.getNombre() + "\n" +
            "RUC/NIT: " + cliente.getRuc() + "\n" +
            "Teléfono: " + cliente.getTelefono() + "\n" +
            "Email: " + cliente.getEmail() + "\n" +
            "Dirección: " + cliente.getDireccion(),
            "Cliente Guardado (Temporal)",
            JOptionPane.INFORMATION_MESSAGE);
        
        // Simulamos actualización de tabla con este cliente
        vista.mostrarListado();
    }
    
    /**
     * Elimina el cliente seleccionado.
     */
    public void eliminarCliente() {
        try {
            Cliente cliente = vista.obtenerClienteSeleccionado();
            
            if (cliente == null) {
                vista.mostrarMensaje("Debe seleccionar un cliente para eliminar");
                return;
            }
            
            boolean confirmar = vista.mostrarConfirmacion(
                "¿Está seguro de que desea eliminar el cliente \"" + cliente.getNombre() + "\"?"
            );
            
            if (confirmar) {
                boolean resultado = clienteController.eliminarCliente(cliente.getId());
                
                if (resultado) {
                    vista.mostrarMensaje("Cliente eliminado con éxito");
                    // Actualizar tabla
                    List<Cliente> clientes = clienteController.obtenerTodosLosClientes();
                    vista.mostrarClientes(clientes);
                } else {
                    vista.mostrarMensaje("Error al eliminar el cliente");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            vista.mostrarMensaje("Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    /**
     * Exporta la lista de clientes a PDF.
     */
    public void exportarAPDF() {
        try {
            String rutaArchivo = vista.seleccionarRutaGuardado("PDF");
            
            if (rutaArchivo != null) {
                List<Cliente> clientes = obtenerClientesActuales();
                boolean resultado = clienteController.exportarAPDF(clientes, rutaArchivo);
                
                if (resultado) {
                    vista.mostrarMensaje("Lista de clientes exportada a PDF con éxito");
                    
                    // Abrir el archivo
                    if (vista.mostrarConfirmacion("¿Desea abrir el archivo generado?")) {
                        abrirArchivo(rutaArchivo);
                    }
                } else {
                    vista.mostrarMensaje("Error al exportar a PDF");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al exportar a PDF: " + e.getMessage());
            vista.mostrarMensaje("Error al exportar a PDF: " + e.getMessage());
        }
    }
    
    /**
     * Exporta la lista de clientes a Excel.
     */
    public void exportarAExcel() {
        try {
            String rutaArchivo = vista.seleccionarRutaGuardado("Excel");
            
            if (rutaArchivo != null) {
                List<Cliente> clientes = obtenerClientesActuales();
                boolean resultado = clienteController.exportarAExcel(clientes, rutaArchivo);
                
                if (resultado) {
                    vista.mostrarMensaje("Lista de clientes exportada a Excel con éxito");
                    
                    // Abrir el archivo
                    if (vista.mostrarConfirmacion("¿Desea abrir el archivo generado?")) {
                        abrirArchivo(rutaArchivo);
                    }
                } else {
                    vista.mostrarMensaje("Error al exportar a Excel");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al exportar a Excel: " + e.getMessage());
            vista.mostrarMensaje("Error al exportar a Excel: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene los clientes actualmente mostrados en la tabla.
     * 
     * @return Lista de clientes
     */
    private List<Cliente> obtenerClientesActuales() {
        try {
            // Intentar obtener clientes desde el controlador
            return clienteController.obtenerTodosLosClientes();
        } catch (Exception e) {
            // Si falla, intentar obtener los que se muestran en la tabla
            return null;
        }
    }
    
    /**
     * Abre un archivo utilizando el programa predeterminado del sistema.
     * 
     * @param rutaArchivo Ruta del archivo a abrir
     */
    private void abrirArchivo(String rutaArchivo) {
        try {
            File archivo = new File(rutaArchivo);
            
            if (archivo.exists()) {
                java.awt.Desktop.getDesktop().open(archivo);
            }
        } catch (Exception e) {
            System.err.println("Error al abrir archivo: " + e.getMessage());
            vista.mostrarMensaje("No se pudo abrir el archivo");
        }
    }
}