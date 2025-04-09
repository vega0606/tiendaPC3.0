package controlador;

import modelo.Factura;
import modelo.DetalleFactura;
import modelo.Cliente;
import modelo.Producto;
import DAO.FacturaDAO;
import DAO.ClienteDAO;
import DAO.ProductoDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la gestión de facturas
 */
public class FacturaController {
    private static final Logger logger = LoggerFactory.getLogger(FacturaController.class);
    private FacturaDAO facturaDAO;
    private ClienteDAO clienteDAO;
    private ProductoDAO productoDAO;
    
    public FacturaController() {
        facturaDAO = new FacturaDAO();
        clienteDAO = new ClienteDAO();
        productoDAO = new ProductoDAO();
    }
    
    /**
     * Crea una nueva factura
     * @param cliente Cliente de la factura
     * @param fecha Fecha de la factura
     * @param observaciones Observaciones
     * @param detalles Lista de detalles de la factura
     * @return La factura creada o null si ocurre un error
     */
    public Factura crearFactura(Cliente cliente, LocalDate fecha, String observaciones, 
                               List<DetalleFactura> detalles) {
        try {
            // Validaciones básicas
            if (cliente == null) {
                logger.error("No se puede crear factura sin cliente");
                return null;
            }
            
            if (detalles == null || detalles.isEmpty()) {
                logger.error("No se puede crear factura sin detalles");
                return null;
            }
            
            // Generar número de factura
            String numeroFactura = facturaDAO.generarNuevoNumero();
            
            // Crear la factura
            Factura factura = new Factura();
            factura.setNumero(numeroFactura);
            factura.setCliente(cliente);
            factura.setFecha(fecha != null ? fecha : LocalDate.now());
            factura.setObservaciones(observaciones);
            factura.setEstado("Emitida");
            
            // Obtener usuario actual del sistema (desde LoginController)
            if (LoginController.getUsuarioActual() != null) {
                factura.setIdUsuario(LoginController.getUsuarioActual().getId());
            }
            
            // Agregar detalles
            for (DetalleFactura detalle : detalles) {
                detalle.setNumeroFactura(numeroFactura);
                factura.agregarDetalle(detalle);
            }
            
            // Guardar la factura
            return facturaDAO.crear(factura);
        } catch (Exception e) {
            logger.error("Error al crear factura: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene una factura por su número
     * @param numero Número de factura
     * @return La factura encontrada o null si no existe
     */
    public Factura obtenerFactura(String numero) {
        try {
            return facturaDAO.buscarPorId(numero);
        } catch (Exception e) {
            logger.error("Error al obtener factura: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene todas las facturas
     * @return Lista de facturas
     */
    public List<Factura> listarFacturas() {
        try {
            return facturaDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Error al listar facturas: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Anula una factura
     * @param numero Número de factura
     * @param motivo Motivo de la anulación
     * @return true si la anulación fue exitosa
     */
    public boolean anularFactura(String numero, String motivo) {
        try {
            return facturaDAO.anularFactura(numero, motivo);
        } catch (Exception e) {
            logger.error("Error al anular factura: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Busca facturas por cliente
     * @param idCliente ID del cliente
     * @return Lista de facturas del cliente
     */
    public List<Factura> buscarFacturasPorCliente(String idCliente) {
        try {
            return facturaDAO.buscarPorCliente(idCliente);
        } catch (Exception e) {
            logger.error("Error al buscar facturas por cliente: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca facturas por fecha
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de facturas en el rango de fechas
     */
    public List<Factura> buscarFacturasPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            return facturaDAO.buscarPorFecha(fechaInicio, fechaFin);
        } catch (Exception e) {
            logger.error("Error al buscar facturas por fecha: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Crea un detalle de factura
     * @param codigoProducto Código del producto
     * @param cantidad Cantidad
     * @return Detalle creado o null si ocurre un error
     */
    public DetalleFactura crearDetalleFactura(String codigoProducto, int cantidad) {
        try {
            Producto producto = productoDAO.buscarPorId(codigoProducto);
            if (producto == null) {
                logger.error("Producto no encontrado con código: {}", codigoProducto);
                return null;
            }
            
            if (cantidad <= 0) {
                logger.error("Cantidad debe ser mayor a 0");
                return null;
            }
            
            if (producto.getStock() < cantidad) {
                logger.error("Stock insuficiente. Disponible: {}, Solicitado: {}", 
                            producto.getStock(), cantidad);
                return null;
            }
            
            return new DetalleFactura(producto, cantidad);
        } catch (Exception e) {
            logger.error("Error al crear detalle de factura: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Busca un cliente por su ID
     * @param idCliente ID del cliente
     * @return Cliente encontrado o null si no existe
     */
    public Cliente buscarCliente(String idCliente) {
        try {
            return clienteDAO.buscarPorId(idCliente);
        } catch (Exception e) {
            logger.error("Error al buscar cliente: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Busca un cliente por su RUC/NIT
     * @param ruc RUC/NIT del cliente
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
     * Busca un producto por su código
     * @param codigo Código del producto
     * @return Producto encontrado o null si no existe
     */
    public Producto buscarProducto(String codigo) {
        try {
            return productoDAO.buscarPorId(codigo);
        } catch (Exception e) {
            logger.error("Error al buscar producto: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Valida si hay stock suficiente para un producto
     * @param codigoProducto Código del producto
     * @param cantidad Cantidad solicitada
     * @return true si hay stock suficiente
     */
    public boolean validarStock(String codigoProducto, int cantidad) {
        try {
            Producto producto = productoDAO.buscarPorId(codigoProducto);
            return producto != null && producto.getStock() >= cantidad;
        } catch (Exception e) {
            logger.error("Error al validar stock: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Genera un nuevo número de factura
     * @return Nuevo número de factura
     */
    public String generarNumeroFactura() {
        try {
            return facturaDAO.generarNuevoNumero();
        } catch (Exception e) {
            logger.error("Error al generar número de factura: {}", e.getMessage(), e);
            return "F-ERROR";
        }
    }
}
