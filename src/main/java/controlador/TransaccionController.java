package controlador;

import modelo.Transaccion;
import DAO.TransaccionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la gestión de transacciones financieras
 */
public class TransaccionController {
    private static final Logger logger = LoggerFactory.getLogger(TransaccionController.class);
    private TransaccionDAO transaccionDAO;
    
    public TransaccionController() {
        transaccionDAO = new TransaccionDAO();
    }
    
    /**
     * Registra una nueva transacción
     * @param fecha Fecha de la transacción
     * @param entidad Nombre del cliente o proveedor
     * @param tipo Tipo de transacción (Venta, Compra, Devolución)
     * @param referencia Número de factura o pedido relacionado
     * @param total Monto total de la transacción
     * @param estado Estado de la transacción
     * @return La transacción creada o null si ocurre un error
     */
    public Transaccion registrarTransaccion(LocalDate fecha, String entidad, String tipo,
                                           String referencia, BigDecimal total, String estado) {
        try {
            // Generar ID para la nueva transacción
            String id = transaccionDAO.generarNuevoId();
            
            Transaccion transaccion = new Transaccion();
            transaccion.setId(id);
            transaccion.setFecha(fecha != null ? fecha : LocalDate.now());
            transaccion.setEntidad(entidad);
            transaccion.setTipo(tipo);
            transaccion.setReferencia(referencia);
            transaccion.setTotal(total);
            transaccion.setEstado(estado != null ? estado : "Completada");
            
            // Obtener usuario actual del sistema (desde LoginController)
            if (LoginController.getUsuarioActual() != null) {
                transaccion.setIdUsuario(LoginController.getUsuarioActual().getId());
            }
            
            return transaccionDAO.crear(transaccion);
        } catch (Exception e) {
            logger.error("Error al registrar transacción: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene una transacción por su ID
     * @param id ID de la transacción
     * @return La transacción encontrada o null si no existe
     */
    public Transaccion obtenerTransaccion(String id) {
        try {
            return transaccionDAO.buscarPorId(id);
        } catch (Exception e) {
            logger.error("Error al obtener transacción: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene todas las transacciones
     * @return Lista de transacciones
     */
    public List<Transaccion> listarTransacciones() {
        try {
            return transaccionDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Error al listar transacciones: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Actualiza el estado de una transacción
     * @param id ID de la transacción
     * @param nuevoEstado Nuevo estado
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarEstadoTransaccion(String id, String nuevoEstado) {
        try {
            return transaccionDAO.actualizarEstado(id, nuevoEstado);
        } catch (Exception e) {
            logger.error("Error al actualizar estado de transacción: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Busca transacciones por tipo
     * @param tipo Tipo de transacción (Venta, Compra, Devolución)
     * @return Lista de transacciones del tipo especificado
     */
    public List<Transaccion> buscarTransaccionesPorTipo(String tipo) {
        try {
            return transaccionDAO.buscarPorTipo(tipo);
        } catch (Exception e) {
            logger.error("Error al buscar transacciones por tipo: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca transacciones por entidad (cliente o proveedor)
     * @param entidad Nombre de la entidad
     * @return Lista de transacciones de la entidad
     */
    public List<Transaccion> buscarTransaccionesPorEntidad(String entidad) {
        try {
            return transaccionDAO.buscarPorEntidad(entidad);
        } catch (Exception e) {
            logger.error("Error al buscar transacciones por entidad: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca transacciones por referencia (factura o pedido)
     * @param referencia Número de referencia
     * @return Lista de transacciones con la referencia especificada
     */
    public List<Transaccion> buscarTransaccionesPorReferencia(String referencia) {
        try {
            return transaccionDAO.buscarPorReferencia(referencia);
        } catch (Exception e) {
            logger.error("Error al buscar transacciones por referencia: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca transacciones por fecha
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de transacciones en el rango de fechas
     */
    public List<Transaccion> buscarTransaccionesPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            return transaccionDAO.buscarPorFecha(fechaInicio, fechaFin);
        } catch (Exception e) {
            logger.error("Error al buscar transacciones por fecha: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Busca transacciones por estado
     * @param estado Estado de las transacciones
     * @return Lista de transacciones con el estado especificado
     */
    public List<Transaccion> buscarTransaccionesPorEstado(String estado) {
        try {
            return transaccionDAO.buscarPorEstado(estado);
        } catch (Exception e) {
            logger.error("Error al buscar transacciones por estado: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene un resumen de transacciones por tipo
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Mapa con totales por tipo de transacción
     */
    public Map<String, BigDecimal> obtenerResumenPorTipo(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            Map<String, BigDecimal> resumen = new HashMap<>();
            
            // Inicializar totales
            resumen.put("Venta", BigDecimal.ZERO);
            resumen.put("Compra", BigDecimal.ZERO);
            resumen.put("Devolución", BigDecimal.ZERO);
            
            List<Transaccion> transacciones = transaccionDAO.buscarPorFecha(fechaInicio, fechaFin);
            
            for (Transaccion transaccion : transacciones) {
                String tipo = transaccion.getTipo();
                BigDecimal total = transaccion.getTotal();
                
                if (resumen.containsKey(tipo)) {
                    resumen.put(tipo, resumen.get(tipo).add(total));
                }
            }
            
            // Calcular balance (Ventas - Compras + Devoluciones)
            BigDecimal balance = resumen.get("Venta")
                               .subtract(resumen.get("Compra"))
                               .add(resumen.get("Devolución"));
            
            resumen.put("Balance", balance);
            
            return resumen;
        } catch (Exception e) {
            logger.error("Error al obtener resumen por tipo: {}", e.getMessage(), e);
            
            // Devolver resumen vacío
            Map<String, BigDecimal> resumenVacio = new HashMap<>();
            resumenVacio.put("Venta", BigDecimal.ZERO);
            resumenVacio.put("Compra", BigDecimal.ZERO);
            resumenVacio.put("Devolución", BigDecimal.ZERO);
            resumenVacio.put("Balance", BigDecimal.ZERO);
            
            return resumenVacio;
        }
    }
    
    /**
     * Cancela una transacción
     * @param id ID de la transacción
     * @param motivo Motivo de la cancelación
     * @return true si la cancelación fue exitosa
     */
    public boolean cancelarTransaccion(String id, String motivo) {
        try {
            return transaccionDAO.cancelarTransaccion(id, motivo);
        } catch (Exception e) {
            logger.error("Error al cancelar transacción: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Genera un nuevo ID de transacción
     * @return Nuevo ID de transacción
     */
    public String generarIdTransaccion() {
        try {
            return transaccionDAO.generarNuevoId();
        } catch (Exception e) {
            logger.error("Error al generar ID de transacción: {}", e.getMessage(), e);
            return "T-ERROR";
        }
    }
}
