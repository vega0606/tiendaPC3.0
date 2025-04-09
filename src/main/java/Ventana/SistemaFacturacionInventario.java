package Ventana;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import Ventana.MenuLateral;
import Ventana.SistemaFacturacionInventario;
import Ventana.VistaAlertas;
import Ventana.VistaClientes;
import Ventana.VistaDevoluciones;
import Ventana.VistaExportImport;
import Ventana.VistaFacturacion;
import Ventana.VistaInventario;
import Ventana.VistaPedidos;
import Ventana.VistaReportes;
import Ventana.VistaTransacciones;
import Ventana.VistaUsuarios;

public class SistemaFacturacionInventario extends JFrame {

	private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel menuPanel;
    private Map<String, JPanel> vistas = new HashMap<>();
    
    public SistemaFacturacionInventario() {
        super("Sistema de Facturación e Inventario");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
     // Panel principal con CardLayout para cambiar entre vistas
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Panel de menú lateral
        MenuLateral menu = new MenuLateral(this);
        menuPanel = menu.crearMenuLateral();
        
        // Crear las diferentes vistas
        VistaFacturacion vistaFacturacion = new VistaFacturacion();
        mainPanel.add(vistaFacturacion.getPanel(), "Facturación");
        vistas.put("Facturación", vistaFacturacion.getPanel());
        
        VistaInventario vistaInventario = new VistaInventario();
        mainPanel.add(vistaInventario.getPanel(), "Inventario");
        vistas.put("Inventario", vistaInventario.getPanel());
        
        VistaReportes vistaReportes = new VistaReportes();
        mainPanel.add(vistaReportes.getPanel(), "Reportes");
        vistas.put("Reportes", vistaReportes.getPanel());
        
        VistaClientes vistaClientes = new VistaClientes();
        mainPanel.add(vistaClientes.getPanel(), "Clientes/Proveedores");
        vistas.put("Clientes/Proveedores", vistaClientes.getPanel());
        
        VistaPedidos vistaPedidos = new VistaPedidos();
        mainPanel.add(vistaPedidos.getPanel(), "Pedidos");
        vistas.put("Pedidos", vistaPedidos.getPanel());
        
        VistaDevoluciones vistaDevoluciones = new VistaDevoluciones();
        mainPanel.add(vistaDevoluciones.getPanel(), "Devoluciones");
        vistas.put("Devoluciones", vistaDevoluciones.getPanel());
        
        VistaAlertas vistaAlertas = new VistaAlertas();
        mainPanel.add(vistaAlertas.getPanel(), "Alertas");
        vistas.put("Alertas", vistaAlertas.getPanel());
        
        VistaTransacciones vistaTransacciones = new VistaTransacciones();
        mainPanel.add(vistaTransacciones.getPanel(), "Transacciones");
        vistas.put("Transacciones", vistaTransacciones.getPanel());
        
        VistaUsuarios vistaUsuarios = new VistaUsuarios();
        mainPanel.add(vistaUsuarios.getPanel(), "Usuarios");
        vistas.put("Usuarios", vistaUsuarios.getPanel());
        
        VistaExportImport vistaExportImport = new VistaExportImport();
        mainPanel.add(vistaExportImport.getPanel(), "Export/Import");
        vistas.put("Export/Import", vistaExportImport.getPanel());
        
        // Layout principal
        setLayout(new BorderLayout());
        add(menuPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        
        // Mostrar primera vista por defecto
        cardLayout.show(mainPanel, "Facturación");
    }
    
    public void mostrarVista(String nombreVista) {
        cardLayout.show(mainPanel, nombreVista);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SistemaFacturacionInventario sistema = new SistemaFacturacionInventario();
            sistema.setVisible(true);
        });
    }
	
}
