package controlador;

import modelo.Cliente;
import modelo.Proveedor;
import Ventana.VistaClientes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador para la vista de clientes y proveedores
 */
public class VistaClientesController {
    private static final Logger logger = LoggerFactory.getLogger(VistaClientesController.class);
    
    private VistaClientes vista;
    private ClienteController clienteController;
    private ProveedorController proveedorController;
    private DefaultTableModel modeloTablaClientes;
    private DefaultTableModel modeloTablaProveedores;
    
    public VistaClientesController(VistaClientes vista) {
        this.vista = vista;
        this.clienteController = new ClienteController();
        this.proveedorController = new ProveedorController();
    }
    
    /**
     * Inicializa la vista
     */
    public void inicializar() {
        // Configurar las tablas
        JTable clientesTable = vista.getClientesTable();
        JTable proveedoresTable = vista.getProveedoresTable();
        
        modeloTablaClientes = (DefaultTableModel) clientesTable.getModel();
        modeloTablaProveedores = (DefaultTableModel) proveedoresTable.getModel();
        
        // Configurar eventos para clientes
        JTextField clienteSearchField = vista.getClienteSearchField();
        JButton clienteSearchButton = vista.getClienteSearchButton();
        JButton newClientButton = vista.getNewClientButton();
        
        clienteSearchButton.addActionListener(e -> buscarClientes(clienteSearchField.getText()));
        clienteSearchField.addActionListener(e -> buscarClientes(clienteSearchField.getText()));
        newClientButton.addActionListener(e -> nuevoCliente());
        
        // Configurar eventos para proveedores
        JTextField provSearchField = vista.getProvSearchField();
        JButton provSearchButton = vista.getProvSearchButton();
        JButton newProvButton = vista.getNewProvButton();
        
        provSearchButton.addActionListener(e -> buscarProveedores(provSearchField.getText()));
        provSearchField.addActionListener(e -> buscarProveedores(provSearchField.getText()));
        newProvButton.addActionListener(e -> nuevoProveedor());
        
        // Configurar eventos para botones en las tablas
        clientesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int columna = clientesTable.getColumnModel().getColumnIndexAtX(evt.getX());
                int fila = evt.getY() / clientesTable.getRowHeight();
                
                if (fila < clientesTable.getRowCount() && fila >= 0 && 
                    columna < clientesTable.getColumnCount() && columna >= 0) {
                    // Si se hace clic en la columna "Acciones" (última columna)
                    if (columna == clientesTable.getColumnCount() - 1) {
                        mostrarOpcionesCliente(fila);
                    }
                }
            }
        });
        
        proveedoresTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int columna = proveedoresTable.getColumnModel().getColumnIndexAtX(evt.getX());
                int fila = evt.getY() / proveedoresTable.getRowHeight();
                
                if (fila < proveedoresTable.getRowCount() && fila >= 0 && 
                    columna < proveedoresTable.getColumnCount() && columna >= 0) {
                    // Si se hace clic en la columna "Acciones" (última columna)
                    if (columna == proveedoresTable.getColumnCount() - 1) {
                        mostrarOpcionesProveedor(fila);
                    }
                }
            }
        });
        
        // Pestañas
        JTabbedPane tabbedPane = vista.getTabbedPane();
        tabbedPane.addChangeListener(e -> {
            int tabIndex = tabbedPane.getSelectedIndex();
            if (tabIndex == 0) { // Clientes
                cargarClientes();
            } else if (tabIndex == 1) { // Proveedores
                cargarProveedores();
            }
        });
        
        // Cargar datos iniciales
        cargarClientes();
    }
    
    /**
     * Carga la lista de clientes en la tabla
     */
    public void cargarClientes() {
        try {
            // Limpiar tabla
            while (modeloTablaClientes.getRowCount() > 0) {
                modeloTablaClientes.removeRow(0);
            }
            
            // Obtener clientes
            List<Cliente> clientes = clienteController.listarClientes();
            
            // Llenar tabla
            for (Cliente cliente : clientes) {
                Object[] rowData = {
                    cliente.getId(),
                    cliente.getNombre(),
                    cliente.getEmail(),
                    cliente.getTelefono(),
                    cliente.getDireccion(),
                    cliente.getEstado(),
                    "Acciones" // Este campo se maneja con un renderer especial
                };
                
                modeloTablaClientes.addRow(rowData);
            }
        } catch (Exception e) {
            logger.error("Error al cargar clientes: {}", e.getMessage(), e);
            mostrarError("Error al cargar clientes", e);
        }
    }
    
    /**
     * Carga la lista de proveedores en la tabla
     */
    public void cargarProveedores() {
        try {
            // Limpiar tabla
            while (modeloTablaProveedores.getRowCount() > 0) {
                modeloTablaProveedores.removeRow(0);
            }
            
            // Obtener proveedores
            List<Proveedor> proveedores = proveedorController.listarProveedores();
            
            // Llenar tabla
            for (Proveedor proveedor : proveedores) {
                Object[] rowData = {
                    proveedor.getId(),
                    proveedor.getEmpresa(),
                    proveedor.getContacto(),
                    proveedor.getEmail(),
                    proveedor.getTelefono(),
                    proveedor.getCategoria(),
                    proveedor.getEstado(),
                    "Acciones" // Este campo se maneja con un renderer especial
                };
                
                modeloTablaProveedores.addRow(rowData);
            }
        } catch (Exception e) {
            logger.error("Error al cargar proveedores: {}", e.getMessage(), e);
            mostrarError("Error al cargar proveedores", e);
        }
    }
    
    /**
     * Busca clientes por nombre
     * @param termino Término de búsqueda
     */
    private void buscarClientes(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            cargarClientes();
            return;
        }
        
        try {
            // Buscar clientes
            List<Cliente> clientes = clienteController.buscarClientesPorNombre(termino);
            
            // Limpiar tabla
            while (modeloTablaClientes.getRowCount() > 0) {
                modeloTablaClientes.removeRow(0);
            }
            
            // Si no hay clientes, mostrar mensaje
            if (clientes.isEmpty()) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "No se encontraron clientes con ese término de búsqueda",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                cargarClientes();
                return;
            }
            
            // Llenar tabla con clientes encontrados
            for (Cliente cliente : clientes) {
                Object[] rowData = {
                    cliente.getId(),
                    cliente.getNombre(),
                    cliente.getEmail(),
                    cliente.getTelefono(),
                    cliente.getDireccion(),
                    cliente.getEstado(),
                    "Acciones"
                };
                
                modeloTablaClientes.addRow(rowData);
            }
        } catch (Exception e) {
            logger.error("Error al buscar clientes: {}", e.getMessage(), e);
            mostrarError("Error al buscar clientes", e);
        }
    }
    
    /**
     * Busca proveedores por nombre o empresa
     * @param termino Término de búsqueda
     */
    private void buscarProveedores(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            cargarProveedores();
            return;
        }
        
        try {
            // Buscar proveedores (implementar método en ProveedorController)
            List<Proveedor> proveedores = proveedorController.buscarProveedoresPorNombre(termino);
            
            // Limpiar tabla
            while (modeloTablaProveedores.getRowCount() > 0) {
                modeloTablaProveedores.removeRow(0);
            }
            
            // Si no hay proveedores, mostrar mensaje
            if (proveedores.isEmpty()) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "No se encontraron proveedores con ese término de búsqueda",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                cargarProveedores();
                return;
            }
            
            // Llenar tabla con proveedores encontrados
            for (Proveedor proveedor : proveedores) {
                Object[] rowData = {
                    proveedor.getId(),
                    proveedor.getEmpresa(),
                    proveedor.getContacto(),
                    proveedor.getEmail(),
                    proveedor.getTelefono(),
                    proveedor.getCategoria(),
                    proveedor.getEstado(),
                    "Acciones"
                };
                
                modeloTablaProveedores.addRow(rowData);
            }
        } catch (Exception e) {
            logger.error("Error al buscar proveedores: {}", e.getMessage(), e);
            mostrarError("Error al buscar proveedores", e);
        }
    }
    
    /**
     * Muestra el diálogo para crear un nuevo cliente
     */
    private void nuevoCliente() {
        try {
            // Nombre
            String nombre = JOptionPane.showInputDialog(vista.getPanel(),
                "Nombre del cliente:",
                "Nuevo Cliente",
                JOptionPane.QUESTION_MESSAGE);
            
            if (nombre == null || nombre.trim().isEmpty()) {
                return;
            }
            
            // Email
            String email = JOptionPane.showInputDialog(vista.getPanel(),
                "Email:",
                "Nuevo Cliente",
                JOptionPane.QUESTION_MESSAGE);
            
            // Teléfono
            String telefono = JOptionPane.showInputDialog(vista.getPanel(),
                "Teléfono:",
                "Nuevo Cliente",
                JOptionPane.QUESTION_MESSAGE);
            
            // Dirección
            String direccion = JOptionPane.showInputDialog(vista.getPanel(),
                "Dirección:",
                "Nuevo Cliente",
                JOptionPane.QUESTION_MESSAGE);
            
            // RUC/NIT
            String ruc = JOptionPane.showInputDialog(vista.getPanel(),
                "RUC/NIT:",
                "Nuevo Cliente",
                JOptionPane.QUESTION_MESSAGE);
            
            // Crear cliente
            Cliente cliente = clienteController.crearCliente(nombre, email, telefono, direccion, ruc);
            
            if (cliente != null) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Cliente creado exitosamente",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarClientes();
            } else {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Error al crear el cliente",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("Error al crear cliente: {}", e.getMessage(), e);
            mostrarError("Error al crear cliente", e);
        }
    }
    
    /**
     * Muestra el diálogo para crear un nuevo proveedor
     */
    private void nuevoProveedor() {
        try {
            // Empresa
            String empresa = JOptionPane.showInputDialog(vista.getPanel(),
                "Nombre de la empresa:",
                "Nuevo Proveedor",
                JOptionPane.QUESTION_MESSAGE);
            
            if (empresa == null || empresa.trim().isEmpty()) {
                return;
            }
            
            // Contacto
            String contacto = JOptionPane.showInputDialog(vista.getPanel(),
                "Nombre del contacto:",
                "Nuevo Proveedor",
                JOptionPane.QUESTION_MESSAGE);
            
            // Email
            String email = JOptionPane.showInputDialog(vista.getPanel(),
                "Email:",
                "Nuevo Proveedor",
                JOptionPane.QUESTION_MESSAGE);
            
            // Teléfono
            String telefono = JOptionPane.showInputDialog(vista.getPanel(),
                "Teléfono:",
                "Nuevo Proveedor",
                JOptionPane.QUESTION_MESSAGE);
            
            // Dirección
            String direccion = JOptionPane.showInputDialog(vista.getPanel(),
                "Dirección:",
                "Nuevo Proveedor",
                JOptionPane.QUESTION_MESSAGE);
            
            // RUC/NIT
            String ruc = JOptionPane.showInputDialog(vista.getPanel(),
                "RUC/NIT:",
                "Nuevo Proveedor",
                JOptionPane.QUESTION_MESSAGE);
            
            // Categoría
            String categoria = JOptionPane.showInputDialog(vista.getPanel(),
                "Categoría:",
                "Nuevo Proveedor",
                JOptionPane.QUESTION_MESSAGE);
            
            // Crear proveedor
            Proveedor proveedor = proveedorController.crearProveedor(empresa, contacto, email, telefono, direccion, ruc, categoria);
            
            if (proveedor != null) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Proveedor creado exitosamente",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarProveedores();
            } else {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Error al crear el proveedor",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("Error al crear proveedor: {}", e.getMessage(), e);
            mostrarError("Error al crear proveedor", e);
        }
    }
    
    /**
     * Muestra opciones para un cliente seleccionado
     * @param fila Fila seleccionada en la tabla
     */
    private void mostrarOpcionesCliente(int fila) {
        String id = (String) modeloTablaClientes.getValueAt(fila, 0);
        String nombre = (String) modeloTablaClientes.getValueAt(fila, 1);
        
        String[] opciones = {"Editar", "Eliminar", "Ver Facturas", "Cancelar"};
        
        int seleccion = JOptionPane.showOptionDialog(vista.getPanel(),
            "Seleccione una acción para el cliente: " + nombre,
            "Opciones de Cliente",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]);
        
        switch (seleccion) {
            case 0: // Editar
                editarCliente(id);
                break;
            case 1: // Eliminar
                eliminarCliente(id, nombre);
                break;
            case 2: // Ver Facturas
                verFacturasCliente(id, nombre);
                break;
            default:
                // No hacer nada
                break;
        }
    }
    
    /**
     * Muestra opciones para un proveedor seleccionado
     * @param fila Fila seleccionada en la tabla
     */
    private void mostrarOpcionesProveedor(int fila) {
        String id = (String) modeloTablaProveedores.getValueAt(fila, 0);
        String empresa = (String) modeloTablaProveedores.getValueAt(fila, 1);
        
        String[] opciones = {"Editar", "Eliminar", "Ver Pedidos", "Cancelar"};
        
        int seleccion = JOptionPane.showOptionDialog(vista.getPanel(),
            "Seleccione una acción para el proveedor: " + empresa,
            "Opciones de Proveedor",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]);
        
        switch (seleccion) {
            case 0: // Editar
                editarProveedor(id);
                break;
            case 1: // Eliminar
                eliminarProveedor(id, empresa);
                break;
            case 2: // Ver Pedidos
                verPedidosProveedor(id, empresa);
                break;
            default:
                // No hacer nada
                break;
        }
    }
    
    /**
     * Abre el diálogo para editar un cliente
     * @param id ID del cliente a editar
     */
    private void editarCliente(String id) {
        try {
            Cliente cliente = clienteController.obtenerCliente(id);
            
            if (cliente == null) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Cliente no encontrado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Nombre
            String nombre = JOptionPane.showInputDialog(vista.getPanel(),
                "Nombre del cliente:",
                cliente.getNombre());
            
            if (nombre != null && !nombre.trim().isEmpty()) {
                cliente.setNombre(nombre);
            }
            
            // Email
            String email = JOptionPane.showInputDialog(vista.getPanel(),
                "Email:",
                cliente.getEmail());
            
            if (email != null) {
                cliente.setEmail(email);
            }
            
            // Teléfono
            String telefono = JOptionPane.showInputDialog(vista.getPanel(),
                "Teléfono:",
                cliente.getTelefono());
            
            if (telefono != null) {
                cliente.setTelefono(telefono);
            }
            
            // Dirección
            String direccion = JOptionPane.showInputDialog(vista.getPanel(),
                "Dirección:",
                cliente.getDireccion());
            
            if (direccion != null) {
                cliente.setDireccion(direccion);
            }
            
            // RUC/NIT
            String ruc = JOptionPane.showInputDialog(vista.getPanel(),
                "RUC/NIT:",
                cliente.getRuc());
            
            if (ruc != null) {
                cliente.setRuc(ruc);
            }
            
            // Estado
            String[] estados = {"Activo", "Inactivo"};
            String estado = (String) JOptionPane.showInputDialog(vista.getPanel(),
                "Estado:",
                "Editar Cliente",
                JOptionPane.QUESTION_MESSAGE,
                null,
                estados,
                cliente.getEstado());
            
            if (estado != null) {
                cliente.setEstado(estado);
            }
            
            // Actualizar cliente
            boolean actualizado = clienteController.actualizarCliente(cliente);
            
            if (actualizado) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Cliente actualizado exitosamente",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarClientes();
            } else {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Error al actualizar el cliente",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("Error al editar cliente: {}", e.getMessage(), e);
            mostrarError("Error al editar cliente", e);
        }
    }
    
    /**
     * Abre el diálogo para editar un proveedor
     * @param id ID del proveedor a editar
     */
    private void editarProveedor(String id) {
        try {
            Proveedor proveedor = proveedorController.obtenerProveedor(id);
            
            if (proveedor == null) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Proveedor no encontrado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Empresa
            String empresa = JOptionPane.showInputDialog(vista.getPanel(),
                "Nombre de la empresa:",
                proveedor.getEmpresa());
            
            if (empresa != null && !empresa.trim().isEmpty()) {
                proveedor.setEmpresa(empresa);
            }
            
            // Contacto
            String contacto = JOptionPane.showInputDialog(vista.getPanel(),
                "Nombre del contacto:",
                proveedor.getContacto());
            
            if (contacto != null) {
                proveedor.setContacto(contacto);
            }
            
            // Email
            String email = JOptionPane.showInputDialog(vista.getPanel(),
                "Email:",
                proveedor.getEmail());
            
            if (email != null) {
                proveedor.setEmail(email);
            }
            
            // Teléfono
            String telefono = JOptionPane.showInputDialog(vista.getPanel(),
                "Teléfono:",
                proveedor.getTelefono());
            
            if (telefono != null) {
                proveedor.setTelefono(telefono);
            }
            
            // Dirección
            String direccion = JOptionPane.showInputDialog(vista.getPanel(),
                "Dirección:",
                proveedor.getDireccion());
            
            if (direccion != null) {
                proveedor.setDireccion(direccion);
            }
            
            // RUC/NIT
            String ruc = JOptionPane.showInputDialog(vista.getPanel(),
                "RUC/NIT:",
                proveedor.getRuc());
            
            if (ruc != null) {
                proveedor.setRuc(ruc);
            }
            
            // Categoría
            String categoria = JOptionPane.showInputDialog(vista.getPanel(),
                "Categoría:",
                proveedor.getCategoria());
            
            if (categoria != null) {
                proveedor.setCategoria(categoria);
            }
            
            // Estado
            String[] estados = {"Activo", "Inactivo"};
            String estado = (String) JOptionPane.showInputDialog(vista.getPanel(),
                "Estado:",
                "Editar Proveedor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                estados,
                proveedor.getEstado());
            
            if (estado != null) {
                proveedor.setEstado(estado);
            }
            
            // Actualizar proveedor
            boolean actualizado = proveedorController.actualizarProveedor(proveedor);
            
            if (actualizado) {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Proveedor actualizado exitosamente",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarProveedores();
            } else {
                JOptionPane.showMessageDialog(vista.getPanel(),
                    "Error al actualizar el proveedor",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("Error al editar proveedor: {}", e.getMessage(), e);
            mostrarError("Error al editar proveedor", e);
        }
    }
    
    /**
     * Elimina un cliente
     * @param id ID del cliente a eliminar
     * @param nombre Nombre del cliente (para confirmación)
     */
    private void eliminarCliente(String id, String nombre) {
        try {
            int confirmacion = JOptionPane.showConfirmDialog(vista.getPanel(),
                "¿Está seguro de eliminar el cliente: " + nombre + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean eliminado = clienteController.eliminarCliente(id);
                
                if (eliminado) {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Cliente eliminado exitosamente",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarClientes();
                } else {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Error al eliminar el cliente",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            logger.error("Error al eliminar cliente: {}", e.getMessage(), e);
            mostrarError("Error al eliminar cliente", e);
        }
    }
    
    /**
     * Elimina un proveedor
     * @param id ID del proveedor a eliminar
     * @param empresa Nombre de la empresa (para confirmación)
     */
    private void eliminarProveedor(String id, String empresa) {
        try {
            int confirmacion = JOptionPane.showConfirmDialog(vista.getPanel(),
                "¿Está seguro de eliminar el proveedor: " + empresa + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean eliminado = proveedorController.eliminarProveedor(id);
                
                if (eliminado) {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Proveedor eliminado exitosamente",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarProveedores();
                } else {
                    JOptionPane.showMessageDialog(vista.getPanel(),
                        "Error al eliminar el proveedor",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            logger.error("Error al eliminar proveedor: {}", e.getMessage(), e);
            mostrarError("Error al eliminar proveedor", e);
        }
    }
    
    /**
     * Muestra las facturas de un cliente
     * @param id ID del cliente
     * @param nombre Nombre del cliente
     */
    private void verFacturasCliente(String id, String nombre) {
        // Aquí se mostraría un diálogo con las facturas del cliente
        // Esta funcionalidad debería implementarse en otra vista
        JOptionPane.showMessageDialog(vista.getPanel(),
            "Funcionalidad no implementada: Ver facturas del cliente " + nombre,
            "Información",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Muestra los pedidos de un proveedor
     * @param id ID del proveedor
     * @param empresa Nombre de la empresa
     */
    private void verPedidosProveedor(String id, String empresa) {
        // Aquí se mostraría un diálogo con los pedidos del proveedor
        // Esta funcionalidad debería implementarse en otra vista
        JOptionPane.showMessageDialog(vista.getPanel(),
            "Funcionalidad no implementada: Ver pedidos del proveedor " + empresa,
            "Información",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Muestra un diálogo de error
     * @param mensaje Mensaje de error
     * @param e Excepción (opcional)
     */
    private void mostrarError(String mensaje, Exception e) {
        String detalles = e != null ? ": " + e.getMessage() : "";
        JOptionPane.showMessageDialog(vista.getPanel(),
            mensaje + detalles,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
