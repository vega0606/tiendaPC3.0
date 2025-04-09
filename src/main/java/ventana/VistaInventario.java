package ventana;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;

import controlador.VistaInventarioController;

public class VistaInventario extends Vista {
    
    private JTextField searchField;
    private JButton searchButton;
    private JButton newProductButton;
    private JTable productTable;
    
    // Etiquetas para estadísticas
    private JLabel totalProductosLabel;
    private JLabel bajoStockLabel;
    private JLabel valorInventarioLabel;
    
    // Controlador
    private VistaInventarioController controller;
    
    public VistaInventario() {
        super("Control de Inventarios");
    }
    
    @Override
    protected void inicializarPanel() {
        // Header
        JPanel headerPanel = crearHeaderPanel();
        
        // Panel izquierdo - Opciones
        String[] sidebarItems = {
            "Productos", "Entradas/Salidas", "Stock Mínimo", 
            "Valoración", "Ajustes"
        };
        JPanel sidebarPanel = crearSidebarPanel(sidebarItems);
        
        // Panel principal - Listado de productos
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainContentPanel.setLayout(new BorderLayout());
        
        // Título
        JLabel titleLabel = new JLabel("Listado de Productos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Panel de estadísticas
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        
        // Tarjeta 1: Total productos
        JPanel card1 = createStatsCard("Total Productos", "0", new Color(74, 134, 232, 60), new Color(74, 134, 232));
        totalProductosLabel = (JLabel) findLabelInPanel(card1, "0");
        statsPanel.add(card1);
        
        // Tarjeta 2: Productos bajo stock
        JPanel card2 = createStatsCard("Bajo Stock", "0", new Color(244, 67, 54, 60), new Color(244, 67, 54));
        bajoStockLabel = (JLabel) findLabelInPanel(card2, "0");
        statsPanel.add(card2);
        
        // Tarjeta 3: Valor total
        JPanel card3 = createStatsCard("Valor Inventario", "$0.00", new Color(76, 175, 80, 60), new Color(76, 175, 80));
        valorInventarioLabel = (JLabel) findLabelInPanel(card3, "$0.00");
        statsPanel.add(card3);
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        searchButton = new JButton("Buscar");
        searchButton.setBackground(new Color(74, 134, 232));
        searchButton.setForeground(Color.WHITE);
        searchPanel.add(searchButton);
        
        newProductButton = new JButton("Nuevo Producto");
        newProductButton.setBackground(new Color(74, 134, 232));
        newProductButton.setForeground(Color.WHITE);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(newProductButton);
        
        // Tabla de productos
        String[] columnNames = {"Código", "Producto", "Categoría", "Stock", "Precio Compra", "Precio Venta", "Estado", "Acciones"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        
        productTable = new JTable(tableModel);
        
        // Colorear la columna de estado
        productTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if ("Activo".equals(value)) {
                    label.setBackground(new Color(106, 168, 79));
                    label.setForeground(Color.WHITE);
                } else if ("Bajo stock".equals(value)) {
                    label.setBackground(new Color(255, 173, 51));
                    label.setForeground(Color.WHITE);
                } else if ("Inactivo".equals(value)) {
                    label.setBackground(new Color(204, 65, 37));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);
                }
                
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
        });
        
        // Columna de acciones
        ButtonRenderer buttonRenderer = new ButtonRenderer();
        productTable.getColumnModel().getColumn(7).setCellRenderer(buttonRenderer);
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        
        // Añadir componentes al panel principal
        JPanel headerSearchPanel = new JPanel(new BorderLayout());
        headerSearchPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel headerContentPanel = new JPanel(new BorderLayout());
        headerContentPanel.add(statsPanel, BorderLayout.NORTH);
        headerContentPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        headerContentPanel.add(searchPanel, BorderLayout.SOUTH);
        
        headerSearchPanel.add(headerContentPanel, BorderLayout.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(headerSearchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainContentPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Añadir todo al panel principal
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(sidebarPanel, BorderLayout.WEST);
        panel.add(mainContentPanel, BorderLayout.CENTER);
        
        // Inicializar controlador
        controller = new VistaInventarioController(this);
        controller.inicializar();
    }
    
    /**
     * Crea una tarjeta de estadísticas
     * @param title Título
     * @param value Valor
     * @param bgColor Color de fondo
     * @param fgColor Color de texto
     * @return Panel con la tarjeta
     */
    private JPanel createStatsCard(String title, String value, Color bgColor, Color fgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fgColor, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(fgColor);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(fgColor);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Busca un JLabel en un panel por su texto
     * @param panel Panel donde buscar
     * @param text Texto a buscar
     * @return JLabel encontrado o null
     */
    private Component findLabelInPanel(JPanel panel, String text) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (text.equals(label.getText())) {
                    return label;
                }
            }
        }
        return null;
    }
    
    /**
     * Actualiza las estadísticas mostradas
     * @param totalProductos Total de productos
     * @param productosBajoStock Productos con bajo stock
     * @param valorTotal Valor total del inventario
     */
    public void actualizarEstadisticas(int totalProductos, int productosBajoStock, BigDecimal valorTotal) {
        totalProductosLabel.setText(String.valueOf(totalProductos));
        bajoStockLabel.setText(String.valueOf(productosBajoStock));
        valorInventarioLabel.setText("$" + valorTotal.toString());
    }
    
    // Getters para que el controlador pueda acceder a los componentes
    public JTextField getSearchField() {
        return searchField;
    }
    
    public JButton getSearchButton() {
        return searchButton;
    }
    
    public JButton getNewProductButton() {
        return newProductButton;
    }
    
    public JTable getProductTable() {
        return productTable;
    }
    
    /**
     * Renderer para los botones de acciones
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(74, 134, 232));
            setForeground(Color.WHITE);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
       boolean isSelected, boolean hasFocus,
       int row, int column) {
            setText("Opciones");
            return this;
        }
    }
}