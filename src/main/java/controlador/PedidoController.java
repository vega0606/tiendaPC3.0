package controlador;

import modelo.Pedido;
import modelo.DetallePedido;
import modelo.Proveedor;
import modelo.Producto;
import DAO.PedidoDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la gestión de pedidos a proveedores
 */
public class PedidoController {
    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);
    private PedidoDAO pedidoDAO;
    private ProveedorController proveedorController;
    private ProductoController productoController;
    
    public PedidoController() {
        pedidoDAO = new PedidoDAO();
        proveedorController = new ProveedorController();
        productoController = new ProductoController();
    }
    
    /**
     * Crea un nuevo pedido
     * @param proveedor Proveedor del pedido
     * @param fechaEntrega Fecha de entrega prevista
     * @param observaciones Observaciones
     * @param detalles Lista de detalles del pedido
     * @return El pedido creado o null si ocurre un error
     */
    public Pedido crearPedido(Proveedor proveedor, LocalDate fechaEntrega, String observaciones, 
                             List<DetallePedido> detalles) {
        try {
            // Validaciones básicas
            if (proveedor == null) {
                logger.error("No se puede crear pedido sin proveedor");
                return null;
            }
            
            if (detalles == null || detalles.isEmpty()) {
                logger.error("No se puede crear pedido sin detalles");
                return null;
            }
            
            // Generar número de pedido
            String numeroPedido = pedidoDAO.generarNuevoNumero();
            
            // Crear el pedido
            Pedido pedido = new Pedido();
            pedido.setNumero(numeroPedido);
            pedido.setProveedor(proveedor);
            pedido.setFecha(LocalDate.now());
            pedido.setFechaEntrega(fechaEntrega);
            pedido.setObservaciones(observaciones);
            pedido.setEstado("Pendiente");
            
            // Obtener usuario actual del sistema (desde LoginController)
            if (LoginController.getUsuarioActual() != null) {
                pedido.setIdUsuario(LoginController.getUsuarioActual().getId());
            }
            
            // Agregar detalles
            for (DetallePedido detalle : detalles) {
                detalle.setNumeroPedido(numeroPedido);
                pedido.agregarDetalle(detalle);
            }
            
            // Guardar el pedido
            return pedidoDAO.crear(pedido);
        } catch (Exception e) {
            logger.error("Error al crear pedido: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene un pedido por su número
     * @param numero Número de pedido
     * @return El pedido encontrado o null si no existe
     */
    public Pedido obtenerPedido(String numero) {
        try {
            return pedidoDAO.buscarPorId(numero);
        } catch (Exception e) {
            logger.error("Error al obtener pedido: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene todos los pedidos
     * @return Lista de pedidos
     */
    public List<Pedido> listarPedidos() {
        try {
            return pedidoDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Error al listar pedidos: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Actualiza el estado de un pedido
     * @param numero Número de pedido
     * @param nuevoEstado Nuevo estado
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarEstadoPedido(String numero, String nuevoEstado) {
        try {
            return pedidoDAO.actualizarEstado(numero, nuevoEstado);
        } catch (Exception e) {
            logger.error("Error al actualizar estado del pedido: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Cancela un pedido
     * @param numero Número de pedido
     * @param motivo Motivo de la cancelación
     * @return true si la cancelación fue exitosa
     */
    public boolean cancelarPedido(String numero, String motivo) {
        try {
            return pedidoDAO.cancelarPedido(numero, motivo);
        } catch (Exception e) {
            logger.error("Error al cancelar pedido: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Busca pedidos por proveedor
     * @param idProveedor ID del proveedor
     * @return Lista de pedidos del proveedor
     */
    public List<Pedido> buscarPedidosPorProveedor(String idProveedor) {
        try {
            return pedidoDAO.buscarPorProveedor(idProveedor);
        } catch (Exception e) {
            logger.error("Error al buscar pedidos por proveedor: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca pedidos por fecha
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de pedidos en el rango de fechas
     */
    public List<Pedido> buscarPedidosPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            return pedidoDAO.buscarPorFecha(fechaInicio, fechaFin);
        } catch (Exception e) {
            logger.error("Error al buscar pedidos por fecha: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca pedidos por estado
     * @param estado Estado de los pedidos
     * @return Lista de pedidos con el estado especificado
     */
    public List<Pedido> buscarPedidosPorEstado(String estado) {
        try {
            return pedidoDAO.buscarPorEstado(estado);
        } catch (Exception e) {
            logger.error("Error al buscar pedidos por estado: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Confirma la recepción de un pedido
     * @param numero Número de pedido
     * @return true si la recepción fue confirmada exitosamente
     */
    public boolean confirmarRecepcionPedido(String numero) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(numero);
            if (pedido == null || !"Enviado".equals(pedido.getEstado())) {
                return false;
            }
            
            // Actualizar el estado del pedido
            pedidoDAO.actualizarEstado(numero, "Entregado");
            
            // Actualizar el inventario con los productos recibidos
            for (DetallePedido detalle : pedido.getDetalles()) {
                productoController.actualizarStock(detalle.getCodigoProducto(), detalle.getCantidad());
            }
            
            return true;
        } catch (Exception e) {
            logger.error("Error al confirmar recepción del pedido: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Crea un detalle de pedido
     * @param codigoProducto Código del producto
     * @param cantidad Cantidad
     * @param precioUnitario Precio unitario
     * @return Detalle creado o null si ocurre un error
     */
    public DetallePedido crearDetallePedido(String codigoProducto, int cantidad, BigDecimal precioUnitario) {
        try {
            Producto producto = productoController.obtenerProducto(codigoProducto);
            if (producto == null) {
                logger.error("Producto no encontrado con código: {}", codigoProducto);
                return null;
            }
            
            if (cantidad <= 0) {
                logger.error("Cantidad debe ser mayor a 0");
                return null;
            }
            
            DetallePedido detalle = new DetallePedido(producto, cantidad);
            if (precioUnitario != null) {
                detalle.setPrecioUnitario(precioUnitario);
            }
            
            return detalle;
        } catch (Exception e) {
            logger.error("Error al crear detalle de pedido: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Genera un nuevo número de pedido
     * @return Nuevo número de pedido
     */
    public String generarNumeroPedido() {
        try {
            return pedidoDAO.generarNuevoNumero();
        } catch (Exception e) {
            logger.error("Error al generar número de pedido: {}", e.getMessage(), e);
            return "PO-ERROR";
        }
    }
}
