package controlador;

import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import modelo.Pedido;
import modelo.Proveedor;
import ventana.VistaPedidos;

/**
 * Controlador para la vista de pedidos.
 * Gestiona la interacción entre la vista de pedidos y el modelo de datos.
 */
public class VistaPedidosController {
    
    private VistaPedidos vista;
    private PedidoController pedidoController;
    private ProveedorController proveedorController;
    
    /**
     * Constructor del controlador de vista de pedidos.
     * 
     * @param vista La vista de pedidos
     * @param pedidoController El controlador de pedidos
     * @param proveedorController El controlador de proveedores
     */
    public VistaPedidosController(VistaPedidos vista, PedidoController pedidoController, 
                                 ProveedorController proveedorController) {
        this.vista = vista;
        this.pedidoController = pedidoController;
        this.proveedorController = proveedorController;
        
        // Inicializar los listeners y componentes de la vista
        inicializarVista();
    }
    
    /**
     * Inicializa los componentes de la vista y configura los listeners.
     */
    private void inicializarVista() {
        // Cargar los pedidos al iniciar la vista
        cargarPedidos();
        
        // Cargar proveedores para el combobox
        cargarProveedores();
        
        // Configurar listeners para los botones de la vista
        configurarListeners();
    }
    
    /**
     * Configura los listeners para los botones y componentes interactivos de la vista.
     */
    private void configurarListeners() {
        // Listener para el botón de buscar pedido
        vista.getBtnBuscar().addActionListener(e -> buscarPedidoPorNumero());
        
        // Listener para el botón de mostrar todos los pedidos
        vista.getBtnMostrarTodos().addActionListener(e -> cargarPedidos());
        
        // Listener para el botón de nuevo pedido
        vista.getBtnNuevoPedido().addActionListener(e -> abrirFormularioNuevoPedido());
        
        // Listener para el botón de editar pedido
        vista.getBtnEditar().addActionListener(e -> editarPedidoSeleccionado());
        
        // Listener para el botón de eliminar pedido
        vista.getBtnEliminar().addActionListener(e -> eliminarPedidoSeleccionado());
        
        // Listener para filtro por estado
        vista.getBtnFiltrarPorEstado().addActionListener(e -> filtrarPedidosPorEstado());
        
        // Listener para filtro por proveedor
        vista.getBtnFiltrarPorProveedor().addActionListener(e -> filtrarPedidosPorProveedor());
        
        // Listener para cambiar estado a "Recibido"
        vista.getBtnMarcarRecibido().addActionListener(e -> marcarPedidoComoRecibido());
        
        // Listener para exportar a PDF
        vista.getBtnExportarPDF().addActionListener(e -> exportarAFormato("PDF"));
        
        // Listener para exportar a Excel
        vista.getBtnExportarExcel().addActionListener(e -> exportarAFormato("Excel"));
    }
    
    /**
     * Carga todos los pedidos en la tabla de la vista.
     */
    public void cargarPedidos() {
        List<Pedido> pedidos = pedidoController.obtenerTodosPedidos();
        if (pedidos != null) {
            vista.mostrarPedidos(pedidos);
        } else {
            vista.mostrarMensaje("Error al cargar los pedidos");
        }
    }
    
    /**
     * Carga todos los proveedores para el combobox de la vista.
     */
    private void cargarProveedores() {
        List<Proveedor> proveedores = proveedorController.obtenerTodosProveedores();
        if (proveedores != null) {
            vista.cargarProveedoresEnComboBox(proveedores);
        } else {
            vista.mostrarMensaje("Error al cargar los proveedores");
        }
    }
    
    /**
     * Busca un pedido por su número y lo muestra en la vista.
     */
    private void buscarPedidoPorNumero() {
        try {
            String numeroTexto = vista.getTxtBusqueda().getText();
            if (numeroTexto == null || numeroTexto.isEmpty()) {
                vista.mostrarMensaje("Ingrese un número de pedido para buscar");
                return;
            }
            
            int numeroPedido = Integer.parseInt(numeroTexto);
            Pedido pedido = pedidoController.buscarPedidoPorNumero(numeroPedido);
            
            if (pedido != null) {
                vista.mostrarPedido(pedido);
            } else {
                vista.mostrarMensaje("No se encontró ningún pedido con el número: " + numeroPedido);
            }
        } catch (NumberFormatException e) {
            vista.mostrarMensaje("El número de pedido debe ser un número entero");
        }
    }
    
    /**
     * Abre el formulario para crear un nuevo pedido.
     */
    private void abrirFormularioNuevoPedido() {
        vista.mostrarFormularioPedido(null);
    }
    
    /**
     * Edita el pedido seleccionado en la tabla.
     */
    private void editarPedidoSeleccionado() {
        Pedido pedidoSeleccionado = vista.obtenerPedidoSeleccionado();
        
        if (pedidoSeleccionado != null) {
            vista.mostrarFormularioPedido(pedidoSeleccionado);
        } else {
            vista.mostrarMensaje("Seleccione un pedido para editar");
        }
    }
    
    /**
     * Elimina el pedido seleccionado en la tabla.
     */
    private void eliminarPedidoSeleccionado() {
        Pedido pedidoSeleccionado = vista.obtenerPedidoSeleccionado();
        
        if (pedidoSeleccionado != null) {
            boolean confirmacion = vista.mostrarConfirmacion("¿Está seguro de eliminar este pedido?");
            
            if (confirmacion) {
                boolean eliminado = pedidoController.eliminarPedido(pedidoSeleccionado.getNumero());
                
                if (eliminado) {
                    vista.mostrarMensaje("Pedido eliminado correctamente");
                    cargarPedidos();
                } else {
                    vista.mostrarMensaje("No se pudo eliminar el pedido");
                }
            }
        } else {
            vista.mostrarMensaje("Seleccione un pedido para eliminar");
        }
    }
    
    /**
     * Filtra los pedidos por estado.
     */
    private void filtrarPedidosPorEstado() {
        Object estadoSeleccionado = vista.getComboEstado().getSelectedItem();
        
        if (estadoSeleccionado == null) {
            vista.mostrarMensaje("Seleccione un estado para filtrar");
            return;
        }
        
        String estado = estadoSeleccionado.toString();
        
        if ("Todos".equals(estado)) {
            cargarPedidos();
        } else {
            List<Pedido> pedidosFiltrados = pedidoController.filtrarPedidosPorEstado(estado);
            if (pedidosFiltrados != null) {
                vista.mostrarPedidos(pedidosFiltrados);
            } else {
                vista.mostrarMensaje("Error al filtrar por estado");
            }
        }
    }
    
    /**
     * Filtra los pedidos por proveedor.
     */
    private void filtrarPedidosPorProveedor() {
        Object seleccionado = vista.getComboProveedor().getSelectedItem();
        
        if (seleccionado == null) {
            vista.mostrarMensaje("Seleccione un proveedor para filtrar");
            return;
        }
        
        if (!(seleccionado instanceof Proveedor)) {
            vista.mostrarMensaje("Error: El elemento seleccionado no es un proveedor válido");
            return;
        }
        
        Proveedor proveedor = (Proveedor) seleccionado;
        
        List<Pedido> pedidosFiltrados = pedidoController.filtrarPedidosPorProveedor(proveedor.getId());
        if (pedidosFiltrados != null) {
            vista.mostrarPedidos(pedidosFiltrados);
        } else {
            vista.mostrarMensaje("Error al filtrar por proveedor");
        }
    }
    
    /**
     * Marca el pedido seleccionado como recibido.
     */
    private void marcarPedidoComoRecibido() {
        Pedido pedidoSeleccionado = vista.obtenerPedidoSeleccionado();
        
        if (pedidoSeleccionado == null) {
            vista.mostrarMensaje("Seleccione un pedido para marcar como recibido");
            return;
        }
        
        if ("Recibido".equals(pedidoSeleccionado.getEstado())) {
            vista.mostrarMensaje("Este pedido ya ha sido marcado como recibido");
            return;
        }
        
        boolean confirmacion = vista.mostrarConfirmacion("¿Confirma que ha recibido este pedido?");
        
        if (confirmacion) {
            pedidoSeleccionado.setEstado("Recibido");
            
            // Uso de LocalDate y conversión a Date si es necesario
            LocalDate hoy = LocalDate.now();
            Date fechaRecepcion = Date.from(hoy.atStartOfDay(ZoneId.systemDefault()).toInstant());
            pedidoSeleccionado.setFechaRecepcion(fechaRecepcion);
            
            boolean actualizado = pedidoController.actualizarPedido(pedidoSeleccionado);
            
            if (actualizado) {
                vista.mostrarMensaje("Pedido marcado como recibido correctamente");
                cargarPedidos();
            } else {
                vista.mostrarMensaje("Error al marcar el pedido como recibido");
            }
        }
    }
    
    /**
     * Exporta los pedidos mostrados a un formato específico.
     * 
     * @param formato El formato de exportación ("PDF" o "Excel")
     */
    private void exportarAFormato(String formato) {
        List<Pedido> pedidos = vista.obtenerPedidosMostrados();
        
        if (pedidos == null || pedidos.isEmpty()) {
            vista.mostrarMensaje("No hay datos para exportar");
            return;
        }
        
        String rutaArchivo = vista.seleccionarRutaGuardado(formato);
        
        if (rutaArchivo != null && !rutaArchivo.trim().isEmpty()) {
            boolean exportado = false;
            
            if ("PDF".equals(formato)) {
                exportado = pedidoController.exportarAPDF(pedidos, rutaArchivo);
            } else if ("Excel".equals(formato)) {
                exportado = pedidoController.exportarAExcel(pedidos, rutaArchivo);
            }
            
            if (exportado) {
                vista.mostrarMensaje("Datos exportados correctamente a " + formato);
            } else {
                vista.mostrarMensaje("Error al exportar a " + formato);
            }
        }
    }
    
    /**
     * Guarda un pedido nuevo o actualizado.
     * 
     * @param pedido El pedido a guardar
     * @param esNuevo Indica si es un nuevo pedido o una actualización
     * @return true si se guardó correctamente, false en caso contrario
     */
    public boolean guardarPedido(Pedido pedido, boolean esNuevo) {
        if (pedido == null) {
            vista.mostrarMensaje("Error: El pedido no puede ser nulo");
            return false;
        }
        
        // Validar campos obligatorios
        if (pedido.getProveedor() == null || pedido.getFechaPedido() == null) {
            vista.mostrarMensaje("Error: Proveedor y fecha de pedido son obligatorios");
            return false;
        }
        
        boolean resultado;
        
        if (esNuevo) {
            resultado = pedidoController.crearPedido(pedido);
        } else {
            resultado = pedidoController.actualizarPedido(pedido);
        }
        
        if (resultado) {
            cargarPedidos();
        }
        
        return resultado;
    }
}