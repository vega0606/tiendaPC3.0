package ventana;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Vista para gestionar los pedidos de la tienda de PC
 */
public class VistaPedidos {
    
    private JPanel mainPanel;
    private JTable tablaPedidos;
    private JScrollPane scrollPane;
    private JButton btnNuevo, btnEliminar, btnModificar, btnVerDetalle, btnBuscar, btnVolver;
    private JTextField txtBuscar;
    private JComboBox<String> comboEstado;
    private JPanel panelFiltros;
    
    /**
     * Constructor de la vista de pedidos
     */
    public VistaPedidos() {
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
        panelFiltros = new JPanel();
        panelFiltros.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>(new String[]{"Todos", "Pendiente", "En proceso", "Completado", "Cancelado"});
        panelFiltros.add(comboEstado);
        panelSuperior.add(panelFiltros, BorderLayout.SOUTH);
        
        mainPanel.add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de pedidos
        String[] columnas = {"ID", "Cliente", "Fecha", "Total", "Estado"};
        Object[][] datos = {}; // Sin datos iniciales
        
        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No permitir edición directa
            }
        };
        
        tablaPedidos = new JTable(modelo);
        tablaPedidos.getTableHeader().setReorderingAllowed(false);
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        scrollPane = new JScrollPane(tablaPedidos);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior para botones
        JPanel panelInferior = new JPanel();
        btnNuevo = new JButton("Nuevo Pedido");
        btnEliminar = new JButton("Eliminar");
        btnModificar = new JButton("Modificar Estado");
        btnVerDetalle = new JButton("Ver Detalle");
        btnVolver = new JButton("Volver");
        
        panelInferior.add(btnNuevo);
        panelInferior.add(btnEliminar);
        panelInferior.add(btnModificar);
        panelInferior.add(btnVerDetalle);
        panelInferior.add(btnVolver);
        
        mainPanel.add(panelInferior, BorderLayout.SOUTH);
    }
    
    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        btnNuevo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí irá la lógica para crear un nuevo pedido
                JOptionPane.showMessageDialog(mainPanel, 
                    "Función para crear nuevo pedido", 
                    "Nuevo Pedido", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaPedidos.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    int confirmar = JOptionPane.showConfirmDialog(mainPanel,
                        "¿Está seguro de eliminar este pedido?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirmar == JOptionPane.YES_OPTION) {
                        // Aquí iría la lógica para eliminar el pedido
                        ((DefaultTableModel)tablaPedidos.getModel()).removeRow(filaSeleccionada);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPanel,
                        "Debe seleccionar un pedido",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaPedidos.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    String[] opciones = {"Pendiente", "En proceso", "Completado", "Cancelado"};
                    String nuevoEstado = (String) JOptionPane.showInputDialog(
                        mainPanel,
                        "Seleccione el nuevo estado:",
                        "Modificar Estado",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]);
                    
                    if (nuevoEstado != null) {
                        // Aquí iría la lógica para actualizar el estado
                        tablaPedidos.getModel().setValueAt(nuevoEstado, filaSeleccionada, 4);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPanel,
                        "Debe seleccionar un pedido",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnVerDetalle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaPedidos.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    // Aquí iría la lógica para mostrar el detalle del pedido
                    JOptionPane.showMessageDialog(mainPanel,
                        "Detalles del pedido " + tablaPedidos.getValueAt(filaSeleccionada, 0),
                        "Detalle de Pedido",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainPanel,
                        "Debe seleccionar un pedido",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String criterioBusqueda = txtBuscar.getText().trim();
                // Aquí iría la lógica para buscar pedidos
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
     * Actualiza la tabla de pedidos con nuevos datos
     * @param datos Los datos a mostrar en la tabla
     */
    public void actualizarTablaPedidos(Object[][] datos) {
        String[] columnas = {"ID", "Cliente", "Fecha", "Total", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPedidos.setModel(modelo);
    }
    
    /**
     * Retorna el panel principal de la vista
     * @return El panel principal
     */
    public JPanel getPanel() {
        return mainPanel;
    }
    
}