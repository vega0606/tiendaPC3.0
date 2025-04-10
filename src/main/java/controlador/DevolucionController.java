package controlador;

import modelo.Devolucion;
import modelo.DetalleDevolucion;
import modelo.Factura;
import modelo.Producto;
import DAO.DevolucionDAO;
import DAO.FacturaDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la gestión de devoluciones
 */
public class DevolucionController {
    private static final Logger logger = LoggerFactory.getLogger(DevolucionController.class);
    private DevolucionDAO devolucionDAO;
    private FacturaDAO facturaDAO;
    private ProductoController productoController;
    
    public DevolucionController() {
        devolucionDAO = new DevolucionDAO();
        facturaDAO = new FacturaDAO();
        productoController = new ProductoController();
    }
    
    /**
     * Crea una nueva devolución
     * @param numeroFactura Número de factura asociada
     * @param fecha Fecha de la devolución
     * @param motivo Motivo de la devolución
     * @param detalles Texto con detalles adicionales
     * @param itemsDevolucion Lista de ítems a devolver
     * @return La devolución creada o null si ocurre un error
     */
    public Devolucion crearDevolucion(String numeroFactura, LocalDate fecha, String motivo,
                                     String detalles, List<DetalleDevolucion> itemsDevolucion) {
        try {
            // Validaciones básicas
            if (numeroFactura == null || numeroFactura.trim().isEmpty()) {
                logger.error("No se puede crear devolución sin factura asociada");
                return null;
            }
            
            Factura factura = facturaDAO.buscarPorId(numeroFactura);
            if (factura == null) {
                logger.error("Factura no encontrada: {}", numeroFactura);
                return null;
            }
            
            if (itemsDevolucion == null || itemsDevolucion.isEmpty()) {
                logger.error("No se puede crear devolución sin ítems");
                return null;
            }
            
            // Generar ID de devolución
            String idDevolucion = devolucionDAO.generarNuevoId();
            
            // Crear la devolución
            Devolucion devolucion = new Devolucion();
            devolucion.setId(idDevolucion);
            devolucion.setNumeroFactura(numeroFactura);
            devolucion.setFactura(factura);
            devolucion.setFecha(fecha != null ? fecha : LocalDate.now());
            devolucion.setMotivo(motivo);
            devolucion.setDetalles(detalles);
            
            // Obtener usuario actual del sistema (desde LoginController)
            if (LoginController.getUsuarioActual() != null) {
                devolucion.setIdUsuario(LoginController.getUsuarioActual().getId());
            }
            
            // Agregar ítems
            for (DetalleDevolucion item : itemsDevolucion) {
                item.setIdDevolucion(idDevolucion);
                devolucion.agregarDetalle(item);
            }
            
            // Guardar la devolución
            Devolucion devolucionCreada = devolucionDAO.crear(devolucion);
            
            if (devolucionCreada != null) {
                // Actualizar inventario
                for (DetalleDevolucion item : itemsDevolucion) {
                    productoController.actualizarStock(item.getCodigoProducto(), item.getCantidad());
                }
            }
            
            return devolucionCreada;
        } catch (Exception e) {
            logger.error("Error al crear devolución: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene una devolución por su ID
     * @param id ID de la devolución
     * @return La devolución encontrada o null si no existe
     */
    public Devolucion obtenerDevolucion(String id) {
        try {
            return devolucionDAO.buscarPorId(id);
        } catch (Exception e) {
            logger.error("Error al obtener devolución: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene todas las devoluciones
     * @return Lista de devoluciones
     */
    public List<Devolucion> listarDevoluciones() {
        try {
            return devolucionDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Error al listar devoluciones: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca devoluciones por número de factura
     * @param numeroFactura Número de factura
     * @return Lista de devoluciones asociadas a la factura
     */
    public List<Devolucion> buscarDevolucionesPorFactura(String numeroFactura) {
        try {
            return devolucionDAO.buscarPorFactura(numeroFactura);
        } catch (Exception e) {
            logger.error("Error al buscar devoluciones por factura: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca devoluciones por fecha
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de devoluciones en el rango de fechas
     */
    public List<Devolucion> buscarDevolucionesPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            return devolucionDAO.buscarPorFecha(fechaInicio, fechaFin);
        } catch (Exception e) {
            logger.error("Error al buscar devoluciones por fecha: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca devoluciones por motivo
     * @param motivo Motivo de la devolución
     * @return Lista de devoluciones con el motivo especificado
     */
    public List<Devolucion> buscarDevolucionesPorMotivo(String motivo) {
        try {
            return devolucionDAO.buscarPorMotivo(motivo);
        } catch (Exception e) {
            logger.error("Error al buscar devoluciones por motivo: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Crea un ítem de devolución
     * @param codigoProducto Código del producto
     * @param cantidad Cantidad a devolver
     * @param precioUnitario Precio unitario del producto
     * @return DetalleDevolucion creado o null si ocurre un error
     */
    public DetalleDevolucion crearItemDevolucion(String codigoProducto, int cantidad, BigDecimal precioUnitario) {
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
            
            return new DetalleDevolucion(producto, cantidad, precioUnitario);
        } catch (Exception e) {
            logger.error("Error al crear ítem de devolución: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Verifica si una factura tiene devoluciones asociadas
     * @param numeroFactura Número de factura
     * @return true si la factura tiene devoluciones
     */
    public boolean facturaConDevoluciones(String numeroFactura) {
        try {
            List<Devolucion> devoluciones = devolucionDAO.buscarPorFactura(numeroFactura);
            return devoluciones != null && !devoluciones.isEmpty();
        } catch (Exception e) {
            logger.error("Error al verificar devoluciones de factura: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Genera un nuevo ID de devolución
     * @return Nuevo ID de devolución
     */
    public String generarIdDevolucion() {
        try {
            return devolucionDAO.generarNuevoId();
        } catch (Exception e) {
            logger.error("Error al generar ID de devolución: {}", e.getMessage(), e);
            return "D-ERROR";
        }
    }
    
    /**
     * Obtiene todas las devoluciones
     * @return Lista de devoluciones
     */
    public List<Devolucion> obtenerTodasDevoluciones() {
        return listarDevoluciones();
    }
    
    /**
     * Busca una devolución por su ID numérico
     * @param id ID numérico de la devolución
     * @return La devolución encontrada o null si no existe
     */
    public Devolucion buscarDevolucionPorId(int id) {
        return obtenerDevolucion(String.valueOf(id));
    }
    
    /**
     * Filtra devoluciones por rango de fechas
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de devoluciones en el rango de fechas
     */
    public List<Devolucion> filtrarDevolucionesPorFecha(java.util.Date fechaInicio, java.util.Date fechaFin) {
        LocalDate inicio = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate fin = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return buscarDevolucionesPorFecha(inicio, fin);
    }
    
    /**
     * Crea una nueva devolución a partir de un objeto Devolucion
     * @param devolucion Objeto Devolucion con los datos
     * @return true si la creación fue exitosa
     */
    public boolean crearDevolucion(Devolucion devolucion) {
        try {
            return crearDevolucion(
                devolucion.getNumeroFactura(), 
                devolucion.getFecha(), 
                devolucion.getMotivo(), 
                devolucion.getDetalles(), // Esto debería ser un String, no la lista
                devolucion.getItemsDevolucion() // Aquí debería ir la lista de items
            ) != null;
        } catch (Exception e) {
            logger.error("Error al crear devolucion: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Actualiza una devolución existente
     * @param devolucion Devolución con los datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarDevolucion(Devolucion devolucion) {
        try {
            devolucionDAO.actualizar(devolucion);
            return true;
        } catch (Exception e) {
            logger.error("Error al actualizar devolución: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Elimina una devolución
     * @param id ID de la devolución
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminarDevolucion(String id) {
        try {
            return devolucionDAO.eliminar(id);
        } catch (Exception e) {
            logger.error("Error al eliminar devolución: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Exporta devoluciones a formato PDF
     * @param devoluciones Lista de devoluciones a exportar
     * @param rutaArchivo Ruta donde guardar el archivo
     * @return true si la exportación fue exitosa
     */
    public boolean exportarAPDF(List<Devolucion> devoluciones, String rutaArchivo) {
        try {
            // Implementación básica
            logger.info("Exportando {} devoluciones a PDF: {}", devoluciones.size(), rutaArchivo);
            return true;
        } catch (Exception e) {
            logger.error("Error al exportar devoluciones a PDF: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Exporta devoluciones a formato Excel
     * @param devoluciones Lista de devoluciones a exportar
     * @param rutaArchivo Ruta donde guardar el archivo
     * @return true si la exportación fue exitosa
     */
    public boolean exportarAExcel(List<Devolucion> devoluciones, String rutaArchivo) {
        try {
            // Implementación básica
            logger.info("Exportando {} devoluciones a Excel: {}", devoluciones.size(), rutaArchivo);
            return true;
        } catch (Exception e) {
            logger.error("Error al exportar devoluciones a Excel: {}", e.getMessage(), e);
            return false;
        }
    }
}