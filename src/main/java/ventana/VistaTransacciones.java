package ventana;  

import javax.swing.*;

import controlador.VistaTransaccionesController;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Vista para gestionar las transacciones de la tienda de PC
 */
public class VistaTransacciones {
    
    private JPanel mainPanel;
    private JTable tablaTransacciones;
    private JScrollPane scrollPane;
    private JButton btnAgregar, btnEliminar, btnBuscar, btnVolver;
    private JTextField txtBuscar;
    private VistaTransaccionesController controller;
    
    /**
     * Constructor de la vista de transacciones
     */
    public VistaTransacciones() {
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
        
        // Panel superior para búsqueda
        JPanel panelSuperior = new JPanel();
        txtBuscar = new JTextField(20);
        btnBuscar = new JButton("Buscar");
        panelSuperior.add(new JLabel("Buscar: "));
        panelSuperior.add(txtBuscar);
        panelSuperior.add(btnBuscar);
        mainPanel.add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de transacciones (con modelo de datos vacío por ahora)
        tablaTransacciones = new JTable();
        scrollPane = new JScrollPane(tablaTransacciones);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior para botones
        JPanel panelInferior = new JPanel();
        btnAgregar = new JButton("Nueva Transacción");
        btnEliminar = new JButton("Eliminar");
        btnVolver = new JButton("Volver");
        panelInferior.add(btnAgregar);
        panelInferior.add(btnEliminar);
        panelInferior.add(btnVolver);
        mainPanel.add(panelInferior, BorderLayout.SOUTH);
        
        // Inicializar el controlador
        controller = new VistaTransaccionesController(this);
    }
    
    /**
     * Configura los eventos de los componentes
     */
    private void configurarEventos() {
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.nuevaTransaccion();
            }
        });
        
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaTransacciones.getSelectedRow();
                controller.eliminarTransaccion(filaSeleccionada);
            }
        });
        
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String criterioBusqueda = txtBuscar.getText();
                controller.buscarTransacciones(criterioBusqueda);
            }
        });
        
        btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // No cerramos ningún frame, ya que ahora es un panel
            }
        });
    }
    
    /**
     * Actualiza la tabla de transacciones con nuevos datos
     * @param datos Los datos a mostrar en la tabla
     * @param columnas Los nombres de las columnas
     */
    public void actualizarTabla(Object[][] datos, String[] columnas) {
        tablaTransacciones.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));
    }
    
    /**
     * Muestra un mensaje al usuario
     * @param mensaje El mensaje a mostrar
     * @param tipo El tipo de mensaje (JOptionPane constants)
     */
    public void mostrarMensaje(String mensaje, int tipo) {
        JOptionPane.showMessageDialog(mainPanel, mensaje, "Sistema de Transacciones", tipo);
    }
    
    /**
     * Retorna la referencia a la tabla de transacciones
     * @return La tabla de transacciones
     */
    public JTable getTablaTransacciones() {
        return tablaTransacciones;
    }
    
    /**
     * Retorna el panel principal de la vista
     * @return El panel principal
     */
    public JPanel getPanel() {
        return mainPanel;
    }
}