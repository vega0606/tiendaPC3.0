package controlador;

import java.util.List;

import modelo.Usuario;
import Ventana.VistaUsuarios;

/**
 * Controlador para la vista de usuarios.
 * Gestiona la interacción entre la vista de usuarios y el modelo de datos.
 */
public class VistaUsuariosController {
    
    private VistaUsuarios vista;
    private UsuarioController usuarioController;
    
    /**
     * Constructor del controlador de vista de usuarios.
     * 
     * @param vista La vista de usuarios
     * @param usuarioController El controlador de usuarios
     */
    public VistaUsuariosController(VistaUsuarios vista, UsuarioController usuarioController) {
        this.vista = vista;
        this.usuarioController = usuarioController;
        
        // Inicializar los listeners y componentes de la vista
        inicializarVista();
    }
    
    /**
     * Inicializa los componentes de la vista y configura los listeners.
     */
    private void inicializarVista() {
        // Cargar los usuarios al iniciar la vista
        cargarUsuarios();
        
        // Configurar listeners para los botones de la vista
        configurarListeners();
    }
    
    /**
     * Configura los listeners para los botones y componentes interactivos de la vista.
     */
    private void configurarListeners() {
        // Listener para el botón de buscar usuario
        vista.getBtnBuscar().addActionListener(e -> buscarUsuarioPorUsername());
        
        // Listener para el botón de mostrar todos los usuarios
        vista.getBtnMostrarTodos().addActionListener(e -> cargarUsuarios());
        
        // Listener para el botón de nuevo usuario
        vista.getBtnNuevoUsuario().addActionListener(e -> abrirFormularioNuevoUsuario());
        
        // Listener para el botón de editar usuario
        vista.getBtnEditar().addActionListener(e -> editarUsuarioSeleccionado());
        
        // Listener para el botón de eliminar usuario
        vista.getBtnEliminar().addActionListener(e -> eliminarUsuarioSeleccionado());
        
        // Listener para el botón de cambiar contraseña
        vista.getBtnCambiarPassword().addActionListener(e -> cambiarPasswordUsuarioSeleccionado());
        
        // Listener para el botón de restablecer contraseña
        vista.getBtnRestablecerPassword().addActionListener(e -> restablecerPasswordUsuarioSeleccionado());
        
        // Listener para filtro por rol
        vista.getBtnFiltrarPorRol().addActionListener(e -> filtrarUsuariosPorRol());
        
        // Listener para el botón de activar/desactivar usuario
        vista.getBtnCambiarEstado().addActionListener(e -> cambiarEstadoUsuarioSeleccionado());
        
   
        
     
    }
    
    /**
     * Carga todos los usuarios en la tabla de la vista.
     */
    public void cargarUsuarios() {
        List<Usuario> usuarios = usuarioController.obtenerTodosUsuarios();
        vista.mostrarUsuarios(usuarios);
    }
    
    /**
     * Busca un usuario por su nombre de usuario y lo muestra en la vista.
     */
    private void buscarUsuarioPorUsername() {
        String username = vista.getTxtBusqueda().getText();
        
        if (username.isEmpty()) {
            vista.mostrarMensaje("Ingrese un nombre de usuario para buscar");
            return;
        }
        
        Usuario usuario = usuarioController.buscarUsuarioPorUsername(username);
        
        if (usuario != null) {
            vista.mostrarUsuario(usuario);
        } else {
            vista.mostrarMensaje("No se encontró ningún usuario con el nombre: " + username);
        }
    }
    
    /**
     * Abre el formulario para crear un nuevo usuario.
     */
    private void abrirFormularioNuevoUsuario() {
        vista.mostrarFormularioUsuario(null);
    }
    
    /**
     * Edita el usuario seleccionado en la tabla.
     */
    private void editarUsuarioSeleccionado() {
        Usuario usuarioSeleccionado = vista.obtenerUsuarioSeleccionado();
        
        if (usuarioSeleccionado != null) {
            vista.mostrarFormularioUsuario(usuarioSeleccionado);
        } else {
            vista.mostrarMensaje("Seleccione un usuario para editar");
        }
    }
    
    /**
     * Elimina el usuario seleccionado en la tabla.
     */
    private void eliminarUsuarioSeleccionado() {
        Usuario usuarioSeleccionado = vista.obtenerUsuarioSeleccionado();
        
        if (usuarioSeleccionado == null) {
            vista.mostrarMensaje("Seleccione un usuario para eliminar");
            return;
        }
        
        // Verificar que no se está eliminando al usuario actual
        Usuario usuarioActual = usuarioController.obtenerUsuarioActual();
        if (usuarioSeleccionado.getId() == usuarioActual.getId()) {
            vista.mostrarMensaje("No puede eliminar su propio usuario");
            return;
        }
        
        boolean confirmacion = vista.mostrarConfirmacion("¿Está seguro de eliminar este usuario?");
        
        if (confirmacion) {
            boolean eliminado = usuarioController.eliminarUsuario(usuarioSeleccionado.getId());
            
            if (eliminado) {
                vista.mostrarMensaje("Usuario eliminado correctamente");
                cargarUsuarios();
            } else {
                vista.mostrarMensaje("No se pudo eliminar el usuario");
            }
        }
    }
    
    /**
     * Cambia la contraseña del usuario seleccionado.
     */
    private void cambiarPasswordUsuarioSeleccionado() {
        Usuario usuarioSeleccionado = vista.obtenerUsuarioSeleccionado();
        
        if (usuarioSeleccionado == null) {
            vista.mostrarMensaje("Seleccione un usuario para cambiar su contraseña");
            return;
        }
        
        vista.mostrarFormularioCambioPassword(usuarioSeleccionado);
    }
    
    /**
     * Restablece la contraseña del usuario seleccionado a un valor predeterminado.
     */
    private void restablecerPasswordUsuarioSeleccionado() {
        Usuario usuarioSeleccionado = vista.obtenerUsuarioSeleccionado();
        
        if (usuarioSeleccionado == null) {
            vista.mostrarMensaje("Seleccione un usuario para restablecer su contraseña");
            return;
        }
        
        boolean confirmacion = vista.mostrarConfirmacion("¿Está seguro de restablecer la contraseña de este usuario?");
        
        if (confirmacion) {
            boolean restablecido = usuarioController.restablecerPassword(usuarioSeleccionado.getId());
            
            if (restablecido) {
                vista.mostrarMensaje("Contraseña restablecida correctamente");
            } else {
                vista.mostrarMensaje("No se pudo restablecer la contraseña");
            }
        }
    }
    
    /**
     * Filtra los usuarios por rol.
     */
    private void filtrarUsuariosPorRol() {
        String rol = vista.getComboRol().getSelectedItem().toString();
        
        if (rol.equals("Todos")) {
            cargarUsuarios();
            return;
        }
        
        List<Usuario> usuariosFiltrados = usuarioController.filtrarUsuariosPorRol(rol);
        vista.mostrarUsuarios(usuariosFiltrados);
    }
    
    /**
     * Cambia el estado (activo/inactivo) del usuario seleccionado.
     */
    private void cambiarEstadoUsuarioSeleccionado() {
        Usuario usuarioSeleccionado = vista.obtenerUsuarioSeleccionado();
        
        if (usuarioSeleccionado == null) {
            vista.mostrarMensaje("Seleccione un usuario para cambiar su estado");
            return;
        }
        
        // Verificar que no se está cambiando el estado del usuario actual
        Usuario usuarioActual = usuarioController.obtenerUsuarioActual();
        if (usuarioSeleccionado.getId() == usuarioActual.getId()) {
            vista.mostrarMensaje("No puede cambiar el estado de su propio usuario");
            return;
        }
        
        String estadoActual = usuarioSeleccionado.isActivo() ? "activo" : "inactivo";
        String nuevoEstado = usuarioSeleccionado.isActivo() ? "inactivo" : "activo";
        
        boolean confirmacion = vista.mostrarConfirmacion(
                "El usuario está actualmente " + estadoActual + ". ¿Desea cambiarlo a " + nuevoEstado + "?");
        
        if (confirmacion) {
            boolean actualizado = usuarioController.cambiarEstadoUsuario(
                    usuarioSeleccionado.getId(), !usuarioSeleccionado.isActivo());
            
            if (actualizado) {
                vista.mostrarMensaje("Estado del usuario actualizado correctamente");
                cargarUsuarios();
            } else {
                vista.mostrarMensaje("No se pudo actualizar el estado del usuario");
            }
        }
    }
    
    
   
    
    /**
     * Guarda un usuario nuevo o actualizado.
     * 
     * @param usuario El usuario a guardar
     * @param password La contraseña (solo para usuarios nuevos)
     * @param esNuevo Indica si es un nuevo usuario o una actualización
     * @return true si se guardó correctamente, false en caso contrario
     */
    public boolean guardarUsuario(Usuario usuario, String password, boolean esNuevo) {
        boolean resultado;
        
        if (esNuevo) {
            resultado = usuarioController.crearUsuario(usuario, password);
        } else {
            resultado = usuarioController.actualizarUsuario(usuario);
        }
        
        if (resultado) {
            cargarUsuarios();
        }
        
        return resultado;
    }
    
    /**
     * Cambia la contraseña de un usuario.
     * 
     * @param idUsuario El ID del usuario
     * @param passwordActual La contraseña actual
     * @param passwordNueva La nueva contraseña
     * @return true si se cambió correctamente, false en caso contrario
     */
    public boolean cambiarPassword(int idUsuario, String passwordActual, String passwordNueva) {
        return usuarioController.cambiarPassword(idUsuario, passwordActual, passwordNueva);
    }
}