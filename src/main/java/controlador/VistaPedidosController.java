package controlador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import ventana.VistaPedidos;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Controlador para la vista de pedidos
 */
public class VistaPedidosController {
    
    private VistaPedidos vista;
    private List<Pedido> pedidos;
    
    /**
     * Constructor del controlador
     * @param vista La vista de pedidos asociada
     */
    public VistaPedidosController(VistaPedidos vista) {
        this.vista = vista;
        this.pedidos = new ArrayList<>();
        cargarDatosDePrueba();
        actualizarTabla();
    }
    
    /**
     * Carga datos de prueba para la tabla de pedidos
     */
    private void cargarDatosDePrueba() {
        // Esto simularía la carga desde una base de datos
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
            pedidos.add(new Pedido(1, "Juan Pérez", sdf.parse("2025-04-10"), 1200.00, "Pendiente"));
            pedidos.add(new Pedido(2, "María López", sdf.parse("2025-04-09"), 850.50, "En proceso"));
            pedidos.add(new Pedido(3, "Carlos Gómez", sdf.parse("2025-04-08"), 3500.00, "Completado"));
            pedidos.add(new Pedido(4, "Ana Martínez", sdf.parse("2025-04-07"), 450.75, "Cancelado"));
            pedidos.add(new Pedido(5, "Pedro Rodríguez", sdf.parse("2025-04-06"), 1740.25, "Pendiente"));
        } catch (Exception e) {
            System.err.println("Error al cargar datos de prueba: " + e.getMessage());
        }
    }
    
    /**
     * Crea un nuevo pedido
     */
    public void nuevoPedido() {
        // Aquí se mostraría un diálogo para ingresar los datos del pedido
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(vista.getPanel()), "Nuevo Pedido", true);
        dialogo.setSize(400, 300);
        dialogo.setLocationRelativeTo(vista.getPanel());
        dialogo.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Cliente:"));
        JTextField txtCliente = new JTextField();
        panel.add(txtCliente);
        
        panel.add(new JLabel("Fecha:"));
        JTextField txtFecha = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtFecha.setEditable(false);
        panel.add(txtFecha);
        
        panel.add(new JLabel("Total:"));
        JTextField txtTotal = new JTextField();
        panel.add(txtTotal);
        
        panel.add(new JLabel("Estado:"));
        JComboBox<String> comboEstado = new JComboBox<>(new String[]{"Pendiente", "En proceso", "Completado", "Cancelado"});
        panel.add(comboEstado);
        
        dialogo.add(panel, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(e -> {
            try {
                String cliente = txtCliente.getText();
                Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse(txtFecha.getText());
                double total = Double.parseDouble(txtTotal.getText());
                String estado = (String) comboEstado.getSelectedItem();
                
                if (cliente.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo,
                        "El nombre del cliente no puede estar vacío",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Generar un nuevo ID
                int nuevoId = pedidos.size() > 0 ? 
                    pedidos.get(pedidos.size() - 1).getId() + 1 : 1;
                
                // Crear y agregar el nuevo pedido
                Pedido nuevoPedido = new Pedido(nuevoId, cliente, fecha, total, estado);
                pedidos.add(nuevoPedido);
                
                // Actualizar la tabla
                actualizarTabla();
                
                JOptionPane.showMessageDialog(dialogo, 
                    "Pedido registrado correctamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialogo.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, 
                    "El total debe ser un número válido", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, 
                    "Error al guardar el pedido: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
    
    /**
     * Elimina un pedido seleccionado
     * @param filaSeleccionada Índice de la fila seleccionada en la tabla
     */
    public void eliminarPedido(int filaSeleccionada) {
        if (filaSeleccionada >= 0 && filaSeleccionada < pedidos.size()) {
            int confirmar = JOptionPane.showConfirmDialog(vista.getPanel(),
                "¿Está seguro de eliminar este pedido?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmar == JOptionPane.YES_OPTION) {
                pedidos.remove(filaSeleccionada);
                actualizarTabla();
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Pedido eliminado correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(vista.getPanel(),
                "Debe seleccionar un pedido",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Modifica el estado de un pedido seleccionado
     * @param filaSeleccionada Índice de la fila seleccionada en la tabla
     */
    public void modificarEstadoPedido(int filaSeleccionada) {
        if (filaSeleccionada >= 0 && filaSeleccionada < pedidos.size()) {
            String[] opciones = {"Pendiente", "En proceso", "Completado", "Cancelado"};
            String nuevoEstado = (String) JOptionPane.showInputDialog(
                vista.getPanel(),
                "Seleccione el nuevo estado:",
                "Modificar Estado",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                pedidos.get(filaSeleccionada).getEstado());
            
            if (nuevoEstado != null) {
                pedidos.get(filaSeleccionada).setEstado(nuevoEstado);
                actualizarTabla();
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Estado modificado correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(vista.getPanel(),
                "Debe seleccionar un pedido",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Muestra el detalle de un pedido seleccionado
     * @param filaSeleccionada Índice de la fila seleccionada en la tabla
     */
    public void verDetallePedido(int filaSeleccionada) {
        if (filaSeleccionada >= 0 && filaSeleccionada < pedidos.size()) {
            Pedido pedido = pedidos.get(filaSeleccionada);
            
            // Aquí se mostraría un diálogo con los detalles del pedido
            // Por ahora solo mostramos un mensaje
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            JOptionPane.showMessageDialog(vista.getPanel(),
                "Detalles del Pedido #" + pedido.getId() + "\n\n" +
                "Cliente: " + pedido.getCliente() + "\n" +
                "Fecha: " + sdf.format(pedido.getFecha()) + "\n" +
                "Total: $" + String.format("%.2f", pedido.getTotal()) + "\n" +
                "Estado: " + pedido.getEstado(),
                "Detalle de Pedido",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vista.getPanel(),
                "Debe seleccionar un pedido",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Busca pedidos según un criterio
     * @param criterioBusqueda El texto a buscar
     */
    public void buscarPedidos(String criterioBusqueda) {
        if (criterioBusqueda == null || criterioBusqueda.trim().isEmpty()) {
            actualizarTabla();
            return;
        }
        
        criterioBusqueda = criterioBusqueda.toLowerCase();
        List<Pedido> resultados = new ArrayList<>();
        
        for (Pedido p : pedidos) {
            if (String.valueOf(p.getId()).contains(criterioBusqueda) ||
                p.getCliente().toLowerCase().contains(criterioBusqueda) ||
                p.getEstado().toLowerCase().contains(criterioBusqueda) ||
                String.valueOf(p.getTotal()).contains(criterioBusqueda)) {
                
                resultados.add(p);
            }
        }
        
        actualizarTablaConResultados(resultados);
    }
    
    /**
     * Filtra los pedidos por estado
     * @param estado El estado por el que filtrar
     */
    public void filtrarPorEstado(String estado) {
        if ("Todos".equals(estado)) {
            actualizarTabla();
            return;
        }
        
        List<Pedido> resultados = new ArrayList<>();
        
        for (Pedido p : pedidos) {
            if (p.getEstado().equals(estado)) {
                resultados.add(p);
            }
        }
        
        actualizarTablaConResultados(resultados);
    }
    
    /**
     * Actualiza la tabla con la lista completa de pedidos
     */
    private void actualizarTabla() {
        actualizarTablaConResultados(pedidos);
    }
    
    /**
     * Actualiza la tabla con una lista específica de pedidos
     * @param listaPedidos Lista de pedidos a mostrar
     */
    private void actualizarTablaConResultados(List<Pedido> listaPedidos) {
        Object[][] datos = new Object[listaPedidos.size()][5];
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (int i = 0; i < listaPedidos.size(); i++) {
            Pedido p = listaPedidos.get(i);
            datos[i][0] = p.getId();
            datos[i][1] = p.getCliente();
            datos[i][2] = sdf.format(p.getFecha());
            datos[i][3] = String.format("$%.2f", p.getTotal());
            datos[i][4] = p.getEstado();
        }
        
        vista.actualizarTablaPedidos(datos);
    }
    
    /**
     * Clase interna para representar un pedido
     */
    private class Pedido {
        private int id;
        private String cliente;
        private Date fecha;
        private double total;
        private String estado;
        
        public Pedido(int id, String cliente, Date fecha, double total, String estado) {
            this.id = id;
            this.cliente = cliente;
            this.fecha = fecha;
            this.total = total;
            this.estado = estado;
        }
        
        public int getId() {
            return id;
        }
        
        public String getCliente() {
            return cliente;
        }
        
        public Date getFecha() {
            return fecha;
        }
        
        public double getTotal() {
            return total;
        }
        
        public String getEstado() {
            return estado;
        }
        
        public void setEstado(String estado) {
            this.estado = estado;
        }
    }
}