package Ventana;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class Vista {
	protected JPanel panel;
    protected String titulo;
    
    public Vista(String titulo) {
        this.titulo = titulo;
        this.panel = new JPanel(new BorderLayout());
        inicializarPanel();
    }
    
    protected JPanel crearHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(74, 134, 232));
        headerPanel.setPreferredSize(new Dimension(0, 40));
        JLabel headerLabel = new JLabel("  " + titulo);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        return headerPanel;
    }
    
    protected JPanel crearSidebarPanel(String[] items) {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(51, 51, 51));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        
        for (String item : items) {
            JButton sidebarButton = new JButton(item);
            sidebarButton.setForeground(Color.WHITE);
            sidebarButton.setBackground(new Color(51, 51, 51));
            sidebarButton.setFocusPainted(false);
            sidebarButton.setBorderPainted(false);
            sidebarButton.setHorizontalAlignment(SwingConstants.LEFT);
            sidebarButton.setMaximumSize(new Dimension(200, 30));
            
            sidebarButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    sidebarButton.setBackground(new Color(74, 134, 232));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    sidebarButton.setBackground(new Color(51, 51, 51));
                }
            });
            
            sidebarPanel.add(sidebarButton);
        }
        
        return sidebarPanel;
    }
    
    public JPanel getPanel() {
        return panel;
    }
    
    protected abstract void inicializarPanel();
}

