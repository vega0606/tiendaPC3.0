package Ventana;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.CellEditor;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import controlador.VistaAlertasController;

/**
 * Vista para la gestión de alertas del sistema
 */
public class VistaAlertas extends Vista {
    
    private JComboBox<String> filterCombo;
    private JComboBox<String> priorityCombo;
    private JButton refreshButton;
    private JTable alertsTable;
    private Map<String, JPanel> summaryCards;
    
    // Controlador
    private VistaAlertasController controller;
    
    public VistaAlertas() {
        super("Sistema de Alertas");
        summaryCards = new HashMap<>();
    }
    
    @Override
    protected void inicializarPanel() {
        // Header
        JPanel headerPanel = crearHeaderPanel();
        
        // Panel izquierdo - Opciones
        String[] sidebarItems = {
            "Todas las Alertas", "Stock Mínimo", "Pagos Pendientes", 
            "Vencimientos", "Configuración"
        };
        JPanel sidebarPanel = crearSidebarPanel(sidebarItems);
        
        // Panel principal - Dashboard de alertas
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainContentPanel.setLayout(new BorderLayout());
        
        // Título
        JLabel titleLabel = new JLabel("Panel de Alertas del Sistema");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Panel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filtrar por tipo:"));
        filterCombo = new JComboBox<>(new String[]{"Todas", "Stock", "Pago", "Vencimiento", "Sistema"});
        filterPanel.add(filterCombo);
        
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Prioridad:"));
        priorityCombo = new JComboBox<>(new String[]{"Todas", "Alta", "Media", "Baja"});
        filterPanel.add(priorityCombo);
        
        refreshButton = new JButton("Actualizar");
        refreshButton.setBackground(new Color(74, 134, 232));
        refreshButton.setForeground(Color.WHITE);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(refreshButton);
        
        // Paneles de resumen (tarjetas)
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        
        // Tarjeta 1: Alertas críticas
        JPanel card1 = createSummaryCard("Alertas Críticas", "8", new Color(244, 67, 54, 80), Color.RED);
        summaryCards.put("Alertas Críticas", card1);
        summaryPanel.add(card1);
        
        // Tarjeta 2: Stock bajo
        JPanel card2 = createSummaryCard("Stock Bajo", "15", new Color(255, 152, 0, 80), Color.ORANGE.darker());
        summaryCards.put("Stock Bajo", card2);
        summaryPanel.add(card2);
        
        // Tarjeta 3: Pagos pendientes
        JPanel card3 = createSummaryCard("Pagos Pendientes", "4", new Color(33, 150, 243, 80), Color.BLUE);
        summaryCards.put("Pagos Pendientes", card3);
        summaryPanel.add(card3);
        
        // Tarjeta 4: Expirando pronto
        JPanel card4 = createSummaryCard("Expirando Pronto", "6", new Color(76, 175, 80, 80), Color.GREEN.darker());
        summaryCards.put("Expirando Pronto", card4);
        summaryPanel.add(card4);
        
        // Tabla de alertas
        String[] columnNames = {"ID", "Tipo", "Descripción", "Fecha", "Prioridad", "Estado", "Acciones"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        alertsTable = new JTable(tableModel);
        
        // Colorear la columna de prioridad
        alertsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if ("Alta".equals(value)) {
                    label.setBackground(new Color(244, 67, 54));
                    label.setForeground(Color.WHITE);
                } else if ("Media".equals(value)) {
                    label.setBackground(new Color(255, 152, 0));
                    label.setForeground(Color.WHITE);
                } else if ("Baja".equals(value)) {
                    label.setBackground(new Color(76, 175, 80));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);
                }
                
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
        });
        
        // Colorear la columna de estado
        alertsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if ("Pendiente".equals(value)) {
                    label.setBackground(new Color(33, 150, 243));
                    label.setForeground(Color.WHITE);
                } else if ("Atendida".equals(value)) {
                    label.setBackground(new Color(76, 175, 80));
                    label.setForeground(Color.WHITE);
                } else if ("Programada".equals(value)) {
                    label.setBackground(new Color(156, 39, 176));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);
                }
                
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
        });
        
        // Renderer para la columna de acciones (botones)
        alertsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        alertsTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor());
        
        JScrollPane scrollPane = new JScrollPane(alertsTable);
        
        // Añadir componentes al panel principal
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel headerFilterPanel = new JPanel(new BorderLayout(0, 10));
        headerFilterPanel.add(titleLabel, BorderLayout.NORTH);
        headerFilterPanel.add(filterPanel, BorderLayout.CENTER);
        
        contentPanel.add(headerFilterPanel, BorderLayout.NORTH);
        contentPanel.add(summaryPanel, BorderLayout.PAGE_START);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainContentPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Añadir todo al panel principal
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(sidebarPanel, BorderLayout.WEST);
        panel.add(mainContentPanel, BorderLayout.CENTER);
        
        // Inicializar controlador
        controller = new VistaAlertasController(this);
        controller.inicializar();
    }
    
    private JPanel createSummaryCard(String title, String value, Color bgColor, Color fgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setName(title); // Nombre para identificar la tarjeta
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
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Actualiza el valor de una tarjeta de resumen
     * @param title Título de la tarjeta
     * @param value Nuevo valor a mostrar
     */
    public void actualizarTarjetaResumen(String title, String value) {
        JPanel card = summaryCards.get(title);
        if (card != null) {
            Component[] components = card.getComponents();
            for (Component c : components) {
                if (c instanceof JLabel && ((JLabel) c).getFont().getSize() > 20) {
                    ((JLabel) c).setText(value);
                    break;
                }
            }
        }
    }
    
    // Getters para que el controlador pueda acceder a los componentes
    public JComboBox<String> getFilterCombo() {
        return filterCombo;
    }
    
    public JComboBox<String> getPriorityCombo() {
        return priorityCombo;
    }
    
    public JButton getRefreshButton() {
        return refreshButton;
    }
    
    public JTable getAlertsTable() {
        return alertsTable;
    }
    
    /**
     * Renderer personalizado para mostrar botones en la tabla
     */
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton atenderButton;
        private JButton eliminarButton;
        
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            
            atenderButton = new JButton("Atender");
            atenderButton.setBackground(new Color(33, 150, 243));
            atenderButton.setForeground(Color.WHITE);
            atenderButton.setFocusPainted(false);
            
            eliminarButton = new JButton("Eliminar");
            eliminarButton.setBackground(new Color(244, 67, 54));
            eliminarButton.setForeground(Color.WHITE);
            eliminarButton.setFocusPainted(false);
            
            add(atenderButton);
            add(eliminarButton);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus, 
                                                      int row, int column) {
            // Determinar si la alerta está atendida o no
            String estado = (String) table.getValueAt(row, 5);
            atenderButton.setEnabled(!"Atendida".equals(estado));
            
            return this;
        }
    }
    
    /**
     * Editor personalizado para manejar los eventos de botones en la tabla
     */
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton atenderButton;
        private JButton eliminarButton;
        private String alertId;
        
        public ButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            
            atenderButton = new JButton("Atender");
            atenderButton.setBackground(new Color(33, 150, 243));
            atenderButton.setForeground(Color.WHITE);
            atenderButton.setFocusPainted(false);
            
            eliminarButton = new JButton("Eliminar");
            eliminarButton.setBackground(new Color(244, 67, 54));
            eliminarButton.setForeground(Color.WHITE);
            eliminarButton.setFocusPainted(false);
            
            // Añadir eventos a los botones
            atenderButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controller.atenderAlerta(alertId);
                    fireEditingStopped();
                }
            });
            
            eliminarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controller.eliminarAlerta(alertId);
                    fireEditingStopped();
                }
            });
            
            panel.add(atenderButton);
            panel.add(eliminarButton);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                    boolean isSelected, int row, int column) {
            // Guardar el ID de la alerta para usarlo en el evento
            alertId = (String) table.getValueAt(row, 0);
            
            // Determinar si la alerta está atendida o no
            String estado = (String) table.getValueAt(row, 5);
            atenderButton.setEnabled(!"Atendida".equals(estado));
            
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Acciones";
        }
    }
    
    /**
     * AbstractCellEditor implementación básica
     */
    abstract class AbstractCellEditor implements TableCellEditor {
        private EventListenerList listenerList = new EventListenerList();
        
        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }
        
        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }
        
        @Override
        public boolean stopCellEditing() {
            fireEditingStopped();
            return true;
        }
        
        @Override
        public void cancelCellEditing() {
            fireEditingCanceled();
        }
        
        @Override
        public void addCellEditorListener(CellEditorListener l) {
            listenerList.add(CellEditorListener.class, l);
        }
        
        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            listenerList.remove(CellEditorListener.class, l);
        }
        
        protected void fireEditingStopped() {
            CellEditorListener[] listeners = listenerList.getListeners(CellEditorListener.class);
            if (listeners != null && listeners.length > 0) {
                ChangeEvent event = new ChangeEvent(this);
                for (CellEditorListener listener : listeners) {
                    listener.editingStopped(event);
                }
            }
        }
        
        protected void fireEditingCanceled() {
            CellEditorListener[] listeners = listenerList.getListeners(CellEditorListener.class);
            if (listeners != null && listeners.length > 0) {
                ChangeEvent event = new ChangeEvent(this);
                for (CellEditorListener listener : listeners) {
                    listener.editingCanceled(event);
                }
            }
        }
    }
}
