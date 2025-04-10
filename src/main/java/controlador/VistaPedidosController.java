package controlador;

import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ArrayList;

import modelo.Pedido;
import modelo.Proveedor;
import modelo.DetallePedido;
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
        List<Pedido> pedidos = pedidoController.listarPedidos();
        // listarPedidos ya devuelve un ArrayList vacío si hay error, no puede ser null
        vista.mostrarPedidos(pedidos);
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
            
            // El PedidoController utiliza String como número de pedido, no int
            Pedido pedido = pedidoController.obtenerPedido(numeroTexto);
            
            if (pedido != null) {
                vista.mostrarPedido(pedido);
            } else {
                vista.mostrarMensaje("No se encontró ningún pedido con el número: " + numeroTexto);
            }
        } catch (Exception e) {
            vista.mostrarMensaje("Error al buscar el pedido: " + e.getMessage());
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
                // No hay método directo para eliminar, pero podemos cancelar el pedido
                boolean eliminado = pedidoController.cancelarPedido(
                    pedidoSeleccionado.getNumero(), 
                    "Eliminado por el usuario"
                );
                
                if (eliminado) {
                    vista.mostrarMensaje("Pedido cancelado correctamente");
                    cargarPedidos();
                } else {
                    vista.mostrarMensaje("No se pudo cancelar el pedido");
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
            List<Pedido> pedidosFiltrados = pedidoController.buscarPedidosPorEstado(estado);
            // buscarPedidosPorEstado ya devuelve ArrayList vacío en caso de error
            vista.mostrarPedidos(pedidosFiltrados);
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
        
        List<Pedido> pedidosFiltrados = pedidoController.buscarPedidosPorProveedor(proveedor.getId());
        // buscarPedidosPorProveedor ya devuelve ArrayList vacío en caso de error
        vista.mostrarPedidos(pedidosFiltrados);
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
        
        // Verificamos si el pedido está en estado "Enviado"
        if (!"Enviado".equals(pedidoSeleccionado.getEstado())) {
            if ("Entregado".equals(pedidoSeleccionado.getEstado())) {
                vista.mostrarMensaje("Este pedido ya ha sido marcado como recibido");
            } else {
                vista.mostrarMensaje("Solo los pedidos en estado 'Enviado' pueden marcarse como recibidos");
            }
            return;
        }
        
        boolean confirmacion = vista.mostrarConfirmacion("¿Confirma que ha recibido este pedido?");
        
        if (confirmacion) {
            boolean recibido = pedidoController.confirmarRecepcionPedido(pedidoSeleccionado.getNumero());
            
            if (recibido) {
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
            
            // PedidoController no tiene métodos de exportación, debemos crear una implementación propia
            // o implementarla en otra clase, como un servicio de exportación.
            try {
                // Aquí se podría implementar la exportación o delegarla a otra clase
                // Por ahora, informamos al usuario que la funcionalidad no está disponible
                vista.mostrarMensaje("La funcionalidad de exportación a " + formato + 
                    " no está implementada en el controlador actual");
                
                // TODO: Implementar o integrar la exportación a diferentes formatos
                exportado = false;
            } catch (Exception e) {
                vista.mostrarMensaje("Error al exportar a " + formato + ": " + e.getMessage());
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
        if (pedido.getProveedor() == null) {
            vista.mostrarMensaje("Error: El proveedor es obligatorio");
            return false;
        }
        
        boolean resultado = false;
        
        if (esNuevo) {
            LocalDate fechaEntrega = pedido.getFechaEntrega();
            String observaciones = pedido.getObservaciones();
            
            // Verificar que existan detalles en el pedido
            if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
                vista.mostrarMensaje("Error: El pedido debe tener al menos un detalle");
                return false;
            }
            
            // Llamar al método apropiado del PedidoController
            Pedido nuevoPedido = pedidoController.crearPedido(
                pedido.getProveedor(), 
                fechaEntrega, 
                observaciones, 
                pedido.getDetalles()
            );
            
            resultado = (nuevoPedido != null);
        } else {
            // Para actualizar el estado del pedido
            resultado = pedidoController.actualizarEstadoPedido(
                pedido.getNumero(), 
                pedido.getEstado()
            );
        }
        
        if (resultado) {
            cargarPedidos();
        }
        
        return resultado;
    }
}