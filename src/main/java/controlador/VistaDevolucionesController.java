package controlador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import ventana.VistaDevoluciones;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Controlador para la vista de devoluciones
 */
public class VistaDevolucionesController {
    
    private VistaDevoluciones vista;
    private List<Devolucion> devoluciones;
    
    /**
     * Constructor del controlador
     * @param vista La vista de devoluciones asociada
     */
    public VistaDevolucionesController(VistaDevoluciones vista) {
        this.vista = vista;
        this.devoluciones = new ArrayList<>();
        cargarDatosDePrueba();
        actualizarTabla();
    }
    
    /**
     * Carga datos de prueba para la tabla de devoluciones
     */
    private void cargarDatosDePrueba() {
        // Esto simularía la carga desde una base de datos
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
            devoluciones.add(new Devolucion(1, "V100", "Juan Pérez", sdf.parse("2025-04-10"), "Producto defectuoso", "Pendiente"));
            devoluciones.add(new Devolucion(2, "V101", "María López", sdf.parse("2025-04-09"), "Producto incorrecto", "Aprobada"));
            devoluciones.add(new Devolucion(3, "V102", "Carlos Gómez", sdf.parse("2025-04-08"), "Insatisfacción con el producto", "Rechazada"));
            devoluciones.add(new Devolucion(4, "V103", "Ana Martínez", sdf.parse("2025-04-07"), "Otro", "Pendiente"));
        } catch (Exception e) {
            System.err.println("Error al cargar datos de prueba: " + e.getMessage());
        }
    }
    
    /**
     * Registra una nueva devolución
     */
    public void registrarDevolucion(String idVenta, String cliente, Date fecha, String motivo, String descripcion) {
        // Verificar que los campos obligatorios no estén vacíos
        if (idVenta == null || idVenta.trim().isEmpty() || cliente == null || cliente.trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista.getPanel(),
                "Los campos ID Venta y Cliente son obligatorios",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Generar un nuevo ID
        int nuevoId = devoluciones.size() > 0 ? 
            devoluciones.get(devoluciones.size() - 1).getId() + 1 : 1;
        
        // Crear y agregar la nueva devolución
        Devolucion nuevaDevolucion = new Devolucion(nuevoId, idVenta, cliente, fecha, motivo, "Pendiente");
        devoluciones.add(nuevaDevolucion);
        
        // Actualizar la tabla
        actualizarTabla();
        
        JOptionPane.showMessageDialog(vista.getPanel(), 
            "Devolución registrada correctamente", 
            "Éxito", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Aprueba una devolución seleccionada
     * @param filaSeleccionada Índice de la fila seleccionada en la tabla
     */
    public void aprobarDevolucion(int filaSeleccionada) {
        if (filaSeleccionada >= 0 && filaSeleccionada < devoluciones.size()) {
            Devolucion devolucion = devoluciones.get(filaSeleccionada);
            
            if ("Pendiente".equals(devolucion.getEstado())) {
                int confirmar = JOptionPane.showConfirmDialog(vista.getPanel(),
                    "¿Está seguro de aprobar esta devolución?",
                    "Confirmar aprobación",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirmar == JOptionPane.YES_OPTION) {
                    devolucion.setEstado("Aprobada");
                    actualizarTabla();
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Devolución aprobada correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Solo se pueden aprobar devoluciones en estado Pendiente",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(vista.getPanel(),
                "Debe seleccionar una devolución",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Rechaza una devolución seleccionada
     * @param filaSeleccionada Índice de la fila seleccionada en la tabla
     * @param motivo Motivo del rechazo
     */
    public void rechazarDevolucion(int filaSeleccionada, String motivo) {
        if (filaSeleccionada >= 0 && filaSeleccionada < devoluciones.size()) {
            Devolucion devolucion = devoluciones.get(filaSeleccionada);
            
            if ("Pendiente".equals(devolucion.getEstado())) {
                if (motivo != null && !motivo.trim().isEmpty()) {
                    devolucion.setEstado("Rechazada");
                    actualizarTabla();
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Devolución rechazada correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Debe ingresar un motivo para rechazar la devolución",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Solo se pueden rechazar devoluciones en estado Pendiente",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(vista.getPanel(),
                "Debe seleccionar una devolución",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Muestra el detalle de una devolución seleccionada
     * @param filaSeleccionada Índice de la fila seleccionada en la tabla
     */
    public void verDetalleDevolucion(int filaSeleccionada) {
        if (filaSeleccionada >= 0 && filaSeleccionada < devoluciones.size()) {
            Devolucion devolucion = devoluciones.get(filaSeleccionada);
            
            // Aquí se mostraría un diálogo con los detalles de la devolución
            // Por ahora solo mostramos un mensaje
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            JOptionPane.showMessageDialog(vista.getPanel(),
                "Detalles de la Devolución #" + devolucion.getId() + "\n\n" +
                "ID Venta: " + devolucion.getIdVenta() + "\n" +
                "Cliente: " + devolucion.getCliente() + "\n" +
                "Fecha: " + sdf.format(devolucion.getFecha()) + "\n" +
                "Motivo: " + devolucion.getMotivo() + "\n" +
                "Estado: " + devolucion.getEstado(),
                "Detalle de Devolución",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vista.getPanel(),
                "Debe seleccionar una devolución",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Busca devoluciones según un criterio
     * @param criterioBusqueda El texto a buscar
     */
    public void buscarDevoluciones(String criterioBusqueda) {
        if (criterioBusqueda == null || criterioBusqueda.trim().isEmpty()) {
            actualizarTabla();
            return;
        }
        
        criterioBusqueda = criterioBusqueda.toLowerCase();
        List<Devolucion> resultados = new ArrayList<>();
        
        for (Devolucion d : devoluciones) {
            if (String.valueOf(d.getId()).contains(criterioBusqueda) ||
                d.getIdVenta().toLowerCase().contains(criterioBusqueda) ||
                d.getCliente().toLowerCase().contains(criterioBusqueda) ||
                d.getMotivo().toLowerCase().contains(criterioBusqueda) ||
                d.getEstado().toLowerCase().contains(criterioBusqueda)) {
                
                resultados.add(d);
            }
        }
        
        actualizarTablaConResultados(resultados);
    }
    
    /**
     * Filtra las devoluciones por estado
     * @param estado El estado por el que filtrar
     */
    public void filtrarPorEstado(String estado) {
        if ("Todos".equals(estado)) {
            actualizarTabla();
            return;
        }
        
        List<Devolucion> resultados = new ArrayList<>();
        
        for (Devolucion d : devoluciones) {
            if (d.getEstado().equals(estado)) {
                resultados.add(d);
            }
        }
        
        actualizarTablaConResultados(resultados);
    }
    
    /**
     * Actualiza la tabla con la lista completa de devoluciones
     */
    private void actualizarTabla() {
        actualizarTablaConResultados(devoluciones);
    }
    
    /**
     * Actualiza la tabla con una lista específica de devoluciones
     * @param listaDevoluciones Lista de devoluciones a mostrar
     */
    private void actualizarTablaConResultados(List<Devolucion> listaDevoluciones) {
        Object[][] datos = new Object[listaDevoluciones.size()][6];
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (int i = 0; i < listaDevoluciones.size(); i++) {
            Devolucion d = listaDevoluciones.get(i);
            datos[i][0] = d.getId();
            datos[i][1] = d.getIdVenta();
            datos[i][2] = d.getCliente();
            datos[i][3] = sdf.format(d.getFecha());
            datos[i][4] = d.getMotivo();
            datos[i][5] = d.getEstado();
        }
        
        vista.actualizarTablaDevoluciones(datos);
    }
    
    /**
     * Clase interna para representar una devolución
     */
    private class Devolucion {
        private int id;
        private String idVenta;
        private String cliente;
        private Date fecha;
        private String motivo;
        private String estado;
        
        public Devolucion(int id, String idVenta, String cliente, Date fecha, String motivo, String estado) {
            this.id = id;
            this.idVenta = idVenta;
            this.cliente = cliente;
            this.fecha = fecha;
            this.motivo = motivo;
            this.estado = estado;
        }
        
        public int getId() {
            return id;
        }
        
        public String getIdVenta() {
            return idVenta;
        }
        
        public String getCliente() {
            return cliente;
        }
        
        public Date getFecha() {
            return fecha;
        }
        
        public String getMotivo() {
            return motivo;
        }
        
        public String getEstado() {
            return estado;
        }
        
        public void setEstado(String estado) {
            this.estado = estado;
        }
    }
}
