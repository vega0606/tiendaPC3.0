package controlador;

import modelo.Alerta;
import DAO.AlertaDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la gestión de alertas del sistema
 */
public class AlertaController {
    private static final Logger logger = LoggerFactory.getLogger(AlertaController.class);
    private AlertaDAO alertaDAO;
    
    public AlertaController() {
        alertaDAO = new AlertaDAO();
    }
    
    /**
     * Crea una nueva alerta
     * @param tipo Tipo de alerta (Stock, Pago, Vencimiento, Sistema, etc.)
     * @param descripcion Descripción de la alerta
     * @param fecha Fecha asociada a la alerta
     * @param prioridad Prioridad de la alerta (Alta, Media, Baja)
     * @param estado Estado inicial de la alerta (Pendiente, Atendida, Programada)
     * @param referencia Referencia a la entidad relacionada con la alerta
     * @return La alerta creada o null si ocurre un error
     */
    public Alerta crearAlerta(String tipo, String descripcion, LocalDate fecha, String prioridad, 
                             String estado, String referencia) {
        try {
            // Generar ID para la nueva alerta
            String id = alertaDAO.generarNuevoId();
            
            Alerta alerta = new Alerta();
            alerta.setId(id);
            alerta.setTipo(tipo);
            alerta.setDescripcion(descripcion);
            alerta.setFecha(fecha);
            alerta.setPrioridad(prioridad);
            alerta.setEstado(estado != null ? estado : "Pendiente");
            alerta.setReferencia(referencia);
            
            return alertaDAO.crear(alerta);
        } catch (Exception e) {
            logger.error("Error al crear alerta: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene una alerta por su ID
     * @param id ID de la alerta
     * @return La alerta encontrada o null si no existe
     */
    public Alerta obtenerAlerta(String id) {
        try {
            return alertaDAO.buscarPorId(id);
        } catch (Exception e) {
            logger.error("Error al obtener alerta: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Obtiene todas las alertas
     * @return Lista de alertas
     */
    public List<Alerta> listarAlertas() {
        try {
            return alertaDAO.listarTodos();
        } catch (Exception e) {
            logger.error("Error al listar alertas: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Actualiza una alerta existente
     * @param alerta Alerta con los datos actualizados
     * @return true si la actualización fue exitosa
     */
    public boolean actualizarAlerta(Alerta alerta) {
        try {
            alertaDAO.actualizar(alerta);
            return true;
        } catch (Exception e) {
            logger.error("Error al actualizar alerta: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Elimina una alerta
     * @param id ID de la alerta
     * @return true si la eliminación fue exitosa
     */
    public boolean eliminarAlerta(String id) {
        try {
            return alertaDAO.eliminar(id);
        } catch (Exception e) {
            logger.error("Error al eliminar alerta: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Obtiene alertas por tipo
     * @param tipo Tipo de alertas a buscar
     * @return Lista de alertas del tipo especificado
     */
    public List<Alerta> listarAlertasPorTipo(String tipo) {
        try {
            return alertaDAO.listarPorTipo(tipo);
        } catch (Exception e) {
            logger.error("Error al listar alertas por tipo: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene alertas por prioridad
     * @param prioridad Prioridad de las alertas a buscar
     * @return Lista de alertas con la prioridad especificada
     */
    public List<Alerta> listarAlertasPorPrioridad(String prioridad) {
        try {
            return alertaDAO.listarPorPrioridad(prioridad);
        } catch (Exception e) {
            logger.error("Error al listar alertas por prioridad: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Cambia el estado de una alerta
     * @param id ID de la alerta
     * @param nuevoEstado Nuevo estado
     * @return true si el cambio fue exitoso
     */
    public boolean cambiarEstadoAlerta(String id, String nuevoEstado) {
        try {
            return alertaDAO.cambiarEstado(id, nuevoEstado);
        } catch (Exception e) {
            logger.error("Error al cambiar estado de alerta: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Obtiene un resumen de alertas
     * @return Mapa con cantidades de alertas por tipo y estado
     */
    public Map<String, Integer> obtenerResumenAlertas() {
        Map<String, Integer> resumen = new HashMap<>();
        
        try {
            // Alertas críticas (alta prioridad y pendientes)
            int alertasCriticas = 0;
            List<Alerta> alertasAlta = alertaDAO.listarPorPrioridad("Alta");
            for (Alerta alerta : alertasAlta) {
                if ("Pendiente".equals(alerta.getEstado())) {
                    alertasCriticas++;
                }
            }
            resumen.put("alertasCriticas", alertasCriticas);
            
            // Stock bajo
            resumen.put("stockBajo", alertaDAO.contarAlertas("Stock", "Pendiente"));
            
            // Pagos pendientes
            resumen.put("pagosPendientes", alertaDAO.contarAlertas("Pago", "Pendiente"));
            
            // Expirando pronto (vencimientos)
            resumen.put("expirandoPronto", alertaDAO.contarAlertas("Vencimiento", "Pendiente"));
            
            // Total de alertas pendientes
            resumen.put("totalPendientes", alertaDAO.contarAlertas(null, "Pendiente"));
            
            return resumen;
        } catch (Exception e) {
            logger.error("Error al obtener resumen de alertas: {}", e.getMessage(), e);
            
            // Devolver valores por defecto
            resumen.put("alertasCriticas", 0);
            resumen.put("stockBajo", 0);
            resumen.put("pagosPendientes", 0);
            resumen.put("expirandoPronto", 0);
            resumen.put("totalPendientes", 0);
            
            return resumen;
        }
    }
    
    /**
     * Crea una alerta de stock bajo
     * @param codigoProducto Código del producto
     * @param nombreProducto Nombre del producto
     * @param stockActual Stock actual
     * @param stockMinimo Stock mínimo
     * @return La alerta creada o null si ocurre un error
     */
    public Alerta crearAlertaStockBajo(String codigoProducto, String nombreProducto, 
                                      int stockActual, int stockMinimo) {
        String descripcion = nombreProducto + " bajo stock mínimo (" + 
                            stockActual + " unidades, mínimo: " + stockMinimo + ")";
        
        return crearAlerta("Stock", descripcion, LocalDate.now(), "Alta", "Pendiente", codigoProducto);
    }
    
    /**
     * Crea una alerta de pago pendiente
     * @param numeroFactura Número de factura
     * @param diasVencimiento Días para vencimiento
     * @return La alerta creada o null si ocurre un error
     */
    public Alerta crearAlertaPagoPendiente(String numeroFactura, int diasVencimiento) {
        String descripcion = "Factura #" + numeroFactura + " vence en " + diasVencimiento + " días";
        String prioridad = diasVencimiento <= 3 ? "Alta" : (diasVencimiento <= 7 ? "Media" : "Baja");
        
        return crearAlerta("Pago", descripcion, LocalDate.now().plusDays(diasVencimiento), 
                          prioridad, "Pendiente", numeroFactura);
    }
    
    /**
     * Crea una alerta de vencimiento
     * @param descripcion Descripción del vencimiento
     * @param fechaVencimiento Fecha de vencimiento
     * @return La alerta creada o null si ocurre un error
     */
    public Alerta crearAlertaVencimiento(String descripcion, LocalDate fechaVencimiento) {
        int diasRestantes = (int) (fechaVencimiento.toEpochDay() - LocalDate.now().toEpochDay());
        String prioridad = diasRestantes <= 7 ? "Alta" : (diasRestantes <= 15 ? "Media" : "Baja");
        
        return crearAlerta("Vencimiento", descripcion, fechaVencimiento, prioridad, "Pendiente", null);
    }
    
    /**
     * Verifica y genera alertas de stock bajo para todos los productos
     * @return Número de alertas generadas
     */
    public int verificarStockBajoTodosProductos() {
        try {
            // Este método requeriría acceso al ProductoDAO
            // Implementación simplificada
            logger.info("Verificación de stock bajo para todos los productos");
            return 0;
        } catch (Exception e) {
            logger.error("Error al verificar stock bajo: {}", e.getMessage(), e);
            return 0;
        }
    }
}
