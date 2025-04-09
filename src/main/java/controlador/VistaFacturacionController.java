package controlador;

import modelo.Cliente;
import modelo.DetalleFactura;
import modelo.Factura;
import modelo.Producto;
import ventana.VistaFacturacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador para la vista de facturación
 */
public class VistaFacturacionController {
    private static final Logger logger = LoggerFactory.getLogger(VistaFacturacionController.class);
    
    private VistaFacturacion vista;
    private FacturaController facturaController;
    private ClienteController clienteController;
    private ProductoController productoController;
    
    private Cliente clienteActual;
    private DefaultTableModel modeloTablaProductos;
    private List<DetalleFactura> detallesFactura;
    
    public VistaFacturacionController(VistaFacturacion vista) {
        this.vista = vista;
        this.facturaController = new FacturaController();
        this.clienteController = new ClienteController();
        this.productoController = new ProductoController();
        this.detallesFactura = new ArrayList<>();
    }
    
    /**
     * Inicializa la vista
     */
    public void inicializar() {
        // Configurar la tabla de productos
        JTable tablaProductos = vista.getProductTable();
        modeloTablaProductos = (DefaultTableModel) tablaProductos.getModel();
        
        // Configurar el campo de fecha con la fecha actual
        JTextField fechaField = vista.getFechaField();
        fechaField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        // Configurar eventos
        JTextField clienteField = vista.getClienteField();
        JTextField rucField = vista.getRucField();
        JButton buscarClienteButton = vista.getBuscarClienteButton();
        JButton nuevoClienteButton = vista.getNuevoClienteButton();
        JButton agregarProductoButton = vista.getAgregarProductoButton();
        JButton guardarButton = vista.getGuardarButton();
        JButton cancelarButton = vista.getCancelarButton();
        
        // Evento para buscar cliente
        buscarClienteButton.addActionListener(e -> buscarCliente());
        
        // Evento para nuevo cliente
        nuevoClienteButton.addActionListener(e -> nuevoCliente());
        
        // Evento para agregar producto
        agregarProductoButton.addActionListener(e -> agregarProducto());
        
        // Evento para guardar factura
        guardarButton.addActionListener(e -> guardarFactura());
        
        // Evento para cancelar
        cancelarButton.addActionListener(e -> limpiarFormulario());
        
        // Eventos para eliminar producto de la tabla
        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int columna = tablaProductos.getColumnModel().getColumnIndexAtX(evt.getX());
                int fila = evt.getY() / tablaProductos.getRowHeight();
                
                if (fila < tablaProductos.getRowCount() && fila >= 0 && 
                    columna < tablaProductos.getColumnCount() && columna >= 0) {
                    // Si se hace clic en la columna "Acciones" (última columna)
                    if (columna == tablaProductos.getColumnCount() - 1) {
                        eliminarProducto(fila);
                    }
                }
            }
        });
    }
    
    /**
     * Busca un cliente por su RUC
     */
    private void buscarCliente() {
        try {
            String ruc = vista.getRucField().getText().trim();
            
            if (ruc.isEmpty()) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Ingrese el RUC/NIT del cliente",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            Cliente cliente = clienteController.buscarClientePorRuc(ruc);
            
            if (cliente == null) {
                int respuesta = JOptionPane.showConfirmDialog(vista.getPanel(),
                    "Cliente no encontrado. ¿Desea crear un nuevo cliente?",
                    "Cliente no encontrado",
                    JOptionPane.YES_NO_OPTION);
                
                if (respuesta == JOptionPane.YES_OPTION) {
                    nuevoCliente();
                }
            } else {
                mostrarCliente(cliente);
            }
        } catch (Exception e) {
            logger.error("Error al buscar cliente: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(vista.getPanel(),
                "Error al buscar cliente: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Muestra la información del cliente en el formulario
     * @param cliente Cliente a mostrar
     */
    private void mostrarCliente(Cliente cliente) {
        clienteActual = cliente;
        vista.getClienteField().setText(cliente.getNombre());
        vista.getRucField().setText(cliente.getRuc());
    }
    
    /**
     * Abre el diálogo para crear un nuevo cliente
     */
    private void nuevoCliente() {
        // Aquí se abriría un diálogo para crear un nuevo cliente
        // Por simplicidad, mostramos un diálogo básico
        String nombre = JOptionPane.showInputDialog(vista.getPanel(),
            "Nombre del cliente:",
            "Nuevo Cliente",
            JOptionPane.QUESTION_MESSAGE);
        
        if (nombre != null && !nombre.trim().isEmpty()) {
            String ruc = vista.getRucField().getText().trim();
            if (ruc.isEmpty()) {
                ruc = JOptionPane.showInputDialog(vista.getPanel(),
                    "RUC/NIT del cliente:",
                    "Nuevo Cliente",
                    JOptionPane.QUESTION_MESSAGE);
            }
            
            if (ruc != null && !ruc.trim().isEmpty()) {
                Cliente nuevoCliente = clienteController.crearCliente(
                    nombre, "", "", "", ruc);
                
                if (nuevoCliente != null) {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Cliente creado exitosamente",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    mostrarCliente(nuevoCliente);
                } else {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Error al crear el cliente",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Agrega un producto a la factura
     */
    private void agregarProducto() {
        // Aquí se abriría un diálogo para buscar y agregar un producto
        // Por simplicidad, mostramos un diálogo básico
        String codigo = JOptionPane.showInputDialog(vista.getPanel(),
            "Código del producto:",
            "Agregar Producto",
            JOptionPane.QUESTION_MESSAGE);
        
        if (codigo != null && !codigo.trim().isEmpty()) {
            Producto producto = productoController.obtenerProducto(codigo);
            
            if (producto != null) {
                String cantidadStr = JOptionPane.showInputDialog(vista.getPanel(),
                    "Cantidad:",
                    "Agregar Producto",
                    JOptionPane.QUESTION_MESSAGE);
                
                try {
                    int cantidad = Integer.parseInt(cantidadStr);
                    
                    if (cantidad <= 0) {
                        JOptionPane.showMessageDialog(vista.getPanel(),
                            "La cantidad debe ser mayor a 0",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (!facturaController.validarStock(codigo, cantidad)) {
                        JOptionPane.showMessageDialog(vista.getPanel(),
                            "Stock insuficiente",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    DetalleFactura detalle = facturaController.crearDetalleFactura(codigo, cantidad);
                    
                    if (detalle != null) {
                        agregarDetalleATabla(detalle);
                        detallesFactura.add(detalle);
                        actualizarTotales();
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Cantidad inválida",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Producto no encontrado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Agrega un detalle a la tabla de productos
     * @param detalle Detalle a agregar
     */
    private void agregarDetalleATabla(DetalleFactura detalle) {
        Producto producto = detalle.getProducto();
        
        Object[] rowData = {
            producto.getCodigo(),
            producto.getNombre(),
            detalle.getCantidad(),
            detalle.getPrecioUnitario().toString(),
            detalle.getIva().toString(),
            detalle.getSubtotal().toString(),
            "Eliminar" // Texto para el botón de eliminar
        };
        
        modeloTablaProductos.addRow(rowData);
    }
    
    /**
     * Elimina un producto de la tabla y de la lista de detalles
     * @param fila Fila a eliminar
     */
    private void eliminarProducto(int fila) {
        if (fila >= 0 && fila < detallesFactura.size()) {
            detallesFactura.remove(fila);
            modeloTablaProductos.removeRow(fila);
            actualizarTotales();
        }
    }
    
    /**
     * Actualiza los totales de la factura
     */
    private void actualizarTotales() {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal iva = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        
        for (DetalleFactura detalle : detallesFactura) {
            subtotal = subtotal.add(detalle.getSubtotal());
            iva = iva.add(detalle.getIva());
        }
        
        total = subtotal.add(iva);
        
        // Actualizar etiquetas de totales (si existieran en la vista)
        // vista.getSubtotalLabel().setText(subtotal.toString());
        // vista.getIvaLabel().setText(iva.toString());
        // vista.getTotalLabel().setText(total.toString());
    }
    
    /**
     * Guarda la factura
     */
    private void guardarFactura() {
        try {
            // Validar cliente
            if (clienteActual == null) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Seleccione un cliente",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar productos
            if (detallesFactura.isEmpty()) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Agregue al menos un producto",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Obtener fecha
            LocalDate fecha;
            try {
                fecha = LocalDate.parse(vista.getFechaField().getText(), 
                                      DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Formato de fecha inválido. Use YYYY-MM-DD",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear la factura
            Factura factura = facturaController.crearFactura(
                clienteActual, 
                fecha, 
                "", // Observaciones
                detallesFactura
            );
            
            if (factura != null) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Factura " + factura.getNumero() + " creada exitosamente",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Error al crear la factura",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("Error al guardar factura: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(vista.getPanel(),
                "Error al guardar factura: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Limpia el formulario
     */
    private void limpiarFormulario() {
        clienteActual = null;
        vista.getClienteField().setText("");
        vista.getRucField().setText("");
        vista.getFechaField().setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        
        // Limpiar tabla de productos
        while (modeloTablaProductos.getRowCount() > 0) {
            modeloTablaProductos.removeRow(0);
        }
        
        // Limpiar lista de detalles
        detallesFactura.clear();
        
        // Actualizar totales
        actualizarTotales();
    }
    
    /**
     * Actualiza los campos de la vista con la información de una factura existente
     * @param numeroFactura Número de factura a cargar
     */
    public void cargarFactura(String numeroFactura) {
        try {
            Factura factura = facturaController.obtenerFactura(numeroFactura);
            
            if (factura != null) {
                // Limpiar formulario primero
                limpiarFormulario();
                
                // Mostrar datos de la factura
                clienteActual = factura.getCliente();
                
                if (clienteActual != null) {
                    vista.getClienteField().setText(clienteActual.getNombre());
                    vista.getRucField().setText(clienteActual.getRuc());
                }
                
                vista.getFechaField().setText(factura.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE));
                
                // Cargar detalles
                for (DetalleFactura detalle : factura.getDetalles()) {
                    detallesFactura.add(detalle);
                    agregarDetalleATabla(detalle);
                }
                
                // Actualizar totales
                actualizarTotales();
                
                // Si la factura está anulada o cerrada, deshabilitar edición
                if ("Anulada".equals(factura.getEstado()) || "Cerrada".equals(factura.getEstado())) {
                    deshabilitarEdicion();
                }
            } else {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Factura no encontrada",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("Error al cargar factura: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(vista.getPanel(),
                "Error al cargar factura: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Deshabilita la edición del formulario
     */
    private void deshabilitarEdicion() {
        vista.getClienteField().setEnabled(false);
        vista.getRucField().setEnabled(false);
        vista.getFechaField().setEnabled(false);
        vista.getBuscarClienteButton().setEnabled(false);
        vista.getNuevoClienteButton().setEnabled(false);
        vista.getAgregarProductoButton().setEnabled(false);
        vista.getGuardarButton().setEnabled(false);
        
        // Deshabilitar eliminación de productos en la tabla
    }
}
