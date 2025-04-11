package ventana;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Vista para gestionar las devoluciones de la tienda de PC
 */
public class VistaDevoluciones {
    
    private JPanel mainPanel;
    private JTable tablaDevoluciones;
    private JScrollPane scrollPane;
    private JButton btnRegistrar, btnAprobar, btnRechazar, btnVerDetalle, btnBuscar, btnVolver;
    private JTextField txtBuscar;
    private JComboBox<String> comboEstado;
    
    /**
     * Constructor de la vista de devoluciones
     */
    public VistaDevoluciones() {
        inicializarComponentes();
        configurarEventos();
    }
    
    /**
     * Inicializa los componentes de la interfaz
     */
    private void inicializarComponentes() {
        // Panel principal
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Panel superior para búsqueda y filtros
        JPanel panelSuperior = new JPanel(new BorderLayout());
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel();
        txtBuscar = new JTextField(20);
        btnBuscar = new JButton("Buscar");
        panelBusqueda.add(new JLabel("Buscar: "));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelSuperior.add(panelBusqueda, BorderLayout.NORTH);
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel();
        panelFiltros.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>(new String[]{"Todos", "Pendiente", "Aprobada", "Rechazada"});
        panelFiltros.add(comboEstado);
        panelSuperior.add(panelFiltros, BorderLayout.SOUTH);
        
        mainPanel.add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de devoluciones
        String[] columnas = {"ID", "ID Venta", "Cliente", "Fecha", "Motivo", "Estado"};
        Object[][] datos = {}; // Sin datos iniciales
        
        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No permitir edición directa
            }
        };
        
        tablaDevoluciones = new JTable(modelo);
        tablaDevoluciones.getTableHeader().setReorderingAllowed(false);
        tablaDevoluciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        scrollPane = new JScrollPane(tablaDevoluciones);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior para botones
        JPanel panelInferior = new JPanel();
        btnRegistrar = new JButton("Registrar Devolución");
        btnAprobar = new JButton("Aprobar");
        btnRechazar = new JButton("Rechazar");
        btnVerDetalle = new JButton("Ver Detalle");
        btnVolver = new JButton("Volver");
        
        panelInferior.add(btnRegistrar);
        panelInferior.add(btnAprobar);
        panelInferior.add(btnRechazar);
        panelInferior.add(btnVerDetalle);
        panelInferior.add(btnVolver);
        
        mainPanel.add(panelInferior, BorderLayout.SOUTH);
    }
    
    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDialogoRegistrarDevolucion();
            }
        });
        
        btnAprobar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaDevoluciones.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    String estadoActual = (String) tablaDevoluciones.getValueAt(filaSeleccionada, 5);
                    if ("Pendiente".equals(estadoActual)) {
                        int confirmar = JOptionPane.showConfirmDialog(mainPanel,
                            "¿Está seguro de aprobar esta devolución?",
                            "Confirmar aprobación",
                            JOptionPane.YES_NO_OPTION);
                        
                        if (confirmar == JOptionPane.YES_OPTION) {
                            // Aquí iría la lógica para aprobar la devolución
                            tablaDevoluciones.setValueAt("Aprobada", filaSeleccionada, 5);
                            JOptionPane.showMessageDialog(mainPanel,
                                "Devolución aprobada correctamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(mainPanel,
                            "Solo se pueden aprobar devoluciones en estado Pendiente",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPanel,
                        "Debe seleccionar una devolución",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnRechazar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaDevoluciones.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    String estadoActual = (String) tablaDevoluciones.getValueAt(filaSeleccionada, 5);
                    if ("Pendiente".equals(estadoActual)) {
                        String motivo = JOptionPane.showInputDialog(mainPanel,
                            "Ingrese el motivo del rechazo:",
                            "Motivo de rechazo",
                            JOptionPane.QUESTION_MESSAGE);
                        
                        if (motivo != null && !motivo.trim().isEmpty()) {
                            // Aquí iría la lógica para rechazar la devolución
                            tablaDevoluciones.setValueAt("Rechazada", filaSeleccionada, 5);
                            JOptionPane.showMessageDialog(mainPanel,
                                "Devolución rechazada correctamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(mainPanel,
                            "Solo se pueden rechazar devoluciones en estado Pendiente",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPanel,
                        "Debe seleccionar una devolución",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnVerDetalle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaDevoluciones.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    // Aquí iría la lógica para mostrar el detalle de la devolución
                    JOptionPane.showMessageDialog(mainPanel,
                        "Detalles de la devolución " + tablaDevoluciones.getValueAt(filaSeleccionada, 0),
                        "Detalle de Devolución",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainPanel,
                        "Debe seleccionar una devolución",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String criterioBusqueda = txtBuscar.getText().trim();
                // Aquí iría la lógica para buscar devoluciones
                JOptionPane.showMessageDialog(mainPanel,
                    "Buscando: " + criterioBusqueda,
                    "Búsqueda",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        comboEstado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String estadoSeleccionado = (String) comboEstado.getSelectedItem();
                // Aquí iría la lógica para filtrar por estado
                JOptionPane.showMessageDialog(mainPanel,
                    "Filtrando por estado: " + estadoSeleccionado,
                    "Filtro aplicado",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // No hacemos nada específico para volver
            }
        });
    }
    
    /**
     * Muestra un diálogo para registrar una nueva devolución
     */
    private void mostrarDialogoRegistrarDevolucion() {
        JDialog dialogo = new JDialog((JFrame)SwingUtilities.getWindowAncestor(mainPanel), "Registrar Devolución", true);
        dialogo.setSize(400, 300);
        dialogo.setLocationRelativeTo(mainPanel);
        dialogo.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("ID Venta:"));
        JTextField txtIdVenta = new JTextField();
        panel.add(txtIdVenta);
        
        panel.add(new JLabel("Cliente:"));
        JTextField txtCliente = new JTextField();
        panel.add(txtCliente);
        
        panel.add(new JLabel("Fecha:"));
        JTextField txtFecha = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtFecha.setEditable(false);
        panel.add(txtFecha);
        
        panel.add(new JLabel("Motivo:"));
        JComboBox<String> comboMotivo = new JComboBox<>(new String[]{
            "Producto defectuoso", 
            "Producto incorrecto", 
            "Insatisfacción con el producto", 
            "Otro"
        });
        panel.add(comboMotivo);
        
        panel.add(new JLabel("Descripción:"));
        JTextArea txtDescripcion = new JTextArea();
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        panel.add(scrollDescripcion);
        
        dialogo.add(panel, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtIdVenta.getText().trim().isEmpty() || txtCliente.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo,
                        "Los campos ID Venta y Cliente son obligatorios",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Aquí iría la lógica para guardar la devolución
                // Por ahora solo agregamos una fila a la tabla como ejemplo
                DefaultTableModel modelo = (DefaultTableModel) tablaDevoluciones.getModel();
                Object[] fila = {
                    modelo.getRowCount() + 1,
                    txtIdVenta.getText(),
                    txtCliente.getText(),
                    txtFecha.getText(),
                    comboMotivo.getSelectedItem(),
                    "Pendiente"
                };
                modelo.addRow(fila);
                
                JOptionPane.showMessageDialog(dialogo,
                    "Devolución registrada correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialogo.dispose();
            }
        });
        
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogo.dispose();
            }
        });
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
    
    /**
     * Actualiza la tabla de devoluciones con nuevos datos
     * @param datos Los datos a mostrar en la tabla
     */
    public void actualizarTablaDevoluciones(Object[][] datos) {
        String[] columnas = {"ID", "ID Venta", "Cliente", "Fecha", "Motivo", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDevoluciones.setModel(modelo);
    }
    
    /**
     * Retorna el panel principal de la vista
     * @return El panel principal
     */
    public JPanel getPanel() {
        return mainPanel;
    }
}