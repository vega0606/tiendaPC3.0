package Ventana;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import Controlador.VistaFacturacionController;

public class VistaFacturacion extends Vista {
    private JTextField clienteField;
    private JTextField rucField;
    private JTextField fechaField;
    private JTable productTable;
    private JButton buscarClienteButton;
    private JButton nuevoClienteButton;
    private JButton agregarProductoButton;
    private JButton guardarButton;
    private JButton cancelarButton;
    private JLabel subtotalLabel;
    private JLabel ivaLabel;
    private JLabel totalLabel;
    
    // Controlador
    private VistaFacturacionController controller;
    
    public VistaFacturacion() {
        super("Gestión de Facturación");
    }
    
    @Override
    protected void inicializarPanel() {
        // Header
        JPanel headerPanel = crearHeaderPanel();
        
        // Panel izquierdo - Opciones
        String[] sidebarItems = {
            "Nueva Factura", "Consultar Facturas", "Anular Factura", 
            "Resumen de Ventas", "Imprimir Facturas"
        };
        JPanel sidebarPanel = crearSidebarPanel(sidebarItems);
        
        // Panel principal - Formulario de factura
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainContentPanel.setLayout(new BorderLayout());
        
        // Título
        JLabel titleLabel = new JLabel("Nueva Factura");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Cliente
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Cliente:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        clienteField = new JTextField(20);
        clienteField.setEditable(false);
        formPanel.add(clienteField, gbc);
        
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        buscarClienteButton = new JButton("Buscar");
        buscarClienteButton.setBackground(new Color(74, 134, 232));
        buscarClienteButton.setForeground(Color.WHITE);
        formPanel.add(buscarClienteButton, gbc);
        
        gbc.gridx = 3;
        nuevoClienteButton = new JButton("Nuevo");
        nuevoClienteButton.setBackground(new Color(74, 134, 232));
        nuevoClienteButton.setForeground(Color.WHITE);
        formPanel.add(nuevoClienteButton, gbc);
        
        // RUC/NIT
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("RUC/NIT:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        rucField = new JTextField(10);
        formPanel.add(rucField, gbc);
        
        // Fecha
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Fecha:"), gbc);
        
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        fechaField = new JTextField(10);
        fechaField.setText(java.time.LocalDate.now().toString());
        formPanel.add(fechaField, gbc);
        
        // Tabla de productos
        String[] columnNames = {"Código", "Descripción", "Cantidad", "Precio Unit.", "IVA", "Subtotal", "Acciones"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 6; // La columna de acciones no es editable
            }
        };
        
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        
        // Panel de totales
        JPanel totalsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        totalsPanel.add(new JLabel("Subtotal:"));
        subtotalLabel = new JLabel("$0.00");
        subtotalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalsPanel.add(subtotalLabel);
        
        totalsPanel.add(Box.createHorizontalStrut(20));
        totalsPanel.add(new JLabel("IVA:"));
        ivaLabel = new JLabel("$0.00");
        ivaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalsPanel.add(ivaLabel);
        
        totalsPanel.add(Box.createHorizontalStrut(20));
        totalsPanel.add(new JLabel("Total:"));
        totalLabel = new JLabel("$0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalsPanel.add(totalLabel);
        
        // Panel de acciones para productos
        JPanel productActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        agregarProductoButton = new JButton("Agregar Producto");
        agregarProductoButton.setBackground(new Color(74, 134, 232));
        agregarProductoButton.setForeground(Color.WHITE);
        productActionsPanel.add(agregarProductoButton);
        
        // Botones de acción
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelarButton = new JButton("Cancelar");
        cancelarButton.setBackground(new Color(244, 67, 54));
        cancelarButton.setForeground(Color.WHITE);
        
        guardarButton = new JButton("Guardar");
        guardarButton.setBackground(new Color(74, 134, 232));
        guardarButton.setForeground(Color.WHITE);
        
        buttonPanel.add(cancelarButton);
        buttonPanel.add(guardarButton);
        
        // Añadir componentes al panel principal
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.PAGE_START);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(productActionsPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(totalsPanel, BorderLayout.SOUTH);
        
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainContentPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Añadir todo al panel principal
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(sidebarPanel, BorderLayout.WEST);
        panel.add(mainContentPanel, BorderLayout.CENTER);
        
        // Inicializar controlador
        controller = new VistaFacturacionController(this);
        controller.inicializar();
    }
    
    // Getters para que el controlador pueda acceder a los componentes
    public JTextField getClienteField() {
        return clienteField;
    }
    
    public JTextField getRucField() {
        return rucField;
    }
    
    public JTextField getFechaField() {
        return fechaField;
    }
    
    public JTable getProductTable() {
        return productTable;
    }
    
    public JButton getBuscarClienteButton() {
        return buscarClienteButton;
    }
    
    public JButton getNuevoClienteButton() {
        return nuevoClienteButton;
    }
    
    public JButton getAgregarProductoButton() {
        return agregarProductoButton;
    }
    
    public JButton getGuardarButton() {
        return guardarButton;
    }
    
    public JButton getCancelarButton() {
        return cancelarButton;
    }
    
    public JLabel getSubtotalLabel() {
        return subtotalLabel;
    }
    
    public JLabel getIvaLabel() {
        return ivaLabel;
    }
    
    public JLabel getTotalLabel() {
        return totalLabel;
    }
    
    /**
     * Carga una factura existente en la vista
     * @param numeroFactura Número de factura a cargar
     */
    public void cargarFactura(String numeroFactura) {
        controller.cargarFactura(numeroFactura);
    }
}
