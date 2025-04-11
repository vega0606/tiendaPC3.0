package controlador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import ventana.VistaTransacciones;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * Controlador para la vista de transacciones
 */
public class VistaTransaccionesController {
    
    private VistaTransacciones vista;
    private List<Transaccion> transacciones;
    
    /**
     * Constructor del controlador
     * @param vista La vista de transacciones asociada
     */
    public VistaTransaccionesController(VistaTransacciones vista) {
        this.vista = vista;
        this.transacciones = new ArrayList<>();
        cargarDatosDePrueba();
        actualizarTabla();
    }
    
    /**
     * Carga datos de prueba para la tabla de transacciones
     */
    private void cargarDatosDePrueba() {
        // Esto simularía la carga desde una base de datos
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Random random = new Random();
        
        try {
            transacciones.add(new Transaccion(1, "Venta", 1500.0, sdf.parse("2025-04-10"), "Venta de componentes"));
            transacciones.add(new Transaccion(2, "Compra", 3200.0, sdf.parse("2025-04-09"), "Compra de inventario"));
            transacciones.add(new Transaccion(3, "Venta", 850.75, sdf.parse("2025-04-08"), "Venta de periféricos"));
            transacciones.add(new Transaccion(4, "Devolución", 450.0, sdf.parse("2025-04-07"), "Devolución por fallo"));
            transacciones.add(new Transaccion(5, "Venta", 2100.0, sdf.parse("2025-04-06"), "Venta de equipo completo"));
        } catch (Exception e) {
            System.err.println("Error al cargar datos de prueba: " + e.getMessage());
        }
    }
    
    /**
     * Crea una nueva transacción
     */
    public void nuevaTransaccion() {
        // Aquí se mostraría un diálogo para ingresar los datos de la transacción
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(vista.getPanel()), "Nueva Transacción", true);
        dialogo.setSize(400, 300);
        dialogo.setLocationRelativeTo(vista.getPanel());
        dialogo.setLayout(new java.awt.BorderLayout());
        
        JPanel panel = new JPanel(new java.awt.GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Tipo:"));
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Venta", "Compra", "Devolución", "Ajuste"});
        panel.add(comboTipo);
        
        panel.add(new JLabel("Monto:"));
        JTextField txtMonto = new JTextField();
        panel.add(txtMonto);
        
        panel.add(new JLabel("Fecha:"));
        JTextField txtFecha = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtFecha.setEditable(false);
        panel.add(txtFecha);
        
        panel.add(new JLabel("Descripción:"));
        JTextField txtDescripcion = new JTextField();
        panel.add(txtDescripcion);
        
        dialogo.add(panel, java.awt.BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(e -> {
            try {
                String tipo = (String) comboTipo.getSelectedItem();
                double monto = Double.parseDouble(txtMonto.getText());
                Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse(txtFecha.getText());
                String descripcion = txtDescripcion.getText();
                
                // Generar un ID único (en una aplicación real esto vendría de la base de datos)
                int nuevoId = transacciones.size() > 0 ? 
                    transacciones.get(transacciones.size() - 1).getId() + 1 : 1;
                
                // Crear y agregar la nueva transacción
                Transaccion nuevaTransaccion = new Transaccion(nuevoId, tipo, monto, fecha, descripcion);
                transacciones.add(nuevaTransaccion);
                
                // Actualizar la tabla
                actualizarTabla();
                
                JOptionPane.showMessageDialog(dialogo, 
                    "Transacción registrada correctamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialogo.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, 
                    "El monto debe ser un número válido", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, 
                    "Error al guardar la transacción: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        dialogo.add(panelBotones, java.awt.BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
    
    /**
     * Elimina una transacción seleccionada
     * @param filaSeleccionada Índice de la fila seleccionada en la tabla
     */
    public void eliminarTransaccion(int filaSeleccionada) {
        if (filaSeleccionada >= 0 && filaSeleccionada < transacciones.size()) {
            int confirmar = JOptionPane.showConfirmDialog(vista.getPanel(),
                "¿Está seguro de eliminar esta transacción?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmar == JOptionPane.YES_OPTION) {
                transacciones.remove(filaSeleccionada);
                actualizarTabla();
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Transacción eliminada correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            vista.mostrarMensaje(
                "Debe seleccionar una transacción",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Busca transacciones según un criterio
     * @param criterioBusqueda El texto a buscar
     */
    public void buscarTransacciones(String criterioBusqueda) {
        if (criterioBusqueda == null || criterioBusqueda.trim().isEmpty()) {
            actualizarTabla();
            return;
        }
        
        criterioBusqueda = criterioBusqueda.toLowerCase();
        List<Transaccion> resultados = new ArrayList<>();
        
        for (Transaccion t : transacciones) {
            if (String.valueOf(t.getId()).contains(criterioBusqueda) ||
                t.getTipo().toLowerCase().contains(criterioBusqueda) ||
                t.getDescripcion().toLowerCase().contains(criterioBusqueda) ||
                String.valueOf(t.getMonto()).contains(criterioBusqueda)) {
                
                resultados.add(t);
            }
        }
        
        actualizarTablaConResultados(resultados);
    }
    
    /**
     * Actualiza la tabla con la lista completa de transacciones
     */
    private void actualizarTabla() {
        actualizarTablaConResultados(transacciones);
    }
    
    /**
     * Actualiza la tabla con una lista específica de transacciones
     * @param listaTransacciones Lista de transacciones a mostrar
     */
    private void actualizarTablaConResultados(List<Transaccion> listaTransacciones) {
        String[] columnas = {"ID", "Tipo", "Monto", "Fecha", "Descripción"};
        Object[][] datos = new Object[listaTransacciones.size()][columnas.length];
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (int i = 0; i < listaTransacciones.size(); i++) {
            Transaccion t = listaTransacciones.get(i);
            datos[i][0] = t.getId();
            datos[i][1] = t.getTipo();
            datos[i][2] = String.format("$%.2f", t.getMonto());
            datos[i][3] = sdf.format(t.getFecha());
            datos[i][4] = t.getDescripcion();
        }
        
        vista.actualizarTabla(datos, columnas);
    }
    
    /**
     * Clase interna para representar una transacción
     */
    private class Transaccion {
        private int id;
        private String tipo;
        private double monto;
        private Date fecha;
        private String descripcion;
        
        public Transaccion(int id, String tipo, double monto, Date fecha, String descripcion) {
            this.id = id;
            this.tipo = tipo;
            this.monto = monto;
            this.fecha = fecha;
            this.descripcion = descripcion;
        }
        
        public int getId() {
            return id;
        }
        
        public String getTipo() {
            return tipo;
        }
        
        public double getMonto() {
            return monto;
        }
        
        public Date getFecha() {
            return fecha;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
}