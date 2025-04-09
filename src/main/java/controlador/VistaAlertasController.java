package controlador;

import modelo.Alerta;
import Ventana.VistaAlertas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la vista de alertas
 */
public class VistaAlertasController {
    private VistaAlertas vista;
    private AlertaController alertaController;
    private DefaultTableModel tableModel;
    
    public VistaAlertasController(VistaAlertas vista) {
        this.vista = vista;
        this.alertaController = new AlertaController();
    }
    
    /**
     * Inicializa la vista
     */
    public void inicializar() {
        // Configurar controladores de eventos en la vista
        JComboBox<String> filterCombo = vista.getFilterCombo();
        JComboBox<String> priorityCombo = vista.getPriorityCombo();
        JButton refreshButton = vista.getRefreshButton();
        
        filterCombo.addActionListener(e -> aplicarFiltros());
        priorityCombo.addActionListener(e -> aplicarFiltros());
        refreshButton.addActionListener(e -> actualizarDatos());
        
        // Configurar la tabla de alertas
        configurarTablaAlertas();
        
        // Cargar datos iniciales
        actualizarDatos();
    }
    
    /**
     * Actualiza todos los datos de la vista
     */
    public void actualizarDatos() {
        actualizarResumenAlertas();
        aplicarFiltros();
    }
    
    /**
     * Actualiza el resumen de alertas (tarjetas)
     */
    private void actualizarResumenAlertas() {
        try {
            Map<String, Integer> resumen = alertaController.obtenerResumenAlertas();
            
            // Actualizar valores en las tarjetas de resumen
            vista.actualizarTarjetaResumen("Alertas Críticas", 
                                         String.valueOf(resumen.get("alertasCriticas")));
            
            vista.actualizarTarjetaResumen("Stock Bajo", 
                                         String.valueOf(resumen.get("stockBajo")));
            
            vista.actualizarTarjetaResumen("Pagos Pendientes", 
                                         String.valueOf(resumen.get("pagosPendientes")));
            
            vista.actualizarTarjetaResumen("Expirando Pronto", 
                                         String.valueOf(resumen.get("expirandoPronto")));
        } catch (Exception e) {
            mostrarError("Error al actualizar resumen de alertas", e);
        }
    }
    
    /**
     * Aplica los filtros seleccionados a la tabla de alertas
     */
    private void aplicarFiltros() {
        try {
            String tipoFiltro = (String) vista.getFilterCombo().getSelectedItem();
            String prioridadFiltro = (String) vista.getPriorityCombo().getSelectedItem();
            
            List<Alerta> alertas;
            
            // Filtrar por tipo
            if (tipoFiltro != null && !tipoFiltro.equals("Todas")) {
                alertas = alertaController.listarAlertasPorTipo(tipoFiltro);
            } else {
                alertas = alertaController.listarAlertas();
            }
            
            // Limpiar tabla
            tableModel.setRowCount(0);
            
            // Filtrar por prioridad y llenar tabla
            for (Alerta alerta : alertas) {
                if (prioridadFiltro != null && !prioridadFiltro.equals("Todas") && 
                    !alerta.getPrioridad().equals(prioridadFiltro)) {
                    continue;
                }
                
                Object[] rowData = {
                    alerta.getId(),
                    alerta.getTipo(),
                    alerta.getDescripcion(),
                    alerta.getFecha().toString(),
                    alerta.getPrioridad(),
                    alerta.getEstado(),
                    "Acciones" // Este campo se maneja con un renderer especial
                };
                
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            mostrarError("Error al aplicar filtros", e);
        }
    }
    
    /**
     * Configura la tabla de alertas
     */
    private void configurarTablaAlertas() {
        JTable alertsTable = vista.getAlertsTable();
        tableModel = (DefaultTableModel) alertsTable.getModel();
        
        // Configurar acciones para la columna de acciones
        // Esto se hace en la clase de vista con un botón o iconos
    }
    
    /**
     * Atiende una alerta
     * @param id ID de la alerta
     */
    public void atenderAlerta(String id) {
        try {
            if (alertaController.cambiarEstadoAlerta(id, "Atendida")) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Alerta marcada como atendida.",
                    "Alerta Atendida",
                    JOptionPane.INFORMATION_MESSAGE);
                actualizarDatos();
            } else {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "No se pudo atender la alerta.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            mostrarError("Error al atender alerta", e);
        }
    }
    
    /**
     * Elimina una alerta
     * @param id ID de la alerta
     */
    public void eliminarAlerta(String id) {
        try {
            int confirmacion = JOptionPane.showConfirmDialog(vista.getPanel(),
                "¿Está seguro de eliminar esta alerta?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                if (alertaController.eliminarAlerta(id)) {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Alerta eliminada correctamente.",
                        "Alerta Eliminada",
                        JOptionPane.INFORMATION_MESSAGE);
                    actualizarDatos();
                } else {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "No se pudo eliminar la alerta.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            mostrarError("Error al eliminar alerta", e);
        }
    }
    
    /**
     * Muestra un diálogo de error
     * @param mensaje Mensaje de error
     * @param e Excepción (opcional)
     */
    private void mostrarError(String mensaje, Exception e) {
        String detalles = e != null ? ": " + e.getMessage() : "";
        JOptionPane.showMessageDialog(vista.getPanel(),
            mensaje + detalles,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
